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



}
