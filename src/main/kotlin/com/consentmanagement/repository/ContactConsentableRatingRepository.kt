package com.consentmanagement.repository

import com.consentmanagement.entity.ConsentableItem
import com.consentmanagement.entity.ContactConsentableRating
import com.consentmanagement.entity.CustomerConsentableRatingKey
import com.consentmanagement.entity.TargetCustomer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ContactConsentableRatingRepository : JpaRepository<ContactConsentableRating, CustomerConsentableRatingKey> {
    fun findByTargetCustomer(targetCustomer: TargetCustomer): List<ContactConsentableRating>
    fun findByConsentableItem(consentableItem: ConsentableItem): List<ContactConsentableRating>
    fun findByTargetCustomerIdentifier(targetCustomerId: UUID): List<ContactConsentableRating>
    fun findByConsentableItemIdentifier(consentableItemId: UUID): List<ContactConsentableRating>
}
