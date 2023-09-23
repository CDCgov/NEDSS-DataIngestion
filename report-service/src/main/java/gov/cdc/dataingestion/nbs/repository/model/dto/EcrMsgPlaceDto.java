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
    public void initDataMap() {
        dataMap = new HashMap<>();

        Field[] fields = this.getClass().getDeclaredFields();
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
