USE [master];
GO

CREATE DATABASE deduplication;
GO

USE [deduplication];
GO

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'last_processed_id')
BEGIN
CREATE TABLE last_processed_id (
                                   id BIGINT PRIMARY KEY,
                                   last_processed_id BIGINT
);

-- Insert default row with id = 1 and last_processed_id = NULL
INSERT INTO last_processed_id (id, last_processed_id)
VALUES (1, NULL);
END
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
