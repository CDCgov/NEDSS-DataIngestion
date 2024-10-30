package gov.cdc.dataprocessing.repository.nbs.odse.model.person;

import gov.cdc.dataprocessing.model.dto.person.PersonDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

import static gov.cdc.dataprocessing.utilities.time.TimeStampUtil.getCurrentTimeStamp;

@Entity
@Getter
@Setter
@Table(name = "Person")
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 2139 - exception rethrow complain
 3740 - parametrized  type for generic complaint
 1149 - replacing HashTable complaint
 112 - throwing dedicate exception complaint
 107 - max parameter complaint
 1195 - duplicate complaint
 1135 - Todos complaint
 6201 - instanceof check
 1192 - duplicate literal
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201", "java:S1192"})
public class Person  {

    @Id
    @Column(name = "person_uid")
    private Long personUid;

//    @Version
    @Column(name = "version_ctrl_nbr", nullable = false)
    private Integer versionCtrlNbr;

    @Column(name = "add_reason_cd")
    private String addReasonCd;

    @Column(name = "add_time")
    private Timestamp addTime;

    @Column(name = "add_user_id")
    private Long addUserId;

    @Column(name = "administrative_gender_cd")
    private String administrativeGenderCd;

    @Column(name = "age_calc")
    private Integer ageCalc;

    @Column(name = "age_calc_time")
    private Timestamp ageCalcTime;

    @Column(name = "age_calc_unit_cd")
    private String ageCalcUnitCd;

    @Column(name = "age_category_cd")
    private String ageCategoryCd;

    @Column(name = "age_reported")
    private String ageReported;

    @Column(name = "age_reported_time")
    private Timestamp ageReportedTime;

    @Column(name = "age_reported_unit_cd")
    private String ageReportedUnitCd;

    @Column(name = "birth_gender_cd")
    private String birthGenderCd;

    @Column(name = "birth_order_nbr")
    private Integer birthOrderNbr;

    @Column(name = "birth_time")
    private Timestamp birthTime;

    @Column(name = "birth_time_calc")
    private Timestamp birthTimeCalc;

    @Column(name = "cd")
    private String cd;

    @Column(name = "cd_desc_txt")
    private String cdDescTxt;

    @Column(name = "curr_sex_cd")
    private String currSexCd;

    @Column(name = "deceased_ind_cd")
    private String deceasedIndCd;

    @Column(name = "deceased_time")
    private Timestamp deceasedTime;

    @Column(name = "description")
    private String description;

    @Column(name = "education_level_cd")
    private String educationLevelCd;

    @Column(name = "education_level_desc_txt")
    private String educationLevelDescTxt;

    @Column(name = "ethnic_group_ind")
    private String ethnicGroupInd;

    @Column(name = "last_chg_reason_cd")
    private String lastChgReasonCd;

    @Column(name = "last_chg_time")
    private Timestamp lastChgTime;

    @Column(name = "last_chg_user_id")
    private Long lastChgUserId;

    @Column(name = "local_id")
    private String localId;

    @Column(name = "marital_status_cd")
    private String maritalStatusCd;

    @Column(name = "marital_status_desc_txt")
    private String maritalStatusDescTxt;

    @Column(name = "mothers_maiden_nm")
    private String mothersMaidenNm;

    @Column(name = "multiple_birth_ind")
    private String multipleBirthInd;

    @Column(name = "occupation_cd")
    private String occupationCd;

    @Column(name = "preferred_gender_cd")
    private String preferredGenderCd;

    @Column(name = "prim_lang_cd")
    private String primLangCd;

    @Column(name = "prim_lang_desc_txt")
    private String primLangDescTxt;

    @Column(name = "record_status_cd")
    private String recordStatusCd;

    @Column(name = "record_status_time")
    private Timestamp recordStatusTime;

    @Column(name = "status_cd")
    private String statusCd;

    @Column(name = "status_time")
    private Timestamp statusTime;

    @Column(name = "survived_ind_cd")
    private String survivedIndCd;

