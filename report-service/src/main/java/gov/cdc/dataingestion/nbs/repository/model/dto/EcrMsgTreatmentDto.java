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
public class EcrMsgTreatmentDto {
    private String trtLocalId;
    private String trtAuthorId;
    private String trtCompositeCd;
    private String trtCommentTxt;
    private String trtCustomTreatmentTxt;
    private Integer trtDosageAmt;
    private String trtDosageUnitCd;
    private String trtDrugCd;
    private Integer trtDurationAmt;
    private String trtDurationUnitCd;
    private Timestamp trtEffectiveTime;
    private String trtFrequencyAmtCd;
    private String trtRouteCd;
    private Timestamp trtTreatmentDt;
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
