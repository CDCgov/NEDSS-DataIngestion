package gov.cdc.dataingestion.nbs.repository.model.dao.lookup;

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
public class QuestionIdentifierMapDao
{
    private String dynamicQuestionIdentifier;
    private String questionIdentifier;
}
