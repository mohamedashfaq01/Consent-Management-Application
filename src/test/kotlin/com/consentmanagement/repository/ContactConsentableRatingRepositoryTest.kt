package com.consentmanagement.repository

import com.consentmanagement.entity.ConsentableCategory
import com.consentmanagement.entity.ConsentableItem
import com.consentmanagement.entity.ContactConsentableRating
import com.consentmanagement.entity.CustomerConsentableRatingKey
import com.consentmanagement.entity.TargetCustomer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@SpringBootTest
@Transactional
class ContactConsentableRatingRepositoryTest {

    @Autowired
    private lateinit var consentableCategoryRepository: ConsentableCategoryRepository

    @Autowired
    private lateinit var consentableItemRepository: ConsentableItemRepository

    @Autowired
    private lateinit var targetCustomerRepository: TargetCustomerRepository

    @Autowired
    private lateinit var contactConsentableRatingRepository: ContactConsentableRatingRepository

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    @Test
    fun `saves and retrieves ContactConsentableRating with composite key and all fields`() {
        val category = consentableCategoryRepository.save(ConsentableCategory(name = "KYC"))
        val item = consentableItemRepository.save(
            ConsentableItem(name = "ID Verification", category = category, status = "ACTIVE")
        )
        val customer = targetCustomerRepository.save(
            TargetCustomer(customerId = "CUST-RATING-1")
        )

        val rating = ContactConsentableRating(
            consentableItem = item,
            targetCustomer = customer,
            isConsentOpen = true,
            hasCascadeDependency = true,
            hasCategoryDependency = false
        )
        contactConsentableRatingRepository.save(rating)
        contactConsentableRatingRepository.flush()

        val key = CustomerConsentableRatingKey(item.identifier, customer.identifier)
        val found = contactConsentableRatingRepository.findById(key)

        assertTrue(found.isPresent)
        with(found.get()) {
            assertEquals(item.identifier, consentableItemId)
            assertEquals(customer.identifier, targetCustomerId)
            assertTrue(isConsentOpen)
            assertTrue(hasCascadeDependency)
            assertFalse(hasCategoryDependency)
            assertEquals(item.identifier, this.consentableItem.identifier)
            assertEquals(customer.identifier, this.targetCustomer.identifier)
        }
    }

    @Test
    fun `queries ratings by TargetCustomer and ConsentableItem`() {
        val category = consentableCategoryRepository.save(ConsentableCategory(name = "GTC"))
        val item1 = consentableItemRepository.save(
            ConsentableItem(name = "Terms A", category = category, status = "ACTIVE")
        )
        val item2 = consentableItemRepository.save(
            ConsentableItem(name = "Terms B", category = category, status = "ACTIVE")
        )
        val customer = targetCustomerRepository.save(
            TargetCustomer(customerId = "CUST-MULTI-RATING")
        )

        contactConsentableRatingRepository.saveAll(
            listOf(
                ContactConsentableRating(consentableItem = item1, targetCustomer = customer, isConsentOpen = true),
                ContactConsentableRating(consentableItem = item2, targetCustomer = customer, isConsentOpen = false)
            )
        )
        contactConsentableRatingRepository.flush()

        val customerRatings = contactConsentableRatingRepository.findByTargetCustomer(customer)
        assertEquals(2, customerRatings.size)

        val item1Ratings = contactConsentableRatingRepository.findByConsentableItem(item1)
        assertEquals(1, item1Ratings.size)
        assertTrue(item1Ratings[0].isConsentOpen)
    }

    @Test
    fun `enforces foreign key constraints for both customer and item in H2`() {
        // Missing item foreign key violation
        assertThrows<DataIntegrityViolationException> {
            jdbcTemplate.execute(
                """
                INSERT INTO contact_consentable_rating_v2 (consentable_item_id, target_customer_id, is_consent_open, has_cascade_dependency, has_category_dependency)
                VALUES ('${UUID.randomUUID()}', '${UUID.randomUUID()}', true, false, false)
                """.trimIndent()
            )
        }
    }
}
