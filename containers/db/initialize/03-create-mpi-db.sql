USE [master];
GO
IF NOT EXISTS(SELECT *
              FROM sys.databases
              WHERE name = 'mpi')
BEGIN

CREATE DATABASE mpi;
END
GO
