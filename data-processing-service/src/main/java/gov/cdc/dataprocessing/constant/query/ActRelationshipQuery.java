package gov.cdc.dataprocessing.constant.query;

public class ActRelationshipQuery {
    public static final String INSERT_SQL_ACT_RELATIONSHIP = """
    INSERT INTO Act_relationship (
        source_act_uid, target_act_uid, type_cd, add_reason_cd, add_time, add_user_id,
        duration_amt, duration_unit_cd, from_time, last_chg_reason_cd, last_chg_time, last_chg_user_id,
        record_status_cd, record_status_time, sequence_nbr, source_class_cd,
        status_cd, status_time, target_class_cd, to_time, type_desc_txt, user_affiliation_txt
    ) VALUES (
        :source_act_uid, :target_act_uid, :type_cd, :add_reason_cd, :add_time, :add_user_id,
        :duration_amt, :duration_unit_cd, :from_time, :last_chg_reason_cd, :last_chg_time, :last_chg_user_id,
        :record_status_cd, :record_status_time, :sequence_nbr, :source_class_cd,
        :status_cd, :status_time, :target_class_cd, :to_time, :type_desc_txt, :user_affiliation_txt
    )
""";

    public static final String UPDATE_SQL_ACT_RELATIONSHIP = """
              UPDATE Act_relationship SET
                              add_reason_cd = ?, add_time = ?, add_user_id = ?, duration_amt = ?, duration_unit_cd = ?,
                              from_time = ?, last_chg_reason_cd = ?, last_chg_time = ?, last_chg_user_id = ?,
                              record_status_cd = ?, record_status_time = ?, sequence_nbr = ?, source_class_cd = ?,
                              status_cd = ?, status_time = ?, target_class_cd = ?, to_time = ?, type_desc_txt = ?, user_affiliation_txt = ?
                          WHERE source_act_uid = ? AND target_act_uid = ? AND type_cd = ?
            """;



    public static final String DELETE_SQL_ACT_RELATIONSHIP =  """
    DELETE FROM Act_relationship
    WHERE source_act_uid = :source_act_uid
      AND target_act_uid = :target_act_uid
      AND type_cd = :type_cd
    """;


    public static final String SELECT_BY_SOURCE = """
        SELECT
            source_act_uid AS sourceActUid,
            target_act_uid AS targetActUid,
            type_cd AS typeCd,
            add_reason_cd AS addReasonCd,
            add_time AS addTime,
            add_user_id AS addUserId,
            duration_amt AS durationAmt,
            duration_unit_cd AS durationUnitCd,
            from_time AS fromTime,
            last_chg_reason_cd AS lastChgReasonCd,
            last_chg_time AS lastChgTime,
            last_chg_user_id AS lastChgUserId,
            record_status_cd AS recordStatusCd,
            record_status_time AS recordStatusTime,
            sequence_nbr AS sequenceNbr,
            source_class_cd AS sourceClassCd,
            status_cd AS statusCd,
            status_time AS statusTime,
            target_class_cd AS targetClassCd,
            to_time AS toTime,
            type_desc_txt AS typeDescTxt,
            user_affiliation_txt AS userAffiliationTxt
        FROM Act_relationship
        WHERE source_act_uid = :sourceActUid
        """;

    public static final String SELECT_BY_TARGET = """
        SELECT
            source_act_uid AS sourceActUid,
            target_act_uid AS targetActUid,
            type_cd AS typeCd,
            add_reason_cd AS addReasonCd,
            add_time AS addTime,
            add_user_id AS addUserId,
            duration_amt AS durationAmt,
            duration_unit_cd AS durationUnitCd,
            from_time AS fromTime,
            last_chg_reason_cd AS lastChgReasonCd,
            last_chg_time AS lastChgTime,
            last_chg_user_id AS lastChgUserId,
            record_status_cd AS recordStatusCd,
            record_status_time AS recordStatusTime,
            sequence_nbr AS sequenceNbr,
            source_class_cd AS sourceClassCd,
            status_cd AS statusCd,
            status_time AS statusTime,
            target_class_cd AS targetClassCd,
            to_time AS toTime,
            type_desc_txt AS typeDescTxt,
            user_affiliation_txt AS userAffiliationTxt
        FROM Act_relationship
        WHERE target_act_uid = :targetActUid
        """;

    public static final String CREATE_ACT_RELATIONSHIP_HISTORY = """
        INSERT INTO Act_relationship_hist (
            source_act_uid,
            target_act_uid,
            type_cd,
            version_ctrl_nbr,
            add_reason_cd,
            add_time,
            add_user_id,
            duration_amt,
            duration_unit_cd,
            from_time,
            last_chg_reason_cd,
            last_chg_time,
            last_chg_user_id,
            record_status_cd,
            record_status_time,
            sequence_nbr,
            status_cd,
            status_time,
            source_class_cd,
            target_class_cd,
            to_time,
            type_desc_txt,
            user_affiliation_txt
        ) VALUES (
            :sourceActUid,
            :targetActUid,
            :typeCd,
            :versionCrl,
            :addReasonCd,
            :addTime,
            :addUserId,
            :durationAmt,
            :durationUnitCd,
            :fromTime,
            :lastChgReasonCd,
            :lastChgTime,
            :lastChgUserId,
            :recordStatusCd,
            :recordStatusTime,
            :sequenceNbr,
            :statusCd,
            :statusTime,
            :sourceClassCd,
            :targetClassCd,
            :toTime,
            :typeDescTxt,
            :userAffiliationTxt
        )
    """;
}
