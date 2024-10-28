package gov.cdc.dataingestion.nbs.repository.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
/**
 1118 - require constructor complaint
 125 - comment complaint
 6126 - String block complaint
 1135 - todos complaint
 * */
@SuppressWarnings({"java:S1118","java:S125", "java:S6126", "java:S1135"})
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
