package gov.cdc.dataprocessing.repository.nbs.odse.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "Entity_id")
public class EntityId {

    @Id
    @Column(name = "entity_uid", nullable = false)
    private Long entityUid;

    @Column(name = "entity_id_seq", nullable = false)
    private Short entityIdSeq;

    @Column(name = "add_reason_cd", length = 20)
    private String addReasonCode;

    @Column(name = "add_time")
    private Date addTime;

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
    private Date effectiveFromTime;

    @Column(name = "effective_to_time")
    private Date effectiveToTime;

    @Column(name = "last_chg_reason_cd", length = 20)
    private String lastChangeReasonCode;

    @Column(name = "last_chg_time")
    private Date lastChangeTime;

    @Column(name = "last_chg_user_id")
    private Long lastChangeUserId;

    @Column(name = "record_status_cd", length = 20)
    private String recordStatusCode;

    @Column(name = "record_status_time")
    private Date recordStatusTime;

    @Column(name = "root_extension_txt", length = 100)
    private String rootExtensionText;

    @Column(name = "status_cd", length = 1)
    private Character statusCode;

    @Column(name = "status_time")
    private Date statusTime;

    @Column(name = "type_cd", length = 50)
    private String typeCode;

    @Column(name = "type_desc_txt", length = 100)
    private String typeDescriptionText;

    @Column(name = "user_affiliation_txt", length = 20)
    private String userAffiliationText;

    @Column(name = "valid_from_time")
    private Date validFromTime;

    @Column(name = "valid_to_time")
    private Date validToTime;

    @Column(name = "as_of_date")
    private Date asOfDate;

    @Column(name = "assigning_authority_id_type", length = 50)
    private String assigningAuthorityIdType;
//
//    @ManyToOne
//    @JoinColumn(name = "entity_uid", referencedColumnName = "entityUid", insertable = false, updatable = false)
//    private Entity entity;

    // Constructors and other methods (if needed)
}
