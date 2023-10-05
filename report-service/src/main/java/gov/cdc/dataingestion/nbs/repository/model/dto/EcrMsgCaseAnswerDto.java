package gov.cdc.dataingestion.nbs.repository.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@Getter
@Setter
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
    public void initDataMap() {
        dataMap = new HashMap<>();

        if (msgContainerUid == null) {
            msgContainerUid = -1;
        }

        Field[] fields = EcrMsgCaseAnswerDto.class.getDeclaredFields();
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
