-- changeset liquibase:1
--
-- Creates the tables used by Data Ingestion Service
--

IF NOT EXISTS(
        SELECT 'X'
        FROM INFORMATION_SCHEMA.TABLES
        WHERE TABLE_NAME = 'elr_raw')
    BEGIN
        CREATE TABLE elr_raw
        (
            id           UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
            message_type NVARCHAR(255) NOT NULL,
            payload      NTEXT         NOT NULL,
            created_by   NVARCHAR(255) NOT NULL,
            updated_by   NVARCHAR(255) NOT NULL,
            created_on   DATETIME      NOT NULL       DEFAULT getdate(),
            updated_on   DATETIME      NULL
        );
    END

IF NOT EXISTS(
        SELECT 'X'
        FROM INFORMATION_SCHEMA.TABLES
        WHERE TABLE_NAME = 'elr_validated')
    BEGIN
        CREATE TABLE elr_validated
        (
            id                UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
            raw_message_id    UNIQUEIDENTIFIER FOREIGN KEY REFERENCES elr_raw (id),
            message_type      NVARCHAR(255) NOT NULL,
            message_version   NVARCHAR(255),
            validated_message ntext         NOT NULL,
            hashed_hl7_string varchar(64)   NULL,
            created_by        NVARCHAR(255) NOT NULL,
            updated_by        NVARCHAR(255) NOT NULL,
            created_on        DATETIME      NOT NULL       DEFAULT getdate(),
            updated_on        DATETIME      NULL
        );
    END

IF NOT EXISTS(
        SELECT 'X'
        FROM INFORMATION_SCHEMA.TABLES
        WHERE TABLE_NAME = 'elr_dlt')
    BEGIN
        CREATE TABLE elr_dlt
        (
            error_message_id        UNIQUEIDENTIFIER PRIMARY KEY,
            error_message_source    NVARCHAR(255) NOT NULL,
            error_stack_trace       NVARCHAR(MAX) NOT NULL,
            error_stack_trace_short NVARCHAR(MAX) NOT NULL,
            dlt_status              NVARCHAR(10)  NOT NULL,
            dlt_occurrence          INT,
            message                 ntext         NOT NULL,
            created_by              NVARCHAR(255) NOT NULL,
            updated_by              NVARCHAR(255) NOT NULL,
            created_on              DATETIME      NOT NULL DEFAULT getdate(),
            updated_on              DATETIME      NULL
        );
    END

