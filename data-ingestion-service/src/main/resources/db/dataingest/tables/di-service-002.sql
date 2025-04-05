USE NBS_DataIngest;
ALTER TABLE NBS_DataIngest.dbo.elr_raw
    ADD version VARCHAR(1);

ALTER TABLE NBS_DataIngest.dbo.elr_dlt
    ALTER COLUMN dlt_status NVARCHAR(30);