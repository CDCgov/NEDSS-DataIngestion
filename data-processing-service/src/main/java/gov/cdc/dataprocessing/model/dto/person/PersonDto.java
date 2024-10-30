package gov.cdc.dataprocessing.model.dto.person;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.model.dto.RootDtoInterface;
import gov.cdc.dataprocessing.repository.nbs.odse.model.person.Person;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
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
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107"})
public class PersonDto extends BaseContainer implements RootDtoInterface {
    private static final long serialVersionUID = 1L;
    private String dedupMatchInd;
    private Integer groupNbr;
    private Timestamp groupTime;
    private Long personUid;
    private Long personParentUid;
    private String addReasonCd;
    private Timestamp addTime;
    private Long addUserId;
    private String administrativeGenderCd;
    private Integer ageCalc;
    private Timestamp ageCalcTime;
    private String ageCalcUnitCd;
    private String ageCategoryCd;
    private String ageReported;
    private Timestamp ageReportedTime;
    private String ageReportedUnitCd;
    private Timestamp asOfDateAdmin;
    private Timestamp asOfDateEthnicity;
    private Timestamp asOfDateGeneral;
    private Timestamp asOfDateMorbidity;
    private Timestamp asOfDateSex;
    private String birthGenderCd;
    private Integer birthOrderNbr;
    private String birthOrderNbrStr;
    private Timestamp birthTime;
    private Timestamp birthTimeCalc;
    private String cd;
    private String cdDescTxt;
    private String currSexCd;
    private String deceasedIndCd;
    private Timestamp deceasedTime;
    private String description;
    private String educationLevelCd;
    private String educationLevelDescTxt;
    private String electronicInd;
    private String ethnicGroupInd;
    private String lastChgReasonCd;
    private Timestamp lastChgTime;
    private Long lastChgUserId;
    private String localId;
    private String maritalStatusCd;
    private String maritalStatusDescTxt;
    private String mothersMaidenNm;
    private String multipleBirthInd;
    private String occupationCd;

    private String preferredGenderCd;
    private String primLangCd;
    private String primLangDescTxt;
    private String recordStatusCd;
    private Timestamp recordStatusTime;
    private String statusCd;
    private Timestamp statusTime;
    private String survivedIndCd;
    private String userAffiliationTxt;
    private String firstNm;
    private String lastNm;
    private String middleNm;
    private String nmPrefix;
    private String nmSuffix;
    private String preferredNm;
    private String hmStreetAddr1;
    private String hmStreetAddr2;
    private String hmCityCd;
    private String hmCityDescTxt;
    private String hmStateCd;
    private String hmZipCd;
    private String hmCntyCd;
    private String hmCntryCd;
    private String hmPhoneNbr;
    private String hmPhoneCntryCd;
    private String hmEmailAddr;
    private String cellPhoneNbr;
    private String wkStreetAddr1;
    private String wkStreetAddr2;
    private String wkCityCd;
    private String wkCityDescTxt;
    private String wkStateCd;
    private String wkZipCd;
    private String wkCntyCd;
    private String wkCntryCd;
    private String wkPhoneNbr;
    private String wkPhoneCntryCd;
    private String wkEmailAddr;
    private String SSN;
    private String medicaidNum;
    private String dlNum;
    private String dlStateCd;
    private String raceCd;
    private Integer raceSeqNbr;
    private String raceCategoryCd;
    private String ethnicityGroupCd;
    private Integer ethnicGroupSeqNbr;
    private Integer adultsInHouseNbr;
    private String adultsInHouseNbrStr;
    private Integer childrenInHouseNbr;
    private String childrenInHouseNbrStr;
    private String birthCityCd;
    private String birthCityDescTxt;
    private String birthCntryCd;
    private String birthStateCd;
    private String raceDescTxt;
    private String ethnicGroupDescTxt;
    private Integer versionCtrlNbr;
    private String progAreaCd = null;
    private String jurisdictionCd = null;
    private Long programJurisdictionOid = null;
    private String sharedInd = null;
    private String edxInd =null;
    private boolean isCaseInd= false;
    private String speaksEnglishCd;
    private String additionalGenderCd;
    private String eharsId;
    private String ethnicUnkReasonCd;
    private String sexUnkReasonCd;
    private boolean isReentrant= false;

    // Same on PersonHIST
    public String getSuperclass() {
        this.superClassType = NEDSSConstant.CLASSTYPE_ENTITY;
        return superClassType;
    }

    @Override
    public Long getUid() {
        return personUid;
    }

    public PersonDto() {
        itDirty = false;
        itNew = true;
        itDelete = false;
    }

