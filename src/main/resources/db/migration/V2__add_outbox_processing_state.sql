ALTER TABLE signup_coupon_outbox
    ADD COLUMN processing_started_at TIMESTAMP WITH TIME ZONE;

ALTER TABLE signup_coupon_outbox
DROP CONSTRAINT ck_signup_coupon_outbox_status;

ALTER TABLE signup_coupon_outbox
    ADD CONSTRAINT ck_signup_coupon_outbox_status
        CHECK (
            status IN (
                       'PENDING',
                       'PROCESSING',
                       'PUBLISHED',
                       'FAILED'
                )
            );