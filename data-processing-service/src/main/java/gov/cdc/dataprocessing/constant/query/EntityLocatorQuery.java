package gov.cdc.dataprocessing.constant.query;

public class EntityLocatorQuery {
    public static final String INSERT_SQL_PHYSICAL_LOCATOR = """
INSERT INTO Physical_locator (
    physical_locator_uid, add_reason_cd, add_time, add_user_id,
    image_txt, last_chg_reason_cd, last_chg_time, last_chg_user_id,
    locator_txt, record_status_cd, record_status_time, user_affiliation_txt
) VALUES (
    :physical_locator_uid, :add_reason_cd, :add_time, :add_user_id,
    :image_txt, :last_chg_reason_cd, :last_chg_time, :last_chg_user_id,
    :locator_txt, :record_status_cd, :record_status_time, :user_affiliation_txt
)
""";

    public static final String INSERT_SQL_POSTAL_LOCATOR = """
INSERT INTO Postal_locator (
    postal_locator_uid, add_reason_cd, add_time, add_user_id,
    census_block_cd, census_minor_civil_division_cd, census_track_cd,
    city_cd, city_desc_txt, cntry_cd, cntry_desc_txt, cnty_cd, cnty_desc_txt,
    last_chg_reason_cd, last_chg_time, last_chg_user_id, MSA_congress_district_cd,
    record_status_cd, record_status_time, region_district_cd, state_cd,
    street_addr1, street_addr2, user_affiliation_txt, zip_cd, geocode_match_ind,
    within_city_limits_ind, census_tract
) VALUES (
    :postal_locator_uid, :add_reason_cd, :add_time, :add_user_id,
    :census_block_cd, :census_minor_civil_division_cd, :census_track_cd,
    :city_cd, :city_desc_txt, :cntry_cd, :cntry_desc_txt, :cnty_cd, :cnty_desc_txt,
    :last_chg_reason_cd, :last_chg_time, :last_chg_user_id, :MSA_congress_district_cd,
    :record_status_cd, :record_status_time, :region_district_cd, :state_cd,
    :street_addr1, :street_addr2, :user_affiliation_txt, :zip_cd, :geocode_match_ind,
    :within_city_limits_ind, :census_tract
)
""";

    public static final String INSERT_SQL_TELE_LOCATOR = """
INSERT INTO Tele_locator (
    tele_locator_uid, add_reason_cd, add_time, add_user_id,
    cntry_cd, email_address, extension_txt, last_chg_reason_cd,
    last_chg_time, last_chg_user_id, phone_nbr_txt, record_status_cd,
    record_status_time, url_address, user_affiliation_txt
) VALUES (
    :tele_locator_uid, :add_reason_cd, :add_time, :add_user_id,
    :cntry_cd, :email_address, :extension_txt, :last_chg_reason_cd,
    :last_chg_time, :last_chg_user_id, :phone_nbr_txt, :record_status_cd,
    :record_status_time, :url_address, :user_affiliation_txt
)
""";

    public static final String INSERT_SQL_ENTITY_LOCATOR_PARTICIPATION = """
INSERT INTO Entity_locator_participation (
    entity_uid, locator_uid, version_ctrl_nbr, add_reason_cd, add_time, add_user_id,
    cd, cd_desc_txt, class_cd, duration_amt, duration_unit_cd, from_time,
    last_chg_reason_cd, last_chg_time, last_chg_user_id, locator_desc_txt,
    record_status_cd, record_status_time, status_cd, status_time, to_time, use_cd,
    user_affiliation_txt, valid_time_txt, as_of_date
) VALUES (
    :entity_uid, :locator_uid, :version_ctrl_nbr, :add_reason_cd, :add_time, :add_user_id,
    :cd, :cd_desc_txt, :class_cd, :duration_amt, :duration_unit_cd, :from_time,
    :last_chg_reason_cd, :last_chg_time, :last_chg_user_id, :locator_desc_txt,
    :record_status_cd, :record_status_time, :status_cd, :status_time, :to_time, :use_cd,
    :user_affiliation_txt, :valid_time_txt, :as_of_date
)
""";

