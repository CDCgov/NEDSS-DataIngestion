package gov.cdc.dataprocessing.constant.query;

public class OrganizationQuery {
    public static final String INSERT_ORGANIZATION = """
INSERT INTO Organization (
    organization_uid, add_reason_cd, add_time, add_user_id, cd, cd_desc_txt, description,
    duration_amt, duration_unit_cd, from_time, last_chg_reason_cd, last_chg_time, last_chg_user_id,
    local_id, record_status_cd, record_status_time, standard_industry_class_cd,
    standard_industry_desc_txt, status_cd, status_time, to_time, user_affiliation_txt,
    display_nm, street_addr1, street_addr2, city_cd, city_desc_txt, state_cd, cnty_cd, cntry_cd,
    zip_cd, phone_nbr, phone_cntry_cd, version_ctrl_nbr, electronic_ind, edx_ind
) VALUES (
    :organizationUid, :addReasonCode, :addTime, :addUserId, :code, :codeDescTxt, :description,
    :durationAmt, :durationUnitCd, :fromTime, :lastChgReasonCd, :lastChgTime, :lastChgUserId,
    :localId, :recordStatusCd, :recordStatusTime, :standardIndustryClassCd,
    :standardIndustryDescTxt, :statusCd, :statusTime, :toTime, :userAffiliationTxt,
    :displayNm, :streetAddr1, :streetAddr2, :cityCd, :cityDescTxt, :stateCd, :cntyCd, :cntryCd,
    :zipCd, :phoneNbr, :phoneCntryCd, :versionCtrlNbr, :electronicInd, :edxInd
)
""";

    public static final String UPDATE_ORGANIZATION = """
UPDATE Organization SET
    add_reason_cd = :addReasonCode,
    add_time = :addTime,
    add_user_id = :addUserId,
    cd = :code,
    cd_desc_txt = :codeDescTxt,
    description = :description,
    duration_amt = :durationAmt,
    duration_unit_cd = :durationUnitCd,
    from_time = :fromTime,
    last_chg_reason_cd = :lastChgReasonCd,
    last_chg_time = :lastChgTime,
    last_chg_user_id = :lastChgUserId,
    local_id = :localId,
    record_status_cd = :recordStatusCd,
    record_status_time = :recordStatusTime,
    standard_industry_class_cd = :standardIndustryClassCd,
    standard_industry_desc_txt = :standardIndustryDescTxt,
    status_cd = :statusCd,
    status_time = :statusTime,
    to_time = :toTime,
    user_affiliation_txt = :userAffiliationTxt,
    display_nm = :displayNm,
    street_addr1 = :streetAddr1,
    street_addr2 = :streetAddr2,
    city_cd = :cityCd,
    city_desc_txt = :cityDescTxt,
    state_cd = :stateCd,
    cnty_cd = :cntyCd,
    cntry_cd = :cntryCd,
    zip_cd = :zipCd,
    phone_nbr = :phoneNbr,
    phone_cntry_cd = :phoneCntryCd,
    version_ctrl_nbr = :versionCtrlNbr,
    electronic_ind = :electronicInd,
    edx_ind = :edxInd
WHERE organization_uid = :organizationUid
""";

    public static final String SELECT_ORGANIZATION_BY_ID = """
SELECT
    organization_uid              AS organizationUid,
    add_reason_cd                AS addReasonCode,
    add_time                     AS addTime,
    add_user_id                  AS addUserId,
    cd                           AS code,
    cd_desc_txt                  AS codeDescTxt,
    description                  AS description,
    duration_amt                 AS durationAmt,
    duration_unit_cd             AS durationUnitCd,
    from_time                    AS fromTime,
    last_chg_reason_cd           AS lastChgReasonCd,
    last_chg_time                AS lastChgTime,
    last_chg_user_id             AS lastChgUserId,
    local_id                     AS localId,
    record_status_cd             AS recordStatusCd,
    record_status_time           AS recordStatusTime,
    standard_industry_class_cd   AS standardIndustryClassCd,
    standard_industry_desc_txt   AS standardIndustryDescTxt,
    status_cd                    AS statusCd,
    status_time                  AS statusTime,
    to_time                      AS toTime,
    user_affiliation_txt         AS userAffiliationTxt,
    display_nm                   AS displayNm,
    street_addr1                 AS streetAddr1,
    street_addr2                 AS streetAddr2,
    city_cd                      AS cityCd,
    city_desc_txt                AS cityDescTxt,
    state_cd                     AS stateCd,
    cnty_cd                      AS cntyCd,
    cntry_cd                     AS cntryCd,
    zip_cd                       AS zipCd,
    phone_nbr                    AS phoneNbr,
    phone_cntry_cd               AS phoneCntryCd,
    version_ctrl_nbr             AS versionCtrlNbr,
    electronic_ind               AS electronicInd,
    edx_ind                      AS edxInd
FROM Organization
WHERE organization_uid = :organizationUid
""";


}