    public PersonDto(Person person) {
        this.dedupMatchInd = person.getDedupMatchInd();
        this.groupNbr = person.getGroupNbr();
        this.groupTime = person.getGroupTime();
        this.personUid = person.getPersonUid();
        this.personParentUid = person.getPersonParentUid();
        this.addReasonCd = person.getAddReasonCd();
        this.addTime = person.getAddTime();
        this.addUserId = person.getAddUserId();
        this.administrativeGenderCd = person.getAdministrativeGenderCd();
        this.ageCalc = person.getAgeCalc();
        this.ageCalcTime = person.getAgeCalcTime();
        this.ageCalcUnitCd = person.getAgeCalcUnitCd();
        this.ageCategoryCd = person.getAgeCategoryCd();
        this.ageReported = person.getAgeReported();
        this.ageReportedTime = person.getAgeReportedTime();
        this.ageReportedUnitCd = person.getAgeReportedUnitCd();
        this.asOfDateAdmin = person.getAsOfDateAdmin();
        this.asOfDateEthnicity = person.getAsOfDateEthnicity();
        this.asOfDateGeneral = person.getAsOfDateGeneral();
        this.asOfDateMorbidity = person.getAsOfDateMorbidity();
        this.asOfDateSex = person.getAsOfDateSex();
        this.birthGenderCd = person.getBirthGenderCd();
        this.birthOrderNbr = person.getBirthOrderNbr();
        this.birthTime = person.getBirthTime();
        this.birthTimeCalc = person.getBirthTimeCalc();
        this.cd = person.getCd();
        this.cdDescTxt = person.getCdDescTxt();
        this.currSexCd = person.getCurrSexCd();
        this.deceasedIndCd = person.getDeceasedIndCd();
        this.deceasedTime = person.getDeceasedTime();
        this.description = person.getDescription();
        this.educationLevelCd = person.getEducationLevelCd();
        this.educationLevelDescTxt = person.getEducationLevelDescTxt();
        this.electronicInd = person.getElectronicInd();
        this.ethnicGroupInd = person.getEthnicGroupInd();
        this.lastChgReasonCd = person.getLastChgReasonCd();
        this.lastChgTime = person.getLastChgTime();
        this.lastChgUserId = person.getLastChgUserId();
        this.localId = person.getLocalId();
        this.maritalStatusCd = person.getMaritalStatusCd();
        this.maritalStatusDescTxt = person.getMaritalStatusDescTxt();
        this.mothersMaidenNm = person.getMothersMaidenNm();
        this.multipleBirthInd = person.getMultipleBirthInd();
        this.occupationCd = person.getOccupationCd();
        this.preferredGenderCd = person.getPreferredGenderCd();
        this.primLangCd = person.getPrimLangCd();
        this.primLangDescTxt = person.getPrimLangDescTxt();
        this.recordStatusCd = person.getRecordStatusCd();
        this.recordStatusTime = person.getRecordStatusTime();
        this.statusCd = person.getStatusCd();
        this.statusTime = person.getStatusTime();
        this.survivedIndCd = person.getSurvivedIndCd();
        this.userAffiliationTxt = person.getUserAffiliationTxt();
        this.firstNm = person.getFirstNm();
        this.lastNm = person.getLastNm();
        this.middleNm = person.getMiddleNm();
        this.nmPrefix = person.getNmPrefix();
        this.nmSuffix = person.getNmSuffix();
        this.preferredNm = person.getPreferredNm();
        this.hmStreetAddr1 = person.getHmStreetAddr1();
        this.hmStreetAddr2 = person.getHmStreetAddr2();
        this.hmCityCd = person.getHmCityCd();
        this.hmCityDescTxt = person.getHmCityDescTxt();
        this.hmStateCd = person.getHmStateCd();
        this.hmZipCd = person.getHmZipCd();
        this.hmCntyCd = person.getHmCntyCd();
        this.hmCntryCd = person.getHmCntryCd();
        this.hmPhoneNbr = person.getHmPhoneNbr();
        this.hmPhoneCntryCd = person.getHmPhoneCntryCd();
        this.hmEmailAddr = person.getHmEmailAddr();
        this.cellPhoneNbr = person.getCellPhoneNbr();
        this.wkStreetAddr1 = person.getWkStreetAddr1();
        this.wkStreetAddr2 = person.getWkStreetAddr2();
        this.wkCityCd = person.getWkCityCd();
        this.wkCityDescTxt = person.getWkCityDescTxt();
        this.wkStateCd = person.getWkStateCd();
        this.wkZipCd = person.getWkZipCd();
        this.wkCntyCd = person.getWkCntyCd();
        this.wkCntryCd = person.getWkCntryCd();
        this.wkPhoneNbr = person.getWkPhoneNbr();
        this.wkPhoneCntryCd = person.getWkPhoneCntryCd();
        this.wkEmailAddr = person.getWkEmailAddr();
        this.SSN = person.getSsn();
        this.medicaidNum = person.getMedicaidNum();
        this.dlNum = person.getDlNum();
        this.dlStateCd = person.getDlStateCd();
        this.raceCd = person.getRaceCd();
        this.raceSeqNbr = person.getRaceSeqNbr();
        this.raceCategoryCd = person.getRaceCategoryCd();
        this.ethnicityGroupCd = person.getEthnicityGroupCd();
        this.ethnicGroupSeqNbr = person.getEthnicGroupSeqNbr();
        this.adultsInHouseNbr = person.getAdultsInHouseNbr();
        this.childrenInHouseNbr = person.getChildrenInHouseNbr();
        this.birthCityCd = person.getBirthCityCd();
        this.birthCityDescTxt = person.getBirthCityDescTxt();
        this.birthCntryCd = person.getBirthCntryCd();
        this.birthStateCd = person.getBirthStateCd();
        this.raceDescTxt = person.getRaceDescTxt();
        this.ethnicGroupDescTxt = person.getEthnicGroupDescTxt();
        this.versionCtrlNbr = person.getVersionCtrlNbr();
        this.edxInd = person.getEdxInd();
        this.speaksEnglishCd = person.getSpeaksEnglishCd();
        this.additionalGenderCd = person.getAdditionalGenderCd();
        this.eharsId = person.getEharsId();
    }

}
