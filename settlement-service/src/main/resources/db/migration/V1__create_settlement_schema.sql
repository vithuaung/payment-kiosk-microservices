-- Settlement schema for settlement-service

CREATE TABLE mtxn_settlement (
    settlement_id    UNIQUEIDENTIFIER NOT NULL DEFAULT NEWID(),
    payment_id       UNIQUEIDENTIFIER NOT NULL,
    session_ref      VARCHAR(40)      NOT NULL,
    settle_status    VARCHAR(20)      NOT NULL DEFAULT 'PENDING',
    retry_count      INT              NOT NULL DEFAULT 0,
    max_retry        INT              NOT NULL DEFAULT 5,
    last_attempt_at  DATETIME2        NULL,
    settled_at       DATETIME2        NULL,
    ext_ref          VARCHAR(60)      NULL,
    fail_reason      VARCHAR(255)     NULL,
    version_no       BIGINT           NOT NULL DEFAULT 0,
    created_at       DATETIME2        NOT NULL DEFAULT GETDATE(),
    updated_at       DATETIME2        NOT NULL DEFAULT GETDATE(),
    CONSTRAINT PK_mtxn_settlement PRIMARY KEY (settlement_id)
);

CREATE UNIQUE INDEX UX_settlement_session_ref ON mtxn_settlement (session_ref);

CREATE TABLE mtxn_settle_attempt (
    attempt_id       UNIQUEIDENTIFIER NOT NULL DEFAULT NEWID(),
    settlement_id    UNIQUEIDENTIFIER NOT NULL,
    attempt_no       INT              NOT NULL,
    result_status    VARCHAR(20)      NOT NULL,
    sent_data        NVARCHAR(MAX)    NULL,
    recv_data        NVARCHAR(MAX)    NULL,
    attempted_at     DATETIME2        NOT NULL,
    created_at       DATETIME2        NOT NULL DEFAULT GETDATE(),
    CONSTRAINT PK_mtxn_settle_attempt    PRIMARY KEY (attempt_id),
    CONSTRAINT FK_attempt_settlement     FOREIGN KEY (settlement_id)
        REFERENCES mtxn_settlement (settlement_id)
);

CREATE INDEX IX_settle_attempt_settlement_id ON mtxn_settle_attempt (settlement_id);
