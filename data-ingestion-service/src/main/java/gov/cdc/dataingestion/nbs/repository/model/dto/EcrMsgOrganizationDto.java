package gov.cdc.dataingestion.nbs.repository.model.dto;

import gov.cdc.dataingestion.exception.EcrCdaXmlException;
import gov.cdc.dataingestion.share.helper.EcrXmlModelingHelper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@Getter
@Setter
/**
 1118 - require constructor complaint
 125 - comment complaint
 6126 - String block complaint
 * */
@SuppressWarnings({"java:S1118","java:S125", "java:S6126"})
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
    private Map<String, Object> dataMap;
    public void initDataMap() throws EcrCdaXmlException {
        dataMap = new HashMap<>();

        Field[] fields = EcrMsgOrganizationDto.class.getDeclaredFields();
        EcrXmlModelingHelper helper = new EcrXmlModelingHelper();
        dataMap = helper.setupDataMap(fields, dataMap, this);
    }
}
