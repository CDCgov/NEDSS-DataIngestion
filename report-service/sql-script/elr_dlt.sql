CREATE TABLE [NBS_DataIngest].[dbo].[elr_dlt] (
    id UNIQUEIDENTIFIER PRIMARY KEY default NEWID(),
    error_message_id UNIQUEIDENTIFIER not null,
    error_message_source nvarchar(255) not null,
    error_stack_trace nvarchar(max) not null,
    dlt_status nvarchar(10) not null,
    dlt_occurrence int,
    created_by nvarchar(255) not null,
    updated_by nvarchar(255) not null,
    created_on DATETIME not null default getdate(),
    updated_on DATETIME null
)