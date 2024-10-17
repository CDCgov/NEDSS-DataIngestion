USE NBS_Data_Ingestion_Local;

CREATE TABLE token (
    id int IDENTITY(1,1) PRIMARY KEY,
    token NVARCHAR(MAX) NOT NULL,
    created_on DATETIME NOT NULL DEFAULT getdate()
);

CREATE TABLE nbs_interface_uid (
    id int IDENTITY(1,1) NOT NULL,
    nbs_interface_uid] bigint NOT NULL,
    injected_report_name] nvarchar(255) NOT NULL,
    created_on datetime NOT NULL DEFAULT getdate()
);


CREATE LOGIN rhapsodyuser
WITH PASSWORD='ReplaceThisPasswordWithYourPassword123$'

CREATE USER rhapsodyuser FOR LOGIN rhapsodyuser;

ALTER ROLE db_datareader ADD MEMBER rhapsodyuser;
ALTER ROLE db_datawriter ADD MEMBER rhapsodyuser;