package com.consentmanagement.controller

import com.consentmanagement.dto.ConsentableCategoryDto
import com.consentmanagement.service.ConsentableCategoryService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/consentableCategory")
class ConsentableCategoryController(
    private val consentableCategoryService: ConsentableCategoryService
) {
    @GetMapping("/all")
    fun getAllCategories(): ResponseEntity<List<ConsentableCategoryDto>> {
        val categories = consentableCategoryService.getAllCategories()
        return ResponseEntity.ok(categories)
    }
}
