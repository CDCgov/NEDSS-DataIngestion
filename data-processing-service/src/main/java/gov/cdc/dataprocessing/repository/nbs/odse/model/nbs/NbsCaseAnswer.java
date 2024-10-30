package gov.cdc.dataprocessing.repository.nbs.odse.model.nbs;

import gov.cdc.dataprocessing.model.dto.nbs.NbsCaseAnswerDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Table(name = "NBS_case_answer")
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
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201", "java:S1192"})
public class NbsCaseAnswer {

    @Id
    @Column(name = "nbs_case_answer_uid")
    private Long id;

    @Column(name = "act_uid")
    private Long actUid;

    @Column(name = "add_time")
    private Timestamp addTime;

    @Column(name = "add_user_id")
    private Long addUserId;

    @Column(name = "answer_txt", length = 2000)
    private String answerTxt;

    @Column(name = "nbs_question_uid")
    private Long nbsQuestionUid;

    @Column(name = "nbs_question_version_ctrl_nbr")
    private Integer nbsQuestionVersionCtrlNbr;

    @Column(name = "last_chg_time")
    private Timestamp lastChgTime;

    @Column(name = "last_chg_user_id")
    private Long lastChgUserId;

    @Column(name = "record_status_cd", length = 20)
    private String recordStatusCd;

    @Column(name = "record_status_time")
    private Timestamp recordStatusTime;

    @Column(name = "seq_nbr")
    private Integer seqNbr;

    @Column(name = "answer_large_txt", columnDefinition = "text")
    private String answerLargeTxt;

    @Column(name = "nbs_table_metadata_uid")
    private Long nbsTableMetadataUid;

    @Column(name = "nbs_ui_metadata_ver_ctrl_nbr")
    private Integer nbsUiMetadataVerCtrlNbr;

    @Column(name = "answer_group_seq_nbr")
    private Integer answerGroupSeqNbr;

    public NbsCaseAnswer() {

    }

    public NbsCaseAnswer(NbsCaseAnswerDto nbsCaseAnswerDto) {
        this.actUid = nbsCaseAnswerDto.getActUid();
        this.addTime = nbsCaseAnswerDto.getAddTime();
        this.addUserId = nbsCaseAnswerDto.getAddUserId();
        this.answerTxt = nbsCaseAnswerDto.getAnswerTxt();
        this.nbsQuestionUid = nbsCaseAnswerDto.getNbsQuestionUid();
        this.nbsQuestionVersionCtrlNbr = nbsCaseAnswerDto.getNbsQuestionVersionCtrlNbr();
        this.lastChgTime = nbsCaseAnswerDto.getLastChgTime();
        this.lastChgUserId = nbsCaseAnswerDto.getLastChgUserId();
        this.recordStatusCd = nbsCaseAnswerDto.getRecordStatusCd();
        this.recordStatusTime = nbsCaseAnswerDto.getRecordStatusTime();
        this.seqNbr = nbsCaseAnswerDto.getSeqNbr();
        this.answerLargeTxt = nbsCaseAnswerDto.getAnswerLargeTxt().toString();
        this.nbsTableMetadataUid = nbsCaseAnswerDto.getNbsTableMetadataUid();
        this.nbsUiMetadataVerCtrlNbr = nbsCaseAnswerDto.getNbsQuestionVersionCtrlNbr();
        this.answerGroupSeqNbr = nbsCaseAnswerDto.getAnswerGroupSeqNbr();
    }
}
