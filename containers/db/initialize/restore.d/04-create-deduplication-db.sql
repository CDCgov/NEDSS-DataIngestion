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
    person_add_time datetime NOT NULL,
    PRIMARY KEY ([id])
);
GO

CREATE TABLE matches_requiring_review (
  id BIGINT IDENTITY(1,1) PRIMARY KEY,
  person_uid BIGINT NOT NULL,
  person_name NVARCHAR(300),
  person_add_time DATETIME NOT NULL,
  date_identified DATETIME DEFAULT GETDATE()
);
GO

CREATE TABLE match_candidates (
  id BIGINT IDENTITY(1,1) PRIMARY KEY,
  match_id BIGINT NOT NULL,
  person_uid BIGINT NOT NULL,
  is_merge BIT NULL,
  FOREIGN KEY (match_id) REFERENCES matches_requiring_review(id)
);
GO
