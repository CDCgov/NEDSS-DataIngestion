package gov.cdc.dataprocessing.constant.query;

public class ActIdQuery {
    public static final String MERGE_SQL_ACT_ID = """
        MERGE INTO Act_id AS target
        USING (SELECT :act_uid AS act_uid, :act_id_seq AS act_id_seq) AS source
            ON target.act_uid = source.act_uid AND target.act_id_seq = source.act_id_seq
        WHEN MATCHED THEN UPDATE SET
            add_reason_cd = :add_reason_cd, add_time = :add_time, add_user_id = :add_user_id,
            assigning_authority_cd = :assigning_authority_cd, assigning_authority_desc_txt = :assigning_authority_desc_txt,
            duration_amt = :duration_amt, duration_unit_cd = :duration_unit_cd,
            last_chg_reason_cd = :last_chg_reason_cd, last_chg_time = :last_chg_time, last_chg_user_id = :last_chg_user_id,
            record_status_cd = :record_status_cd, record_status_time = :record_status_time,
            root_extension_txt = :root_extension_txt, status_cd = :status_cd, status_time = :status_time,
            type_cd = :type_cd, type_desc_txt = :type_desc_txt, user_affiliation_txt = :user_affiliation_txt,
            valid_from_time = :valid_from_time, valid_to_time = :valid_to_time
        WHEN NOT MATCHED THEN INSERT (
            act_uid, act_id_seq, add_reason_cd, add_time, add_user_id,
            assigning_authority_cd, assigning_authority_desc_txt,
            duration_amt, duration_unit_cd,
            last_chg_reason_cd, last_chg_time, last_chg_user_id,
            record_status_cd, record_status_time,
            root_extension_txt, status_cd, status_time,
            type_cd, type_desc_txt, user_affiliation_txt,
            valid_from_time, valid_to_time
        ) VALUES (
            :act_uid, :act_id_seq, :add_reason_cd, :add_time, :add_user_id,
            :assigning_authority_cd, :assigning_authority_desc_txt,
            :duration_amt, :duration_unit_cd,
            :last_chg_reason_cd, :last_chg_time, :last_chg_user_id,
            :record_status_cd, :record_status_time,
            :root_extension_txt, :status_cd, :status_time,
            :type_cd, :type_desc_txt, :user_affiliation_txt,
            :valid_from_time, :valid_to_time
        );
        """;

    public static final String SELECT_BY_ACT_UID_SQL = """
    SELECT
        act_uid AS actUid,
        act_id_seq AS actIdSeq,
        add_reason_cd AS addReasonCd,
        add_time AS addTime,
        add_user_id AS addUserId,
        assigning_authority_cd AS assigningAuthorityCd,
        assigning_authority_desc_txt AS assigningAuthorityDescTxt,
        duration_amt AS durationAmt,
        duration_unit_cd AS durationUnitCd,
        last_chg_reason_cd AS lastChgReasonCd,
        last_chg_time AS lastChgTime,
        last_chg_user_id AS lastChgUserId,
        record_status_cd AS recordStatusCd,
        record_status_time AS recordStatusTime,
        root_extension_txt AS rootExtensionTxt,
        status_cd AS statusCd,
        status_time AS statusTime,
        type_cd AS typeCd,
        type_desc_txt AS typeDescTxt,
        user_affiliation_txt AS userAffiliationTxt,
        valid_from_time AS validFromTime,
        valid_to_time AS validToTime
    FROM Act_id
    WHERE act_uid = :act_uid
    """;


}
