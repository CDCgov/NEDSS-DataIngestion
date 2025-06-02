USE NBS_MSGOUTE;
IF
NOT EXISTS(
        SELECT 'X'
        FROM INFORMATION_SCHEMA.TABLES
        WHERE TABLE_NAME = 'ecr_question_identifier_map')
BEGIN
CREATE TABLE [dbo].[ecr_question_identifier_map](
    [COLUMN_NM] [varchar](255) NULL,
    [QUESTION_IDENTIFIER] [varchar](255) NULL,
    [DYNAMIC_QUESTION_IDENTIFIER] [varchar](255) NULL
    ) ON [PRIMARY]
END