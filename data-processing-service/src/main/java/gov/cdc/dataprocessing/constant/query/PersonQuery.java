package gov.cdc.dataprocessing.constant.query;

public class PersonQuery {
    public static final String INSERT_SQL_PERSON = """
    INSERT INTO Person (
        person_uid, version_ctrl_nbr, add_reason_cd, add_time, add_user_id,
        administrative_gender_cd, age_calc, age_calc_time, age_calc_unit_cd, age_category_cd,
        age_reported, age_reported_time, age_reported_unit_cd, birth_gender_cd, birth_order_nbr,
        birth_time, birth_time_calc, cd, cd_desc_txt, curr_sex_cd, deceased_ind_cd, deceased_time,
        description, education_level_cd, education_level_desc_txt, ethnic_group_ind,
        last_chg_reason_cd, last_chg_time, last_chg_user_id, local_id, marital_status_cd,
        marital_status_desc_txt, mothers_maiden_nm, multiple_birth_ind, occupation_cd,
        preferred_gender_cd, prim_lang_cd, prim_lang_desc_txt, record_status_cd,
        record_status_time, status_cd, status_time, survived_ind_cd, user_affiliation_txt,
        first_nm, last_nm, middle_nm, nm_prefix, nm_suffix, preferred_nm, hm_street_addr1,
        hm_street_addr2, hm_city_cd, hm_city_desc_txt, hm_state_cd, hm_zip_cd, hm_cnty_cd,
        hm_cntry_cd, hm_phone_nbr, hm_phone_cntry_cd, hm_email_addr, cell_phone_nbr,
        wk_street_addr1, wk_street_addr2, wk_city_cd, wk_city_desc_txt, wk_state_cd, wk_zip_cd,
        wk_cnty_cd, wk_cntry_cd, wk_phone_nbr, wk_phone_cntry_cd, wk_email_addr, SSN,
        medicaid_num, dl_num, dl_state_cd, race_cd, race_seq_nbr, race_category_cd,
        ethnicity_group_cd, ethnic_group_seq_nbr, adults_in_house_nbr, children_in_house_nbr,
        birth_city_cd, birth_city_desc_txt, birth_cntry_cd, birth_state_cd, race_desc_txt,
        ethnic_group_desc_txt, as_of_date_admin, as_of_date_ethnicity, as_of_date_general,
        as_of_date_morbidity, as_of_date_sex, electronic_ind, person_parent_uid, dedup_match_ind,
        group_nbr, group_time, edx_ind, speaks_english_cd, additional_gender_cd, ehars_id,
        ethnic_unk_reason_cd, sex_unk_reason_cd
    ) VALUES (
        :person_uid, :version_ctrl_nbr, :add_reason_cd, :add_time, :add_user_id,
        :administrative_gender_cd, :age_calc, :age_calc_time, :age_calc_unit_cd, :age_category_cd,
        :age_reported, :age_reported_time, :age_reported_unit_cd, :birth_gender_cd, :birth_order_nbr,
        :birth_time, :birth_time_calc, :cd, :cd_desc_txt, :curr_sex_cd, :deceased_ind_cd, :deceased_time,
        :description, :education_level_cd, :education_level_desc_txt, :ethnic_group_ind,
        :last_chg_reason_cd, :last_chg_time, :last_chg_user_id, :local_id, :marital_status_cd,
        :marital_status_desc_txt, :mothers_maiden_nm, :multiple_birth_ind, :occupation_cd,
        :preferred_gender_cd, :prim_lang_cd, :prim_lang_desc_txt, :record_status_cd,
        :record_status_time, :status_cd, :status_time, :survived_ind_cd, :user_affiliation_txt,
        :first_nm, :last_nm, :middle_nm, :nm_prefix, :nm_suffix, :preferred_nm, :hm_street_addr1,
        :hm_street_addr2, :hm_city_cd, :hm_city_desc_txt, :hm_state_cd, :hm_zip_cd, :hm_cnty_cd,
        :hm_cntry_cd, :hm_phone_nbr, :hm_phone_cntry_cd, :hm_email_addr, :cell_phone_nbr,
        :wk_street_addr1, :wk_street_addr2, :wk_city_cd, :wk_city_desc_txt, :wk_state_cd, :wk_zip_cd,
        :wk_cnty_cd, :wk_cntry_cd, :wk_phone_nbr, :wk_phone_cntry_cd, :wk_email_addr, :SSN,
        :medicaid_num, :dl_num, :dl_state_cd, :race_cd, :race_seq_nbr, :race_category_cd,
        :ethnicity_group_cd, :ethnic_group_seq_nbr, :adults_in_house_nbr, :children_in_house_nbr,
        :birth_city_cd, :birth_city_desc_txt, :birth_cntry_cd, :birth_state_cd, :race_desc_txt,
        :ethnic_group_desc_txt, :as_of_date_admin, :as_of_date_ethnicity, :as_of_date_general,
        :as_of_date_morbidity, :as_of_date_sex, :electronic_ind, :person_parent_uid, :dedup_match_ind,
        :group_nbr, :group_time, :edx_ind, :speaks_english_cd, :additional_gender_cd, :ehars_id,
        :ethnic_unk_reason_cd, :sex_unk_reason_cd
    )
""";

