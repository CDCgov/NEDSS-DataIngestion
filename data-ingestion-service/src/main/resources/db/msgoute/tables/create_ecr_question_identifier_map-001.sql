USE [NBS_MSGOUTE]
GO

CREATE TABLE [dbo].[ecr_question_identifier_map](
    [COLUMN_NM] [varchar](255) NULL,
    [QUESTION_IDENTIFIER] [varchar](255) NULL,
    [DYNAMIC_QUESTION_IDENTIFIER] [varchar](255) NULL
    ) ON [PRIMARY]
    GO