CREATE TABLE [NBS_DataIngest].[dbo].[elr_raw] (
    id UNIQUEIDENTIFIER PRIMARY KEY default NEWID(),
    message_type nvarchar(255) not null,
    payload ntext not null,
    created_by nvarchar(255) not null,
    updated_by nvarchar(255) not null,
    created_on DATETIME not null default getdate(),
    updated_on DATETIME null
)