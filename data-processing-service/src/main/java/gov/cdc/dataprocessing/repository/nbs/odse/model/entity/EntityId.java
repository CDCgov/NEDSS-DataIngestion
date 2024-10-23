package gov.cdc.dataprocessing.repository.nbs.odse.model.entity;

import gov.cdc.dataprocessing.model.dto.entity.EntityIdDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.id_class.EntityIdId;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

import static gov.cdc.dataprocessing.utilities.time.TimeStampUtil.getCurrentTimeStamp;


@Data
@Entity
@Table(name = "Entity_id")
@IdClass(EntityIdId.class)
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
 6809 - TEST
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S6809"})
public class EntityId {

    @Id
    @Column(name = "entity_uid", nullable = false)
    private Long entityUid;

    @Id
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
    public EntityId(EntityIdDto entityIdDto) {
        var timestamp = getCurrentTimeStamp();
        this.entityUid = entityIdDto.getEntityUid();
        this.entityIdSeq = entityIdDto.getEntityIdSeq();
        this.addReasonCode = entityIdDto.getAddReasonCd();
        this.addTime = entityIdDto.getAddTime();
        this.addUserId = entityIdDto.getAddUserId();
        this.assigningAuthorityCode = entityIdDto.getAssigningAuthorityCd();
        this.assigningAuthorityDescription = entityIdDto.getAssigningAuthorityDescTxt();
        this.durationAmount = entityIdDto.getDurationAmt();
        this.durationUnitCode = entityIdDto.getDurationUnitCd();
        this.effectiveFromTime = entityIdDto.getEffectiveFromTime();
        this.effectiveToTime = entityIdDto.getEffectiveToTime();
        this.lastChangeReasonCode = entityIdDto.getLastChgReasonCd();
        this.lastChangeTime = entityIdDto.getLastChgTime();
        this.lastChangeUserId = entityIdDto.getLastChgUserId();
        this.recordStatusCode = entityIdDto.getRecordStatusCd();
        if (entityIdDto.getRecordStatusTime() == null) {
            this.recordStatusTime = timestamp;
        } else {
            this.recordStatusTime = entityIdDto.getRecordStatusTime();
        }
        this.rootExtensionText = entityIdDto.getRootExtensionTxt();
        this.statusCode = entityIdDto.getStatusCd();
        if (entityIdDto.getStatusTime() == null) {
            this.statusTime = timestamp;
        } else {
            this.statusTime = entityIdDto.getStatusTime();
        }
        this.typeCode = entityIdDto.getTypeCd();
        this.typeDescriptionText = entityIdDto.getTypeDescTxt();
        this.userAffiliationText = entityIdDto.getUserAffiliationTxt();
        this.validFromTime = entityIdDto.getValidFromTime();
        this.validToTime = entityIdDto.getValidToTime();
        this.asOfDate = entityIdDto.getAsOfDate();
        this.assigningAuthorityIdType = entityIdDto.getAssigningAuthorityIdType();
    }

}
