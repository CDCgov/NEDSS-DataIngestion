package gov.cdc.dataingestion.nbs.ecr.model.cases;

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
public class CdaCaseParticipantRepeat extends  CdaCaseParticipant{
    int repeatComponentCounter;
    int answerGroupCounter;
    int questionGroupCounter;
    int sectionCounter;

}
