CREATE TABLE consent_v2 (
    identifier UUID NOT NULL PRIMARY KEY,
    target_customer_id UUID NOT NULL,
    consent_decision VARCHAR(50) NOT NULL,
    timestamp TIMESTAMP WITH TIME ZONE NOT NULL,
    frontend_type VARCHAR(255) NOT NULL,
    authorization_type VARCHAR(255) NOT NULL,
    advisor_id VARCHAR(255),
    user_id VARCHAR(255),
    CONSTRAINT fk_consent_target_customer FOREIGN KEY (target_customer_id) REFERENCES target_customer_v2(identifier)
);
