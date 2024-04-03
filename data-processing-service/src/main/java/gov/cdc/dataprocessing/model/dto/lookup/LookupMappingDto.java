package gov.cdc.dataprocessing.model.dto.lookup;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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

}
