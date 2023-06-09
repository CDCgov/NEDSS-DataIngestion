CREATE DATABASE NBS_DataIngest;

USE NBS_DataIngest;

CREATE TABLE [NBS_DataIngest].[dbo].[elr_dlt] (
    error_message_id UNIQUEIDENTIFIER PRIMARY KEY,
    error_message_source nvarchar(255) not null,
    error_stack_trace nvarchar(max) not null,
    error_stack_trace_short nvarchar(255) null,
    dlt_status nvarchar(10) not null,
    dlt_occurrence int,
    message ntext null,
    created_by nvarchar(255) not null,
    updated_by nvarchar(255) not null,
    created_on DATETIME not null default getdate(),
    updated_on DATETIME null
    );

CREATE TABLE [NBS_DataIngest].[dbo].[elr_raw] (
    id UNIQUEIDENTIFIER PRIMARY KEY default NEWID(),
    message_type nvarchar(255) not null,
    payload ntext not null,
    created_by nvarchar(255) not null,
    updated_by nvarchar(255) not null,
    created_on DATETIME not null default getdate(),
    updated_on DATETIME null
    );

CREATE TABLE [NBS_DataIngest].[dbo].[elr_fhir] (
    id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    fhir_message NVARCHAR(MAX) not null,
    raw_message_id UNIQUEIDENTIFIER FOREIGN KEY REFERENCES [NBS_DataIngest].[dbo].[elr_raw](id),
    created_by NVARCHAR(255) NOT NULL,
    updated_by NVARCHAR(255) NOT NULL,
    created_on DATETIME NOT NULL DEFAULT getdate(),
    updated_on DATETIME NULL
    );

CREATE TABLE [NBS_DataIngest].[dbo].[elr_validated] (
    id UNIQUEIDENTIFIER PRIMARY KEY default NEWID(),
    raw_message_id UNIQUEIDENTIFIER FOREIGN KEY REFERENCES [NBS_DataIngest].[dbo].[elr_raw](id),
    message_type nvarchar(255) not null,
    message_version nvarchar(255),
    validated_message ntext not null,
    hashed_hl7_string varchar(64) null,
    created_by nvarchar(255) not null,
    updated_by nvarchar(255) not null,
    created_on DATETIME not null default getdate(),
    updated_on DATETIME null
    );