    public static final String INSERT_SQL_PERSON_NAME = """
INSERT INTO Person_name (
    person_uid, person_name_seq, add_reason_cd, add_time, add_user_id, default_nm_ind,
    duration_amt, duration_unit_cd, first_nm, first_nm_sndx, from_time, last_chg_reason_cd,
    last_chg_time, last_chg_user_id, last_nm, last_nm_sndx, last_nm2, last_nm2_sndx, middle_nm,
    middle_nm2, nm_degree, nm_prefix, nm_suffix, nm_use_cd, record_status_cd,
    record_status_time, status_cd, status_time, to_time, user_affiliation_txt, as_of_date
) VALUES (
    :person_uid, :person_name_seq, :add_reason_cd, :add_time, :add_user_id, :default_nm_ind,
    :duration_amt, :duration_unit_cd, :first_nm, :first_nm_sndx, :from_time, :last_chg_reason_cd,
    :last_chg_time, :last_chg_user_id, :last_nm, :last_nm_sndx, :last_nm2, :last_nm2_sndx, :middle_nm,
    :middle_nm2, :nm_degree, :nm_prefix, :nm_suffix, :nm_use_cd, :record_status_cd,
    :record_status_time, :status_cd, :status_time, :to_time, :user_affiliation_txt, :as_of_date
)
""";

    public static final String INSERT_SQL_PERSON_RACE = """
INSERT INTO Person_race (
    person_uid, race_cd, add_reason_cd, add_time, add_user_id,
    last_chg_reason_cd, last_chg_time, last_chg_user_id, race_category_cd,
    race_desc_txt, record_status_cd, record_status_time,
    user_affiliation_txt, as_of_date
) VALUES (
    :person_uid, :race_cd, :add_reason_cd, :add_time, :add_user_id,
    :last_chg_reason_cd, :last_chg_time, :last_chg_user_id, :race_category_cd,
    :race_desc_txt, :record_status_cd, :record_status_time,
    :user_affiliation_txt, :as_of_date
)
""";

    public static final String INSERT_SQL_PERSON_ETHNIC = """
INSERT INTO Person_ethnic_group (
    person_uid, ethnic_group_cd, add_reason_cd, add_time, add_user_id,
    ethnic_group_desc_txt, last_chg_reason_cd, last_chg_time, last_chg_user_id,
    record_status_cd, record_status_time, user_affiliation_txt
) VALUES (
    :person_uid, :ethnic_group_cd, :add_reason_cd, :add_time, :add_user_id,
    :ethnic_group_desc_txt, :last_chg_reason_cd, :last_chg_time, :last_chg_user_id,
    :record_status_cd, :record_status_time, :user_affiliation_txt
)
""";


    public static final String INSERT_SQL_ROLE = """
INSERT INTO Role (
    subject_entity_uid, cd, role_seq, add_reason_cd, add_time, add_user_id,
    cd_desc_txt, effective_duration_amt, effective_duration_unit_cd, effective_from_time,
    effective_to_time, last_chg_reason_cd, last_chg_time, last_chg_user_id,
    record_status_cd, record_status_time, scoping_class_cd, scoping_entity_uid,
    scoping_role_cd, scoping_role_seq, status_cd, status_time, subject_class_cd, user_affiliation_txt
) VALUES (
    :subject_entity_uid, :cd, :role_seq, :add_reason_cd, :add_time, :add_user_id,
    :cd_desc_txt, :effective_duration_amt, :effective_duration_unit_cd, :effective_from_time,
    :effective_to_time, :last_chg_reason_cd, :last_chg_time, :last_chg_user_id,
    :record_status_cd, :record_status_time, :scoping_class_cd, :scoping_entity_uid,
    :scoping_role_cd, :scoping_role_seq, :status_cd, :status_time, :subject_class_cd, :user_affiliation_txt
)
""";

}
