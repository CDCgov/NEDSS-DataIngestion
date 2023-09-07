package gov.cdc.dataingestion.nbs.repository.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@NoArgsConstructor
@Getter
@Setter
public class EcrMsgProviderDto {
    private String prvLocalId;
    private String prvAuthorId;
    private String prvAddrCityTxt;
    private String prvAddrCommentTxt;
    private String prvAddrCountyCd;
    private String prvAddrCountryCd;
    private String prvAddrStreetAddr1Txt;
    private String prvAddrStreetAddr2Txt;
    private String prvAddrStateCd;
    private String prvAddrZipCodeTxt;
    private String prvCommentTxt;
    private String prvIdAltIdNbrTxt;
    private String prvIdQuickCodeTxt;
    private String prvIdNbrTxt;
    private String prvIdNpiTxt;
    private Timestamp prvEffectiveTime;
    private String prvEmailAddressTxt;
    private String prvNameDegreeCd;
    private String prvNameFirstTxt;
    private String prvNameLastTxt;
    private String prvNameMiddleTxt;
    private String prvNamePrefixCd;
    private String prvNameSuffixCd;
    private String prvPhoneCommentTxt;
    private String prvPhoneCountryCodeTxt;
    private Integer prvPhoneExtensionTxt;
    private String prvPhoneNbrTxt;
    private String prvRoleCd;
    private String prvUrlAddressTxt;

}
