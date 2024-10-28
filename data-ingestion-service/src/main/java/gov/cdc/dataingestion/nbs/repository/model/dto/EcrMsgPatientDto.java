package gov.cdc.dataingestion.nbs.repository.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
/**
 1118 - require constructor complaint
 125 - comment complaint
 6126 - String block complaint
 * */
@SuppressWarnings({"java:S1118","java:S125", "java:S6126"})
public class EcrMsgPatientDto {
    public EcrMsgPatientDto() {
        // empty constructor
    }

    private Integer msgContainerUid;
    private String patLocalId;
    private String patAuthorId;
    private Timestamp patAddrAsOfDt;
    private String patAddrCityTxt;
    private String patAddrCommentTxt;
    private String patAddrCountyCd;
    private String patAddrCountryCd;
    private String patAdditionalGenderTxt;
    private String patAddrCensusTractTxt;
    private String patAddrStateCd;
    private String patAddrStreetAddr1Txt;
    private String patAddrStreetAddr2Txt;
    private String patAddrZipCodeTxt;
    private String patBirthCountryCd;
    private Timestamp patBirthDt;
    private String patBirthSexCd;
    private String patCellPhoneNbrTxt;
    private String patCommentTxt;
    private String patCurrentSexCd;
    private String patDeceasedIndCd;
    private Timestamp patDeceasedDt;
    private String patEffectiveTime;
    private String patIdMedicalRecordNbrTxt;
    private String patIdStateHivCaseNbrTxt;
    private Timestamp patInfoAsOfDt;
    private String patIdSsnTxt;
    private String patEmailAddressTxt;
    private String patEthnicGroupIndCd;
    private String patEthnicityUnkReasonCd;
    private String patHomePhoneNbrTxt;
    private String patNameAliasTxt;
    private Timestamp patNameAsOfDt;
    private String patNameDegreeCd;
    private String patNameFirstTxt;
    private String patNameLastTxt;
    private String patNameMiddleTxt;
    private String patNamePrefixCd;
    private String patNameSuffixCd;
    private String patMaritalStatusCd;
    private String patPhoneCommentTxt;
    private Integer patPhoneCountryCodeTxt;
    private String patPrimaryLanguageCd;
    private String patPreferredGenderCd;
    private String patRaceCategoryCd;
    private String patRaceDescTxt;
    private Integer patReportedAge;
    private String patReportedAgeUnitCd;
    private String patSexUnkReasonCd;
    private String patSpeaksEnglishIndCd;
    private Timestamp patPhoneAsOfDt;
    private String patUrlAddressTxt;
    private String patWorkPhoneNbrTxt;
    private Integer patWorkPhoneExtensionTxt;


}
