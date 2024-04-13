package gov.cdc.dataprocessing.repository.nbs.odse.model;

import gov.cdc.dataprocessing.repository.nbs.odse.model.lookup.LookupQuestion;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "LOOKUP_ANSWER")
public class LookupQuestionExtended extends LookupQuestion {
    private String fromAnsCodeSystemCd;
    private String toAnsCodeSystemCd;
    private Long lookupAnswerUid;
    private String fromAnswerCode;
    private String toAnswerCode;
}
