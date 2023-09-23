package gov.cdc.dataingestion.nbs.repository.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.lang.reflect.Field;
import java.sql.Timestamp;

@Getter
@Setter
public class EcrMsgPatientDto {
    public EcrMsgPatientDto() {
        this.numberOfField = CountFields();
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

    private Integer numberOfField;

    private int CountFields() {
        Field[] fields = this.getClass().getDeclaredFields();
        int count = 0;
        for (Field field : fields) {
            // Exclude the 'numberOfVariable' field
            if (!"numberOfVariable".equals(field.getName())) {
                count++;
            }
        }
        return count;
    }

}
