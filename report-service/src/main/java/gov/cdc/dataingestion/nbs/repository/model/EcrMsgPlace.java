package gov.cdc.dataingestion.nbs.repository.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@NoArgsConstructor
@Getter
@Setter
public class EcrMsgPlace {
    private Integer msgContainerUid;
    private String plaLocalId;
    private String plaAuthorId;
    private Timestamp plaEffectiveTime;
    private Timestamp plaAddrAsOfDt;
    private String plaAddrCityTxt;
    private String plaAddrCountyCd;
    private String plaAddrCountryCd;
    private String plaAddrStateCd;
    private String plaAddrStreetAddr1Txt;
    private String plaAddrStreetAddr2Txt;
    private String plaAddrZipCodeTxt;
    private String plaAddrCommentTxt;
    private String plaCensusTractTxt;
    private String plaCommentTxt;
    private String plaEmailAddressTxt;
    private String plaIdQuickCode;
    private String plaNameTxt;
    private Timestamp plaPhoneAsOfDt;
    private String plaPhoneCountryCodeTxt;
    private String plaPhoneExtensionTxt;
    private String plaPhoneNbrTxt;
    private String plaPhoneCommentTxt;
    private String plaTypeCd;
    private String plaUrlAddressTxt;

}
