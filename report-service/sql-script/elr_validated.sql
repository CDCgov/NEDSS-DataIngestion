CREATE TABLE [NBS_DataIngest].[dbo].[elr_validated] (
    id UNIQUEIDENTIFIER PRIMARY KEY default NEWID(),
    raw_message_id UNIQUEIDENTIFIER FOREIGN KEY REFERENCES [NBS_DataIngest].[dbo].[elr_raw](id),
    message_type nvarchar(255) not null,
    message_version nvarchar(255),
    validated_message ntext not null,
    created_by nvarchar(255) not null,
    updated_by nvarchar(255) not null,
    created_on DATETIME  not null default getdate(),
    updated_on DATETIME null
)