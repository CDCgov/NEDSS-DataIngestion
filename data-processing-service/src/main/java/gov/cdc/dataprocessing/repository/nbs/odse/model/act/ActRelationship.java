package gov.cdc.dataprocessing.repository.nbs.odse.model.act;

import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.ActRelationshipDT;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "Act_relationship")
@Data
public class ActRelationship implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "source_act_uid")
    private Long sourceActUid;

    @Id
    @Column(name = "target_act_uid")
    private Long targetActUid;

    @Id
    @Column(name = "type_cd")
    private String typeCd;

    @Column(name = "add_reason_cd")
    private String addReasonCd;

    @Column(name = "add_time")
    private Timestamp addTime;

    @Column(name = "add_user_id")
    private Long addUserId;

    @Column(name = "duration_amt")
    private String durationAmt;

    @Column(name = "duration_unit_cd")
    private String durationUnitCd;

    @Column(name = "from_time")
    private Timestamp fromTime;

    @Column(name = "last_chg_reason_cd")
    private String lastChgReasonCd;

    @Column(name = "last_chg_time")
    private Timestamp lastChgTime;

    @Column(name = "last_chg_user_id")
    private Long lastChgUserId;

    @Column(name = "record_status_cd")
    private String recordStatusCd;

    @Column(name = "record_status_time")
    private Timestamp recordStatusTime;

    @Column(name = "sequence_nbr")
    private Integer sequenceNbr;

    @Column(name = "source_class_cd")
    private String sourceClassCd;

    @Column(name = "status_cd")
    private String statusCd;

    @Column(name = "status_time")
    private Timestamp statusTime;

    @Column(name = "target_class_cd")
    private String targetClassCd;

    @Column(name = "to_time")
    private Timestamp toTime;

    @Column(name = "type_desc_txt")
    private String typeDescTxt;

    @Column(name = "user_affiliation_txt")
    private String userAffiliationTxt;

    // Constructors, getters, and setters

    public ActRelationship() {

    }

    public ActRelationship(ActRelationshipDT actRelationshipDT) {
        this.addReasonCd = actRelationshipDT.getAddReasonCd();
        this.addTime = actRelationshipDT.getAddTime();
        this.addUserId = actRelationshipDT.getAddUserId();
        this.durationAmt = actRelationshipDT.getDurationAmt();
        this.durationUnitCd = actRelationshipDT.getDurationUnitCd();
        this.fromTime = actRelationshipDT.getFromTime();
        this.lastChgReasonCd = actRelationshipDT.getLastChgReasonCd();
        this.lastChgTime = actRelationshipDT.getLastChgTime();
        this.lastChgUserId = actRelationshipDT.getLastChgUserId();
        this.recordStatusCd = actRelationshipDT.getRecordStatusCd();
        this.recordStatusTime = actRelationshipDT.getRecordStatusTime();
        this.sequenceNbr = actRelationshipDT.getSequenceNbr();
        this.statusCd = actRelationshipDT.getStatusCd();
        this.statusTime = actRelationshipDT.getStatusTime();
        this.toTime = actRelationshipDT.getToTime();
        this.userAffiliationTxt = actRelationshipDT.getUserAffiliationTxt();
        this.sourceActUid = actRelationshipDT.getSourceActUid();
        this.typeDescTxt = actRelationshipDT.getTypeDescTxt();
        this.targetActUid = actRelationshipDT.getTargetActUid();
        this.sourceClassCd = actRelationshipDT.getSourceClassCd();
        this.targetClassCd = actRelationshipDT.getTargetClassCd();
        this.typeCd = actRelationshipDT.getTypeCd();
    }
}