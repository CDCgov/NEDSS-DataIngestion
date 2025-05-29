USE [NBS_MSGOUTE]
GO

CREATE TABLE [dbo].[ecr_phdc_answer_lookup](
    [ANS_FROM_CODE] [varchar](255) NULL,
    [ANS_FROM_CODE_SYSTEM_CD] [varchar](255) NULL,
    [ANS_FROM_CODE_SYSTEM_DESC_TXT] [varchar](255) NULL,
    [ANS_FROM_DISPLAY_NM] [varchar](255) NULL,
    [ANS_TO_CODE] [varchar](255) NULL,
    [ANS_TO_CODE_SYSTEM_CD] [varchar](255) NULL,
    [ANS_TO_CODE_SYSTEM_DESC_TXT] [varchar](255) NULL,
    [ANS_TO_DISPLAY_NM] [varchar](255) NULL,
    [CODE_TRANSLATION_REQUIRED] [varchar](255) NULL,
    [DOC_TYPE_CD] [varchar](255) NULL,
    [DOC_TYPE_VERSION_TXT] [real] NULL,
    [QUES_CODE_SYSTEM_CD] [varchar](255) NULL,
    [QUESTION_IDENTIFIER] [varchar](255) NULL,
    [SENDING_SYSTEM_CD] [varchar](255) NULL
    ) ON [PRIMARY]
    GO