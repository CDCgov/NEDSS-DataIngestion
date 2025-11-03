USE NBS_DataIngest;
GO

IF NOT EXISTS (
    SELECT 1
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'elr_raw'
      AND COLUMN_NAME = 'data_source'
)
    BEGIN
        ALTER TABLE elr_raw
            ADD data_source NVARCHAR(255) NULL;
    END
