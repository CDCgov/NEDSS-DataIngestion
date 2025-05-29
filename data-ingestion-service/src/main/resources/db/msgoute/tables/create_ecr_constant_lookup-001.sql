USE [NBS_MSGOUTE]
GO

CREATE TABLE [dbo].[ecr_constant_lookup](
    [ID] [int] NULL,
    [SubjectArea] [varchar](255) NULL,
    [QuestionIdentifier] [varchar](255) NULL,
    [QuestionDisplayName] [varchar](255) NULL,
    [SampleValue] [varchar](255) NULL,
    [Usage] [varchar](255) NULL
    ) ON [PRIMARY]
    GO