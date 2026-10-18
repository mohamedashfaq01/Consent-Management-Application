package com.consentmanagement.repository

import com.consentmanagement.entity.ConsentableCategory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class ConsentableCategoryRepositoryTest {

    @Autowired
    private lateinit var consentableCategoryRepository: ConsentableCategoryRepository

    @Test
    fun `saves and retrieves a category by name`() {
        consentableCategoryRepository.save(ConsentableCategory(name = "GTC"))

        val found = consentableCategoryRepository.findById("GTC")

        assertTrue(found.isPresent)
        assertEquals("GTC", found.get().name)
    }

    @Test
    fun `saves multiple categories and retrieves them all`() {
        consentableCategoryRepository.saveAll(
            listOf(
                ConsentableCategory(name = "KYC"),
                ConsentableCategory(name = "RU-NATV")
            )
        )

        val names = consentableCategoryRepository.findAll().map { it.name }.toSet()

        assertEquals(setOf("KYC", "RU-NATV"), names)
    }
}
