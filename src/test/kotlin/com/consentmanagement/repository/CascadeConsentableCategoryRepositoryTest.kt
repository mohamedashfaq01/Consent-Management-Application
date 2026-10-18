package com.consentmanagement.repository

import com.consentmanagement.entity.CascadeConsentableCategory
import com.consentmanagement.entity.CascadeConsentableCategoryKey
import com.consentmanagement.entity.ConsentableCategory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class CascadeConsentableCategoryRepositoryTest {

    @Autowired
    private lateinit var consentableCategoryRepository: ConsentableCategoryRepository

    @Autowired
    private lateinit var cascadeConsentableCategoryRepository: CascadeConsentableCategoryRepository

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    @Test
    fun `saves and retrieves CascadeConsentableCategory dependency relationship`() {
        val parentCategory = consentableCategoryRepository.save(ConsentableCategory(name = "PARENT_CAT"))
        val childCategory = consentableCategoryRepository.save(ConsentableCategory(name = "CHILD_CAT"))

        val cascade = CascadeConsentableCategory(parentCategory = parentCategory, childCategory = childCategory)
        cascadeConsentableCategoryRepository.save(cascade)
        cascadeConsentableCategoryRepository.flush()

        val key = CascadeConsentableCategoryKey(parentCategory.name, childCategory.name)
        val found = cascadeConsentableCategoryRepository.findById(key)

        assertTrue(found.isPresent)
        with(found.get()) {
            assertEquals("PARENT_CAT", parentCategoryId)
            assertEquals("CHILD_CAT", childCategoryId)
            assertEquals("PARENT_CAT", this.parentCategory.name)
            assertEquals("CHILD_CAT", this.childCategory.name)
        }

        val childCascades = cascadeConsentableCategoryRepository.findByParentCategory(parentCategory)
        assertEquals(1, childCascades.size)
        assertEquals("CHILD_CAT", childCascades[0].childCategory.name)

        val parentCascades = cascadeConsentableCategoryRepository.findByChildCategory(childCategory)
        assertEquals(1, parentCascades.size)
        assertEquals("PARENT_CAT", parentCascades[0].parentCategory.name)
    }

    @Test
    fun `enforces foreign key constraints for parent and child categories in H2`() {
        assertThrows<DataIntegrityViolationException> {
            jdbcTemplate.execute(
                """
                INSERT INTO consentable_category_cascade_v2 (parent_category_id, child_category_id)
                VALUES ('NON_EXISTENT_PARENT', 'NON_EXISTENT_CHILD')
                """.trimIndent()
            )
        }
    }
}
