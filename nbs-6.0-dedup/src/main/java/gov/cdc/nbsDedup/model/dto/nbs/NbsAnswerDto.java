package gov.cdc.nbsDedup.model.dto.nbs;


import gov.cdc.nbsDedup.model.container.base.BaseContainer;
import gov.cdc.nbsDedup.nbs.odse.model.nbs.NbsAnswer;
import lombok.Getter;
import lombok.Setter;

import java.sql.Clob;
import java.sql.Timestamp;

@Getter
@Setter
public class NbsAnswerDto extends BaseContainer {
    private static final long serialVersionUID = 1L;
    protected Long nbsAnswerUid;
    protected Integer seqNbr;
    protected String answerTxt;
    protected Timestamp lastChgTime;
    protected Long lastChgUserId;
    protected Long nbsQuestionUid;
    protected Integer nbsQuestionVersionCtrlNbr;
    protected String recordStatusCd;
    protected Timestamp recordStatusTime;
    protected Long actUid;
    protected Clob answerLargeTxt;

    protected Integer answerGroupSeqNbr;
    protected Timestamp addTime;
    protected Long addUserId;

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
