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
    public void initDataMap() throws EcrCdaXmlException {
        dataMap = new HashMap<>();

        Field[] fields = EcrMsgInterviewDto.class.getDeclaredFields();
        EcrXmlModelingHelper helper = new EcrXmlModelingHelper();
        dataMap = helper.setupDataMap(fields, dataMap, this);
    }

}
