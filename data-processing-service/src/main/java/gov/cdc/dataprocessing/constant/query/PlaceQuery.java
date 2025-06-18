package gov.cdc.dataprocessing.constant.query;

public class PlaceQuery {
    public static final String FIND_BY_ID = """
            SELECT
                place_uid AS placeUid,
                add_reason_cd AS addReasonCd,
                add_time AS addTime,
                add_user_id AS addUserId,
                cd AS cd,
                cd_desc_txt AS cdDescTxt,
                description AS description,
                duration_amt AS durationAmt,
                duration_unit_cd AS durationUnitCd,
                from_time AS fromTime,
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
                street_addr1 AS streetAddr1,
                street_addr2 AS streetAddr2,
                city_cd AS cityCd,
                city_desc_txt AS cityDescTxt,
                state_cd AS stateCd,
                zip_cd AS zipCd,
                cnty_cd AS cntyCd,
                cntry_cd AS cntryCd,
                phone_nbr AS phoneNbr,
                phone_cntry_cd AS phoneCntryCd,
                version_ctrl_nbr AS versionCtrlNbr
            FROM Place
            WHERE place_uid = :placeUid
            
            """;
}
