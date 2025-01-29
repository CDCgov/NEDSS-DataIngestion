USE [master];
GO

CREATE DATABASE deduplication;
GO

USE [deduplication];
GO


CREATE TABLE data_element_configuration (
	id int IDENTITY (1,1) PRIMARY KEY,
	configuration NVARCHAR(MAX) NOT NULL,
	add_time datetime NOT NULL default(current_timestamp));
GO

CREATE TABLE match_configuration (
	id int IDENTITY (1,1) PRIMARY KEY,
	configuration NVARCHAR(MAX) NOT NULL,
	add_time datetime NOT NULL default(current_timestamp));
GO

CREATE TABLE nbs_mpi_mapping (
    id bigint IDENTITY(1,1),
    person_uid bigint,
    person_parent_uid bigint,
    mpi_person uniqueidentifier,
    mpi_patient uniqueidentifier,
    status varchar,
    PRIMARY KEY ([id])
);
GO

CREATE TABLE match_candidates (
  id bigint IDENTITY(1,1),
  person_uid bigint,
  mpi_person_id uniqueidentifier
);
GO

CREATE TABLE job_logs (
    id INT IDENTITY(1,1) PRIMARY KEY,
    step_name NVARCHAR(255) NOT NULL,
    message NVARCHAR(MAX),
    timestamp DATETIME NOT NULL DEFAULT GETDATE(),
    exception_type NVARCHAR(255),
    exception_message NVARCHAR(MAX),
    failed_ids NVARCHAR(MAX)
);

GO
