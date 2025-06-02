USE NBS_MSGOUTE;
IF
NOT EXISTS(
        SELECT 'X'
        FROM INFORMATION_SCHEMA.TABLES
        WHERE TABLE_NAME = 'ecr_constant_lookup')
BEGIN
CREATE TABLE [dbo].[ecr_constant_lookup] (
    [ID]                    INT NULL,
    [SubjectArea]           VARCHAR(255) NULL,
    [QuestionIdentifier]    VARCHAR(255) NULL,
    [QuestionDisplayName]   VARCHAR(255) NULL,
    [SampleValue]           VARCHAR(255) NULL,
    [Usage]                 VARCHAR(255) NULL
) ON [PRIMARY]
END