package com.consentmanagement.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "consentable_category_v2")
class ConsentableCategory(
    @Id
    @Column(name = "name", nullable = false)
    var name: String
)
