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

    private Map<String, Object> dataMap;

    private Integer numberOfField;
    public void initDataMap() {
        dataMap = new HashMap<>();

        Field[] fields = EcrMsgProviderDto.class.getDeclaredFields();
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
