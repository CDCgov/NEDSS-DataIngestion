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

    @Column(name = "mothersMaidenNm")
    private String mothersMaidenNm;

    @Column(name = "multipleBirthInd")
    private String multipleBirthInd;

    @Column(name = "occupationCd")
    private String occupationCd;

    @Column(name = "preferredGenderCd")
    private String preferredGenderCd;

    @Column(name = "primLangCd")
    private String primLangCd;

    @Column(name = "primLangDescTxt")
    private String primLangDescTxt;

    @Column(name = "recordStatusCd")
    private String recordStatusCd;

    @Column(name = "recordStatusTime")
    private Timestamp recordStatusTime;

    @Column(name = "statusCd")
    private String statusCd;

    @Column(name = "statusTime")
    private Timestamp statusTime;

    @Column(name = "survivedIndCd")
    private String survivedIndCd;

    @Column(name = "userAffiliationTxt")
    private String userAffiliationTxt;

    @Column(name = "firstNm")
    private String firstNm;

    @Column(name = "lastNm")
    private String lastNm;

    @Column(name = "middleNm")
    private String middleNm;

    @Column(name = "nmPrefix")
    private String nmPrefix;

    @Column(name = "nmSuffix")
    private String nmSuffix;

    @Column(name = "preferredNm")
    private String preferredNm;

    @Column(name = "hmStreetAddr1")
    private String hmStreetAddr1;

    @Column(name = "hmStreetAddr2")
    private String hmStreetAddr2;

    @Column(name = "hmCityCd")
    private String hmCityCd;

    @Column(name = "hmCityDescTxt")
    private String hmCityDescTxt;

    @Column(name = "hmStateCd")
    private String hmStateCd;

    @Column(name = "hmZipCd")
    private String hmZipCd;

    @Column(name = "hmCntyCd")
    private String hmCntyCd;

    @Column(name = "hmCntryCd")
    private String hmCntryCd;

    @Column(name = "hmPhoneNbr")
    private String hmPhoneNbr;

    @Column(name = "hmPhoneCntryCd")
    private String hmPhoneCntryCd;

    @Column(name = "hmEmailAddr")
    private String hmEmailAddr;

    @Column(name = "cellPhoneNbr")
    private String cellPhoneNbr;

    @Column(name = "wkStreetAddr1")
    private String wkStreetAddr1;

    @Column(name = "wkStreetAddr2")
    private String wkStreetAddr2;

    @Column(name = "wkCityCd")
    private String wkCityCd;

    @Column(name = "wkCityDescTxt")
    private String wkCityDescTxt;

    @Column(name = "wkStateCd")
    private String wkStateCd;

    @Column(name = "wkZipCd")
    private String wkZipCd;

    @Column(name = "wkCntyCd")
    private String wkCntyCd;

    @Column(name = "wkCntryCd")
    private String wkCntryCd;

    @Column(name = "wkPhoneNbr")
    private String wkPhoneNbr;

    @Column(name = "wkPhoneCntryCd")
    private String wkPhoneCntryCd;

    @Column(name = "wkEmailAddr")
    private String wkEmailAddr;

    @Column(name = "SSN")
    private String ssn;

    @Column(name = "medicaidNum")
    private String medicaidNum;

    @Column(name = "dlNum")
    private String dlNum;

    @Column(name = "dlStateCd")
    private String dlStateCd;

    @Column(name = "raceCd")
    private String raceCd;

    @Column(name = "raceSeqNbr")
    private Integer raceSeqNbr;

    @Column(name = "raceCategoryCd")
    private String raceCategoryCd;

    @Column(name = "ethnicityGroupCd")
    private String ethnicityGroupCd;

    @Column(name = "ethnicGroupSeqNbr")
    private Integer ethnicGroupSeqNbr;

    @Column(name = "adultsInHouseNbr")
    private Integer adultsInHouseNbr;

    @Column(name = "childrenInHouseNbr")
    private Integer childrenInHouseNbr;

    @Column(name = "birthCityCd")
    private String birthCityCd;

    @Column(name = "birthCityDescTxt")
    private String birthCityDescTxt;

    @Column(name = "birthCntryCd")
    private String birthCntryCd;

    @Column(name = "birthStateCd")
    private String birthStateCd;

    @Column(name = "raceDescTxt")
    private String raceDescTxt;

    @Column(name = "ethnicGroupDescTxt")
    private String ethnicGroupDescTxt;

    @Column(name = "versionCtrlNbr")
    private Integer versionCtrlNbr;

    @Column(name = "asOfDateAdmin")
    private Timestamp asOfDateAdmin;

    @Column(name = "asOfDateEthnicity")
    private Timestamp asOfDateEthnicity;

    @Column(name = "asOfDateGeneral")
    private Timestamp asOfDateGeneral;

    @Column(name = "asOfDateMorbidity")
    private Timestamp asOfDateMorbidity;

    @Column(name = "asOfDateSex")
    private Timestamp asOfDateSex;

    @Column(name = "electronicInd")
    private String electronicInd;

    @Column(name = "personParentUid")
    private Long personParentUid;

    @Column(name = "dedupMatchInd")
    private String dedupMatchInd;

    @Column(name = "groupNbr")
    private Integer groupNbr;

    @Column(name = "groupTime")
    private Timestamp groupTime;

    @Column(name = "edxInd")
    private String edxInd;

    @Column(name = "speaksEnglishCd")
    private String speaksEnglishCd;

    @Column(name = "additionalGenderCd")
    private String additionalGenderCd;

    @Column(name = "eharsId")
    private String eharsId;

    @Column(name = "ethnicUnkReasonCd")
    private String ethnicUnkReasonCd;

    @Column(name = "sexUnkReasonCd")
    private String sexUnkReasonCd;

    // Constructors, getters, and setters
    public Person() {

    }
    public Person(PersonDT personDT) {
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
        this.recordStatusTime = personDT.getRecordStatusTime();
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
