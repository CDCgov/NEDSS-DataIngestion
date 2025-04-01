IF
EXISTS (SELECT 1 FROM sysobjects WHERE name = 'NBS_interface' and xtype = 'U')
BEGIN

IF
NOT EXISTS(SELECT 1 FROM sys.columns WHERE name = N'original_payload_RR' AND Object_ID = Object_ID(N'NBS_interface'))
BEGIN
ALTER TABLE dbo.NBS_interface
    ADD original_payload_RR TEXT;
END;

IF
NOT EXISTS(SELECT 1 FROM sys.columns WHERE name = N'original_doc_type_cd_RR' AND Object_ID = Object_ID(N'NBS_interface'))
BEGIN
ALTER TABLE dbo.NBS_interface
    ADD original_doc_type_cd_RR varchar(100);
END;
END;