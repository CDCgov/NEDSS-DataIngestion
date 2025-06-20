USE NBS_MSGOUTE;

IF NOT EXISTS (
    SELECT 1
    FROM INFORMATION_SCHEMA.TABLES
    WHERE TABLE_NAME = 'rti_dlt' AND TABLE_SCHEMA = 'dbo'
)
    BEGIN
        CREATE TABLE [dbo].[rti_dlt] (
                                         [id] UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
                                         [nbs_interface_id] BIGINT,
                                         [origin] VARCHAR(100),
                                         [status] VARCHAR(255),
                                         [stack_trace] NVARCHAR(MAX),
                                         [payload] NVARCHAR(MAX),
                                         [created_on] DATETIME DEFAULT GETDATE(),
                                         [updated_on] DATETIME DEFAULT GETDATE()
        ) ON [PRIMARY]
    END
