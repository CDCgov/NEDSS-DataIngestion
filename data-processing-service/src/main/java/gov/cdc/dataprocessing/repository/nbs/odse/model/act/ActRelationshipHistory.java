package gov.cdc.dataprocessing.repository.nbs.odse.model.act;

import gov.cdc.dataprocessing.model.dto.act.ActRelationshipDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.id_class.ActRelationshipId;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "Act_relationship_hist")
@IdClass(ActRelationshipId.class)
@Data

public class ActRelationshipHistory implements Serializable {
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

    @Column(name = "version_ctrl_nbr")
    private Integer versionCrl;


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

    @Column(name = "status_cd")
    private String statusCd;

    @Column(name = "status_time")
    private Timestamp statusTime;

    @Column(name = "source_class_cd")
    private String sourceClassCd;

    @Column(name = "target_class_cd")
    private String targetClassCd;

    @Column(name = "to_time")
    private Timestamp toTime;

    @Column(name = "type_desc_txt")
    private String typeDescTxt;

    @Column(name = "user_affiliation_txt")
    private String userAffiliationTxt;

    public ActRelationshipHistory() {

    }

    public ActRelationshipHistory(ActRelationshipDto actRelationshipDto) {
        this.setSourceActUid(actRelationshipDto.getSourceActUid());
        this.setTargetActUid(actRelationshipDto.getTargetActUid());
        this.setTypeCd(actRelationshipDto.getTypeCd());
        this.setVersionCrl(1);
        this.addReasonCd = actRelationshipDto.getAddReasonCd();
        this.addTime = actRelationshipDto.getAddTime();
        this.addUserId = actRelationshipDto.getAddUserId();
        this.durationAmt = actRelationshipDto.getDurationAmt();
        this.durationUnitCd = actRelationshipDto.getDurationUnitCd();
        this.fromTime = actRelationshipDto.getFromTime();
        this.lastChgReasonCd = actRelationshipDto.getLastChgReasonCd();
        this.lastChgTime = actRelationshipDto.getLastChgTime();
        this.lastChgUserId = actRelationshipDto.getLastChgUserId();
        this.recordStatusCd = actRelationshipDto.getRecordStatusCd();
        this.recordStatusTime = actRelationshipDto.getRecordStatusTime();
        this.sequenceNbr = actRelationshipDto.getSequenceNbr();
        this.statusCd = actRelationshipDto.getStatusCd();
        this.statusTime = actRelationshipDto.getStatusTime();
        this.sourceClassCd = actRelationshipDto.getSourceClassCd();
        this.targetClassCd = actRelationshipDto.getTargetClassCd();
        this.toTime = actRelationshipDto.getToTime();
        this.typeDescTxt = actRelationshipDto.getTypeDescTxt();
        this.userAffiliationTxt = actRelationshipDto.getUserAffiliationTxt();
    }
}
