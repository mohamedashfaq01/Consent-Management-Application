package com.consentmanagement.repository

import com.consentmanagement.entity.CascadeConsentableItem
import com.consentmanagement.entity.CascadeConsentableItemKey
import com.consentmanagement.entity.ConsentableItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface CascadeConsentableItemRepository : JpaRepository<CascadeConsentableItem, CascadeConsentableItemKey> {
    fun findByParent(parent: ConsentableItem): List<CascadeConsentableItem>
    fun findByChild(child: ConsentableItem): List<CascadeConsentableItem>
    fun findByParentIdentifier(parentId: UUID): List<CascadeConsentableItem>
    fun findByChildIdentifier(childId: UUID): List<CascadeConsentableItem>
}
