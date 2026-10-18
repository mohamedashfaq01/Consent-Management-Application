package com.consentmanagement.repository

import com.consentmanagement.entity.Consent
import com.consentmanagement.entity.ConsentAccountRole
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
import java.util.UUID

@SpringBootTest
@Transactional
class ConsentAccountRoleRepositoryTest {

    @Autowired
    private lateinit var targetCustomerRepository: TargetCustomerRepository

    @Autowired
    private lateinit var consentRepository: ConsentRepository

    @Autowired
    private lateinit var consentAccountRoleRepository: ConsentAccountRoleRepository

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    @Test
    fun `saves and retrieves a ConsentAccountRole with all fields mapped`() {
        val customer = targetCustomerRepository.save(
            TargetCustomer(customerId = "CUST-ROLE-1")
        )
        val consent = consentRepository.save(
            Consent(
                targetCustomer = customer,
                consentDecision = ConsentDecision.ACCEPTED,
                frontendType = "WEB",
                authorizationType = "PASSWORD"
            )
        )

        val roleId = UUID.randomUUID()
        val accountRole = ConsentAccountRole(
            identifier = roleId,
            consent = consent,
            accountId = "ACC-12345",
            accountType = "CHECKING",
            role = "PRIMARY_OWNER"
        )
        val saved = consentAccountRoleRepository.save(accountRole)
        consentAccountRoleRepository.flush()

        val found = consentAccountRoleRepository.findById(saved.identifier)
        assertTrue(found.isPresent)
        with(found.get()) {
            assertEquals(roleId, identifier)
            assertEquals(consent.identifier, this.consent.identifier)
            assertEquals("ACC-12345", accountId)
            assertEquals("CHECKING", accountType)
            assertEquals("PRIMARY_OWNER", role)
        }
    }

    @Test
    fun `supports multiple ConsentAccountRole records belonging to one Consent`() {
        val customer = targetCustomerRepository.save(
            TargetCustomer(customerId = "CUST-MULTI-ROLE")
        )
        val consent = consentRepository.save(
            Consent(
                targetCustomer = customer,
                consentDecision = ConsentDecision.ACCEPTED,
                frontendType = "MOBILE",
                authorizationType = "BIOMETRIC"
            )
        )

        val role1 = ConsentAccountRole(
            consent = consent,
            accountId = "ACC-001",
            accountType = "SAVINGS",
            role = "HOLDER"
        )
        val role2 = ConsentAccountRole(
            consent = consent,
            accountId = "ACC-002",
            accountType = "CREDIT_CARD",
            role = "AUTHORIZED_SIGNER"
        )
        consentAccountRoleRepository.saveAll(listOf(role1, role2))
        consentAccountRoleRepository.flush()

        val rolesByConsent = consentAccountRoleRepository.findByConsent(consent)
        assertEquals(2, rolesByConsent.size)

        val rolesByConsentIdentifier = consentAccountRoleRepository.findByConsentIdentifier(consent.identifier)
        assertEquals(2, rolesByConsentIdentifier.size)

        val accountIds = rolesByConsent.map { it.accountId }.toSet()
        assertEquals(setOf("ACC-001", "ACC-002"), accountIds)
    }

    @Test
    fun `optional accountType can be null`() {
        val customer = targetCustomerRepository.save(
            TargetCustomer(customerId = "CUST-ROLE-NULL")
        )
        val consent = consentRepository.save(
            Consent(
                targetCustomer = customer,
                consentDecision = ConsentDecision.ACCEPTED,
                frontendType = "BRANCH",
                authorizationType = "SIGNATURE"
            )
        )

        val accountRole = ConsentAccountRole(
            consent = consent,
            accountId = "ACC-NO-TYPE",
            accountType = null,
            role = "MEMBER"
        )
        val saved = consentAccountRoleRepository.save(accountRole)
        consentAccountRoleRepository.flush()

        val found = consentAccountRoleRepository.findById(saved.identifier)
        assertTrue(found.isPresent)
        assertNull(found.get().accountType)
    }

    @Test
    fun `enforces foreign key constraint when consent does not exist`() {
        val nonExistentCustomer = TargetCustomer(
            identifier = UUID.randomUUID(),
            customerId = "NON_EXISTENT_CUST"
        )
        val nonExistentConsent = Consent(
            identifier = UUID.randomUUID(),
            targetCustomer = nonExistentCustomer,
            consentDecision = ConsentDecision.ACCEPTED,
            frontendType = "WEB",
            authorizationType = "PASSWORD"
        )

        val role = ConsentAccountRole(
            consent = nonExistentConsent,
            accountId = "ACC-INVALID",
            role = "OWNER"
        )

        assertThrows<DataAccessException> {
            consentAccountRoleRepository.saveAndFlush(role)
        }

        assertThrows<DataIntegrityViolationException> {
            jdbcTemplate.execute(
                """
                INSERT INTO consent_account_role_v2 (identifier, consent_id, account_id, role)
                VALUES ('${UUID.randomUUID()}', '${UUID.randomUUID()}', 'ACC-RAW', 'OWNER')
                """.trimIndent()
            )
        }
    }
}
