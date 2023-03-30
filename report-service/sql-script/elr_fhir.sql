CREATE TABLE [NBS_DataIngest].[dbo].[elr_fhir] (
    id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    fhir_message NVARCHAR(MAX) not null,
    raw_message_id UNIQUEIDENTIFIER FOREIGN KEY REFERENCES [NBS_DataIngest].[dbo].[elr_raw](id),
    created_by NVARCHAR(255) NOT NULL,
    updated_by NVARCHAR(255) NOT NULL,
    created_on DATETIME NOT NULL DEFAULT getdate(),
    updated_on DATETIME NULL
)