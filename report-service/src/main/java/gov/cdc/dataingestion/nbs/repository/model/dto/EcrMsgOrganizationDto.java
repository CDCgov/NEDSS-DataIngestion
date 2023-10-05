package gov.cdc.dataingestion.nbs.repository.model.dto;

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
    public void initDataMap() {
        dataMap = new HashMap<>();

        Field[] fields = EcrMsgOrganizationDto.class.getDeclaredFields();
        for (Field field : fields) {
            if (!"numberOfField".equals(field.getName()) && !"dataMap".equals(field.getName())) {
                field.setAccessible(true);  // make sure we can access private fields
                try {
                    // Store the field name and its value in the dataMap
                    dataMap.put(field.getName(), field.get(this));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