    @Column(name = "user_affiliation_txt")
    private String userAffiliationTxt;

    @Column(name = "first_nm")
    private String firstNm;

    @Column(name = "last_nm")
    private String lastNm;

    @Column(name = "middle_nm")
    private String middleNm;

    @Column(name = "nm_prefix")
    private String nmPrefix;

    @Column(name = "nm_suffix")
    private String nmSuffix;

    @Column(name = "preferred_nm")
    private String preferredNm;

    @Column(name = "hm_street_addr1")
    private String hmStreetAddr1;

    @Column(name = "hm_street_addr2")
    private String hmStreetAddr2;

    @Column(name = "hm_city_cd")
    private String hmCityCd;

    @Column(name = "hm_city_desc_txt")
    private String hmCityDescTxt;

    @Column(name = "hm_state_cd")
    private String hmStateCd;

    @Column(name = "hm_zip_cd")
    private String hmZipCd;

    @Column(name = "hm_cnty_cd")
    private String hmCntyCd;

    @Column(name = "hm_cntry_cd")
    private String hmCntryCd;

    @Column(name = "hm_phone_nbr")
    private String hmPhoneNbr;

    @Column(name = "hm_phone_cntry_cd")
    private String hmPhoneCntryCd;

    @Column(name = "hm_email_addr")
    private String hmEmailAddr;

    @Column(name = "cell_phone_nbr")
    private String cellPhoneNbr;

    @Column(name = "wk_street_addr1")
    private String wkStreetAddr1;

    @Column(name = "wk_street_addr2")
    private String wkStreetAddr2;

    @Column(name = "wk_city_cd")
    private String wkCityCd;

    @Column(name = "wk_city_desc_txt")
    private String wkCityDescTxt;

    @Column(name = "wk_state_cd")
    private String wkStateCd;

    @Column(name = "wk_zip_cd")
    private String wkZipCd;

    @Column(name = "wk_cnty_cd")
    private String wkCntyCd;

    @Column(name = "wk_cntry_cd")
    private String wkCntryCd;

    @Column(name = "wk_phone_nbr")
    private String wkPhoneNbr;

    @Column(name = "wk_phone_cntry_cd")
    private String wkPhoneCntryCd;

    @Column(name = "wk_email_addr")
    private String wkEmailAddr;

    @Column(name = "SSN")
    private String ssn;

    @Column(name = "medicaid_num")
    private String medicaidNum;

    @Column(name = "dl_num")
    private String dlNum;

    @Column(name = "dl_state_cd")
    private String dlStateCd;

    @Column(name = "race_cd")
    private String raceCd;

    @Column(name = "race_seq_nbr")
    private Integer raceSeqNbr;

    @Column(name = "race_category_cd")
    private String raceCategoryCd;

    @Column(name = "ethnicity_group_cd")
    private String ethnicityGroupCd;

    @Column(name = "ethnic_group_seq_nbr")
    private Integer ethnicGroupSeqNbr;

    @Column(name = "adults_in_house_nbr")
    private Integer adultsInHouseNbr;

    @Column(name = "children_in_house_nbr")
    private Integer childrenInHouseNbr;

    @Column(name = "birth_city_cd")
    private String birthCityCd;

    @Column(name = "birth_city_desc_txt")
    private String birthCityDescTxt;

    @Column(name = "birth_cntry_cd")
    private String birthCntryCd;

    @Column(name = "birth_state_cd")
    private String birthStateCd;

    @Column(name = "race_desc_txt")
    private String raceDescTxt;

    @Column(name = "ethnic_group_desc_txt")
    private String ethnicGroupDescTxt;

    @Column(name = "as_of_date_admin")
    private Timestamp asOfDateAdmin;

    @Column(name = "as_of_date_ethnicity")
    private Timestamp asOfDateEthnicity;

    @Column(name = "as_of_date_general")
    private Timestamp asOfDateGeneral;

    @Column(name = "as_of_date_morbidity")
    private Timestamp asOfDateMorbidity;

