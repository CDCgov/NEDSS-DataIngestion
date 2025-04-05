IF NOT EXISTS(SELECT *
              FROM sys.databases
              WHERE name = 'NBS_DataIngest')
BEGIN
        CREATE DATABASE NBS_DataIngest
END
