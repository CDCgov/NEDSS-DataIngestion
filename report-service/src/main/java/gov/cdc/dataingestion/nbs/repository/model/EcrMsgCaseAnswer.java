package gov.cdc.dataingestion.nbs.repository.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class EcrMsgCaseAnswer {
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
}
