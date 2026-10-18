package com.consentmanagement.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "consent_account_role_v2")
class ConsentAccountRole(
    @Id
    @Column(name = "identifier", nullable = false)
    var identifier: UUID = UUID.randomUUID(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consent_id", nullable = false)
    var consent: Consent,

    @Column(name = "account_id", nullable = false)
    var accountId: String,

    @Column(name = "account_type")
    var accountType: String? = null,

    @Column(name = "role", nullable = false)
    var role: String
)
