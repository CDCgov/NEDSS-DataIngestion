package gov.cdc.dataprocessing.model.dto.nbs;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsAnswer;
import lombok.Getter;
import lombok.Setter;

import java.sql.Clob;
import java.sql.Timestamp;

@Getter
@Setter
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
 6809 - TEST
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S6809"})
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
