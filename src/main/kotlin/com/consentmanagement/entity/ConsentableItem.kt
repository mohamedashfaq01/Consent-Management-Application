package com.consentmanagement.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDate
import java.util.UUID

@Entity
@Table(name = "consentable_item_v2")
class ConsentableItem(
    @Id
    @Column(name = "identifier", nullable = false)
    var identifier: UUID = UUID.randomUUID(),

    @Column(name = "name", nullable = false)
    var name: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category", referencedColumnName = "name", nullable = false)
    var category: ConsentableCategory,

    @Column(name = "status", nullable = false)
    var status: String,

    @Column(name = "business_line")
    var businessLine: String? = null,

    @Column(name = "start_date")
    var startDate: LocalDate? = null,

    @Column(name = "expiration_date")
    var expirationDate: LocalDate? = null,

    @Column(name = "version")
    var version: Int? = null
)
