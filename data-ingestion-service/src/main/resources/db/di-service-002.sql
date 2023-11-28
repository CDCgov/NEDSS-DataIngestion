IF NOT EXISTS(
        SELECT 'X'
        FROM INFORMATION_SCHEMA.TABLES
        WHERE TABLE_NAME = 'elr_record_status_id')
    BEGIN
        CREATE TABLE elr_record_status_id
        (
            id               UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
            raw_message_id   UNIQUEIDENTIFIER FOREIGN KEY REFERENCES elr_raw (id),
            nbs_interface_id NVARCHAR(255) NOT NULL,
            created_by       NVARCHAR(255) NOT NULL,
            updated_by       NVARCHAR(255) NOT NULL,
            created_on       DATETIME      NOT NULL       DEFAULT getdate(),
            updated_on       DATETIME      NULL
        );
    END