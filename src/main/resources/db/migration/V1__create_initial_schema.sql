CREATE TABLE members (
                         id BIGSERIAL PRIMARY KEY,
                         email VARCHAR(255) NOT NULL,
                         CONSTRAINT uk_members_email UNIQUE (email)
);

CREATE TABLE member_coupons (
                        id BIGSERIAL PRIMARY KEY ,
                        member_id BIGINT NOT NULL ,
                        issued_at TIMESTAMP WITH TIME ZONE NOT NULL ,

                        CONSTRAINT uk_member_coupons_member_id
                            UNIQUE (member_id),

                        CONSTRAINT fk_member_coupons_member
                            FOREIGN KEY (member_id)
                            REFERENCES members (id)

);


CREATE TABLE signup_coupon_outbox (
                                      id BIGSERIAL PRIMARY KEY,
                                      member_id BIGINT NOT NULL,
                                      event_type VARCHAR(100) NOT NULL,
                                      status VARCHAR(30) NOT NULL,
                                      occurred_at TIMESTAMP WITH TIME ZONE NOT NULL,
                                      published_at TIMESTAMP WITH TIME ZONE,
                                      retry_count INTEGER NOT NULL DEFAULT 0,
                                      last_error TEXT,
                                      last_failed_at TIMESTAMP WITH TIME ZONE,

                                      CONSTRAINT fk_signup_coupon_outbox_member
                                          FOREIGN KEY (member_id)
                                              REFERENCES members (id),

                                      CONSTRAINT ck_signup_coupon_outbox_status
                                          CHECK (status IN ('PENDING', 'PUBLISHED', 'FAILED'))
);

CREATE INDEX idx_signup_coupon_outbox_status_occurred_at
    ON signup_coupon_outbox (status, occurred_at);
