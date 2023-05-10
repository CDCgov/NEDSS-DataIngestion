CREATE TABLE [NBS_DataIngest].[dbo].[clients] (
    id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    client_id VARCHAR(255) NOT NULL UNIQUE,
    client_secret VARCHAR(255) NOT NULL,
    client_roles VARCHAR(255) NULL,
    created_by VARCHAR(255) NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    created_on DATETIME NOT NULL DEFAULT getdate(),
    updated_on DATETIME NULL
)