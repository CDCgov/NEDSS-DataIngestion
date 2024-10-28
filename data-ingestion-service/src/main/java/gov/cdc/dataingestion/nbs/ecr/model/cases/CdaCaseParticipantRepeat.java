package gov.cdc.dataingestion.nbs.ecr.model.cases;

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
 * */
@SuppressWarnings({"java:S1118","java:S125", "java:S6126"})
public class CdaCaseParticipantRepeat extends  CdaCaseParticipant{
    int repeatComponentCounter;
    int answerGroupCounter;
    int questionGroupCounter;
    int sectionCounter;

}
