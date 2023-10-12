package gov.cdc.dataingestion.nbs.ecr.model.cases;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class CdaCaseAnswer extends  CdaCaseParticipant{
    String oldQuestionId;
}
