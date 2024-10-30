package gov.cdc.dataprocessing.repository.nbs.odse.model.act;

import gov.cdc.dataprocessing.model.dto.act.ActIdDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.id_class.ActIdId;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "Act_id")
@Data
@IdClass(ActIdId.class)
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
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107"})
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

    public ActId(ActIdDto actIdDto) {
        this.actUid = actIdDto.getActUid();
        this.actIdSeq = actIdDto.getActIdSeq();
        this.addReasonCd = actIdDto.getAddReasonCd();
        this.addTime = actIdDto.getAddTime();
        this.addUserId = actIdDto.getAddUserId();
        this.assigningAuthorityCd = actIdDto.getAssigningAuthorityCd();
        this.assigningAuthorityDescTxt = actIdDto.getAssigningAuthorityDescTxt();
        this.durationAmt = actIdDto.getDurationAmt();
        this.durationUnitCd = actIdDto.getDurationUnitCd();
        this.lastChgReasonCd = actIdDto.getLastChgReasonCd();
        this.lastChgTime = actIdDto.getLastChgTime();
        this.lastChgUserId = actIdDto.getLastChgUserId();
        this.recordStatusCd = actIdDto.getRecordStatusCd();
        this.recordStatusTime = actIdDto.getRecordStatusTime();
        this.rootExtensionTxt = actIdDto.getRootExtensionTxt();
        this.statusCd = actIdDto.getStatusCd();
        this.statusTime = actIdDto.getStatusTime();
        this.typeCd = actIdDto.getTypeCd();
        this.typeDescTxt = actIdDto.getTypeDescTxt();
        this.userAffiliationTxt = actIdDto.getUserAffiliationTxt();
        this.validFromTime = actIdDto.getValidFromTime();
        this.validToTime = actIdDto.getValidToTime();
    }
}
