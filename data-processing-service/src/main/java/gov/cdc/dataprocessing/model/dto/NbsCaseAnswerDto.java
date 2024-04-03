package gov.cdc.dataprocessing.model.dto;

import gov.cdc.dataprocessing.model.dto.nbs.NbsAnswerDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NbsCaseAnswerDto extends NbsAnswerDto {
    private static final long serialVersionUID = 1L;
    private Long nbsCaseAnswerUid;
    private Long nbsTableMetadataUid;
    private String code;
    private String value;
    private String type;
    private String OtherType;
    private boolean updateNbsQuestionUid;

    public NbsCaseAnswerDto() {
    }

//    public NbsCaseAnswerDto(NbsAnswerDto answerDT) {
//        super(answerDT);
//        if (answerDT.getNbsAnswerUid() != null)
//            nbsCaseAnswerUid = answerDT.getNbsAnswerUid();
//    }
}
