package com.consentmanagement.repository

import com.consentmanagement.entity.ConsentableCategory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ConsentableCategoryRepository : JpaRepository<ConsentableCategory, String>
