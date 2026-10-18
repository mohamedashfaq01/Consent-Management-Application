package com.consentmanagement.service

import com.consentmanagement.dto.ConsentableCategoryDto
import com.consentmanagement.repository.ConsentableCategoryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ConsentableCategoryService(
    private val consentableCategoryRepository: ConsentableCategoryRepository
) {
    @Transactional(readOnly = true)
    fun getAllCategories(): List<ConsentableCategoryDto> {
        return consentableCategoryRepository.findAll()
            .map { ConsentableCategoryDto(name = it.name) }
    }
}
