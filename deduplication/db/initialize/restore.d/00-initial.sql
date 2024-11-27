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
    id bigint,
    person_uid bigint,
    person_parent_uid bigint,
    mpi_person uniqueidentifier,
    mpi_patient uniqueidentifier,
    status varchar,
    PRIMARY KEY ([id])
);