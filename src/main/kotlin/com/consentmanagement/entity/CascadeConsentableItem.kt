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
data class CascadeConsentableItemKey(
    @Column(name = "parent_id", nullable = false)
    var parentId: UUID = UUID.randomUUID(),

    @Column(name = "child_id", nullable = false)
    var childId: UUID = UUID.randomUUID()
) : Serializable

@Entity
@Table(name = "consentable_cascade_v2")
class CascadeConsentableItem(
    @EmbeddedId
    var id: CascadeConsentableItemKey = CascadeConsentableItemKey(),

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("parentId")
    @JoinColumn(name = "parent_id", nullable = false)
    var parent: ConsentableItem,

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("childId")
    @JoinColumn(name = "child_id", nullable = false)
    var child: ConsentableItem
) {
    constructor(parent: ConsentableItem, child: ConsentableItem) : this(
        id = CascadeConsentableItemKey(parent.identifier, child.identifier),
        parent = parent,
        child = child
    )

    val parentId: UUID
        get() = id.parentId

    val childId: UUID
        get() = id.childId
}
