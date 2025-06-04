package gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template;

import gov.cdc.dataprocessing.repository.nbs.odse.model.person.Person;
import gov.cdc.dataprocessing.repository.nbs.odse.model.person.PersonEthnicGroup;
import gov.cdc.dataprocessing.repository.nbs.odse.model.person.PersonName;
import gov.cdc.dataprocessing.repository.nbs.odse.model.person.PersonRace;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

import static gov.cdc.dataprocessing.constant.query.PersonQuery.*;

@Component
public class PersonJdbcRepository {
    private final NamedParameterJdbcTemplate jdbcTemplateOdse;

    public PersonJdbcRepository(@Qualifier("odseNamedParameterJdbcTemplate") NamedParameterJdbcTemplate jdbcTemplateOdse) {
        this.jdbcTemplateOdse = jdbcTemplateOdse;
    }

    public void createPerson(Person person) {
        jdbcTemplateOdse.update(INSERT_SQL_PERSON, new MapSqlParameterSource()
                .addValue("person_uid", person.getPersonUid())
                .addValue("version_ctrl_nbr", person.getVersionCtrlNbr())
                .addValue("add_reason_cd", person.getAddReasonCd())
                .addValue("add_time", person.getAddTime())
                .addValue("add_user_id", person.getAddUserId())
                .addValue("administrative_gender_cd", person.getAdministrativeGenderCd())
                .addValue("age_calc", person.getAgeCalc())
                .addValue("age_calc_time", person.getAgeCalcTime())
                .addValue("age_calc_unit_cd", person.getAgeCalcUnitCd())
                .addValue("age_category_cd", person.getAgeCategoryCd())
                .addValue("age_reported", person.getAgeReported())
                .addValue("age_reported_time", person.getAgeReportedTime())
                .addValue("age_reported_unit_cd", person.getAgeReportedUnitCd())
                .addValue("birth_gender_cd", person.getBirthGenderCd())
                .addValue("birth_order_nbr", person.getBirthOrderNbr())
                .addValue("birth_time", person.getBirthTime())
                .addValue("birth_time_calc", person.getBirthTimeCalc())
                .addValue("cd", person.getCd())
                .addValue("cd_desc_txt", person.getCdDescTxt())
                .addValue("curr_sex_cd", person.getCurrSexCd())
                .addValue("deceased_ind_cd", person.getDeceasedIndCd())
                .addValue("deceased_time", person.getDeceasedTime())
                .addValue("description", person.getDescription())
                .addValue("education_level_cd", person.getEducationLevelCd())
                .addValue("education_level_desc_txt", person.getEducationLevelDescTxt())
                .addValue("ethnic_group_ind", person.getEthnicGroupInd())
                .addValue("last_chg_reason_cd", person.getLastChgReasonCd())
                .addValue("last_chg_time", person.getLastChgTime())
                .addValue("last_chg_user_id", person.getLastChgUserId())
                .addValue("local_id", person.getLocalId())
                .addValue("marital_status_cd", person.getMaritalStatusCd())
                .addValue("marital_status_desc_txt", person.getMaritalStatusDescTxt())
                .addValue("mothers_maiden_nm", person.getMothersMaidenNm())
                .addValue("multiple_birth_ind", person.getMultipleBirthInd())
                .addValue("occupation_cd", person.getOccupationCd())
                .addValue("preferred_gender_cd", person.getPreferredGenderCd())
                .addValue("prim_lang_cd", person.getPrimLangCd())
                .addValue("prim_lang_desc_txt", person.getPrimLangDescTxt())
                .addValue("record_status_cd", person.getRecordStatusCd())
                .addValue("record_status_time", person.getRecordStatusTime())
                .addValue("status_cd", person.getStatusCd())
                .addValue("status_time", person.getStatusTime())
                .addValue("survived_ind_cd", person.getSurvivedIndCd())
                .addValue("user_affiliation_txt", person.getUserAffiliationTxt())
                .addValue("first_nm", person.getFirstNm())
                .addValue("last_nm", person.getLastNm())
                .addValue("middle_nm", person.getMiddleNm())
                .addValue("nm_prefix", person.getNmPrefix())
                .addValue("nm_suffix", person.getNmSuffix())
                .addValue("preferred_nm", person.getPreferredNm())
                .addValue("hm_street_addr1", person.getHmStreetAddr1())
                .addValue("hm_street_addr2", person.getHmStreetAddr2())
                .addValue("hm_city_cd", person.getHmCityCd())
                .addValue("hm_city_desc_txt", person.getHmCityDescTxt())
                .addValue("hm_state_cd", person.getHmStateCd())
                .addValue("hm_zip_cd", person.getHmZipCd())
                .addValue("hm_cnty_cd", person.getHmCntyCd())
                .addValue("hm_cntry_cd", person.getHmCntryCd())
                .addValue("hm_phone_nbr", person.getHmPhoneNbr())
                .addValue("hm_phone_cntry_cd", person.getHmPhoneCntryCd())
                .addValue("hm_email_addr", person.getHmEmailAddr())
                .addValue("cell_phone_nbr", person.getCellPhoneNbr())
                .addValue("wk_street_addr1", person.getWkStreetAddr1())
                .addValue("wk_street_addr2", person.getWkStreetAddr2())
                .addValue("wk_city_cd", person.getWkCityCd())
                .addValue("wk_city_desc_txt", person.getWkCityDescTxt())
                .addValue("wk_state_cd", person.getWkStateCd())
                .addValue("wk_zip_cd", person.getWkZipCd())
                .addValue("wk_cnty_cd", person.getWkCntyCd())
                .addValue("wk_cntry_cd", person.getWkCntryCd())
                .addValue("wk_phone_nbr", person.getWkPhoneNbr())
                .addValue("wk_phone_cntry_cd", person.getWkPhoneCntryCd())
                .addValue("wk_email_addr", person.getWkEmailAddr())
                .addValue("SSN", person.getSsn())
                .addValue("medicaid_num", person.getMedicaidNum())
                .addValue("dl_num", person.getDlNum())
                .addValue("dl_state_cd", person.getDlStateCd())
                .addValue("race_cd", person.getRaceCd())
                .addValue("race_seq_nbr", person.getRaceSeqNbr())
                .addValue("race_category_cd", person.getRaceCategoryCd())
                .addValue("ethnicity_group_cd", person.getEthnicityGroupCd())
                .addValue("ethnic_group_seq_nbr", person.getEthnicGroupSeqNbr())
                .addValue("adults_in_house_nbr", person.getAdultsInHouseNbr())
                .addValue("children_in_house_nbr", person.getChildrenInHouseNbr())
                .addValue("birth_city_cd", person.getBirthCityCd())
                .addValue("birth_city_desc_txt", person.getBirthCityDescTxt())
                .addValue("birth_cntry_cd", person.getBirthCntryCd())
                .addValue("birth_state_cd", person.getBirthStateCd())
                .addValue("race_desc_txt", person.getRaceDescTxt())
                .addValue("ethnic_group_desc_txt", person.getEthnicGroupDescTxt())
                .addValue("as_of_date_admin", person.getAsOfDateAdmin())
                .addValue("as_of_date_ethnicity", person.getAsOfDateEthnicity())
                .addValue("as_of_date_general", person.getAsOfDateGeneral())
                .addValue("as_of_date_morbidity", person.getAsOfDateMorbidity())
                .addValue("as_of_date_sex", person.getAsOfDateSex())
                .addValue("electronic_ind", person.getElectronicInd())
                .addValue("person_parent_uid", person.getPersonParentUid())
                .addValue("dedup_match_ind", person.getDedupMatchInd())
                .addValue("group_nbr", person.getGroupNbr())
                .addValue("group_time", person.getGroupTime())
                .addValue("edx_ind", person.getEdxInd())
                .addValue("speaks_english_cd", person.getSpeaksEnglishCd())
                .addValue("additional_gender_cd", person.getAdditionalGenderCd())
                .addValue("ehars_id", person.getEharsId())
                .addValue("ethnic_unk_reason_cd", person.getEthnicUnkReasonCd())
                .addValue("sex_unk_reason_cd", person.getSexUnkReasonCd())
        );
    }

