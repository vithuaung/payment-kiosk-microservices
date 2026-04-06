-- Patient identity table
CREATE TABLE md_person (
    person_id      UNIQUEIDENTIFIER NOT NULL DEFAULT NEWID(),
    id_ref         VARCHAR(20)      NOT NULL,
    full_name      VARCHAR(100)     NOT NULL,
    birth_date     DATE,
    mobile_no      VARCHAR(20),
    email_addr     VARCHAR(100),
    created_at     DATETIME2        NOT NULL DEFAULT GETDATE(),
    updated_at     DATETIME2        NOT NULL DEFAULT GETDATE(),
    CONSTRAINT PK_md_person PRIMARY KEY (person_id),
    CONSTRAINT UQ_md_person_id_ref UNIQUE (id_ref)
);

-- Scheduled or completed visit linked to a patient
CREATE TABLE md_visit (
    visit_id       UNIQUEIDENTIFIER NOT NULL DEFAULT NEWID(),
    person_id      UNIQUEIDENTIFIER NOT NULL,
    scheduled_at   DATETIME2        NOT NULL,
    counter_code   VARCHAR(20)      NOT NULL,
    counter_name   VARCHAR(100),
    queue_no       VARCHAR(20),
    visit_status   VARCHAR(20)      NOT NULL DEFAULT 'SCHEDULED',
    created_at     DATETIME2        NOT NULL DEFAULT GETDATE(),
    updated_at     DATETIME2        NOT NULL DEFAULT GETDATE(),
    CONSTRAINT PK_md_visit PRIMARY KEY (visit_id),
    CONSTRAINT FK_md_visit_person FOREIGN KEY (person_id) REFERENCES md_person (person_id)
);

-- Location reference data (org, branch, counter hierarchy)
CREATE TABLE cd_location (
    location_id    UNIQUEIDENTIFIER NOT NULL DEFAULT NEWID(),
    location_code  VARCHAR(20)      NOT NULL,
    location_name  VARCHAR(100)     NOT NULL,
    parent_code    VARCHAR(20),
    location_type  VARCHAR(20)      NOT NULL,
    is_active      BIT              NOT NULL DEFAULT 1,
    created_at     DATETIME2        NOT NULL DEFAULT GETDATE(),
    CONSTRAINT PK_cd_location PRIMARY KEY (location_id),
    CONSTRAINT UQ_cd_location_code UNIQUE (location_code)
);

-- Check-in transaction records
CREATE TABLE mtxn_checkin (
    checkin_id     UNIQUEIDENTIFIER NOT NULL DEFAULT NEWID(),
    person_id      UNIQUEIDENTIFIER NOT NULL,
    visit_id       UNIQUEIDENTIFIER,
    checkin_type   VARCHAR(20)      NOT NULL,
    queue_no       VARCHAR(20),
    location_code  VARCHAR(20)      NOT NULL,
    checkin_at     DATETIME2        NOT NULL,
    created_at     DATETIME2        NOT NULL DEFAULT GETDATE(),
    CONSTRAINT PK_mtxn_checkin PRIMARY KEY (checkin_id),
    CONSTRAINT FK_mtxn_checkin_person FOREIGN KEY (person_id) REFERENCES md_person (person_id),
    CONSTRAINT FK_mtxn_checkin_visit  FOREIGN KEY (visit_id)  REFERENCES md_visit (visit_id)
);
