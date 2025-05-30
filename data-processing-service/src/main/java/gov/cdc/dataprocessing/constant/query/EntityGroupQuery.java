package gov.cdc.dataprocessing.constant.query;

public class EntityGroupQuery {
    public static final String FIND_BY_UID = """
            SELECT
                entity_group_uid AS entityGroupUid,
                add_reason_cd AS addReasonCd,
                add_time AS addTime,
                add_user_id AS addUserId,
                cd AS cd,
                cd_desc_txt AS cdDescTxt,
                description AS description,
                duration_amt AS durationAmt,
                duration_unit_cd AS durationUnitCd,
                from_time AS fromTime,
                group_cnt AS groupCnt,
                last_chg_reason_cd AS lastChgReasonCd,
                last_chg_time AS lastChgTime,
                last_chg_user_id AS lastChgUserId,
                local_id AS localId,
                nm AS nm,
                record_status_cd AS recordStatusCd,
                record_status_time AS recordStatusTime,
                status_cd AS statusCd,
                status_time AS statusTime,
                to_time AS toTime,
                user_affiliation_txt AS userAffiliationTxt,
                version_ctrl_nbr AS versionCtrlNbr
            FROM Entity_group
            WHERE entity_group_uid = :entityGroupUid
            """;
}
