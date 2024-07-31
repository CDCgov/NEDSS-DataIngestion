SELECT [ANS_FROM_CODE], [ANS_FROM_CODE_SYSTEM_CD],
                [ANS_FROM_CODE_SYSTEM_DESC_TXT],
                [ANS_FROM_DISPLAY_NM],
                [ANS_TO_CODE],
                [ANS_TO_CODE_SYSTEM_CD],
                [ANS_TO_CODE_SYSTEM_DESC_TXT],
                [ANS_TO_DISPLAY_NM],
                [CODE_TRANSLATION_REQUIRED],
                [DOC_TYPE_CD],
                [DOC_TYPE_VERSION_TXT],
                [QUES_CODE_SYSTEM_CD],
                [QUESTION_IDENTIFIER],
                [SENDING_SYSTEM_CD]
                FROM [NBS_MSGOUTE].[dbo].[ecr_phdc_answer_lookup]
                WHERE (ISNULL(:QUESTION_IDENTIFIER, '') = '' OR [QUESTION_IDENTIFIER] = :QUESTION_IDENTIFIER) AND
                    (ISNULL(:ANSWER_FROM_CODE, '') = '' OR [ANS_FROM_CODE] = :ANSWER_FROM_CODE)