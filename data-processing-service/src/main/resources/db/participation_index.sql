CREATE NONCLUSTERED INDEX IX_Participation_SubjectEntityUid
    ON NBS_ODSE.dbo.Participation (subject_entity_uid ASC)
    WITH (
        PAD_INDEX = OFF,
        FILLFACTOR = 100,
        SORT_IN_TEMPDB = OFF,
        IGNORE_DUP_KEY = OFF,
        STATISTICS_NORECOMPUTE = OFF,
        ONLINE = OFF,
        ALLOW_ROW_LOCKS = ON,
        ALLOW_PAGE_LOCKS = ON
        )
    ON [PRIMARY];


CREATE NONCLUSTERED INDEX IX_Participation_SubjectEntity_ActUid
    ON NBS_ODSE.dbo.Participation (subject_entity_uid ASC, act_uid ASC)
    WITH (
        PAD_INDEX = OFF,
        FILLFACTOR = 100,
        SORT_IN_TEMPDB = OFF,
        IGNORE_DUP_KEY = OFF,
        STATISTICS_NORECOMPUTE = OFF,
        ONLINE = OFF,
        ALLOW_ROW_LOCKS = ON,
        ALLOW_PAGE_LOCKS = ON
        )
    ON [PRIMARY];

