ALTER TABLE elr_raw
    ADD version VARCHAR(1);

ALTER TABLE elr_dlt
    ALTER COLUMN dlt_status NVARCHAR(30);