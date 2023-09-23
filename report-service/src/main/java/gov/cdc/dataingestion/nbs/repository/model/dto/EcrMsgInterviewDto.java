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
public class EcrMsgInterviewDto {
    private Integer msgContainerUid;
    private String ixsLocalId;
    private String ixsIntervieweeId;
    private String ixsAuthorId;
    private Timestamp ixsEffectiveTime;
    private Timestamp ixsInterviewDt;
    private String ixsInterviewLocCd;
    private String ixsIntervieweeRoleCd;
    private String ixsInterviewTypeCd;
    private String ixsStatusCd;
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
