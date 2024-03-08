package gov.cdc.dataprocessing.repository.nbs.odse.model.participation;

import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.ParticipationDT;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import gov.cdc.dataprocessing.repository.nbs.odse.model.id_class.ParticipationId;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Entity
@IdClass(ParticipationId.class)
@Table(name = "Participation")
public class Participation {

    @Id
    @Column(name = "subject_entity_uid", nullable = false)
    private Long subjectEntityUid;

    @Id
    @Column(name = "act_uid", nullable = false)
    private Long actUid;

    @Id
    @Column(name = "type_cd", length = 50, nullable = false)
    private String typeCode;

    @Column(name = "act_class_cd", length = 10)
    private String actClassCode;

    @Column(name = "add_reason_cd", length = 20)
    private String addReasonCode;

    @Column(name = "add_time")
    private Timestamp addTime;

    @Column(name = "add_user_id")
    private Long addUserId;

    @Column(name = "awareness_cd", length = 20)
    private String awarenessCode;

    @Column(name = "awareness_desc_txt", length = 100)
    private String awarenessDescription;

    @Column(name = "cd", length = 40)
    private String code;

    @Column(name = "duration_amt", length = 20)
    private String durationAmount;

    @Column(name = "duration_unit_cd", length = 20)
    private String durationUnitCode;

    @Column(name = "from_time")
    private Timestamp fromTime;

    @Column(name = "last_chg_reason_cd", length = 20)
    private String lastChangeReasonCode;

    @Column(name = "last_chg_time")
    private Timestamp lastChangeTime;

    @Column(name = "last_chg_user_id")
    private Long lastChangeUserId;

    @Column(name = "record_status_cd", length = 20)
    private String recordStatusCode;

    @Column(name = "record_status_time")
    private Timestamp recordStatusTime;

    @Column(name = "role_seq")
    private Long roleSeq;

    @Column(name = "status_cd", length = 1)
    private String statusCode;

    @Column(name = "status_time")
    private Timestamp statusTime;

    @Column(name = "subject_class_cd", length = 10)
    private String subjectClassCode;

    @Column(name = "to_time")
    private Timestamp toTime;

    @Column(name = "type_desc_txt", length = 100)
    private String typeDescription;

    @Column(name = "user_affiliation_txt", length = 20)
    private String userAffiliationText;

    public Participation() {
    }

    public Participation(ParticipationDT participationDT) {
        this.subjectEntityUid = participationDT.getSubjectEntityUid();
        this.actUid = participationDT.getActUid();
        this.typeCode = participationDT.getTypeCd();
        this.actClassCode = participationDT.getActClassCd();
        this.addReasonCode = participationDT.getAddReasonCd();
        this.addTime = participationDT.getAddTime();
        this.addUserId = participationDT.getAddUserId();
        this.awarenessCode = participationDT.getAwarenessCd();
        this.awarenessDescription = participationDT.getAwarenessDescTxt();
        this.code = participationDT.getCd();
        this.durationAmount = participationDT.getDurationAmt();
        this.durationUnitCode = participationDT.getDurationUnitCd();
        this.fromTime = participationDT.getFromTime();
        this.lastChangeReasonCode = participationDT.getLastChgReasonCd();
        this.lastChangeTime = participationDT.getLastChgTime();
        this.lastChangeUserId = participationDT.getLastChgUserId();
        this.recordStatusCode = participationDT.getRecordStatusCd();
        this.recordStatusTime = participationDT.getRecordStatusTime();
        this.roleSeq = participationDT.getRoleSeq();
        this.statusCode = participationDT.getStatusCd();
        this.statusTime = participationDT.getStatusTime();
        this.subjectClassCode = participationDT.getSubjectClassCd();
        this.toTime = participationDT.getToTime();
        this.typeDescription = participationDT.getTypeDescTxt();
        this.userAffiliationText = participationDT.getUserAffiliationTxt();
    }
}
