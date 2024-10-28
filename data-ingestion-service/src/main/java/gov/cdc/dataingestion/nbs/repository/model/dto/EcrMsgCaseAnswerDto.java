package gov.cdc.dataingestion.nbs.repository.model.dto;

import gov.cdc.dataingestion.exception.EcrCdaXmlException;
import gov.cdc.dataingestion.share.helper.EcrXmlModelingHelper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@Getter
@Setter
/**
 1118 - require constructor complaint
 * */
@SuppressWarnings({"java:S1118",""})
public class EcrMsgCaseAnswerDto {
    // NEW
    private String questionGroupSeqNbr;
    private String answerGroupSeqNbr;
    private String seqNbr;
    private String dataType;

    private String questionIdentifier;
    private Integer msgContainerUid;
    private String msgEventId;
    private String msgEventType;
    private String ansCodeSystemCd;
    private String ansCodeSystemDescTxt;
    private String ansDisplayTxt;
    private String answerTxt;
    private String partTypeCd;
    private String quesCodeSystemCd;
    private String quesCodeSystemDescTxt;
    private String quesDisplayTxt;
    private String questionDisplayName;
    private String ansToCode;
    private String ansToCodeSystemCd;
    private String ansToDisplayNm;
    private String codeTranslationRequired;
    private String ansToCodeSystemDescTxt;

    private Map<String, Object> dataMap;
    public void initDataMap() throws EcrCdaXmlException {
        dataMap = new HashMap<>();

        if (msgContainerUid == null) {
            msgContainerUid = -1;
        }

        Field[] fields = EcrMsgCaseAnswerDto.class.getDeclaredFields();
        EcrXmlModelingHelper helper = new EcrXmlModelingHelper();
        dataMap = helper.setupDataMap(fields, dataMap, this);
    }
}
