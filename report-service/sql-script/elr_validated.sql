CREATE TABLE [NBS_DataIngest].[dbo].[elr_validated] (
    id varchar(255) primary key,
    raw_id varchar(255) not null,
    message_type varchar(255) not null,
    message_version varchar(255),
    validated_message text not null,
    created_by varchar(255),
    updated_by varchar(255),
    created_on DATETIME  not null default getdate(),
    updated_on DATETIME
)