    public Long findMprUid(Long parentUid) {
        String sql = "SELECT person_parent_uid FROM Person WHERE person_uid = :person_uid AND record_status_cd='ACTIVE'";
        MapSqlParameterSource params = new MapSqlParameterSource("person_uid", parentUid);
        return jdbcTemplateOdse.queryForObject(sql, params, Long.class);
    }

    public List<Person> findPersonsByParentUid(Long parentUid) {
//        String sql = "SELECT * FROM Person WHERE person_parent_uid = :person_parent_uid";
        MapSqlParameterSource params = new MapSqlParameterSource("person_parent_uid", parentUid);

        return jdbcTemplateOdse.query(SELECT_PERSON_BY_PARENT_UID, params, new BeanPropertyRowMapper<>(Person.class));
    }


    public Person findByPersonUid(Long personUid) {
//        String sql = "SELECT * FROM Person WHERE person_uid = :person_uid";
        MapSqlParameterSource params = new MapSqlParameterSource("person_uid", personUid);

        return jdbcTemplateOdse.queryForObject(SELECT_PERSON_BY_PERSON_UID, params, new BeanPropertyRowMapper<>(Person.class));
    }

    public void createPersonName(PersonName personName) {
        jdbcTemplateOdse.update(INSERT_SQL_PERSON_NAME, new MapSqlParameterSource()
                .addValue("person_uid", personName.getPersonUid())
                .addValue("person_name_seq", personName.getPersonNameSeq())
                .addValue("add_reason_cd", personName.getAddReasonCd())
                .addValue("add_time", personName.getAddTime())
                .addValue("add_user_id", personName.getAddUserId())
                .addValue("default_nm_ind", personName.getDefaultNmInd())
                .addValue("duration_amt", personName.getDurationAmt())
                .addValue("duration_unit_cd", personName.getDurationUnitCd())
                .addValue("first_nm", personName.getFirstNm())
                .addValue("first_nm_sndx", personName.getFirstNmSndx())
                .addValue("from_time", personName.getFromTime())
                .addValue("last_chg_reason_cd", personName.getLastChgReasonCd())
                .addValue("last_chg_time", personName.getLastChgTime())
                .addValue("last_chg_user_id", personName.getLastChgUserId())
                .addValue("last_nm", personName.getLastNm())
                .addValue("last_nm_sndx", personName.getLastNmSndx())
                .addValue("last_nm2", personName.getLastNm2())
                .addValue("last_nm2_sndx", personName.getLastNm2Sndx())
                .addValue("middle_nm", personName.getMiddleNm())
                .addValue("middle_nm2", personName.getMiddleNm2())
                .addValue("nm_degree", personName.getNmDegree())
                .addValue("nm_prefix", personName.getNmPrefix())
                .addValue("nm_suffix", personName.getNmSuffix())
                .addValue("nm_use_cd", personName.getNmUseCd())
                .addValue("record_status_cd", personName.getRecordStatusCd())
                .addValue("record_status_time", personName.getRecordStatusTime())
                .addValue("status_cd", personName.getStatusCd())
                .addValue("status_time", personName.getStatusTime())
                .addValue("to_time", personName.getToTime())
                .addValue("user_affiliation_txt", personName.getUserAffiliationTxt())
                .addValue("as_of_date", personName.getAsOfDate())
        );
    }

