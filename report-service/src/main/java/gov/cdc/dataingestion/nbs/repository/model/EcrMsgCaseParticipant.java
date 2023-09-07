package gov.cdc.dataingestion.nbs.repository.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class EcrMsgCaseParticipant {
    private String msgEventId;
    private String msgEventType;
    private String answerTxt;
    private String answerLargeTxt;
    private Integer answerGroupSeqNbr;
    private String partTypeCd;
    private String questionIdentifier;
    private Integer questionGroupSeqNbr;
    private Integer seqNbr;

}
