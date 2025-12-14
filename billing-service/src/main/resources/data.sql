-- Plans
CREATE TABLE IF NOT EXISTS plan (
    plan_code            VARCHAR(50)  PRIMARY KEY,
    name                 VARCHAR(255) NOT NULL,
    monthly_price_cents  INT          NOT NULL,
    annual_price_cents   INT          NOT NULL,
    proration_policy     VARCHAR(50)  NOT NULL, -- DAILY
    discountable         BOOLEAN      NOT NULL,
    active               BOOLEAN      NOT NULL
);

INSERT INTO plan (plan_code, name, monthly_price_cents, annual_price_cents, proration_policy, discountable, active)
SELECT 'BASIC', 'Basic', 10000, 108000, 'DAILY', TRUE, TRUE
WHERE NOT EXISTS (SELECT 1 FROM plan WHERE plan_code = 'BASIC');

INSERT INTO plan (plan_code, name, monthly_price_cents, annual_price_cents, proration_policy, discountable, active)
SELECT 'STANDARD', 'Standard', 20000, 216000, 'DAILY', TRUE, TRUE
WHERE NOT EXISTS (SELECT 1 FROM plan WHERE plan_code = 'STANDARD');

INSERT INTO plan (plan_code, name, monthly_price_cents, annual_price_cents, proration_policy, discountable, active)
SELECT 'PREMIUM', 'Premium', 40000, 432000, 'DAILY', FALSE, TRUE
WHERE NOT EXISTS (SELECT 1 FROM plan WHERE plan_code = 'PREMIUM');

-- Discounts (single applies_to_plan_code per design; null = all plans)
CREATE TABLE IF NOT EXISTS discount (
    discount_code       VARCHAR(50)  PRIMARY KEY,
    discount_type       VARCHAR(50)  NOT NULL, -- PERCENT | AMOUNT
    discount_value      BIGINT       NOT NULL, -- bps when percent, cents when amount
    apply_to_plan_code  VARCHAR(50),
    active              BOOLEAN      NOT NULL,
    CONSTRAINT fk_discount_plan FOREIGN KEY (apply_to_plan_code) REFERENCES plan(plan_code)
);

INSERT INTO discount (discount_code, discount_type, discount_value, apply_to_plan_code, active)
SELECT 'WELCOME10', 'PERCENT', 1000, NULL, TRUE
WHERE NOT EXISTS (SELECT 1 FROM discount WHERE discount_code = 'WELCOME10');

-- NOTE: With single apply_to_plan_code, we map NONPROFIT50 to STANDARD here.
INSERT INTO discount (discount_code, discount_type, discount_value, apply_to_plan_code, active)
SELECT 'NONPROFIT50', 'AMOUNT', 5000, 'STANDARD', TRUE
WHERE NOT EXISTS (SELECT 1 FROM discount WHERE discount_code = 'NONPROFIT50');

-- Billing accounts
CREATE TABLE IF NOT EXISTS billing_account (
    id                UUID PRIMARY KEY,
    patient_id        VARCHAR(255) NOT NULL,
    plan_code         VARCHAR(50)  NOT NULL,
    discount_code     VARCHAR(50),
    account_status    VARCHAR(50)  NOT NULL,   -- ACTIVE | CANCELED
    cycle_anchor      DATE         NOT NULL,
    billing_cadence   VARCHAR(20)  NOT NULL,   -- MONTHLY | ANNUALLY
    currency          VARCHAR(10)  NOT NULL,
    activated_at      DATE         NOT NULL,
    canceled_at       DATE,
    last_invoiced_end DATE,
    CONSTRAINT fk_ba_plan FOREIGN KEY (plan_code) REFERENCES plan(plan_code),
    CONSTRAINT fk_ba_discount FOREIGN KEY (discount_code) REFERENCES discount(discount_code)
);

INSERT INTO billing_account (id, patient_id, plan_code, discount_code, account_status,
                             cycle_anchor, billing_cadence, currency, activated_at,
                             canceled_at, last_invoiced_end)
SELECT '11111111-1111-1111-1111-111111111111',
       '123e4567-e89b-12d3-a456-426614174000',
       'BASIC',
       'WELCOME10',
       'ACTIVE',
       '2024-12-01',
       'MONTHLY',
       'USD',
       '2024-12-01',
       NULL,
       NULL
WHERE NOT EXISTS (SELECT 1 FROM billing_account WHERE id = '11111111-1111-1111-1111-111111111111');

INSERT INTO billing_account (id, patient_id, plan_code, discount_code, account_status,
                             cycle_anchor, billing_cadence, currency, activated_at,
                             canceled_at, last_invoiced_end)
SELECT '22222222-2222-2222-2222-222222222222',
       '123e4567-e89b-12d3-a456-426614174001',
       'STANDARD',
       'NONPROFIT50',
       'ACTIVE',
       '2024-11-15',
       'MONTHLY',
       'USD',
       '2024-11-15',
       NULL,
       '2024-12-15'
WHERE NOT EXISTS (SELECT 1 FROM billing_account WHERE id = '22222222-2222-2222-2222-222222222222');

INSERT INTO billing_account (id, patient_id, plan_code, discount_code, account_status,
                             cycle_anchor, billing_cadence, currency, activated_at,
                             canceled_at, last_invoiced_end)
SELECT '33333333-3333-3333-3333-333333333333',
       '123e4567-e89b-12d3-a456-426614174002',
       'PREMIUM',
       NULL,
       'ACTIVE',
       '2024-01-01',
       'ANNUALLY',
       'USD',
       '2024-01-01',
       NULL,
       '2024-12-31'
WHERE NOT EXISTS (SELECT 1 FROM billing_account WHERE id = '33333333-3333-3333-3333-333333333333');

-- Invoices
CREATE TABLE IF NOT EXISTS invoice (
    id               UUID PRIMARY KEY,
    billing_account_id UUID      NOT NULL,
    period_start     DATE       NOT NULL,
    period_end       DATE       NOT NULL,
    subtotal_cents   BIGINT     NOT NULL,
    proration_cents  BIGINT     NOT NULL,
    discount_cents   BIGINT     NOT NULL,
    total_cents      BIGINT     NOT NULL,
    status           VARCHAR(20) NOT NULL, -- DUE | PAID
    created_at       DATE       NOT NULL,
    due_at           DATE       NOT NULL,
    CONSTRAINT fk_invoice_ba FOREIGN KEY (billing_account_id) REFERENCES billing_account(id)
);

-- Invoice lines
CREATE TABLE IF NOT EXISTS invoice_line (
    id            UUID PRIMARY KEY,
    invoice_id    UUID         NOT NULL,
    description   VARCHAR(255) NOT NULL,
    amount_cents  BIGINT       NOT NULL,
    quantity      INT          NOT NULL,
    CONSTRAINT fk_invoice_line_invoice FOREIGN KEY (invoice_id) REFERENCES invoice(id)
);
