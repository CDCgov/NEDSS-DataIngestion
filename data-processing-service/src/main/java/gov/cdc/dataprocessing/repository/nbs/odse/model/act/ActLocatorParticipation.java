package gov.cdc.dataprocessing.repository.nbs.odse.model.act;

import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.ActivityLocatorParticipationDT;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Entity
@Table(name = "Act_locator_participation")
public class ActLocatorParticipation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "entity_uid")
    private Long entityUid;

    @Column(name = "act_uid")
    private Long actUid;

    @Column(name = "locator_uid")
    private Long locatorUid;

    @Column(name = "add_reason_cd")
    private String addReasonCd;

    @Column(name = "add_time")
    private Timestamp addTime;

    @Column(name = "add_user_id")
    private Long addUserId;

    @Column(name = "duration_amt")
    private String durationAmount;

    @Column(name = "duration_unit_cd")
    private String durationUnitCd;

    @Column(name = "from_time")
    private Timestamp fromTime;

    @Column(name = "last_chg_reason_cd")
    private String lastChangeReasonCd;

    @Column(name = "last_chg_time")
    private Timestamp lastChangeTime;

    @Column(name = "last_chg_user_id")
    private Long lastChangeUserId;

    @Column(name = "record_status_cd")
    private String recordStatusCd;

    @Column(name = "record_status_time")
    private Timestamp recordStatusTime;

    @Column(name = "to_time")
    private Timestamp toTime;

    @Column(name = "status_cd")
    private String statusCd;

    @Column(name = "status_time")
    private Timestamp statusTime;

    @Column(name = "type_cd")
    private String typeCd;

    @Column(name = "type_desc_txt")
    private String typeDescTxt;

    @Column(name = "user_affiliation_txt")
    private String userAffiliationTxt;

    // Constructors, getters, setters, and other methods can be added here
    public ActLocatorParticipation() {

    }

    public ActLocatorParticipation(ActivityLocatorParticipationDT dto) {
        this.actUid = dto.getActUid();
        this.locatorUid = dto.getLocatorUid();
        this.entityUid = dto.getEntityUid();
        this.addReasonCd = dto.getAddReasonCd();
        this.addTime = dto.getAddTime();
        this.addUserId = dto.getAddUserId();
        this.durationAmount = dto.getDurationAmt();
        this.durationUnitCd = dto.getDurationUnitCd();
        this.fromTime = dto.getFromTime();
        this.lastChangeReasonCd = dto.getLastChgReasonCd();
        this.lastChangeTime = dto.getLastChgTime();
        this.lastChangeUserId = dto.getLastChgUserId();
        this.recordStatusCd = dto.getRecordStatusCd();
        this.recordStatusTime = dto.getRecordStatusTime();
        this.toTime = dto.getToTime();
        this.statusCd = dto.getStatusCd();
        this.statusTime = dto.getStatusTime();
        this.typeCd = dto.getTypeCd();
        this.typeDescTxt = dto.getTypeDescTrxt(); // Check this field name
        this.userAffiliationTxt = dto.getUserAffiliationTxt();
    }

}