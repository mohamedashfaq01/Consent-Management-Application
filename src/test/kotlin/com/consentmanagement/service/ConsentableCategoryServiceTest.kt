package com.consentmanagement.service

import com.consentmanagement.entity.ConsentableCategory
import com.consentmanagement.repository.ConsentableCategoryRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class ConsentableCategoryServiceTest {

    @Mock
    private lateinit var consentableCategoryRepository: ConsentableCategoryRepository

    @InjectMocks
    private lateinit var consentableCategoryService: ConsentableCategoryService

    @Test
    fun `getAllCategories returns mapped DTOs`() {
        val entities = listOf(
            ConsentableCategory(name = "GTC"),
            ConsentableCategory(name = "KYC")
        )
        `when`(consentableCategoryRepository.findAll()).thenReturn(entities)

        val result = consentableCategoryService.getAllCategories()

        assertEquals(2, result.size)
        assertEquals("GTC", result[0].name)
        assertEquals("KYC", result[1].name)
    }

    @Test
    fun `getAllCategories returns empty list when no categories`() {
        `when`(consentableCategoryRepository.findAll()).thenReturn(emptyList())

        val result = consentableCategoryService.getAllCategories()

        assertTrue(result.isEmpty())
    }
}
