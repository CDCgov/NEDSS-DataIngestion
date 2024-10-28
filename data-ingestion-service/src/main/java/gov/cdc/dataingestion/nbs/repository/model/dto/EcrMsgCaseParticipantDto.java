package gov.cdc.dataingestion.nbs.repository.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
/**
 1118 - require constructor complaint
 * */
@SuppressWarnings({"java:S1118",""})
public class EcrMsgCaseParticipantDto {
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
