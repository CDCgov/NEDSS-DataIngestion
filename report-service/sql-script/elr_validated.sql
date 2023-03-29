CREATE TABLE [NBS_DataIngest].[dbo].[elr_validated] (
    id UNIQUEIDENTIFIER PRIMARY KEY default NEWID(),
    raw_id UNIQUEIDENTIFIER not null,
    message_type nvarchar(255) not null,
    message_version nvarchar(255),
    validated_message ntext not null,
    created_by nvarchar(255) not null,
    updated_by nvarchar(255) not null,
    created_on DATETIME  not null default getdate(),
    updated_on DATETIME null
)