    public static final String SELECT_ENTITY_LOCATOR_PARTICIPATIONS_BY_ENTITY_UID = """
            SELECT
                entity_uid AS entityUid,
                locator_uid AS locatorUid,
                version_ctrl_nbr AS versionCtrlNbr,
                add_reason_cd AS addReasonCd,
                add_time AS addTime,
                add_user_id AS addUserId,
                cd AS cd,
                cd_desc_txt AS cdDescTxt,
                class_cd AS classCd,
                duration_amt AS durationAmt,
                duration_unit_cd AS durationUnitCd,
                from_time AS fromTime,
                last_chg_reason_cd AS lastChgReasonCd,
                last_chg_time AS lastChgTime,
                last_chg_user_id AS lastChgUserId,
                locator_desc_txt AS locatorDescTxt,
                record_status_cd AS recordStatusCd,
                record_status_time AS recordStatusTime,
                status_cd AS statusCd,
                status_time AS statusTime,
                to_time AS toTime,
                use_cd AS useCd,
                user_affiliation_txt AS userAffiliationTxt,
                valid_time_txt AS validTimeTxt,
                as_of_date AS asOfDate
            FROM Entity_locator_participation
            WHERE entity_uid = :entity_uid
            """;

    public static final String UPDATE_ENTITY_LOCATOR_PARTICIPATION = """
UPDATE Entity_locator_participation
SET
    add_reason_cd = :addReasonCd,
    add_time = :addTime,
    add_user_id = :addUserId,
    cd = :cd,
    cd_desc_txt = :cdDescTxt,
    class_cd = :classCd,
    duration_amt = :durationAmt,
    duration_unit_cd = :durationUnitCd,
    from_time = :fromTime,
    last_chg_reason_cd = :lastChgReasonCd,
    last_chg_time = :lastChgTime,
    last_chg_user_id = :lastChgUserId,
    locator_desc_txt = :locatorDescTxt,
    record_status_cd = :recordStatusCd,
    record_status_time = :recordStatusTime,
    status_cd = :statusCd,
    status_time = :statusTime,
    to_time = :toTime,
    use_cd = :useCd,
    user_affiliation_txt = :userAffiliationTxt,
    valid_time_txt = :validTimeTxt,
    as_of_date = :asOfDate,
    version_ctrl_nbr = :versionCtrlNbr
WHERE entity_uid = :entityUid AND locator_uid = :locatorUid
""";


    public static final String SELECT_ENTITY_LOCATOR_BY_ENTITY_UID = """
SELECT
    entity_uid              AS entityUid,
    locator_uid             AS locatorUid,
    version_ctrl_nbr        AS versionCtrlNbr,
    add_reason_cd           AS addReasonCd,
    add_time                AS addTime,
    add_user_id             AS addUserId,
    cd                      AS cd,
    cd_desc_txt             AS cdDescTxt,
    class_cd                AS classCd,
    duration_amt            AS durationAmt,
    duration_unit_cd        AS durationUnitCd,
    from_time               AS fromTime,
    last_chg_reason_cd      AS lastChgReasonCd,
    last_chg_time           AS lastChgTime,
    last_chg_user_id        AS lastChgUserId,
    locator_desc_txt        AS locatorDescTxt,
    record_status_cd        AS recordStatusCd,
    record_status_time      AS recordStatusTime,
    status_cd               AS statusCd,
    status_time             AS statusTime,
    to_time                 AS toTime,
    use_cd                  AS useCd,
    user_affiliation_txt    AS userAffiliationTxt,
    valid_time_txt          AS validTimeTxt,
    as_of_date              AS asOfDate
FROM Entity_locator_participation
WHERE entity_uid = :entityUid
""";

    public static final String UPDATE_TELE_LOCATOR = """
UPDATE Tele_locator SET
    add_reason_cd = :addReasonCd,
    add_time = :addTime,
    add_user_id = :addUserId,
    cntry_cd = :cntryCd,
    email_address = :emailAddress,
    extension_txt = :extensionTxt,
    last_chg_reason_cd = :lastChgReasonCd,
    last_chg_time = :lastChgTime,
    last_chg_user_id = :lastChgUserId,
    phone_nbr_txt = :phoneNbrTxt,
    record_status_cd = :recordStatusCd,
    record_status_time = :recordStatusTime,
    url_address = :urlAddress,
    user_affiliation_txt = :userAffiliationTxt
WHERE tele_locator_uid = :teleLocatorUid
""";

    public static final String SELECT_TELE_LOCATOR_BY_UIDS = """
SELECT
    tele_locator_uid       AS teleLocatorUid,
    add_reason_cd          AS addReasonCd,
    add_time               AS addTime,
    add_user_id            AS addUserId,
    cntry_cd               AS cntryCd,
    email_address          AS emailAddress,
    extension_txt          AS extensionTxt,
    last_chg_reason_cd     AS lastChgReasonCd,
    last_chg_time          AS lastChgTime,
    last_chg_user_id       AS lastChgUserId,
    phone_nbr_txt          AS phoneNbrTxt,
    record_status_cd       AS recordStatusCd,
    record_status_time     AS recordStatusTime,
    url_address            AS urlAddress,
    user_affiliation_txt   AS userAffiliationTxt
FROM Tele_locator
WHERE tele_locator_uid IN (:uids)
""";

