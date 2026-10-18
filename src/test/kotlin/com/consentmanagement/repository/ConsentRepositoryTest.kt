package com.consentmanagement.repository

import com.consentmanagement.entity.Consent
import com.consentmanagement.entity.ConsentDecision
import com.consentmanagement.entity.TargetCustomer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.dao.DataAccessException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID

@SpringBootTest
@Transactional
class ConsentRepositoryTest {

    @Autowired
    private lateinit var targetCustomerRepository: TargetCustomerRepository

    @Autowired
    private lateinit var consentRepository: ConsentRepository

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    @Test
    fun `saves and retrieves a Consent with all fields mapped`() {
        val customer = targetCustomerRepository.save(
            TargetCustomer(customerId = "CUST-CONSENT-1")
        )

        val consentId = UUID.randomUUID()
        val now = Instant.now()
        val consent = Consent(
            identifier = consentId,
            targetCustomer = customer,
            consentDecision = ConsentDecision.ACCEPTED,
            timestamp = now,
            frontendType = "WEB",
            authorizationType = "PASSWORD_SMS",
            advisorId = "ADV-001",
            userId = "USER-123"
        )
        val saved = consentRepository.save(consent)
        consentRepository.flush()

        val retrieved = consentRepository.findById(saved.identifier)
        assertTrue(retrieved.isPresent)
        with(retrieved.get()) {
            assertEquals(consentId, identifier)
            assertEquals(customer.identifier, targetCustomer.identifier)
            assertEquals(ConsentDecision.ACCEPTED, consentDecision)
            assertEquals("WEB", frontendType)
            assertEquals("PASSWORD_SMS", authorizationType)
            assertEquals("ADV-001", advisorId)
            assertEquals("USER-123", userId)
            assertNotNull(timestamp)
        }
    }

    @Test
    fun `supports multiple Consent records belonging to one TargetCustomer`() {
        val customer = targetCustomerRepository.save(
            TargetCustomer(customerId = "CUST-MULTI-CONSENT")
        )

        val consent1 = Consent(
            targetCustomer = customer,
            consentDecision = ConsentDecision.ACCEPTED,
            frontendType = "MOBILE",
            authorizationType = "BIOMETRIC"
        )
        val consent2 = Consent(
            targetCustomer = customer,
            consentDecision = ConsentDecision.REFUSED,
            frontendType = "BRANCH",
            authorizationType = "PAPER_SIGNATURE",
            advisorId = "ADV-BRANCH-99"
        )
        consentRepository.saveAll(listOf(consent1, consent2))
        consentRepository.flush()

        val customerConsents = consentRepository.findByTargetCustomer(customer)
        assertEquals(2, customerConsents.size)

        val byCustomerIdentifier = consentRepository.findByTargetCustomerIdentifier(customer.identifier)
        assertEquals(2, byCustomerIdentifier.size)

        val decisions = customerConsents.map { it.consentDecision }.toSet()
        assertEquals(setOf(ConsentDecision.ACCEPTED, ConsentDecision.REFUSED), decisions)
    }

    @Test
    fun `optional fields can be null`() {
        val customer = targetCustomerRepository.save(
            TargetCustomer(customerId = "CUST-OPTIONAL")
        )

        val consent = Consent(
            targetCustomer = customer,
            consentDecision = ConsentDecision.ACCEPTED,
            frontendType = "API",
            authorizationType = "OAUTH2",
            advisorId = null,
            userId = null
        )
        val saved = consentRepository.save(consent)
        consentRepository.flush()

        val found = consentRepository.findById(saved.identifier)
        assertTrue(found.isPresent)
        assertNull(found.get().advisorId)
        assertNull(found.get().userId)
    }

    @Test
    fun `enforces foreign key constraint when target customer does not exist`() {
        val nonExistentCustomer = TargetCustomer(
            identifier = UUID.randomUUID(),
            customerId = "NON_EXISTENT"
        )
        val consent = Consent(
            targetCustomer = nonExistentCustomer,
            consentDecision = ConsentDecision.ACCEPTED,
            frontendType = "WEB",
            authorizationType = "PASSWORD"
        )

        assertThrows<DataAccessException> {
            consentRepository.saveAndFlush(consent)
        }

        assertThrows<DataIntegrityViolationException> {
            jdbcTemplate.execute(
                """
                INSERT INTO consent_v2 (identifier, target_customer_id, consent_decision, timestamp, frontend_type, authorization_type)
                VALUES ('${UUID.randomUUID()}', '${UUID.randomUUID()}', 'ACCEPTED', CURRENT_TIMESTAMP, 'WEB', 'PASSWORD')
                """.trimIndent()
            )
        }
    }
}
