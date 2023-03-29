CREATE TABLE [NBS_DataIngest].[dbo].[elr_raw] (
    id varchar(255) primary key,
    message_type varchar(255) not null,
    payload text not null,
    created_by varchar(255),
    updated_by varchar(255),
    created_on DATETIME not null default getdate(),
    updated_on DATETIME
)