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
    public void initDataMap() throws EcrCdaXmlException {
        dataMap = new HashMap<>();

        Field[] fields = EcrMsgTreatmentDto.class.getDeclaredFields();
        EcrXmlModelingHelper helper = new EcrXmlModelingHelper();
        dataMap = helper.setupDataMap(fields, dataMap, this);
    }

}
