package gov.cdc.dataprocessing.model.dto.nbs;

import gov.cdc.dataprocessing.model.container.BaseContainer;
import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsAnswer;
import lombok.Getter;
import lombok.Setter;

import java.sql.Clob;
import java.sql.Timestamp;

@Getter
@Setter
public class NbsAnswerDto extends BaseContainer {
    private static final long serialVersionUID = 1L;
    private Long nbsAnswerUid;
    private Integer seqNbr;
    private String answerTxt;
    private Timestamp lastChgTime;
    private Long lastChgUserId;
    private Long nbsQuestionUid;
    private Integer nbsQuestionVersionCtrlNbr;
    private String recordStatusCd;
    private Timestamp recordStatusTime;
    private Long actUid;
    private Clob answerLargeTxt;

    private Integer answerGroupSeqNbr;
    private Timestamp addTime;
    private Long addUserId;

    public NbsAnswerDto() {
        itDirty = false;
        itNew = true;
        itDelete = false;
    }

    public NbsAnswerDto(NbsAnswer nbsAnswer) {
        this.nbsAnswerUid = nbsAnswer.getNbsAnswerUid();
        this.actUid = nbsAnswer.getActUid();
        this.answerTxt = nbsAnswer.getAnswerTxt();
        this.nbsQuestionUid = nbsAnswer.getNbsQuestionUid();
        this.nbsQuestionVersionCtrlNbr = nbsAnswer.getNbsQuestionVersionCtrlNbr();
        this.seqNbr = nbsAnswer.getSeqNbr();
       // this.answerLargeTxt = nbsAnswer.getAnswerLargeTxt();
        this.answerGroupSeqNbr = nbsAnswer.getAnswerGroupSeqNbr();
        this.recordStatusCd = nbsAnswer.getRecordStatusCd();
        this.recordStatusTime = nbsAnswer.getRecordStatusTime();
        this.lastChgTime = nbsAnswer.getLastChgTime();
        this.lastChgUserId = nbsAnswer.getLastChgUserId();
    }

}
