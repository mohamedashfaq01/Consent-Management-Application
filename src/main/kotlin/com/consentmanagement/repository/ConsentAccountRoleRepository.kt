package com.consentmanagement.repository

import com.consentmanagement.entity.Consent
import com.consentmanagement.entity.ConsentAccountRole
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ConsentAccountRoleRepository : JpaRepository<ConsentAccountRole, UUID> {
    fun findByConsent(consent: Consent): List<ConsentAccountRole>
    fun findByConsentIdentifier(consentIdentifier: UUID): List<ConsentAccountRole>
    fun findByAccountId(accountId: String): List<ConsentAccountRole>
}
