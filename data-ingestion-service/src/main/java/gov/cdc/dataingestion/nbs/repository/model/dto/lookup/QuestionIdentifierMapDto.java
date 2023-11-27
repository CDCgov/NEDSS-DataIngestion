package gov.cdc.dataingestion.nbs.repository.model.dto.lookup;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class QuestionIdentifierMapDto {
    private String columnNm;
    private String questionIdentifier;
    private String dynamicQuestionIdentifier;
}
