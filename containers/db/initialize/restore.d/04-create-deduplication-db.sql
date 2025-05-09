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
  person_uid bigint NOT NULL,
  potential_match_person_uid bigint NOT NULL,
  person_name NVARCHAR(300),
  person_add_time DATETIME NOT NULL,
  date_identified DATETIME DEFAULT GETDATE(),
  is_merge BIT NULL
);
GO
