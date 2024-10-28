package gov.cdc.dataingestion.nbs.ecr.model.shares;

import gov.cdc.dataingestion.nbs.repository.model.dto.lookup.PhdcQuestionLookUpDto;
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
public class Observation {
    PhdcQuestionLookUpDto questionLup;
    String defaultQuestionIdentifier;
    String questionCode;
}
