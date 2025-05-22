package gov.cdc.dataprocessing.constant.query;

public class ActLocatorParticipationQuery {
    public static final String INSERT_SQL_ACT_LOCATOR_PAT = """
        INSERT INTO Act_locator_participation (
            act_uid, locator_uid, add_reason_cd, add_time, add_user_id,
            duration_amt, duration_unit_cd, from_time, last_chg_reason_cd, last_chg_time,
            last_chg_user_id, record_status_cd, record_status_time, to_time,
            status_cd, status_time, type_cd, type_desc_txt, user_affiliation_txt
        ) VALUES (
            :act_uid, :locator_uid, :add_reason_cd, :add_time, :add_user_id,
            :duration_amt, :duration_unit_cd, :from_time, :last_chg_reason_cd, :last_chg_time,
            :last_chg_user_id, :record_status_cd, :record_status_time, :to_time,
            :status_cd, :status_time, :type_cd, :type_desc_txt, :user_affiliation_txt
        )
        """;

    public static final String UPDATE_SQL_ACT_LOCATOR_PAT = """
        UPDATE Act_locator_participation SET
            act_uid = :act_uid,
            locator_uid = :locator_uid,
            add_reason_cd = :add_reason_cd,
            add_time = :add_time,
            add_user_id = :add_user_id,
            duration_amt = :duration_amt,
            duration_unit_cd = :duration_unit_cd,
            from_time = :from_time,
            last_chg_reason_cd = :last_chg_reason_cd,
            last_chg_time = :last_chg_time,
            last_chg_user_id = :last_chg_user_id,
            record_status_cd = :record_status_cd,
            record_status_time = :record_status_time,
            to_time = :to_time,
            status_cd = :status_cd,
            status_time = :status_time,
            type_cd = :type_cd,
            type_desc_txt = :type_desc_txt,
            user_affiliation_txt = :user_affiliation_txt
        WHERE entity_uid = :entity_uid
        """;

    public static final String SELECT_BY_ACT_UID = """
        SELECT
            entity_uid AS entityUid,
            act_uid AS actUid,
            locator_uid AS locatorUid,
            add_reason_cd AS addReasonCd,
            add_time AS addTime,
            add_user_id AS addUserId,
            duration_amt AS durationAmount,
            duration_unit_cd AS durationUnitCd,
            from_time AS fromTime,
            last_chg_reason_cd AS lastChangeReasonCd,
            last_chg_time AS lastChangeTime,
            last_chg_user_id AS lastChangeUserId,
            record_status_cd AS recordStatusCd,
            record_status_time AS recordStatusTime,
            to_time AS toTime,
            status_cd AS statusCd,
            status_time AS statusTime,
            type_cd AS typeCd,
            type_desc_txt AS typeDescTxt,
            user_affiliation_txt AS userAffiliationTxt
        FROM Act_locator_participation
        WHERE act_uid = :act_uid
        """;

}
