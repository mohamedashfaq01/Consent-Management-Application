package com.consentmanagement.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

enum class ConsentDecision {
    ACCEPTED,
    REFUSED
}

@Entity
@Table(name = "consent_v2")
class Consent(
    @Id
    @Column(name = "identifier", nullable = false)
    var identifier: UUID = UUID.randomUUID(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_customer_id", nullable = false)
    var targetCustomer: TargetCustomer,

    @Enumerated(EnumType.STRING)
    @Column(name = "consent_decision", nullable = false)
    var consentDecision: ConsentDecision,

    @Column(name = "timestamp", nullable = false)
    var timestamp: Instant = Instant.now(),

    @Column(name = "frontend_type", nullable = false)
    var frontendType: String,

    @Column(name = "authorization_type", nullable = false)
    var authorizationType: String,

    @Column(name = "advisor_id")
    var advisorId: String? = null,

    @Column(name = "user_id")
    var userId: String? = null
)
