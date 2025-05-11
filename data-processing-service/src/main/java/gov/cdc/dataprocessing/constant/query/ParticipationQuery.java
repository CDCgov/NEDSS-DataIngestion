package gov.cdc.dataprocessing.constant.query;

public class ParticipationQuery {
    public static final String SELECT_PARTICIPATION_BY_ACT_UID = """
        SELECT
            subject_entity_uid AS subjectEntityUid,
            act_uid AS actUid,
            type_cd AS typeCode,
            act_class_cd AS actClassCode,
            add_reason_cd AS addReasonCode,
            add_time AS addTime,
            add_user_id AS addUserId,
            awareness_cd AS awarenessCode,
            awareness_desc_txt AS awarenessDescription,
            cd AS code,
            duration_amt AS durationAmount,
            duration_unit_cd AS durationUnitCode,
            from_time AS fromTime,
            last_chg_reason_cd AS lastChangeReasonCode,
            last_chg_time AS lastChangeTime,
            last_chg_user_id AS lastChangeUserId,
            record_status_cd AS recordStatusCode,
            record_status_time AS recordStatusTime,
            role_seq AS roleSeq,
            status_cd AS statusCode,
            status_time AS statusTime,
            subject_class_cd AS subjectClassCode,
            to_time AS toTime,
            type_desc_txt AS typeDescription,
            user_affiliation_txt AS userAffiliationText
        FROM Participation
        WHERE act_uid = :act_uid
        """;

    // DO top, this mainly use by Organization
    public static final String  SELECT_PARTICIPATION_BY_SUBJECT_ENTITY_ID = """
        SELECT
        TOP(1)
            subject_entity_uid AS subjectEntityUid,
            act_uid AS actUid,
            type_cd AS typeCode,
            act_class_cd AS actClassCode,
            add_reason_cd AS addReasonCode,
            add_time AS addTime,
            add_user_id AS addUserId,
            awareness_cd AS awarenessCode,
            awareness_desc_txt AS awarenessDescription,
            cd AS code,
            duration_amt AS durationAmount,
            duration_unit_cd AS durationUnitCode,
            from_time AS fromTime,
            last_chg_reason_cd AS lastChangeReasonCode,
            last_chg_time AS lastChangeTime,
            last_chg_user_id AS lastChangeUserId,
            record_status_cd AS recordStatusCode,
            record_status_time AS recordStatusTime,
            role_seq AS roleSeq,
            status_cd AS statusCode,
            status_time AS statusTime,
            subject_class_cd AS subjectClassCode,
            to_time AS toTime,
            type_desc_txt AS typeDescription,
            user_affiliation_txt AS userAffiliationText
        FROM Participation
        WHERE subject_entity_uid = :subjectEntityUid
    """;

    public static final String  SELECT_PARTICIPATION_BY_SUBJECT_ENTITY_ID_AND_ACT_UID = """
        SELECT
            subject_entity_uid AS subjectEntityUid,
            act_uid AS actUid,
            type_cd AS typeCode,
            act_class_cd AS actClassCode,
            add_reason_cd AS addReasonCode,
            add_time AS addTime,
            add_user_id AS addUserId,
            awareness_cd AS awarenessCode,
            awareness_desc_txt AS awarenessDescription,
            cd AS code,
            duration_amt AS durationAmount,
            duration_unit_cd AS durationUnitCode,
            from_time AS fromTime,
            last_chg_reason_cd AS lastChangeReasonCode,
            last_chg_time AS lastChangeTime,
            last_chg_user_id AS lastChangeUserId,
            record_status_cd AS recordStatusCode,
            record_status_time AS recordStatusTime,
            role_seq AS roleSeq,
            status_cd AS statusCode,
            status_time AS statusTime,
            subject_class_cd AS subjectClassCode,
            to_time AS toTime,
            type_desc_txt AS typeDescription,
            user_affiliation_txt AS userAffiliationText
        FROM Participation
        WHERE subject_entity_uid = :subjectEntityUid
          AND act_uid = :actUid
    """;
}