    @Column(name = "as_of_date_sex")
    private Timestamp asOfDateSex;

    @Column(name = "electronic_ind")
    private String electronicInd;

    @Column(name = "person_parent_uid")
    private Long personParentUid;

    @Column(name = "dedup_match_ind")
    private String dedupMatchInd;

    @Column(name = "group_nbr")
    private Integer groupNbr;

    @Column(name = "group_time")
    private Timestamp groupTime;

    @Column(name = "edx_ind")
    private String edxInd;

    @Column(name = "speaks_english_cd")
    private String speaksEnglishCd;

    @Column(name = "additional_gender_cd")
    private String additionalGenderCd;

    @Column(name = "ehars_id")
    private String eharsId;

    @Column(name = "ethnic_unk_reason_cd")
    private String ethnicUnkReasonCd;

    @Column(name = "sex_unk_reason_cd")
    private String sexUnkReasonCd;

    
    // Constructors, getters, and setters
    public Person() {

    }
    public Person(PersonDto personDto) {
        var timeStamp = getCurrentTimeStamp();
        this.personUid = personDto.getPersonUid();
        this.addReasonCd = personDto.getAddReasonCd();
        this.addTime = personDto.getAddTime();
        this.addUserId = personDto.getAddUserId();
        this.administrativeGenderCd = personDto.getAdministrativeGenderCd();
        this.ageCalc = personDto.getAgeCalc();
        this.ageCalcTime = personDto.getAgeCalcTime();
        this.ageCalcUnitCd = personDto.getAgeCalcUnitCd();
        this.ageCategoryCd = personDto.getAgeCategoryCd();
        this.ageReported = personDto.getAgeReported();
        this.ageReportedTime = personDto.getAgeReportedTime();
        this.ageReportedUnitCd = personDto.getAgeReportedUnitCd();
        this.birthGenderCd = personDto.getBirthGenderCd();
        this.birthOrderNbr = personDto.getBirthOrderNbr();
        this.birthTime = personDto.getBirthTime();
        this.birthTimeCalc = personDto.getBirthTimeCalc();
        this.cd = personDto.getCd();
        this.cdDescTxt = personDto.getCdDescTxt();
        this.currSexCd = personDto.getCurrSexCd();
        this.deceasedIndCd = personDto.getDeceasedIndCd();
        this.deceasedTime = personDto.getDeceasedTime();
        this.description = personDto.getDescription();
        this.educationLevelCd = personDto.getEducationLevelCd();
        this.educationLevelDescTxt = personDto.getEducationLevelDescTxt();
        this.ethnicGroupInd = personDto.getEthnicGroupInd();
        this.lastChgReasonCd = personDto.getLastChgReasonCd();
        this.lastChgTime = personDto.getLastChgTime();
        this.lastChgUserId = personDto.getLastChgUserId();
        this.localId = personDto.getLocalId();
        this.maritalStatusCd = personDto.getMaritalStatusCd();
        this.maritalStatusDescTxt = personDto.getMaritalStatusDescTxt();
        this.mothersMaidenNm = personDto.getMothersMaidenNm();
        this.multipleBirthInd = personDto.getMultipleBirthInd();
        this.occupationCd = personDto.getOccupationCd();
        this.preferredGenderCd = personDto.getPreferredGenderCd();
        this.primLangCd = personDto.getPrimLangCd();
        this.primLangDescTxt = personDto.getPrimLangDescTxt();
        this.recordStatusCd = personDto.getRecordStatusCd();
        this.recordStatusTime = timeStamp;
        this.statusCd = personDto.getStatusCd();
        this.statusTime = personDto.getStatusTime();
        this.survivedIndCd = personDto.getSurvivedIndCd();
        this.userAffiliationTxt = personDto.getUserAffiliationTxt();
        this.firstNm = personDto.getFirstNm();
        this.lastNm = personDto.getLastNm();
        this.middleNm = personDto.getMiddleNm();
        this.nmPrefix = personDto.getNmPrefix();
        this.nmSuffix = personDto.getNmSuffix();
        this.preferredNm = personDto.getPreferredNm();
        this.hmStreetAddr1 = personDto.getHmStreetAddr1();
        this.hmStreetAddr2 = personDto.getHmStreetAddr2();
        this.hmCityCd = personDto.getHmCityCd();
        this.hmCityDescTxt = personDto.getHmCityDescTxt();
        this.hmStateCd = personDto.getHmStateCd();
        this.hmZipCd = personDto.getHmZipCd();
        this.hmCntyCd = personDto.getHmCntyCd();
        this.hmCntryCd = personDto.getHmCntryCd();
        this.hmPhoneNbr = personDto.getHmPhoneNbr();
        this.hmPhoneCntryCd = personDto.getHmPhoneCntryCd();
        this.hmEmailAddr = personDto.getHmEmailAddr();
        this.cellPhoneNbr = personDto.getCellPhoneNbr();
        this.wkStreetAddr1 = personDto.getWkStreetAddr1();
        this.wkStreetAddr2 = personDto.getWkStreetAddr2();
        this.wkCityCd = personDto.getWkCityCd();
        this.wkCityDescTxt = personDto.getWkCityDescTxt();
        this.wkStateCd = personDto.getWkStateCd();
        this.wkZipCd = personDto.getWkZipCd();
        this.wkCntyCd = personDto.getWkCntyCd();
        this.wkCntryCd = personDto.getWkCntryCd();
        this.wkPhoneNbr = personDto.getWkPhoneNbr();
        this.wkPhoneCntryCd = personDto.getWkPhoneCntryCd();
        this.wkEmailAddr = personDto.getWkEmailAddr();
        this.ssn = personDto.getSSN();
        this.medicaidNum = personDto.getMedicaidNum();
        this.dlNum = personDto.getDlNum();
        this.dlStateCd = personDto.getDlStateCd();
        this.raceCd = personDto.getRaceCd();
        this.raceSeqNbr = personDto.getRaceSeqNbr();
        this.raceCategoryCd = personDto.getRaceCategoryCd();
        this.ethnicityGroupCd = personDto.getEthnicityGroupCd();
        this.ethnicGroupSeqNbr = personDto.getEthnicGroupSeqNbr();
        this.adultsInHouseNbr = personDto.getAdultsInHouseNbr();
        this.childrenInHouseNbr = personDto.getChildrenInHouseNbr();
        this.birthCityCd = personDto.getBirthCityCd();
        this.birthCityDescTxt = personDto.getBirthCityDescTxt();
        this.birthCntryCd = personDto.getBirthCntryCd();
        this.birthStateCd = personDto.getBirthStateCd();
        this.raceDescTxt = personDto.getRaceDescTxt();
        this.ethnicGroupDescTxt = personDto.getEthnicGroupDescTxt();
        this.versionCtrlNbr = personDto.getVersionCtrlNbr();
        this.asOfDateAdmin = personDto.getAsOfDateAdmin();
        this.asOfDateEthnicity = personDto.getAsOfDateEthnicity();
        this.asOfDateGeneral = personDto.getAsOfDateGeneral();
        this.asOfDateMorbidity = personDto.getAsOfDateMorbidity();
        this.asOfDateSex = personDto.getAsOfDateSex();
        this.electronicInd = personDto.getElectronicInd();
        this.personParentUid = personDto.getPersonParentUid();
        this.dedupMatchInd = personDto.getDedupMatchInd();
        this.groupNbr = personDto.getGroupNbr();
        this.groupTime = personDto.getGroupTime();
        this.edxInd = personDto.getEdxInd();
        this.speaksEnglishCd = personDto.getSpeaksEnglishCd();
        this.additionalGenderCd = personDto.getAdditionalGenderCd();
        this.eharsId = personDto.getEharsId();
        this.ethnicUnkReasonCd = personDto.getEthnicUnkReasonCd();
        this.sexUnkReasonCd = personDto.getSexUnkReasonCd();
    }
}
