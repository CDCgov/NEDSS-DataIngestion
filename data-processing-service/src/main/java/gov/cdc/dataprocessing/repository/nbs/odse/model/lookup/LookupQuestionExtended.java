package gov.cdc.dataprocessing.repository.nbs.odse.model.lookup;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
 1118 - Private constructor complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118"})
public class LookupQuestionExtended extends LookupQuestion {
    private String fromAnsCodeSystemCd;
    private String toAnsCodeSystemCd;
    private Long lookupAnswerUid;
    private String fromAnswerCode;
    private String toAnswerCode;
}
