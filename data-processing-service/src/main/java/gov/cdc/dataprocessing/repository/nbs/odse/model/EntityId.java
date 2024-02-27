package gov.cdc.dataprocessing.repository.nbs.odse.model;

import gov.cdc.dataprocessing.model.classic_model.dto.EntityIdDT;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.sql.Timestamp;

import static gov.cdc.dataprocessing.utilities.time.TimeStampUtil.getCurrentTimeStamp;


@Data
@Entity
@Table(name = "Entity_id")
public class EntityId {

    @Id
    @Column(name = "entity_uid", nullable = false)
    private Long entityUid;

    @Column(name = "entity_id_seq", nullable = false)
    private Integer entityIdSeq;

    @Column(name = "add_reason_cd", length = 20)
    private String addReasonCode;

    @Column(name = "add_time")
    private Timestamp addTime;

    @Column(name = "add_user_id")
    private Long addUserId;

    @Column(name = "assigning_authority_cd", length = 199)
    private String assigningAuthorityCode;

    @Column(name = "assigning_authority_desc_txt", length = 100)
    private String assigningAuthorityDescription;

    @Column(name = "duration_amt", length = 20)
    private String durationAmount;

    @Column(name = "duration_unit_cd", length = 20)
    private String durationUnitCode;

    @Column(name = "effective_from_time")
    private Timestamp effectiveFromTime;

    @Column(name = "effective_to_time")
    private Timestamp effectiveToTime;

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

    @Column(name = "root_extension_txt", length = 100)
    private String rootExtensionText;

    @Column(name = "status_cd", length = 1)
    private String statusCode;

    @Column(name = "status_time")
    private Timestamp statusTime;

    @Column(name = "type_cd", length = 50)
    private String typeCode;

    @Column(name = "type_desc_txt", length = 100)
    private String typeDescriptionText;

    @Column(name = "user_affiliation_txt", length = 20)
    private String userAffiliationText;

    @Column(name = "valid_from_time")
    private Timestamp validFromTime;

    @Column(name = "valid_to_time")
    private Timestamp validToTime;

    @Column(name = "as_of_date")
    private Timestamp asOfDate;

    @Column(name = "assigning_authority_id_type", length = 50)
    private String assigningAuthorityIdType;
//
//    @ManyToOne
//    @JoinColumn(name = "entity_uid", referencedColumnName = "entityUid", insertable = false, updatable = false)
//    private Entity entity;

    // Constructors and other methods (if needed)
    public EntityId() {

    }
    public EntityId(EntityIdDT entityIdDT) {
        var timestamp = getCurrentTimeStamp();
        this.entityUid = entityIdDT.getEntityUid();
        this.entityIdSeq = entityIdDT.getEntityIdSeq();
        this.addReasonCode = entityIdDT.getAddReasonCd();
        this.addTime = entityIdDT.getAddTime();
        this.addUserId = entityIdDT.getAddUserId();
        this.assigningAuthorityCode = entityIdDT.getAssigningAuthorityCd();
        this.assigningAuthorityDescription = entityIdDT.getAssigningAuthorityDescTxt();
        this.durationAmount = entityIdDT.getDurationAmt();
        this.durationUnitCode = entityIdDT.getDurationUnitCd();
        this.effectiveFromTime = entityIdDT.getEffectiveFromTime();
        this.effectiveToTime = entityIdDT.getEffectiveToTime();
        this.lastChangeReasonCode = entityIdDT.getLastChgReasonCd();
        this.lastChangeTime = entityIdDT.getLastChgTime();
        this.lastChangeUserId = entityIdDT.getLastChgUserId();
        this.recordStatusCode = entityIdDT.getRecordStatusCd();
        if (entityIdDT.getRecordStatusTime() == null) {
            this.recordStatusTime = timestamp;
        } else {
            this.recordStatusTime = entityIdDT.getRecordStatusTime();
        }
        this.rootExtensionText = entityIdDT.getRootExtensionTxt();
        this.statusCode = entityIdDT.getStatusCd();
        if (entityIdDT.getStatusTime() == null) {
            this.statusTime = timestamp;
        } else {
            this.statusTime = entityIdDT.getStatusTime();
        }
        this.typeCode = entityIdDT.getTypeCd();
        this.typeDescriptionText = entityIdDT.getTypeDescTxt();
        this.userAffiliationText = entityIdDT.getUserAffiliationTxt();
        this.validFromTime = entityIdDT.getValidFromTime();
        this.validToTime = entityIdDT.getValidToTime();
        this.asOfDate = entityIdDT.getAsOfDate();
        this.assigningAuthorityIdType = entityIdDT.getAssigningAuthorityIdType();
    }

}
