package com.consentmanagement.repository

import com.consentmanagement.entity.TargetCustomer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID

@SpringBootTest
@Transactional
class TargetCustomerRepositoryTest {

    @Autowired
    private lateinit var targetCustomerRepository: TargetCustomerRepository

    @Test
    fun `saves and retrieves a TargetCustomer with all fields mapped`() {
        val now = Instant.now()
        val customer = TargetCustomer(
            identifier = UUID.randomUUID(),
            customerId = "CUST-98765",
            createdAt = now,
            lastUpdate = now
        )

        val saved = targetCustomerRepository.save(customer)
        targetCustomerRepository.flush()

        val found = targetCustomerRepository.findById(saved.identifier)
        assertTrue(found.isPresent)
        with(found.get()) {
            assertEquals(saved.identifier, identifier)
            assertEquals("CUST-98765", customerId)
            assertNotNull(createdAt)
            assertNotNull(lastUpdate)
        }
    }

    @Test
    fun `retrieves TargetCustomer by customerId`() {
        val customer = TargetCustomer(
            customerId = "CUST-UNIQUE-1"
        )
        targetCustomerRepository.save(customer)

        val found = targetCustomerRepository.findByCustomerId("CUST-UNIQUE-1")
        assertTrue(found.isPresent)
        assertEquals(customer.identifier, found.get().identifier)
    }
}
