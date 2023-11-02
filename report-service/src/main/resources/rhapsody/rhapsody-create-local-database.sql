IF NOT EXISTS(SELECT * FROM sys.databases WHERE name = 'NBS_Data_Ingestion_Local')
    BEGIN
        CREATE DATABASE NBS_Data_Ingestion_Local
    END