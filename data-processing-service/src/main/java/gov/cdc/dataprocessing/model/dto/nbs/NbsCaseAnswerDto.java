package gov.cdc.dataprocessing.model.dto.nbs;

import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsCaseAnswer;
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
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809"})
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


    public NbsCaseAnswerDto(NbsCaseAnswer nbsCaseAnswer) {
        this.actUid = nbsCaseAnswer.getActUid();
        this.addTime = nbsCaseAnswer.getAddTime();
        this.addUserId = nbsCaseAnswer.getAddUserId();
        this.answerTxt = nbsCaseAnswer.getAnswerTxt();
        this.nbsQuestionUid = nbsCaseAnswer.getNbsQuestionUid();
        this.nbsQuestionVersionCtrlNbr = nbsCaseAnswer.getNbsQuestionVersionCtrlNbr();
        this.lastChgTime = nbsCaseAnswer.getLastChgTime();
        this.lastChgUserId = nbsCaseAnswer.getLastChgUserId();
        this.recordStatusCd = nbsCaseAnswer.getRecordStatusCd();
        this.recordStatusTime = nbsCaseAnswer.getRecordStatusTime();
        this.seqNbr = nbsCaseAnswer.getSeqNbr();
//        this.answerLargeTxt = nbsCaseAnswer.getAnswerLargeTxt();
        this.nbsTableMetadataUid = nbsCaseAnswer.getNbsTableMetadataUid();
        this.nbsQuestionVersionCtrlNbr = nbsCaseAnswer.getNbsUiMetadataVerCtrlNbr();
        this.answerGroupSeqNbr = nbsCaseAnswer.getAnswerGroupSeqNbr();
    }


//    public NbsCaseAnswerDto(NbsAnswerDto answerDT) {
//        super(answerDT);
//        if (answerDT.getNbsAnswerUid() != null)
//            nbsCaseAnswerUid = answerDT.getNbsAnswerUid();
//    }
}
