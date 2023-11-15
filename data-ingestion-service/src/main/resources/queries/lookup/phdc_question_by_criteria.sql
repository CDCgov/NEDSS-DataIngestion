SELECT [DOC_TYPE_CD],
            [DOC_TYPE_VERSION_TXT],
            [QUES_CODE_SYSTEM_CD],
            [QUES_CODE_SYSTEM_DESC_TXT],
            [DATA_TYPE],
            [QUESTION_IDENTIFIER],
            [QUES_DISPLAY_NAME],
            [SECTION_NM],
            [SENDING_SYSTEM_CD]
            FROM [ecr_phdc_question_lookup]
            WHERE [QUESTION_IDENTIFIER] = :QUES_IDENTIFIER