CREATE TABLE [NBS_DataIngest].[dbo].[elr_raw_test] (
    id varchar(255) not null primary key,
    message_type varchar(255) not null,
    payload text not null,
    created_by varchar(255),
    updated_by varchar(255),
    created_on DATETIME,
    updated_on DATETIME
)