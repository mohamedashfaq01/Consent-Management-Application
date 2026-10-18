package com.consentmanagement.repository

import com.consentmanagement.entity.CascadeConsentableCategory
import com.consentmanagement.entity.CascadeConsentableCategoryKey
import com.consentmanagement.entity.ConsentableCategory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CascadeConsentableCategoryRepository : JpaRepository<CascadeConsentableCategory, CascadeConsentableCategoryKey> {
    fun findByParentCategory(parentCategory: ConsentableCategory): List<CascadeConsentableCategory>
    fun findByChildCategory(childCategory: ConsentableCategory): List<CascadeConsentableCategory>
    fun findByParentCategoryName(parentCategoryName: String): List<CascadeConsentableCategory>
    fun findByChildCategoryName(childCategoryName: String): List<CascadeConsentableCategory>
}
