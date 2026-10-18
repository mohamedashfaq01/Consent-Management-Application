package com.consentmanagement.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "target_customer_v2")
class TargetCustomer(
    @Id
    @Column(name = "identifier", nullable = false)
    var identifier: UUID = UUID.randomUUID(),

    @Column(name = "customer_id", nullable = false)
    var customerId: String,

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant = Instant.now(),

    @Column(name = "last_update", nullable = false)
    var lastUpdate: Instant = Instant.now()
)
