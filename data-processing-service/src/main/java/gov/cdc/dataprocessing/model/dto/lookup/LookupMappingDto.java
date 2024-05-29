package gov.cdc.dataprocessing.model.dto.lookup;

import gov.cdc.dataprocessing.repository.nbs.odse.model.lookup.LookupQuestionExtended;
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
