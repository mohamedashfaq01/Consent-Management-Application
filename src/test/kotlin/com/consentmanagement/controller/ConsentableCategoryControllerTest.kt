package com.consentmanagement.controller

import com.consentmanagement.dto.ConsentableCategoryDto
import com.consentmanagement.service.ConsentableCategoryService
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(ConsentableCategoryController::class)
class ConsentableCategoryControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var consentableCategoryService: ConsentableCategoryService

    @Test
    fun `GET all categories returns 200 with list of categories`() {
        val categories = listOf(
            ConsentableCategoryDto(name = "GTC"),
            ConsentableCategoryDto(name = "KYC")
        )
        `when`(consentableCategoryService.getAllCategories()).thenReturn(categories)

        mockMvc.perform(get("/consentableCategory/all"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].name").value("GTC"))
            .andExpect(jsonPath("$[1].name").value("KYC"))
    }

    @Test
    fun `GET all categories returns 200 with empty list when no categories exist`() {
        `when`(consentableCategoryService.getAllCategories()).thenReturn(emptyList())

        mockMvc.perform(get("/consentableCategory/all"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$.length()").value(0))
    }
}