    public List<PersonName> findPersonNameByPersonUid(Long personUid) {
//        String sql = "SELECT * FROM Person_name WHERE person_uid = :person_uid";
        MapSqlParameterSource params = new MapSqlParameterSource("person_uid", personUid);

        return jdbcTemplateOdse.query(SELECT_PERSON_NAME_BY_PERSON_UID, params, new BeanPropertyRowMapper<>(PersonName.class));
    }

    public void createPersonRace(PersonRace personRace) {
        jdbcTemplateOdse.update(INSERT_SQL_PERSON_RACE, new MapSqlParameterSource()
                .addValue("person_uid", personRace.getPersonUid())
                .addValue("race_cd", personRace.getRaceCd())
                .addValue("add_reason_cd", personRace.getAddReasonCd())
                .addValue("add_time", personRace.getAddTime())
                .addValue("add_user_id", personRace.getAddUserId())
                .addValue("last_chg_reason_cd", personRace.getLastChgReasonCd())
                .addValue("last_chg_time", personRace.getLastChgTime())
                .addValue("last_chg_user_id", personRace.getLastChgUserId())
                .addValue("race_category_cd", personRace.getRaceCategoryCd())
                .addValue("race_desc_txt", personRace.getRaceDescTxt())
                .addValue("record_status_cd", personRace.getRecordStatusCd())
                .addValue("record_status_time", personRace.getRecordStatusTime())
                .addValue("user_affiliation_txt", personRace.getUserAffiliationTxt())
                .addValue("as_of_date", personRace.getAsOfDate())
        );
    }


