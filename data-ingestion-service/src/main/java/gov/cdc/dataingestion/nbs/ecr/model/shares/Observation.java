package gov.cdc.dataingestion.nbs.ecr.model.shares;

import gov.cdc.dataingestion.nbs.repository.model.dto.lookup.PhdcQuestionLookUpDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class Observation {
    PhdcQuestionLookUpDto questionLup;
    String defaultQuestionIdentifier;
    String questionCode;
}
