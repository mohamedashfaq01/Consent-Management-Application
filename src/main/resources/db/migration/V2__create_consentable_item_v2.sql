CREATE TABLE consentable_item_v2 (
    identifier UUID NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    category VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    business_line VARCHAR(255),
    start_date DATE,
    expiration_date DATE,
    version INT,
    CONSTRAINT fk_consentable_item_category FOREIGN KEY (category) REFERENCES consentable_category_v2(name)
);
