package gov.cdc.dataprocessing.repository.nbs.odse.model;

import gov.cdc.dataprocessing.model.classic_model.dto.PersonDT;
import gov.cdc.dataprocessing.model.classic_model.vo.PersonVO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Timestamp;

import static gov.cdc.dataprocessing.utilities.time.TimeStampUtil.getCurrentTimeStamp;

@Entity
@Getter
@Setter
@Table(name = "Person")
public class Person  {

    @Id
    @Column(name = "person_uid")
    private Long personUid;

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

    @Column(name = "version_ctrl_nbr", nullable = false)
    private Integer versionCtrlNbr;

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
    public Person(PersonDT personDT) {
        var timeStamp = getCurrentTimeStamp();
        this.personUid = personDT.getPersonUid();
        this.addReasonCd = personDT.getAddReasonCd();
        this.addTime = personDT.getAddTime();
        this.addUserId = personDT.getAddUserId();
        this.administrativeGenderCd = personDT.getAdministrativeGenderCd();
        this.ageCalc = personDT.getAgeCalc();
        this.ageCalcTime = personDT.getAgeCalcTime();
        this.ageCalcUnitCd = personDT.getAgeCalcUnitCd();
        this.ageCategoryCd = personDT.getAgeCategoryCd();
        this.ageReported = personDT.getAgeReported();
        this.ageReportedTime = personDT.getAgeReportedTime();
        this.ageReportedUnitCd = personDT.getAgeReportedUnitCd();
        this.birthGenderCd = personDT.getBirthGenderCd();
        this.birthOrderNbr = personDT.getBirthOrderNbr();
        this.birthTime = personDT.getBirthTime();
        this.birthTimeCalc = personDT.getBirthTimeCalc();
        this.cd = personDT.getCd();
        this.cdDescTxt = personDT.getCdDescTxt();
        this.currSexCd = personDT.getCurrSexCd();
        this.deceasedIndCd = personDT.getDeceasedIndCd();
        this.deceasedTime = personDT.getDeceasedTime();
        this.description = personDT.getDescription();
        this.educationLevelCd = personDT.getEducationLevelCd();
        this.educationLevelDescTxt = personDT.getEducationLevelDescTxt();
        this.ethnicGroupInd = personDT.getEthnicGroupInd();
        this.lastChgReasonCd = personDT.getLastChgReasonCd();
        this.lastChgTime = personDT.getLastChgTime();
        this.lastChgUserId = personDT.getLastChgUserId();
        this.localId = personDT.getLocalId();
        this.maritalStatusCd = personDT.getMaritalStatusCd();
        this.maritalStatusDescTxt = personDT.getMaritalStatusDescTxt();
        this.mothersMaidenNm = personDT.getMothersMaidenNm();
        this.multipleBirthInd = personDT.getMultipleBirthInd();
        this.occupationCd = personDT.getOccupationCd();
        this.preferredGenderCd = personDT.getPreferredGenderCd();
        this.primLangCd = personDT.getPrimLangCd();
        this.primLangDescTxt = personDT.getPrimLangDescTxt();
        this.recordStatusCd = personDT.getRecordStatusCd();
        this.recordStatusTime = timeStamp;
        this.statusCd = personDT.getStatusCd();
        this.statusTime = personDT.getStatusTime();
        this.survivedIndCd = personDT.getSurvivedIndCd();
        this.userAffiliationTxt = personDT.getUserAffiliationTxt();
        this.firstNm = personDT.getFirstNm();
        this.lastNm = personDT.getLastNm();
        this.middleNm = personDT.getMiddleNm();
        this.nmPrefix = personDT.getNmPrefix();
        this.nmSuffix = personDT.getNmSuffix();
        this.preferredNm = personDT.getPreferredNm();
        this.hmStreetAddr1 = personDT.getHmStreetAddr1();
        this.hmStreetAddr2 = personDT.getHmStreetAddr2();
        this.hmCityCd = personDT.getHmCityCd();
        this.hmCityDescTxt = personDT.getHmCityDescTxt();
        this.hmStateCd = personDT.getHmStateCd();
        this.hmZipCd = personDT.getHmZipCd();
        this.hmCntyCd = personDT.getHmCntyCd();
        this.hmCntryCd = personDT.getHmCntryCd();
        this.hmPhoneNbr = personDT.getHmPhoneNbr();
        this.hmPhoneCntryCd = personDT.getHmPhoneCntryCd();
        this.hmEmailAddr = personDT.getHmEmailAddr();
        this.cellPhoneNbr = personDT.getCellPhoneNbr();
        this.wkStreetAddr1 = personDT.getWkStreetAddr1();
        this.wkStreetAddr2 = personDT.getWkStreetAddr2();
        this.wkCityCd = personDT.getWkCityCd();
        this.wkCityDescTxt = personDT.getWkCityDescTxt();
        this.wkStateCd = personDT.getWkStateCd();
        this.wkZipCd = personDT.getWkZipCd();
        this.wkCntyCd = personDT.getWkCntyCd();
        this.wkCntryCd = personDT.getWkCntryCd();
        this.wkPhoneNbr = personDT.getWkPhoneNbr();
        this.wkPhoneCntryCd = personDT.getWkPhoneCntryCd();
        this.wkEmailAddr = personDT.getWkEmailAddr();
        this.ssn = personDT.getSSN();
        this.medicaidNum = personDT.getMedicaidNum();
        this.dlNum = personDT.getDlNum();
        this.dlStateCd = personDT.getDlStateCd();
        this.raceCd = personDT.getRaceCd();
        this.raceSeqNbr = personDT.getRaceSeqNbr();
        this.raceCategoryCd = personDT.getRaceCategoryCd();
        this.ethnicityGroupCd = personDT.getEthnicityGroupCd();
        this.ethnicGroupSeqNbr = personDT.getEthnicGroupSeqNbr();
        this.adultsInHouseNbr = personDT.getAdultsInHouseNbr();
        this.childrenInHouseNbr = personDT.getChildrenInHouseNbr();
        this.birthCityCd = personDT.getBirthCityCd();
        this.birthCityDescTxt = personDT.getBirthCityDescTxt();
        this.birthCntryCd = personDT.getBirthCntryCd();
        this.birthStateCd = personDT.getBirthStateCd();
        this.raceDescTxt = personDT.getRaceDescTxt();
        this.ethnicGroupDescTxt = personDT.getEthnicGroupDescTxt();
        this.versionCtrlNbr = personDT.getVersionCtrlNbr();
        this.asOfDateAdmin = personDT.getAsOfDateAdmin();
        this.asOfDateEthnicity = personDT.getAsOfDateEthnicity();
        this.asOfDateGeneral = personDT.getAsOfDateGeneral();
        this.asOfDateMorbidity = personDT.getAsOfDateMorbidity();
        this.asOfDateSex = personDT.getAsOfDateSex();
        this.electronicInd = personDT.getElectronicInd();
        this.personParentUid = personDT.getPersonParentUid();
        this.dedupMatchInd = personDT.getDedupMatchInd();
        this.groupNbr = personDT.getGroupNbr();
        this.groupTime = personDT.getGroupTime();
        this.edxInd = personDT.getEdxInd();
        this.speaksEnglishCd = personDT.getSpeaksEnglishCd();
        this.additionalGenderCd = personDT.getAdditionalGenderCd();
        this.eharsId = personDT.getEharsId();
        this.ethnicUnkReasonCd = personDT.getEthnicUnkReasonCd();
        this.sexUnkReasonCd = personDT.getSexUnkReasonCd();
    }
}
