SELECT [COLUMN_NM],
                        [QUESTION_IDENTIFIER],
                        [DYNAMIC_QUESTION_IDENTIFIER]
                        FROM [ecr_question_identifier_map]
                        WHERE {COLUMN_NM} = :COLUMN_NM_VALUE