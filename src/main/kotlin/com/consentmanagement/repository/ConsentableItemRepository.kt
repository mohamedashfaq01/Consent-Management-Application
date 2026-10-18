package com.consentmanagement.repository

import com.consentmanagement.entity.ConsentableCategory
import com.consentmanagement.entity.ConsentableItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ConsentableItemRepository : JpaRepository<ConsentableItem, UUID> {
    fun findByCategory(category: ConsentableCategory): List<ConsentableItem>
    fun findByCategoryName(categoryName: String): List<ConsentableItem>
}
