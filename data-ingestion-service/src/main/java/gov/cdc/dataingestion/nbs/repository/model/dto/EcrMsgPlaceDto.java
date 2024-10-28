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
 * */
@SuppressWarnings({"java:S1118",""})
public class EcrMsgPlaceDto {
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
    private Map<String, Object> dataMap;
    public void initDataMap() throws EcrCdaXmlException {
        dataMap = new HashMap<>();

        Field[] fields = EcrMsgPlaceDto.class.getDeclaredFields();
        EcrXmlModelingHelper helper = new EcrXmlModelingHelper();
        dataMap = helper.setupDataMap(fields, dataMap, this);
    }
}
