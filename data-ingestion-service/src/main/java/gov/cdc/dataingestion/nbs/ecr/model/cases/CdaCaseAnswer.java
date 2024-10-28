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
public class CdaCaseAnswer extends  CdaCaseParticipant{
    String oldQuestionId;
}
