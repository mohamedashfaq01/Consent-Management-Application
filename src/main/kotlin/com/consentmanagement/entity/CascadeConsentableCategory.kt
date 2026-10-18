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

@Embeddable
data class CascadeConsentableCategoryKey(
    @Column(name = "parent_category_id", nullable = false)
    var parentCategoryId: String = "",

    @Column(name = "child_category_id", nullable = false)
    var childCategoryId: String = ""
) : Serializable

@Entity
@Table(name = "consentable_category_cascade_v2")
class CascadeConsentableCategory(
    @EmbeddedId
    var id: CascadeConsentableCategoryKey = CascadeConsentableCategoryKey(),

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("parentCategoryId")
    @JoinColumn(name = "parent_category_id", nullable = false)
    var parentCategory: ConsentableCategory,

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("childCategoryId")
    @JoinColumn(name = "child_category_id", nullable = false)
    var childCategory: ConsentableCategory
) {
    constructor(parentCategory: ConsentableCategory, childCategory: ConsentableCategory) : this(
        id = CascadeConsentableCategoryKey(parentCategory.name, childCategory.name),
        parentCategory = parentCategory,
        childCategory = childCategory
    )

    val parentCategoryId: String
        get() = id.parentCategoryId

    val childCategoryId: String
        get() = id.childCategoryId
}
