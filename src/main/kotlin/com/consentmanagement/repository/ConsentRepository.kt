package com.consentmanagement.repository

import com.consentmanagement.entity.Consent
import com.consentmanagement.entity.TargetCustomer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ConsentRepository : JpaRepository<Consent, UUID> {
    fun findByTargetCustomer(targetCustomer: TargetCustomer): List<Consent>
    fun findByTargetCustomerIdentifier(targetCustomerIdentifier: UUID): List<Consent>
}