    public List<PersonRace> findPersonRaceByPersonUid(Long personUid) {
//        String sql = "SELECT * FROM Person_race WHERE person_uid = :person_uid";
        MapSqlParameterSource params = new MapSqlParameterSource("person_uid", personUid);

        return jdbcTemplateOdse.query(SELECT_PERSON_RACE_BY_PERSON_UID, params, new BeanPropertyRowMapper<>(PersonRace.class));
    }


    public void createPersonEthnicGroup(PersonEthnicGroup personEthnicGroup) {
        jdbcTemplateOdse.update(INSERT_SQL_PERSON_ETHNIC, new MapSqlParameterSource()
                .addValue("person_uid", personEthnicGroup.getPersonUid())
                .addValue("ethnic_group_cd", personEthnicGroup.getEthnicGroupCd())
                .addValue("add_reason_cd", personEthnicGroup.getAddReasonCd())
                .addValue("add_time", personEthnicGroup.getAddTime())
                .addValue("add_user_id", personEthnicGroup.getAddUserId())
                .addValue("ethnic_group_desc_txt", personEthnicGroup.getEthnicGroupDescTxt())
                .addValue("last_chg_reason_cd", personEthnicGroup.getLastChgReasonCd())
                .addValue("last_chg_time", personEthnicGroup.getLastChgTime())
                .addValue("last_chg_user_id", personEthnicGroup.getLastChgUserId())
                .addValue("record_status_cd", personEthnicGroup.getRecordStatusCd())
                .addValue("record_status_time", personEthnicGroup.getRecordStatusTime())
                .addValue("user_affiliation_txt", personEthnicGroup.getUserAffiliationTxt())
        );
    }

    public List<PersonEthnicGroup> findPersonEthnicByPersonUid(Long personUid) {
//        String sql = "SELECT * FROM Person_ethnic_group WHERE person_uid = :person_uid";
        MapSqlParameterSource params = new MapSqlParameterSource("person_uid", personUid);

        return jdbcTemplateOdse.query(SELECT_PERSON_ETHNIC_BY_PERSON_UID, params, new BeanPropertyRowMapper<>(PersonEthnicGroup.class));
    }


