package gov.cdc.dataprocessing.repository.nbs.odse.model.nbs;

import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.NbsAnswerDT;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Table(name = "nbs_answer_hist")
@Data
public class NbsAnswerHist {


    @Id
    @Column(name = "nbs_answer_uid")
    private Long nbsAnswerUid;

    @Column(name = "act_uid")
    private Long actUid;

    @Column(name = "answer_txt", length = 2000)
    private String answerTxt;

    @Column(name = "nbs_question_uid")
    private Long nbsQuestionUid;

    @Column(name = "nbs_question_version_ctrl_nbr")
    private Integer nbsQuestionVersionCtrlNbr;

    @Column(name = "seq_nbr")
    private Integer seqNbr;

    @Column(name = "answer_large_txt")
    private String answerLargeTxt;

    @Column(name = "answer_group_seq_nbr")
    private Integer answerGroupSeqNbr;

    @Column(name = "record_status_cd", length = 20)
    private String recordStatusCd;

    @Column(name = "record_status_time")
    private Timestamp recordStatusTime;

    @Column(name = "last_chg_time")
    private Timestamp lastChgTime;

    @Column(name = "last_chg_user_id")
    private Long lastChgUserId;

    public NbsAnswerHist() {

    }

    public NbsAnswerHist(NbsAnswerDT nbsAnswerDT) {
        this.nbsAnswerUid = nbsAnswerDT.getNbsAnswerUid();
        this.actUid = nbsAnswerDT.getActUid();
        this.answerTxt = nbsAnswerDT.getAnswerTxt();
        this.nbsQuestionUid = nbsAnswerDT.getNbsQuestionUid();
        this.nbsQuestionVersionCtrlNbr = nbsAnswerDT.getNbsQuestionVersionCtrlNbr();
        this.seqNbr = nbsAnswerDT.getSeqNbr();
//        this.answerLargeTxt = nbsAnswerDT.getAnswerLargeTxt();
        this.answerGroupSeqNbr = nbsAnswerDT.getAnswerGroupSeqNbr();
        this.recordStatusCd = nbsAnswerDT.getRecordStatusCd();
        this.recordStatusTime = nbsAnswerDT.getRecordStatusTime();
        this.lastChgTime = nbsAnswerDT.getLastChgTime();
        this.lastChgUserId = nbsAnswerDT.getLastChgUserId();
    }
}
