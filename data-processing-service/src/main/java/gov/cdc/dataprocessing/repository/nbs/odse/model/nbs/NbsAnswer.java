package gov.cdc.dataprocessing.repository.nbs.odse.model.nbs;

import gov.cdc.dataprocessing.model.dto.nbs.NbsAnswerDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Table(name = "nbs_answer")
@Data
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
 1149 - replacing HashTable complaint
 112 - throwing dedicate exception complaint
 107 - max parameter complaint
 1195 - duplicate complaint
 1135 - Todos complaint
 6201 - instanceof check
 1192 - duplicate literal
 135 - for loop
 117 - naming
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201", "java:S1192", "java:S135", "java:S117"})
public class NbsAnswer {

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

    public NbsAnswer() {

    }

    public NbsAnswer(NbsAnswerDto nbsAnswerDto) {
        this.nbsAnswerUid = nbsAnswerDto.getNbsAnswerUid();
        this.actUid = nbsAnswerDto.getActUid();
        this.answerTxt = nbsAnswerDto.getAnswerTxt();
        this.nbsQuestionUid = nbsAnswerDto.getNbsQuestionUid();
        this.nbsQuestionVersionCtrlNbr = nbsAnswerDto.getNbsQuestionVersionCtrlNbr();
        this.seqNbr = nbsAnswerDto.getSeqNbr();
//        this.answerLargeTxt = nbsAnswerDto.getAnswerLargeTxt();
        this.answerGroupSeqNbr = nbsAnswerDto.getAnswerGroupSeqNbr();
        this.recordStatusCd = nbsAnswerDto.getRecordStatusCd();
        this.recordStatusTime = nbsAnswerDto.getRecordStatusTime();
        this.lastChgTime = nbsAnswerDto.getLastChgTime();
        this.lastChgUserId = nbsAnswerDto.getLastChgUserId();
    }

}
