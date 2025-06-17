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


    public static final String MERGE_ACT_LOCATOR = """
            MERGE INTO Act_locator_participation AS target
            USING (SELECT
                       :entity_uid AS entity_uid,
                       :act_uid AS act_uid,
                       :locator_uid AS locator_uid,
                       :add_reason_cd AS add_reason_cd,
                       :add_time AS add_time,
                       :add_user_id AS add_user_id,
                       :duration_amt AS duration_amt,
                       :duration_unit_cd AS duration_unit_cd,
                       :from_time AS from_time,
                       :last_chg_reason_cd AS last_chg_reason_cd,
                       :last_chg_time AS last_chg_time,
                       :last_chg_user_id AS last_chg_user_id,
                       :record_status_cd AS record_status_cd,
                       :record_status_time AS record_status_time,
                       :to_time AS to_time,
                       :status_cd AS status_cd,
                       :status_time AS status_time,
                       :type_cd AS type_cd,
                       :type_desc_txt AS type_desc_txt,
                       :user_affiliation_txt AS user_affiliation_txt
                   ) AS source
            ON target.entity_uid = source.entity_uid
            
            WHEN MATCHED THEN
                UPDATE SET
                    act_uid = source.act_uid,
                    locator_uid = source.locator_uid,
                    add_reason_cd = source.add_reason_cd,
                    add_time = source.add_time,
                    add_user_id = source.add_user_id,
                    duration_amt = source.duration_amt,
                    duration_unit_cd = source.duration_unit_cd,
                    from_time = source.from_time,
                    last_chg_reason_cd = source.last_chg_reason_cd,
                    last_chg_time = source.last_chg_time,
                    last_chg_user_id = source.last_chg_user_id,
                    record_status_cd = source.record_status_cd,
                    record_status_time = source.record_status_time,
                    to_time = source.to_time,
                    status_cd = source.status_cd,
                    status_time = source.status_time,
                    type_cd = source.type_cd,
                    type_desc_txt = source.type_desc_txt,
                    user_affiliation_txt = source.user_affiliation_txt
            
            WHEN NOT MATCHED THEN
                INSERT (
                    entity_uid, act_uid, locator_uid, add_reason_cd, add_time, add_user_id,
                    duration_amt, duration_unit_cd, from_time, last_chg_reason_cd, last_chg_time, last_chg_user_id,
                    record_status_cd, record_status_time, to_time, status_cd, status_time,
                    type_cd, type_desc_txt, user_affiliation_txt
                )
                VALUES (
                    source.entity_uid, source.act_uid, source.locator_uid, source.add_reason_cd, source.add_time, source.add_user_id,
                    source.duration_amt, source.duration_unit_cd, source.from_time, source.last_chg_reason_cd, source.last_chg_time, source.last_chg_user_id,
                    source.record_status_cd, source.record_status_time, source.to_time, source.status_cd, source.status_time,
                    source.type_cd, source.type_desc_txt, source.user_affiliation_txt
                );
            """;

}
