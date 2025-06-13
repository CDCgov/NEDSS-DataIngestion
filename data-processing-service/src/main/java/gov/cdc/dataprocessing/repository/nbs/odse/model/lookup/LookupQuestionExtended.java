package gov.cdc.dataprocessing.repository.nbs.odse.model.lookup;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter

public class LookupQuestionExtended extends LookupQuestion {
    private String fromAnsCodeSystemCd;
    private String toAnsCodeSystemCd;
    private Long lookupAnswerUid;
    private String fromAnswerCode;
    private String toAnswerCode;
}
