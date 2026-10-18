package com.consentmanagement.repository

import com.consentmanagement.entity.CascadeConsentableItem
import com.consentmanagement.entity.CascadeConsentableItemKey
import com.consentmanagement.entity.ConsentableCategory
import com.consentmanagement.entity.ConsentableItem
import org.junit.jupiter.api.Assertions.assertEquals
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
class CascadeConsentableItemRepositoryTest {

    @Autowired
    private lateinit var consentableCategoryRepository: ConsentableCategoryRepository

    @Autowired
    private lateinit var consentableItemRepository: ConsentableItemRepository

    @Autowired
    private lateinit var cascadeConsentableItemRepository: CascadeConsentableItemRepository

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    @Test
    fun `saves and retrieves CascadeConsentableItem dependency relationship`() {
        val category = consentableCategoryRepository.save(ConsentableCategory(name = "KYC"))
        val parentItem = consentableItemRepository.save(
            ConsentableItem(name = "Master Agreement", category = category, status = "ACTIVE")
        )
        val childItem = consentableItemRepository.save(
            ConsentableItem(name = "Data Processing Addendum", category = category, status = "ACTIVE")
        )

        val cascade = CascadeConsentableItem(parent = parentItem, child = childItem)
        cascadeConsentableItemRepository.save(cascade)
        cascadeConsentableItemRepository.flush()

        val key = CascadeConsentableItemKey(parentItem.identifier, childItem.identifier)
        val found = cascadeConsentableItemRepository.findById(key)

        assertTrue(found.isPresent)
        with(found.get()) {
            assertEquals(parentItem.identifier, parentId)
            assertEquals(childItem.identifier, childId)
            assertEquals(parentItem.identifier, parent.identifier)
            assertEquals(childItem.identifier, child.identifier)
        }

        val childrenOfParent = cascadeConsentableItemRepository.findByParent(parentItem)
        assertEquals(1, childrenOfParent.size)
        assertEquals(childItem.identifier, childrenOfParent[0].child.identifier)

        val parentsOfChild = cascadeConsentableItemRepository.findByChild(childItem)
        assertEquals(1, parentsOfChild.size)
        assertEquals(parentItem.identifier, parentsOfChild[0].parent.identifier)
    }

    @Test
    fun `enforces foreign key constraints for parent and child items in H2`() {
        assertThrows<DataIntegrityViolationException> {
            jdbcTemplate.execute(
                """
                INSERT INTO consentable_cascade_v2 (parent_id, child_id)
                VALUES ('${UUID.randomUUID()}', '${UUID.randomUUID()}')
                """.trimIndent()
            )
        }
    }
}