    public void updatePerson(Person person) {

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("version_ctrl_nbr", person.getVersionCtrlNbr());
        params.addValue("add_reason_cd", person.getAddReasonCd());
        params.addValue("add_time", person.getAddTime());
        params.addValue("add_user_id", person.getAddUserId());
        params.addValue("administrative_gender_cd", person.getAdministrativeGenderCd());
        params.addValue("age_calc", person.getAgeCalc());
        params.addValue("age_calc_time", person.getAgeCalcTime());
        params.addValue("age_calc_unit_cd", person.getAgeCalcUnitCd());
        params.addValue("age_category_cd", person.getAgeCategoryCd());
        params.addValue("age_reported", person.getAgeReported());
        params.addValue("age_reported_time", person.getAgeReportedTime());
        params.addValue("age_reported_unit_cd", person.getAgeReportedUnitCd());
        params.addValue("birth_gender_cd", person.getBirthGenderCd());
        params.addValue("birth_order_nbr", person.getBirthOrderNbr());
        params.addValue("birth_time", person.getBirthTime());
        params.addValue("birth_time_calc", person.getBirthTimeCalc());
        params.addValue("cd", person.getCd());
        params.addValue("cd_desc_txt", person.getCdDescTxt());
        params.addValue("curr_sex_cd", person.getCurrSexCd());
        params.addValue("deceased_ind_cd", person.getDeceasedIndCd());
        params.addValue("deceased_time", person.getDeceasedTime());
        params.addValue("description", person.getDescription());
        params.addValue("education_level_cd", person.getEducationLevelCd());
        params.addValue("education_level_desc_txt", person.getEducationLevelDescTxt());
        params.addValue("ethnic_group_ind", person.getEthnicGroupInd());
        params.addValue("last_chg_reason_cd", person.getLastChgReasonCd());
        params.addValue("last_chg_time", person.getLastChgTime());
        params.addValue("last_chg_user_id", person.getLastChgUserId());
        params.addValue("local_id", person.getLocalId());
        params.addValue("marital_status_cd", person.getMaritalStatusCd());
        params.addValue("marital_status_desc_txt", person.getMaritalStatusDescTxt());
        params.addValue("mothers_maiden_nm", person.getMothersMaidenNm());
        params.addValue("multiple_birth_ind", person.getMultipleBirthInd());
        params.addValue("occupation_cd", person.getOccupationCd());
        params.addValue("preferred_gender_cd", person.getPreferredGenderCd());
        params.addValue("prim_lang_cd", person.getPrimLangCd());
        params.addValue("prim_lang_desc_txt", person.getPrimLangDescTxt());
        params.addValue("record_status_cd", person.getRecordStatusCd());
        params.addValue("record_status_time", person.getRecordStatusTime());
        params.addValue("status_cd", person.getStatusCd());
        params.addValue("status_time", person.getStatusTime());
        params.addValue("survived_ind_cd", person.getSurvivedIndCd());
        params.addValue("user_affiliation_txt", person.getUserAffiliationTxt());
        params.addValue("first_nm", person.getFirstNm());
        params.addValue("last_nm", person.getLastNm());
        params.addValue("middle_nm", person.getMiddleNm());
        params.addValue("nm_prefix", person.getNmPrefix());
        params.addValue("nm_suffix", person.getNmSuffix());
        params.addValue("preferred_nm", person.getPreferredNm());
        params.addValue("hm_street_addr1", person.getHmStreetAddr1());
        params.addValue("hm_street_addr2", person.getHmStreetAddr2());
        params.addValue("hm_city_cd", person.getHmCityCd());
        params.addValue("hm_city_desc_txt", person.getHmCityDescTxt());
        params.addValue("hm_state_cd", person.getHmStateCd());
        params.addValue("hm_zip_cd", person.getHmZipCd());
        params.addValue("hm_cnty_cd", person.getHmCntyCd());
        params.addValue("hm_cntry_cd", person.getHmCntryCd());
        params.addValue("hm_phone_nbr", person.getHmPhoneNbr());
        params.addValue("hm_phone_cntry_cd", person.getHmPhoneCntryCd());
        params.addValue("hm_email_addr", person.getHmEmailAddr());
        params.addValue("cell_phone_nbr", person.getCellPhoneNbr());
        params.addValue("wk_street_addr1", person.getWkStreetAddr1());
        params.addValue("wk_street_addr2", person.getWkStreetAddr2());
        params.addValue("wk_city_cd", person.getWkCityCd());
        params.addValue("wk_city_desc_txt", person.getWkCityDescTxt());
        params.addValue("wk_state_cd", person.getWkStateCd());
        params.addValue("wk_zip_cd", person.getWkZipCd());
        params.addValue("wk_cnty_cd", person.getWkCntyCd());
        params.addValue("wk_cntry_cd", person.getWkCntryCd());
        params.addValue("wk_phone_nbr", person.getWkPhoneNbr());
        params.addValue("wk_phone_cntry_cd", person.getWkPhoneCntryCd());
        params.addValue("wk_email_addr", person.getWkEmailAddr());
        params.addValue("ssn", person.getSsn());
        params.addValue("medicaid_num", person.getMedicaidNum());
        params.addValue("dl_num", person.getDlNum());
        params.addValue("dl_state_cd", person.getDlStateCd());
        params.addValue("race_cd", person.getRaceCd());
        params.addValue("race_seq_nbr", person.getRaceSeqNbr());
        params.addValue("race_category_cd", person.getRaceCategoryCd());
        params.addValue("ethnicity_group_cd", person.getEthnicityGroupCd());
        params.addValue("ethnic_group_seq_nbr", person.getEthnicGroupSeqNbr());
        params.addValue("adults_in_house_nbr", person.getAdultsInHouseNbr());
        params.addValue("children_in_house_nbr", person.getChildrenInHouseNbr());
        params.addValue("birth_city_cd", person.getBirthCityCd());
        params.addValue("birth_city_desc_txt", person.getBirthCityDescTxt());
        params.addValue("birth_cntry_cd", person.getBirthCntryCd());
        params.addValue("birth_state_cd", person.getBirthStateCd());
        params.addValue("race_desc_txt", person.getRaceDescTxt());
        params.addValue("ethnic_group_desc_txt", person.getEthnicGroupDescTxt());
        params.addValue("as_of_date_admin", person.getAsOfDateAdmin());
        params.addValue("as_of_date_ethnicity", person.getAsOfDateEthnicity());
        params.addValue("as_of_date_general", person.getAsOfDateGeneral());
        params.addValue("as_of_date_morbidity", person.getAsOfDateMorbidity());
        params.addValue("as_of_date_sex", person.getAsOfDateSex());
        params.addValue("electronic_ind", person.getElectronicInd());
        params.addValue("person_parent_uid", person.getPersonParentUid());
        params.addValue("dedup_match_ind", person.getDedupMatchInd());
        params.addValue("group_nbr", person.getGroupNbr());
        params.addValue("group_time", person.getGroupTime());
        params.addValue("edx_ind", person.getEdxInd());
        params.addValue("speaks_english_cd", person.getSpeaksEnglishCd());
        params.addValue("additional_gender_cd", person.getAdditionalGenderCd());
        params.addValue("ehars_id", person.getEharsId());
        params.addValue("ethnic_unk_reason_cd", person.getEthnicUnkReasonCd());
        params.addValue("sex_unk_reason_cd", person.getSexUnkReasonCd());
        params.addValue("person_uid", person.getPersonUid());

        jdbcTemplateOdse.update(UPDATE_PERSON, params);
    }

