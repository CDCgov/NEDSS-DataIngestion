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

    public static final String  SELECT_PARTICIPATION_BY_SUBJECT_ENTITY_ID_LIST = """
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

    public static final String INSERT_PARTICIPATION = """
INSERT INTO Participation (
    subject_entity_uid,
    act_uid,
    type_cd,
    act_class_cd,
    add_reason_cd,
    add_time,
    add_user_id,
    awareness_cd,
    awareness_desc_txt,
    cd,
    duration_amt,
    duration_unit_cd,
    from_time,
    last_chg_reason_cd,
    last_chg_time,
    last_chg_user_id,
    record_status_cd,
    record_status_time,
    role_seq,
    status_cd,
    status_time,
    subject_class_cd,
    to_time,
    type_desc_txt,
    user_affiliation_txt
) VALUES (
    :subjectEntityUid,
    :actUid,
    :typeCode,
    :actClassCode,
    :addReasonCode,
    :addTime,
    :addUserId,
    :awarenessCode,
    :awarenessDescription,
    :code,
    :durationAmount,
    :durationUnitCode,
    :fromTime,
    :lastChangeReasonCode,
    :lastChangeTime,
    :lastChangeUserId,
    :recordStatusCode,
    :recordStatusTime,
    :roleSeq,
    :statusCode,
    :statusTime,
    :subjectClassCode,
    :toTime,
    :typeDescription,
    :userAffiliationText
)
""";

    public static final String UPDATE_PARTICIPATION = """
UPDATE Participation SET
    act_class_cd = :actClassCode,
    add_reason_cd = :addReasonCode,
    add_time = :addTime,
    add_user_id = :addUserId,
    awareness_cd = :awarenessCode,
    awareness_desc_txt = :awarenessDescription,
    cd = :code,
    duration_amt = :durationAmount,
    duration_unit_cd = :durationUnitCode,
    from_time = :fromTime,
    last_chg_reason_cd = :lastChangeReasonCode,
    last_chg_time = :lastChangeTime,
    last_chg_user_id = :lastChangeUserId,
    record_status_cd = :recordStatusCode,
    record_status_time = :recordStatusTime,
    role_seq = :roleSeq,
    status_cd = :statusCode,
    status_time = :statusTime,
    subject_class_cd = :subjectClassCode,
    to_time = :toTime,
    type_desc_txt = :typeDescription,
    user_affiliation_txt = :userAffiliationText
WHERE subject_entity_uid = :subjectEntityUid
  AND act_uid = :actUid
  AND type_cd = :typeCode
""";

    public static final String DELETE_PARTICIPATION = """
DELETE FROM Participation
WHERE subject_entity_uid = :subjectEntityUid
  AND act_uid = :actUid
  AND type_cd = :typeCode
""";

    public static final String MERGE_PARTICIPATION_HIST = """
MERGE INTO Participation_hist AS target
USING (SELECT 
           :subjectEntityUid AS subject_entity_uid,
           :actUid AS act_uid,
           :typeCd AS type_cd,
           :versionCtrlNbr AS version_ctrl_nbr
       ) AS source
ON target.subject_entity_uid = source.subject_entity_uid
   AND target.act_uid = source.act_uid
   AND target.type_cd = source.type_cd
   AND target.version_ctrl_nbr = source.version_ctrl_nbr
WHEN MATCHED THEN
    UPDATE SET
        act_class_cd = :actClassCd,
        add_reason_cd = :addReasonCd,
        add_time = :addTime,
        add_user_id = :addUserId,
        awareness_cd = :awarenessCd,
        awareness_desc_txt = :awarenessDescTxt,
        cd = :cd,
        duration_amt = :durationAmt,
        duration_unit_cd = :durationUnitCd,
        from_time = :fromTime,
        last_chg_reason_cd = :lastChgReasonCd,
        last_chg_time = :lastChgTime,
        last_chg_user_id = :lastChgUserId,
        record_status_cd = :recordStatusCd,
        record_status_time = :recordStatusTime,
        role_seq = :roleSeq,
        status_cd = :statusCd,
        status_time = :statusTime,
        subject_class_cd = :subjectClassCd,
        to_time = :toTime,
        type_desc_txt = :typeDescTxt,
        user_affiliation_txt = :userAffiliationTxt
WHEN NOT MATCHED THEN
    INSERT (
        subject_entity_uid,
        act_uid,
        type_cd,
        version_ctrl_nbr,
        act_class_cd,
        add_reason_cd,
        add_time,
        add_user_id,
        awareness_cd,
        awareness_desc_txt,
        cd,
        duration_amt,
        duration_unit_cd,
        from_time,
        last_chg_reason_cd,
        last_chg_time,
        last_chg_user_id,
        record_status_cd,
        record_status_time,
        role_seq,
        status_cd,
        status_time,
        subject_class_cd,
        to_time,
        type_desc_txt,
        user_affiliation_txt
    )
    VALUES (
        :subjectEntityUid,
        :actUid,
        :typeCd,
        :versionCtrlNbr,
        :actClassCd,
        :addReasonCd,
        :addTime,
        :addUserId,
        :awarenessCd,
        :awarenessDescTxt,
        :cd,
        :durationAmt,
        :durationUnitCd,
        :fromTime,
        :lastChgReasonCd,
        :lastChgTime,
        :lastChgUserId,
        :recordStatusCd,
        :recordStatusTime,
        :roleSeq,
        :statusCd,
        :statusTime,
        :subjectClassCd,
        :toTime,
        :typeDescTxt,
        :userAffiliationTxt
    );
""";



}
