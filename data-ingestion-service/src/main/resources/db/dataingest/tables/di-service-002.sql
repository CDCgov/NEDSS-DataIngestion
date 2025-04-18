USE NBS_DataIngest;
IF NOT EXISTS (
    SELECT 1
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = 'dbo'
      AND TABLE_NAME = 'elr_raw'
      AND COLUMN_NAME = 'version'
)
BEGIN
ALTER TABLE dbo.elr_raw
    ADD version VARCHAR(1);
END

IF EXISTS (
    SELECT 1
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = 'dbo'
      AND TABLE_NAME = 'elr_dlt'
      AND COLUMN_NAME = 'dlt_status'
)
BEGIN
ALTER TABLE dbo.elr_dlt
ALTER COLUMN dlt_status NVARCHAR(30);
END