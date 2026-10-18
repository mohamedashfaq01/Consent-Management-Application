CREATE TABLE consent_account_role_v2 (
    identifier UUID NOT NULL PRIMARY KEY,
    consent_id UUID NOT NULL,
    account_id VARCHAR(255) NOT NULL,
    account_type VARCHAR(255),
    role VARCHAR(255) NOT NULL,
    CONSTRAINT fk_consent_account_role_consent FOREIGN KEY (consent_id) REFERENCES consent_v2(identifier)
);
