package gov.cdc.dataprocessing.utilities.component.patient;

import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.EntityId;
import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.EntityODSE;
import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.Role;
import gov.cdc.dataprocessing.repository.nbs.odse.model.person.Person;
import gov.cdc.dataprocessing.repository.nbs.odse.model.person.PersonEthnicGroup;
import gov.cdc.dataprocessing.repository.nbs.odse.model.person.PersonName;
import gov.cdc.dataprocessing.repository.nbs.odse.model.person.PersonRace;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component
public class PatientRepositoryUtilJdbc {
    private final JdbcTemplate jdbcTemplateOdse;
    private static final String FIND_PERSON_BY_ID = """
        SELECT 
            person_uid, version_ctrl_nbr, add_reason_cd, add_time, add_user_id, administrative_gender_cd, 
            age_calc, age_calc_time, age_calc_unit_cd, age_category_cd, age_reported, age_reported_time, 
            age_reported_unit_cd, birth_gender_cd, birth_order_nbr, birth_time, birth_time_calc, cd, 
            cd_desc_txt, curr_sex_cd, deceased_ind_cd, deceased_time, description, education_level_cd, 
            education_level_desc_txt, ethnic_group_ind, last_chg_reason_cd, last_chg_time, last_chg_user_id, 
            local_id, marital_status_cd, marital_status_desc_txt, mothers_maiden_nm, multiple_birth_ind, 
            occupation_cd, preferred_gender_cd, prim_lang_cd, prim_lang_desc_txt, record_status_cd, 
            record_status_time, status_cd, status_time, survived_ind_cd, user_affiliation_txt, first_nm, 
            last_nm, middle_nm, nm_prefix, nm_suffix, preferred_nm, hm_street_addr1, hm_street_addr2, 
            hm_city_cd, hm_city_desc_txt, hm_state_cd, hm_zip_cd, hm_cnty_cd, hm_cntry_cd, hm_phone_nbr, 
            hm_phone_cntry_cd, hm_email_addr, cell_phone_nbr, wk_street_addr1, wk_street_addr2, wk_city_cd, 
            wk_city_desc_txt, wk_state_cd, wk_zip_cd, wk_cnty_cd, wk_cntry_cd, wk_phone_nbr, wk_phone_cntry_cd, 
            wk_email_addr, ssn, medicaid_num, dl_num, dl_state_cd, race_cd, race_seq_nbr, race_category_cd, 
            ethnicity_group_cd, ethnic_group_seq_nbr, adults_in_house_nbr, children_in_house_nbr, birth_city_cd, 
            birth_city_desc_txt, birth_cntry_cd, birth_state_cd, race_desc_txt, ethnic_group_desc_txt, 
            as_of_date_admin, as_of_date_ethnicity, as_of_date_general, as_of_date_morbidity, as_of_date_sex, 
            electronic_ind, person_parent_uid, dedup_match_ind, group_nbr, group_time, edx_ind, speaks_english_cd, 
            additional_gender_cd, ehars_id, ethnic_unk_reason_cd, sex_unk_reason_cd
        FROM Person
        WHERE person_uid = ?
    """;

    private static final String FIND_PERSON_NAMES_BY_PERSON_UID = """
        SELECT 
            person_uid, person_name_seq, add_reason_cd, add_time, add_user_id, default_nm_ind, duration_amt, 
            duration_unit_cd, first_nm, first_nm_sndx, from_time, last_chg_reason_cd, last_chg_time, 
            last_chg_user_id, last_nm, last_nm_sndx, last_nm2, last_nm2_sndx, middle_nm, middle_nm2, nm_degree, 
            nm_prefix, nm_suffix, nm_use_cd, record_status_cd, record_status_time, status_cd, status_time, 
            to_time, user_affiliation_txt, as_of_date
        FROM Person_name
        WHERE person_uid = ?
    """;

    private static final String FIND_PERSON_RACES_BY_PERSON_UID = """
        SELECT 
            person_uid, race_cd, add_reason_cd, add_time, add_user_id, last_chg_reason_cd, last_chg_time, 
            last_chg_user_id, race_category_cd, race_desc_txt, record_status_cd, record_status_time, 
            user_affiliation_txt, as_of_date
        FROM Person_race
        WHERE person_uid = ?
    """;

