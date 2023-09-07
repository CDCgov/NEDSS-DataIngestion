package gov.cdc.dataingestion.nbs.repository.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@NoArgsConstructor
@Getter
@Setter
public class EcrMsgOrganizationDto {
    private String orgLocalId;
    private String orgAuthorId;
    private Timestamp orgEffectiveTime;
    private String orgNameTxt;
    private String orgAddrCityTxt;
    private String orgAddrCommentTxt;
    private String orgAddrCountyCd;
    private String orgAddrCountryCd;
    private String orgAddrStateCd;
    private String orgAddrStreetAddr1Txt;
    private String orgAddrStreetAddr2Txt;
    private String orgAddrZipCodeTxt;
    private String orgClassCd;
    private String orgCommentTxt;
    private String orgEmailAddressTxt;
    private String orgIdCliaNbrTxt;
    private String orgIdFacilityIdentifierTxt;
    private String orgIdQuickCodeTxt;
    private String orgPhoneCommentTxt;
    private String orgPhoneCountryCodeTxt;
    private Integer orgPhoneExtensionTxt;
    private String orgPhoneNbrTxt;
    private String orgRoleCd;
    private String orgUrlAddressTxt;

}
