CREATE TABLE contact_consentable_rating_v2 (
    consentable_item_id UUID NOT NULL,
    target_customer_id UUID NOT NULL,
    is_consent_open BOOLEAN NOT NULL,
    has_cascade_dependency BOOLEAN NOT NULL DEFAULT FALSE,
    has_category_dependency BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (consentable_item_id, target_customer_id),
    CONSTRAINT fk_rating_consentable_item FOREIGN KEY (consentable_item_id) REFERENCES consentable_item_v2(identifier),
    CONSTRAINT fk_rating_target_customer FOREIGN KEY (target_customer_id) REFERENCES target_customer_v2(identifier)
);

CREATE TABLE consentable_cascade_v2 (
    parent_id UUID NOT NULL,
    child_id UUID NOT NULL,
    PRIMARY KEY (parent_id, child_id),
    CONSTRAINT fk_cascade_parent FOREIGN KEY (parent_id) REFERENCES consentable_item_v2(identifier),
    CONSTRAINT fk_cascade_child FOREIGN KEY (child_id) REFERENCES consentable_item_v2(identifier)
);

CREATE TABLE consentable_category_cascade_v2 (
    parent_category_id VARCHAR(255) NOT NULL,
    child_category_id VARCHAR(255) NOT NULL,
    PRIMARY KEY (parent_category_id, child_category_id),
    CONSTRAINT fk_cat_cascade_parent FOREIGN KEY (parent_category_id) REFERENCES consentable_category_v2(name),
    CONSTRAINT fk_cat_cascade_child FOREIGN KEY (child_category_id) REFERENCES consentable_category_v2(name)
);