    private static final String FIND_PERSON_ETHNIC_GROUPS_BY_PERSON_UID = """
        SELECT 
            person_uid, ethnic_group_cd, add_reason_cd, add_time, add_user_id, ethnic_group_desc_txt, 
            last_chg_reason_cd, last_chg_time, last_chg_user_id, record_status_cd, record_status_time, 
            user_affiliation_txt
        FROM Person_ethnic_group
        WHERE person_uid = ?
    """;

    public PatientRepositoryUtilJdbc(@Qualifier("odseJdbcTemplate") JdbcTemplate jdbcTemplateOdse) {
        this.jdbcTemplateOdse = jdbcTemplateOdse;
    }

    public void insertEntity(EntityODSE entity) {
        String insertEntitySql = "INSERT INTO Entity (entity_uid, class_cd) VALUES (?, ?)";
        jdbcTemplateOdse.update(insertEntitySql, entity.getEntityUid(), entity.getClassCd());

        if (entity.getRoles() != null) {
            String insertRoleSql = """
                INSERT INTO Role (
                    subject_entity_uid, cd, role_seq, add_reason_cd, add_time, add_user_id,
                    cd_desc_txt, effective_duration_amt, effective_duration_unit_cd, effective_from_time, effective_to_time,
                    last_chg_reason_cd, last_chg_time, last_chg_user_id, record_status_cd, record_status_time,
                    scoping_class_cd, scoping_entity_uid, scoping_role_cd, scoping_role_seq, status_cd, status_time,
                    subject_class_cd, user_affiliation_txt
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
            for (Role role : entity.getRoles()) {
                jdbcTemplateOdse.update(insertRoleSql,
                        role.getSubjectEntityUid(),
                        role.getCode(),
                        role.getRoleSeq(),
                        role.getAddReasonCode(),
                        role.getAddTime(),
                        role.getAddUserId(),
                        role.getCodeDescription(),
                        role.getEffectiveDurationAmount(),
                        role.getEffectiveDurationUnitCode(),
                        role.getEffectiveFromTime(),
                        role.getEffectiveToTime(),
                        role.getLastChangeReasonCode(),
                        role.getLastChangeTime(),
                        role.getLastChangeUserId(),
                        role.getRecordStatusCode(),
                        role.getRecordStatusTime(),
                        role.getScopingClassCode(),
                        role.getScopingEntityUid(),
                        role.getScopingRoleCode(),
                        role.getScopingRoleSeq(),
                        role.getStatusCode(),
                        role.getStatusTime(),
                        role.getSubjectClassCode(),
                        role.getUserAffiliationText()
                );
            }
        }

        if (entity.getEntityIds() != null) {
            String insertEntityIdSql = """
                INSERT INTO Entity_id (
                    entity_uid, entity_id_seq, add_reason_cd, add_time, add_user_id, assigning_authority_cd,
                    assigning_authority_desc_txt, duration_amt, duration_unit_cd, effective_from_time, effective_to_time,
                    last_chg_reason_cd, last_chg_time, last_chg_user_id, record_status_cd, record_status_time,
                    root_extension_txt, status_cd, status_time, type_cd, type_desc_txt, user_affiliation_txt,
                    valid_from_time, valid_to_time, as_of_date, assigning_authority_id_type
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
            for (EntityId entityId : entity.getEntityIds()) {
                jdbcTemplateOdse.update(insertEntityIdSql,
                        entityId.getEntityUid(), entityId.getEntityIdSeq(), entityId.getAddReasonCode(), entityId.getAddTime(), entityId.getAddUserId(), entityId.getAssigningAuthorityCode(),
                        entityId.getAssigningAuthorityDescription(), entityId.getDurationAmount(), entityId.getDurationUnitCode(), entityId.getEffectiveFromTime(), entityId.getEffectiveToTime(),
                        entityId.getLastChangeReasonCode(), entityId.getLastChangeTime(), entityId.getLastChangeUserId(), entityId.getRecordStatusCode(), entityId.getRecordStatusTime(),
                        entityId.getRootExtensionText(), entityId.getStatusCode(), entityId.getStatusTime(), entityId.getTypeCode(), entityId.getTypeDescriptionText(), entityId.getUserAffiliationText(),
                        entityId.getValidFromTime(), entityId.getValidToTime(), entityId.getAsOfDate(), entityId.getAssigningAuthorityIdType()
                );
            }
        }
    }


    public void savePersonWithDetails(Person person) {
        String personInsertSql = """
                    INSERT INTO Person (
                        person_uid, version_ctrl_nbr, add_reason_cd, add_time, add_user_id, administrative_gender_cd,
                        age_calc, age_calc_time, age_calc_unit_cd, age_category_cd, age_reported, age_reported_time,
                        age_reported_unit_cd, birth_gender_cd, birth_order_nbr, birth_time, birth_time_calc, cd,
                        cd_desc_txt, curr_sex_cd, deceased_ind_cd, deceased_time, description, education_level_cd,
                        education_level_desc_txt, ethnic_group_ind, last_chg_reason_cd, last_chg_time, last_chg_user_id,
                        local_id, marital_status_cd, marital_status_desc_txt, mothers_maiden_nm, multiple_birth_ind,
                        occupation_cd, preferred_gender_cd, prim_lang_cd, prim_lang_desc_txt, record_status_cd,
                        record_status_time, status_cd, status_time, survived_ind_cd, user_affiliation_txt, first_nm,
                        last_nm, middle_nm, nm_prefix, nm_suffix, preferred_nm, hm_street_addr1, hm_street_addr2,
                        hm_city_cd, hm_city_desc_txt, hm_state_cd, hm_zip_cd, hm_cnty_cd, hm_cntry_cd, hm_phone_nbr,
                        hm_phone_cntry_cd, hm_email_addr, cell_phone_nbr, wk_street_addr1, wk_street_addr2, wk_city_cd,
                        wk_city_desc_txt, wk_state_cd, wk_zip_cd, wk_cnty_cd, wk_cntry_cd, wk_phone_nbr, wk_phone_cntry_cd,
                        wk_email_addr, SSN, medicaid_num, dl_num, dl_state_cd, race_cd, race_seq_nbr, race_category_cd,
                        ethnicity_group_cd, ethnic_group_seq_nbr, adults_in_house_nbr, children_in_house_nbr, birth_city_cd,
                        birth_city_desc_txt, birth_cntry_cd, birth_state_cd, race_desc_txt, ethnic_group_desc_txt,
                        as_of_date_admin, as_of_date_ethnicity, as_of_date_general, as_of_date_morbidity, as_of_date_sex,
                        electronic_ind, person_parent_uid, dedup_match_ind, group_nbr, group_time, edx_ind, speaks_english_cd,
                        additional_gender_cd, ehars_id, ethnic_unk_reason_cd, sex_unk_reason_cd
                    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        jdbcTemplateOdse.update(personInsertSql,
                person.getPersonUid(), person.getVersionCtrlNbr(), person.getAddReasonCd(), person.getAddTime(),
                person.getAddUserId(), person.getAdministrativeGenderCd(), person.getAgeCalc(), person.getAgeCalcTime(),
                person.getAgeCalcUnitCd(), person.getAgeCategoryCd(), person.getAgeReported(), person.getAgeReportedTime(),
                person.getAgeReportedUnitCd(), person.getBirthGenderCd(), person.getBirthOrderNbr(), person.getBirthTime(),
                person.getBirthTimeCalc(), person.getCd(), person.getCdDescTxt(), person.getCurrSexCd(),
                person.getDeceasedIndCd(), person.getDeceasedTime(), person.getDescription(), person.getEducationLevelCd(),
                person.getEducationLevelDescTxt(), person.getEthnicGroupInd(), person.getLastChgReasonCd(),
                person.getLastChgTime(), person.getLastChgUserId(), person.getLocalId(), person.getMaritalStatusCd(),
                person.getMaritalStatusDescTxt(), person.getMothersMaidenNm(), person.getMultipleBirthInd(),
                person.getOccupationCd(), person.getPreferredGenderCd(), person.getPrimLangCd(),
                person.getPrimLangDescTxt(), person.getRecordStatusCd(), person.getRecordStatusTime(),
                person.getStatusCd(), person.getStatusTime(), person.getSurvivedIndCd(), person.getUserAffiliationTxt(),
                person.getFirstNm(), person.getLastNm(), person.getMiddleNm(), person.getNmPrefix(), person.getNmSuffix(),
                person.getPreferredNm(), person.getHmStreetAddr1(), person.getHmStreetAddr2(), person.getHmCityCd(),
                person.getHmCityDescTxt(), person.getHmStateCd(), person.getHmZipCd(), person.getHmCntyCd(),
                person.getHmCntryCd(), person.getHmPhoneNbr(), person.getHmPhoneCntryCd(), person.getHmEmailAddr(),
                person.getCellPhoneNbr(), person.getWkStreetAddr1(), person.getWkStreetAddr2(), person.getWkCityCd(),
                person.getWkCityDescTxt(), person.getWkStateCd(), person.getWkZipCd(), person.getWkCntyCd(),
                person.getWkCntryCd(), person.getWkPhoneNbr(), person.getWkPhoneCntryCd(), person.getWkEmailAddr(),
                person.getSsn(), person.getMedicaidNum(), person.getDlNum(), person.getDlStateCd(), person.getRaceCd(),
                person.getRaceSeqNbr(), person.getRaceCategoryCd(), person.getEthnicityGroupCd(), person.getEthnicGroupSeqNbr(),
                person.getAdultsInHouseNbr(), person.getChildrenInHouseNbr(), person.getBirthCityCd(),
                person.getBirthCityDescTxt(), person.getBirthCntryCd(), person.getBirthStateCd(), person.getRaceDescTxt(),
                person.getEthnicGroupDescTxt(), person.getAsOfDateAdmin(), person.getAsOfDateEthnicity(),
                person.getAsOfDateGeneral(), person.getAsOfDateMorbidity(), person.getAsOfDateSex(),
                person.getElectronicInd(), person.getPersonParentUid(), person.getDedupMatchInd(), person.getGroupNbr(),
                person.getGroupTime(), person.getEdxInd(), person.getSpeaksEnglishCd(), person.getAdditionalGenderCd(),
                person.getEharsId(), person.getEthnicUnkReasonCd(), person.getSexUnkReasonCd()
        );

        if (person.getPersonNames() != null) {
            String personNameInsertSql = """
                        INSERT INTO Person_name (
                            person_uid, person_name_seq, add_reason_cd, add_time, add_user_id, default_nm_ind,
                            duration_amt, duration_unit_cd, first_nm, first_nm_sndx, from_time, last_chg_reason_cd,
                            last_chg_time, last_chg_user_id, last_nm, last_nm_sndx, last_nm2, last_nm2_sndx,
                            middle_nm, middle_nm2, nm_degree, nm_prefix, nm_suffix, nm_use_cd, record_status_cd,
                            record_status_time, status_cd, status_time, to_time, user_affiliation_txt, as_of_date
                        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """;

            for (PersonName name : person.getPersonNames()) {
                jdbcTemplateOdse.update(personNameInsertSql,
                        name.getPersonUid(), name.getPersonNameSeq(), name.getAddReasonCd(), name.getAddTime(),
                        name.getAddUserId(), name.getDefaultNmInd(), name.getDurationAmt(), name.getDurationUnitCd(),
                        name.getFirstNm(), name.getFirstNmSndx(), name.getFromTime(), name.getLastChgReasonCd(),
                        name.getLastChgTime(), name.getLastChgUserId(), name.getLastNm(), name.getLastNmSndx(),
                        name.getLastNm2(), name.getLastNm2Sndx(), name.getMiddleNm(), name.getMiddleNm2(), name.getNmDegree(),
                        name.getNmPrefix(), name.getNmSuffix(), name.getNmUseCd(), name.getRecordStatusCd(),
                        name.getRecordStatusTime(), name.getStatusCd(), name.getStatusTime(), name.getToTime(),
                        name.getUserAffiliationTxt(), name.getAsOfDate()
                );
            }
        }

        if (person.getPersonRaces() != null) {
            String personRaceInsertSql = """
                        INSERT INTO Person_race (
                            person_uid, race_cd, add_reason_cd, add_time, add_user_id, last_chg_reason_cd,
                            last_chg_time, last_chg_user_id, race_category_cd, race_desc_txt, record_status_cd,
                            record_status_time, user_affiliation_txt, as_of_date
                        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """;

            for (PersonRace race : person.getPersonRaces()) {
                jdbcTemplateOdse.update(personRaceInsertSql,
                        race.getPersonUid(), race.getRaceCd(), race.getAddReasonCd(), race.getAddTime(),
                        race.getAddUserId(), race.getLastChgReasonCd(), race.getLastChgTime(), race.getLastChgUserId(),
                        race.getRaceCategoryCd(), race.getRaceDescTxt(), race.getRecordStatusCd(),
                        race.getRecordStatusTime(), race.getUserAffiliationTxt(), race.getAsOfDate()
                );
            }
        }

        if (person.getPersonEthnicGroups() != null) {
            String personEthnicGroupInsertSql = """
                        INSERT INTO Person_ethnic_group (
                            person_uid, ethnic_group_cd, add_reason_cd, add_time, add_user_id, ethnic_group_desc_txt,
                            last_chg_reason_cd, last_chg_time, last_chg_user_id, record_status_cd, record_status_time,
                            user_affiliation_txt
                        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """;

            for (PersonEthnicGroup ethnicGroup : person.getPersonEthnicGroups()) {
                jdbcTemplateOdse.update(personEthnicGroupInsertSql,
                        ethnicGroup.getPersonUid(), ethnicGroup.getEthnicGroupCd(), ethnicGroup.getAddReasonCd(),
                        ethnicGroup.getAddTime(), ethnicGroup.getAddUserId(), ethnicGroup.getEthnicGroupDescTxt(),
                        ethnicGroup.getLastChgReasonCd(), ethnicGroup.getLastChgTime(), ethnicGroup.getLastChgUserId(),
                        ethnicGroup.getRecordStatusCd(), ethnicGroup.getRecordStatusTime(), ethnicGroup.getUserAffiliationTxt()
                );
            }
        }
    }

    public Optional<Person> findById(Long personUid) {
        return jdbcTemplateOdse.query(
                FIND_PERSON_BY_ID,
                new Object[]{personUid},
                (ResultSet rs) -> {
                    if (rs.next()) {
                        Person person = mapRowToPerson(rs);
                        person.setPersonNames(findPersonNamesByPersonUid(personUid));
                        person.setPersonRaces(findPersonRacesByPersonUid(personUid));
                        person.setPersonEthnicGroups(findPersonEthnicGroupsByPersonUid(personUid));
                        return Optional.of(person);
                    }
                    return Optional.empty();
                }
        );
    }

    private Person mapRowToPerson(ResultSet rs) throws SQLException {
        Person person = new Person();
        person.setPersonUid(rs.getLong("person_uid"));
        person.setVersionCtrlNbr(rs.getInt("version_ctrl_nbr"));
        person.setPersonParentUid(rs.getLong("person_parent_uid"));
        person.setAddReasonCd(rs.getString("add_reason_cd"));
        person.setAddTime(rs.getTimestamp("add_time"));
        person.setAddUserId(rs.getLong("add_user_id"));
        person.setAdministrativeGenderCd(rs.getString("administrative_gender_cd"));
        person.setAgeCalc(rs.getInt("age_calc"));
        person.setAgeCalcTime(rs.getTimestamp("age_calc_time"));
        person.setAgeCalcUnitCd(rs.getString("age_calc_unit_cd"));
        person.setAgeCategoryCd(rs.getString("age_category_cd"));
        person.setAgeReported(rs.getString("age_reported"));
        person.setAgeReportedTime(rs.getTimestamp("age_reported_time"));
        person.setAgeReportedUnitCd(rs.getString("age_reported_unit_cd"));
        person.setBirthGenderCd(rs.getString("birth_gender_cd"));
        person.setBirthOrderNbr(rs.getInt("birth_order_nbr"));
        person.setBirthTime(rs.getTimestamp("birth_time"));
        person.setBirthTimeCalc(rs.getTimestamp("birth_time_calc"));
        person.setCd(rs.getString("cd"));
        person.setCdDescTxt(rs.getString("cd_desc_txt"));
        person.setCurrSexCd(rs.getString("curr_sex_cd"));
        person.setDeceasedIndCd(rs.getString("deceased_ind_cd"));
        person.setDeceasedTime(rs.getTimestamp("deceased_time"));
        person.setDescription(rs.getString("description"));
        person.setEducationLevelCd(rs.getString("education_level_cd"));
        person.setEducationLevelDescTxt(rs.getString("education_level_desc_txt"));
        person.setEthnicGroupInd(rs.getString("ethnic_group_ind"));
        person.setLastChgReasonCd(rs.getString("last_chg_reason_cd"));
        person.setLastChgTime(rs.getTimestamp("last_chg_time"));
        person.setLastChgUserId(rs.getLong("last_chg_user_id"));
        person.setLocalId(rs.getString("local_id"));
        person.setMaritalStatusCd(rs.getString("marital_status_cd"));
        person.setMaritalStatusDescTxt(rs.getString("marital_status_desc_txt"));
        person.setMothersMaidenNm(rs.getString("mothers_maiden_nm"));
        person.setMultipleBirthInd(rs.getString("multiple_birth_ind"));
        person.setOccupationCd(rs.getString("occupation_cd"));
        person.setPreferredGenderCd(rs.getString("preferred_gender_cd"));
        person.setPrimLangCd(rs.getString("prim_lang_cd"));
        person.setPrimLangDescTxt(rs.getString("prim_lang_desc_txt"));
        person.setRecordStatusCd(rs.getString("record_status_cd"));
        person.setRecordStatusTime(rs.getTimestamp("record_status_time"));
        person.setStatusCd(rs.getString("status_cd"));
        person.setStatusTime(rs.getTimestamp("status_time"));
        person.setSurvivedIndCd(rs.getString("survived_ind_cd"));
        person.setUserAffiliationTxt(rs.getString("user_affiliation_txt"));
        person.setFirstNm(rs.getString("first_nm"));
        person.setLastNm(rs.getString("last_nm"));
        person.setMiddleNm(rs.getString("middle_nm"));
        person.setNmPrefix(rs.getString("nm_prefix"));
        person.setNmSuffix(rs.getString("nm_suffix"));
        person.setPreferredNm(rs.getString("preferred_nm"));
        return person;
    }

    private List<PersonName> findPersonNamesByPersonUid(Long personUid) {
        return jdbcTemplateOdse.query(
                FIND_PERSON_NAMES_BY_PERSON_UID,
                new Object[]{personUid},
                (ResultSet rs, int rowNum) -> {
                    PersonName personName = new PersonName();
                    personName.setPersonUid(rs.getLong("person_uid"));
                    personName.setPersonNameSeq(rs.getInt("person_name_seq"));
                    personName.setAddReasonCd(rs.getString("add_reason_cd"));
                    personName.setAddTime(rs.getTimestamp("add_time"));
                    personName.setAddUserId(rs.getLong("add_user_id"));
                    personName.setDefaultNmInd(rs.getString("default_nm_ind"));
                    personName.setDurationAmt(rs.getString("duration_amt"));
                    personName.setDurationUnitCd(rs.getString("duration_unit_cd"));
                    personName.setFirstNm(rs.getString("first_nm"));
                    personName.setFirstNmSndx(rs.getString("first_nm_sndx"));
                    personName.setFromTime(rs.getTimestamp("from_time"));
                    personName.setLastNm(rs.getString("last_nm"));
                    return personName;
                }
        );
    }

    private List<PersonRace> findPersonRacesByPersonUid(Long personUid) {
        return jdbcTemplateOdse.query(
                FIND_PERSON_RACES_BY_PERSON_UID,
                new Object[]{personUid},
                (ResultSet rs, int rowNum) -> {
                    PersonRace personRace = new PersonRace();
                    personRace.setPersonUid(rs.getLong("person_uid"));
                    personRace.setRaceCd(rs.getString("race_cd"));
                    personRace.setAddReasonCd(rs.getString("add_reason_cd"));
                    personRace.setAddTime(rs.getTimestamp("add_time"));
                    personRace.setAddUserId(rs.getLong("add_user_id"));
                    return personRace;
                }
        );
    }

    private List<PersonEthnicGroup> findPersonEthnicGroupsByPersonUid(Long personUid) {
        return jdbcTemplateOdse.query(
                FIND_PERSON_ETHNIC_GROUPS_BY_PERSON_UID,
                new Object[]{personUid},
                (ResultSet rs, int rowNum) -> {
                    PersonEthnicGroup personEthnicGroup = new PersonEthnicGroup();
                    personEthnicGroup.setPersonUid(rs.getLong("person_uid"));
                    personEthnicGroup.setEthnicGroupCd(rs.getString("ethnic_group_cd"));
                    personEthnicGroup.setAddReasonCd(rs.getString("add_reason_cd"));
                    personEthnicGroup.setAddTime(rs.getTimestamp("add_time"));
                    personEthnicGroup.setAddUserId(rs.getLong("add_user_id"));
                    return personEthnicGroup;
                }
        );
    }
}
