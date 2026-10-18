package com.consentmanagement.entity

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.MapsId
import jakarta.persistence.Table
import java.io.Serializable
import java.util.UUID

@Embeddable
data class CustomerConsentableRatingKey(
    @Column(name = "consentable_item_id", nullable = false)
    var consentableItemId: UUID = UUID.randomUUID(),

    @Column(name = "target_customer_id", nullable = false)
    var targetCustomerId: UUID = UUID.randomUUID()
) : Serializable

@Entity
@Table(name = "contact_consentable_rating_v2")
class ContactConsentableRating(
    @EmbeddedId
    var id: CustomerConsentableRatingKey = CustomerConsentableRatingKey(),

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("consentableItemId")
    @JoinColumn(name = "consentable_item_id", nullable = false)
    var consentableItem: ConsentableItem,

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("targetCustomerId")
    @JoinColumn(name = "target_customer_id", nullable = false)
    var targetCustomer: TargetCustomer,

    @Column(name = "is_consent_open", nullable = false)
    var isConsentOpen: Boolean,

    @Column(name = "has_cascade_dependency", nullable = false)
    var hasCascadeDependency: Boolean = false,

    @Column(name = "has_category_dependency", nullable = false)
    var hasCategoryDependency: Boolean = false
) {
    constructor(
        consentableItem: ConsentableItem,
        targetCustomer: TargetCustomer,
        isConsentOpen: Boolean,
        hasCascadeDependency: Boolean = false,
        hasCategoryDependency: Boolean = false
    ) : this(
        id = CustomerConsentableRatingKey(consentableItem.identifier, targetCustomer.identifier),
        consentableItem = consentableItem,
        targetCustomer = targetCustomer,
        isConsentOpen = isConsentOpen,
        hasCascadeDependency = hasCascadeDependency,
        hasCategoryDependency = hasCategoryDependency
    )

    val consentableItemId: UUID
        get() = id.consentableItemId

    val targetCustomerId: UUID
        get() = id.targetCustomerId
}
