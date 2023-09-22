package gov.cdc.dataingestion.nbs.repository.model.dao.LookUp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class QuestionIdentifierMapDao
{
    private String dynamicQuestionIdentifier;
    private String questionIdentifier;
}
