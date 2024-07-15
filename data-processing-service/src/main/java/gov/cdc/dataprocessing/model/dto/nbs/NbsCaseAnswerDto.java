package gov.cdc.dataprocessing.model.dto.nbs;

import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsCaseAnswer;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SuppressWarnings("all")
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
