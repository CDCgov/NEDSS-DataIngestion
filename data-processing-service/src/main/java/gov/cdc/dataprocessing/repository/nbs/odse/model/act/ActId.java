package gov.cdc.dataprocessing.repository.nbs.odse.model.act;

import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.ActIdDT;
import gov.cdc.dataprocessing.repository.nbs.odse.model.id_class.ActIdId;
import gov.cdc.dataprocessing.repository.nbs.odse.model.id_class.NNDActivityLogId;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "Act_id")
@Data
@IdClass(ActIdId.class)
public class ActId implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "act_uid")
    private Long actUid;
    @Id
    @Column(name = "act_id_seq")
    private Integer actIdSeq;

    @Column(name = "add_reason_cd")
    private String addReasonCd;

    @Column(name = "add_time")
    private Timestamp addTime;

    @Column(name = "add_user_id")
    private Long addUserId;

    @Column(name = "assigning_authority_cd")
    private String assigningAuthorityCd;

    @Column(name = "assigning_authority_desc_txt")
    private String assigningAuthorityDescTxt;

    @Column(name = "duration_amt")
    private String durationAmt;

    @Column(name = "duration_unit_cd")
    private String durationUnitCd;

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

    @Column(name = "root_extension_txt")
    private String rootExtensionTxt;

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

    @Column(name = "valid_from_time")
    private Timestamp validFromTime;

    @Column(name = "valid_to_time")
    private Timestamp validToTime;


    // Constructors, getters, and setters (Lombok-generated)


    public ActId() {

    }

    public ActId(ActIdDT actIdDT) {
        this.actUid = actIdDT.getActUid();
        this.actIdSeq = actIdDT.getActIdSeq();
        this.addReasonCd = actIdDT.getAddReasonCd();
        this.addTime = actIdDT.getAddTime();
        this.addUserId = actIdDT.getAddUserId();
        this.assigningAuthorityCd = actIdDT.getAssigningAuthorityCd();
        this.assigningAuthorityDescTxt = actIdDT.getAssigningAuthorityDescTxt();
        this.durationAmt = actIdDT.getDurationAmt();
        this.durationUnitCd = actIdDT.getDurationUnitCd();
        this.lastChgReasonCd = actIdDT.getLastChgReasonCd();
        this.lastChgTime = actIdDT.getLastChgTime();
        this.lastChgUserId = actIdDT.getLastChgUserId();
        this.recordStatusCd = actIdDT.getRecordStatusCd();
        this.recordStatusTime = actIdDT.getRecordStatusTime();
        this.rootExtensionTxt = actIdDT.getRootExtensionTxt();
        this.statusCd = actIdDT.getStatusCd();
        this.statusTime = actIdDT.getStatusTime();
        this.typeCd = actIdDT.getTypeCd();
        this.typeDescTxt = actIdDT.getTypeDescTxt();
        this.userAffiliationTxt = actIdDT.getUserAffiliationTxt();
        this.validFromTime = actIdDT.getValidFromTime();
        this.validToTime = actIdDT.getValidToTime();
    }
}
