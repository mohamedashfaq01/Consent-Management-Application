package com.consentmanagement.repository

import com.consentmanagement.entity.ConsentableCategory
import com.consentmanagement.entity.ConsentableItem
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.util.UUID

@SpringBootTest
@Transactional
class ConsentableItemRepositoryTest {

    @Autowired
    private lateinit var consentableCategoryRepository: ConsentableCategoryRepository

    @Autowired
    private lateinit var consentableItemRepository: ConsentableItemRepository

    @Test
    fun `saves and retrieves a ConsentableItem with all fields mapped`() {
        val category = consentableCategoryRepository.save(ConsentableCategory(name = "KYC"))

        val itemId = UUID.randomUUID()
        val item = ConsentableItem(
            identifier = itemId,
            name = "Passport Verification",
            category = category,
            status = "ACTIVE",
            businessLine = "RETAIL",
            startDate = LocalDate.of(2026, 1, 1),
            expirationDate = LocalDate.of(2026, 12, 31),
            version = 1
        )
        consentableItemRepository.save(item)

        val retrieved = consentableItemRepository.findById(itemId)
        assertTrue(retrieved.isPresent)
        with(retrieved.get()) {
            assertEquals(itemId, identifier)
            assertEquals("Passport Verification", name)
            assertEquals("KYC", this.category.name)
            assertEquals("ACTIVE", status)
            assertEquals("RETAIL", businessLine)
            assertEquals(LocalDate.of(2026, 1, 1), startDate)
            assertEquals(LocalDate.of(2026, 12, 31), expirationDate)
            assertEquals(1, version)
        }
    }

    @Test
    fun `supports multiple ConsentableItems belonging to one ConsentableCategory (Many-to-One)`() {
        val category = consentableCategoryRepository.save(ConsentableCategory(name = "GTC"))

        val item1 = ConsentableItem(
            name = "Terms of Service",
            category = category,
            status = "ACTIVE",
            businessLine = "DIGITAL",
            version = 1
        )
        val item2 = ConsentableItem(
            name = "Privacy Policy",
            category = category,
            status = "ACTIVE",
            businessLine = "DIGITAL",
            version = 2
        )
        consentableItemRepository.saveAll(listOf(item1, item2))

        val itemsByCategoryName = consentableItemRepository.findByCategoryName("GTC")
        assertEquals(2, itemsByCategoryName.size)
        val itemNames = itemsByCategoryName.map { it.name }.toSet()
        assertEquals(setOf("Terms of Service", "Privacy Policy"), itemNames)

        val itemsByCategory = consentableItemRepository.findByCategory(category)
        assertEquals(2, itemsByCategory.size)
    }

    @Test
    fun `deleting a ConsentableItem does not delete its ConsentableCategory`() {
        val category = consentableCategoryRepository.save(ConsentableCategory(name = "RU-NATV"))
        val item = consentableItemRepository.save(
            ConsentableItem(
                name = "Russian Native Consent",
                category = category,
                status = "ACTIVE"
            )
        )

        consentableItemRepository.delete(item)
        consentableItemRepository.flush()

        val foundItem = consentableItemRepository.findById(item.identifier)
        assertTrue(foundItem.isEmpty)

        val foundCategory = consentableCategoryRepository.findById("RU-NATV")
        assertTrue(foundCategory.isPresent)
        assertEquals("RU-NATV", foundCategory.get().name)
    }

    @Autowired
    private lateinit var jdbcTemplate: org.springframework.jdbc.core.JdbcTemplate

    @Test
    fun `enforces relationship and foreign key constraint when category does not exist`() {
        // 1. JPA entity validation when category is not persisted
        val nonExistentCategory = ConsentableCategory(name = "NON_EXISTENT")
        val item = ConsentableItem(
            name = "Invalid Item",
            category = nonExistentCategory,
            status = "DRAFT"
        )
        assertThrows<org.springframework.dao.DataAccessException> {
            consentableItemRepository.saveAndFlush(item)
        }

        // 2. Direct H2 database constraint verification
        assertThrows<DataIntegrityViolationException> {
            jdbcTemplate.execute(
                """
                INSERT INTO consentable_item_v2 (identifier, name, category, status)
                VALUES ('${UUID.randomUUID()}', 'Raw Item', 'NON_EXISTENT_CAT', 'ACTIVE')
                """.trimIndent()
            )
        }
    }
}

