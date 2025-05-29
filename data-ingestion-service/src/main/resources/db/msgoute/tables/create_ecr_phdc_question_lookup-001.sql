USE [NBS_MSGOUTE]
GO

CREATE TABLE [dbo].[ecr_phdc_question_lookup](
    [DOC_TYPE_CD] [varchar](255) NULL,
    [DOC_TYPE_VERSION_TXT] [real] NULL,
    [QUES_CODE_SYSTEM_CD] [varchar](255) NULL,
    [QUES_CODE_SYSTEM_DESC_TXT] [varchar](255) NULL,
    [DATA_TYPE] [varchar](255) NULL,
    [QUESTION_IDENTIFIER] [varchar](255) NULL,
    [QUES_DISPLAY_NAME] [varchar](255) NULL,
    [SECTION_NM] [varchar](255) NULL,
    [SENDING_SYSTEM_CD] [varchar](255) NULL
    ) ON [PRIMARY]
    GO