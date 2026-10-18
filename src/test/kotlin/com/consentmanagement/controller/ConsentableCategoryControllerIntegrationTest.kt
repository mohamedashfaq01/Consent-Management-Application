package com.consentmanagement.controller

import com.consentmanagement.entity.ConsentableCategory
import com.consentmanagement.repository.ConsentableCategoryRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class ConsentableCategoryControllerIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var consentableCategoryRepository: ConsentableCategoryRepository

    @AfterEach
    fun cleanUp() {
        consentableCategoryRepository.deleteAll()
    }

    @Test
    fun `GET consentableCategory-all returns 200 with seeded categories from H2 database`() {
        consentableCategoryRepository.saveAll(
            listOf(
                ConsentableCategory(name = "GTC"),
                ConsentableCategory(name = "RU-NATV"),
                ConsentableCategory(name = "KYC"),
                ConsentableCategory(name = "KYC Test")
            )
        )

        mockMvc.perform(get("/consentableCategory/all"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$.length()").value(4))
            .andExpect(jsonPath("$[?(@.name == 'GTC')]").exists())
            .andExpect(jsonPath("$[?(@.name == 'RU-NATV')]").exists())
            .andExpect(jsonPath("$[?(@.name == 'KYC')]").exists())
            .andExpect(jsonPath("$[?(@.name == 'KYC Test')]").exists())
    }

    @Test
    fun `GET consentableCategory-all returns 200 with empty array when table is empty`() {
        mockMvc.perform(get("/consentableCategory/all"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$.length()").value(0))
    }
}