    public Person selectByPersonUid(Long personUid) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("personUid", personUid);

        return jdbcTemplateOdse.queryForObject(
                SELECT_BY_UID,
                params,
                new BeanPropertyRowMapper<>(Person.class)
        );
    }

    public void mergePersonName(PersonName personName) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("personUid", personName.getPersonUid())
                .addValue("personNameSeq", personName.getPersonNameSeq())
                .addValue("addReasonCd", personName.getAddReasonCd())
                .addValue("addTime", personName.getAddTime())
                .addValue("addUserId", personName.getAddUserId())
                .addValue("defaultNmInd", personName.getDefaultNmInd())
                .addValue("durationAmt", personName.getDurationAmt())
                .addValue("durationUnitCd", personName.getDurationUnitCd())
                .addValue("firstNm", personName.getFirstNm())
                .addValue("firstNmSndx", personName.getFirstNmSndx())
                .addValue("fromTime", personName.getFromTime())
                .addValue("lastChgReasonCd", personName.getLastChgReasonCd())
                .addValue("lastChgTime", personName.getLastChgTime())
                .addValue("lastChgUserId", personName.getLastChgUserId())
                .addValue("lastNm", personName.getLastNm())
                .addValue("lastNmSndx", personName.getLastNmSndx())
                .addValue("lastNm2", personName.getLastNm2())
                .addValue("lastNm2Sndx", personName.getLastNm2Sndx())
                .addValue("middleNm", personName.getMiddleNm())
                .addValue("middleNm2", personName.getMiddleNm2())
                .addValue("nmDegree", personName.getNmDegree())
                .addValue("nmPrefix", personName.getNmPrefix())
                .addValue("nmSuffix", personName.getNmSuffix())
                .addValue("nmUseCd", personName.getNmUseCd())
                .addValue("recordStatusCd", personName.getRecordStatusCd())
                .addValue("recordStatusTime", personName.getRecordStatusTime())
                .addValue("statusCd", personName.getStatusCd())
                .addValue("statusTime", personName.getStatusTime())
                .addValue("toTime", personName.getToTime())
                .addValue("userAffiliationTxt", personName.getUserAffiliationTxt())
                .addValue("asOfDate", personName.getAsOfDate());

        jdbcTemplateOdse.update(MERGE_PERSON_NAME, params);
    }

    public List<PersonName> findBySeqIdByParentUid(Long parentUid) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("parentUid", parentUid);

        return jdbcTemplateOdse.query(
                SELECT_BY_PERSON_UID_ORDER_BY_SEQ_DESC,
                params,
                new BeanPropertyRowMapper<>(PersonName.class)
        );
    }

    public void mergePersonRace(PersonRace personRace) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("personUid", personRace.getPersonUid())
                .addValue("raceCd", personRace.getRaceCd())
                .addValue("addReasonCd", personRace.getAddReasonCd())
                .addValue("addTime", personRace.getAddTime())
                .addValue("addUserId", personRace.getAddUserId())
                .addValue("lastChgReasonCd", personRace.getLastChgReasonCd())
                .addValue("lastChgTime", personRace.getLastChgTime())
                .addValue("lastChgUserId", personRace.getLastChgUserId())
                .addValue("raceCategoryCd", personRace.getRaceCategoryCd())
                .addValue("raceDescTxt", personRace.getRaceDescTxt())
                .addValue("recordStatusCd", personRace.getRecordStatusCd())
                .addValue("recordStatusTime", personRace.getRecordStatusTime())
                .addValue("userAffiliationTxt", personRace.getUserAffiliationTxt())
                .addValue("asOfDate", personRace.getAsOfDate());

        jdbcTemplateOdse.update(MERGE_PERSON_RACE, params);
    }


    public List<PersonRace> findByPersonRaceUid(Long parentUid) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("parentUid", parentUid);

        return jdbcTemplateOdse.query(
                FIND_PERSON_RACE_BY_UID,
                params,
                new BeanPropertyRowMapper<>(PersonRace.class)
        );
    }


    public void mergePersonEthnicGroup(PersonEthnicGroup ethnicGroup) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("personUid", ethnicGroup.getPersonUid())
                .addValue("ethnicGroupCd", ethnicGroup.getEthnicGroupCd())
                .addValue("addReasonCd", ethnicGroup.getAddReasonCd())
                .addValue("addTime", ethnicGroup.getAddTime())
                .addValue("addUserId", ethnicGroup.getAddUserId())
                .addValue("ethnicGroupDescTxt", ethnicGroup.getEthnicGroupDescTxt())
                .addValue("lastChgReasonCd", ethnicGroup.getLastChgReasonCd())
                .addValue("lastChgTime", ethnicGroup.getLastChgTime())
                .addValue("lastChgUserId", ethnicGroup.getLastChgUserId())
                .addValue("recordStatusCd", ethnicGroup.getRecordStatusCd())
                .addValue("recordStatusTime", ethnicGroup.getRecordStatusTime())
                .addValue("userAffiliationTxt", ethnicGroup.getUserAffiliationTxt());

        jdbcTemplateOdse.update(MERGE_PERSON_ETHNIC_GROUP, params);
    }


}
