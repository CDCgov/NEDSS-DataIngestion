IF NOT EXISTS(
        SELECT 'X'
        FROM INFORMATION_SCHEMA.TABLES
        WHERE TABLE_NAME = 'rti_log')
BEGIN
CREATE TABLE elr_raw
(
    id           UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    nbs_interface_id INTEGER NOT NULL,
    rti_step     NVARCHAR(255)         NOT NULL,
    stack_trace   NVARCHAR(MAX) NOT NULL,
    created_on   DATETIME DEFAULT getdate(),
);
END
