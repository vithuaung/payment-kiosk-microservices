-- Payment transaction table
CREATE TABLE mtxn_payment (
    payment_id    UNIQUEIDENTIFIER DEFAULT NEWID() PRIMARY KEY,
    session_ref   VARCHAR(40)      NOT NULL UNIQUE,
    terminal_code VARCHAR(20)      NOT NULL,
    person_ref    VARCHAR(20)      NOT NULL,
    total_amt     DECIMAL(10,2)    NOT NULL,
    paid_amt      DECIMAL(10,2)    DEFAULT 0,
    change_amt    DECIMAL(10,2)    DEFAULT 0,
    pay_method    VARCHAR(30)      NOT NULL,
    pay_status    VARCHAR(20)      NOT NULL DEFAULT 'PENDING',
    started_at    DATETIME2,
    finished_at   DATETIME2,
    created_at    DATETIME2        DEFAULT GETDATE(),
    updated_at    DATETIME2        DEFAULT GETDATE()
);

-- Bill items linked to a payment
CREATE TABLE mtxn_bill_item (
    bill_item_id  UNIQUEIDENTIFIER DEFAULT NEWID() PRIMARY KEY,
    payment_id    UNIQUEIDENTIFIER NOT NULL REFERENCES mtxn_payment(payment_id),
    bill_ref      VARCHAR(40)      NOT NULL,
    bill_seq      INT              NOT NULL,
    billed_amt    DECIMAL(10,2)    NOT NULL,
    payable_amt   DECIMAL(10,2)    NOT NULL,
    org_code      VARCHAR(20),
    case_ref      VARCHAR(40),
    created_at    DATETIME2        DEFAULT GETDATE()
);

-- Cash machine session for a payment
CREATE TABLE mtxn_cash_session (
    cash_id        UNIQUEIDENTIFIER DEFAULT NEWID() PRIMARY KEY,
    payment_id     UNIQUEIDENTIFIER NOT NULL REFERENCES mtxn_payment(payment_id),
    inserted_amt   DECIMAL(10,2)    DEFAULT 0,
    returned_amt   DECIMAL(10,2)    DEFAULT 0,
    session_status VARCHAR(20)      NOT NULL DEFAULT 'OPEN',
    opened_at      DATETIME2,
    closed_at      DATETIME2,
    created_at     DATETIME2        DEFAULT GETDATE()
);

-- Card terminal session for a payment
CREATE TABLE mtxn_card_session (
    card_id        UNIQUEIDENTIFIER DEFAULT NEWID() PRIMARY KEY,
    payment_id     UNIQUEIDENTIFIER NOT NULL REFERENCES mtxn_payment(payment_id),
    card_network   VARCHAR(20),
    approval_ref   VARCHAR(60),
    terminal_ref   VARCHAR(60),
    session_status VARCHAR(20)      NOT NULL DEFAULT 'OPEN',
    started_at     DATETIME2,
    finished_at    DATETIME2,
    created_at     DATETIME2        DEFAULT GETDATE()
);

-- Payment method configuration
CREATE TABLE cd_pay_method (
    method_id    UNIQUEIDENTIFIER DEFAULT NEWID() PRIMARY KEY,
    method_code  VARCHAR(30)      NOT NULL UNIQUE,
    method_name  VARCHAR(60)      NOT NULL,
    method_group VARCHAR(20)      NOT NULL,
    is_active    BIT              DEFAULT 1,
    created_at   DATETIME2        DEFAULT GETDATE()
);

-- Kiosk terminal configuration
CREATE TABLE cd_terminal (
    terminal_id     UNIQUEIDENTIFIER DEFAULT NEWID() PRIMARY KEY,
    terminal_code   VARCHAR(20)      NOT NULL UNIQUE,
    location_code   VARCHAR(20),
    terminal_status VARCHAR(20)      DEFAULT 'ONLINE',
    is_active       BIT              DEFAULT 1,
    created_at      DATETIME2        DEFAULT GETDATE()
);
