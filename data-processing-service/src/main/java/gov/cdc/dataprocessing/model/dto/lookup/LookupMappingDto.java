package gov.cdc.dataprocessing.model.dto.lookup;

import gov.cdc.dataprocessing.repository.nbs.odse.model.lookup.LookupQuestionExtended;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 2139 - exception rethrow complain
 3740 - parametrized  type for generic complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740"})
public class LookupMappingDto {
    private Long lookupQuestionUid;
    private String fromQuestionIdentifier;
    private String fromCodeSystemCd;
    private String fromDataType;
    private String fromFormCd;
    private String toFormCd;
    private String toQuestionIdentifier;
    private String toCodeSystemCd;
    private String toDataType;
    private Long lookupAnswerUid;
    private String fromAnswerCode;
    private String fromAnsCodeSystemCd;
    private String toAnswerCode;
    private String toAnsCodeSystemCd;

    public LookupMappingDto() {

    }

    public LookupMappingDto(LookupQuestionExtended data) {
        lookupQuestionUid = data.getId();
        fromQuestionIdentifier = data.getFromQuestionIdentifier();
        fromCodeSystemCd = data.getFromCodeSystemCd();
        fromDataType = data.getFromDataType();
        fromFormCd = data.getFromFormCd();
        toFormCd = data.getToFormCd();
        toQuestionIdentifier = data.getToQuestionIdentifier();
        toCodeSystemCd = data.getToCodeSystemCd();
        toDataType = data.getToDataType();
        lookupAnswerUid = data.getLookupAnswerUid();
        fromAnswerCode = data.getFromAnswerCode();
        fromAnsCodeSystemCd = data.getFromAnsCodeSystemCd();
        toAnswerCode = data.getToAnswerCode();
        toAnsCodeSystemCd = data.getToAnsCodeSystemCd();
    }
}