    public static final String UPDATE_POSTAL_LOCATOR = """
UPDATE Postal_locator SET
    add_reason_cd = :addReasonCd,
    add_time = :addTime,
    add_user_id = :addUserId,
    census_block_cd = :censusBlockCd,
    census_minor_civil_division_cd = :censusMinorCivilDivisionCd,
    census_track_cd = :censusTrackCd,
    city_cd = :cityCd,
    city_desc_txt = :cityDescTxt,
    cntry_cd = :cntryCd,
    cntry_desc_txt = :cntryDescTxt,
    cnty_cd = :cntyCd,
    cnty_desc_txt = :cntyDescTxt,
    last_chg_reason_cd = :lastChgReasonCd,
    last_chg_time = :lastChgTime,
    last_chg_user_id = :lastChgUserId,
    MSA_congress_district_cd = :msaCongressDistrictCd,
    record_status_cd = :recordStatusCd,
    record_status_time = :recordStatusTime,
    region_district_cd = :regionDistrictCd,
    state_cd = :stateCd,
    street_addr1 = :streetAddr1,
    street_addr2 = :streetAddr2,
    user_affiliation_txt = :userAffiliationTxt,
    zip_cd = :zipCd,
    geocode_match_ind = :geocodeMatchInd,
    within_city_limits_ind = :withinCityLimitsInd,
    census_tract = :censusTract
WHERE postal_locator_uid = :postalLocatorUid
""";

    public static final String SELECT_POSTAL_LOCATOR_BY_UIDS = """
SELECT
    postal_locator_uid             AS postalLocatorUid,
    add_reason_cd                  AS addReasonCd,
    add_time                       AS addTime,
    add_user_id                    AS addUserId,
    census_block_cd                AS censusBlockCd,
    census_minor_civil_division_cd AS censusMinorCivilDivisionCd,
    census_track_cd                AS censusTrackCd,
    city_cd                        AS cityCd,
    city_desc_txt                  AS cityDescTxt,
    cntry_cd                       AS cntryCd,
    cntry_desc_txt                 AS cntryDescTxt,
    cnty_cd                        AS cntyCd,
    cnty_desc_txt                  AS cntyDescTxt,
    last_chg_reason_cd             AS lastChgReasonCd,
    last_chg_time                  AS lastChgTime,
    last_chg_user_id               AS lastChgUserId,
    MSA_congress_district_cd       AS msaCongressDistrictCd,
    record_status_cd               AS recordStatusCd,
    record_status_time             AS recordStatusTime,
    region_district_cd             AS regionDistrictCd,
    state_cd                       AS stateCd,
    street_addr1                   AS streetAddr1,
    street_addr2                   AS streetAddr2,
    user_affiliation_txt           AS userAffiliationTxt,
    zip_cd                         AS zipCd,
    geocode_match_ind              AS geocodeMatchInd,
    within_city_limits_ind         AS withinCityLimitsInd,
    census_tract                   AS censusTract
FROM Postal_locator
WHERE postal_locator_uid IN (:uids)
""";

    public static final String SELECT_PHYSICAL_LOCATOR_BY_UIDS = """
SELECT
    physical_locator_uid       AS physicalLocatorUid,
    add_reason_cd              AS addReasonCd,
    add_time                   AS addTime,
    add_user_id                AS addUserId,
    image_txt                  AS imageTxt,
    last_chg_reason_cd         AS lastChgReasonCd,
    last_chg_time              AS lastChgTime,
    last_chg_user_id           AS lastChgUserId,
    locator_txt                AS locatorTxt,
    record_status_cd           AS recordStatusCd,
    record_status_time         AS recordStatusTime,
    user_affiliation_txt       AS userAffiliationTxt
FROM Physical_locator
WHERE physical_locator_uid IN (:uids)
""";

    public static final String UPDATE_PHYSICAL_LOCATOR = """
UPDATE Physical_locator SET
    add_reason_cd = :addReasonCd,
    add_time = :addTime,
    add_user_id = :addUserId,
    image_txt = :imageTxt,
    last_chg_reason_cd = :lastChgReasonCd,
    last_chg_time = :lastChgTime,
    last_chg_user_id = :lastChgUserId,
    locator_txt = :locatorTxt,
    record_status_cd = :recordStatusCd,
    record_status_time = :recordStatusTime,
    user_affiliation_txt = :userAffiliationTxt
WHERE physical_locator_uid = :physicalLocatorUid
""";



}
