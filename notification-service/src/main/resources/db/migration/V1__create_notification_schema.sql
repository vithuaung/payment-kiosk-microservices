-- Notification attempt log: one row per send attempt, regardless of outcome
CREATE TABLE mtxn_notification (
    notif_id      UNIQUEIDENTIFIER  DEFAULT NEWID()  NOT NULL,
    payment_id    UNIQUEIDENTIFIER                   NOT NULL,
    notif_channel VARCHAR(10)                        NOT NULL,
    recipient     VARCHAR(100)                       NOT NULL,
    subject       VARCHAR(200)                       NULL,
    body_text     NVARCHAR(MAX)                      NULL,
    send_status   VARCHAR(20)       DEFAULT 'PENDING' NOT NULL,
    sent_at       DATETIME2                          NULL,
    fail_reason   VARCHAR(255)                       NULL,
    created_at    DATETIME2         DEFAULT GETDATE() NULL,

    CONSTRAINT PK_mtxn_notification PRIMARY KEY (notif_id)
);
