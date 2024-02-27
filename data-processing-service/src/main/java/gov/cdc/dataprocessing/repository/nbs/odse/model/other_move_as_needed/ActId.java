package gov.cdc.dataprocessing.repository.nbs.odse.model.other_move_as_needed;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "Act_id")
public class ActId {

    @Id
    @Column(name = "act_uid")
    private Long actUid;

    @Id
    @Column(name = "act_id_seq")
    private Short actIdSeq;

    @Column(name = "add_reason_cd")
    private String addReasonCd;

    @Column(name = "add_time")
    private Date addTime;

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
    private Date lastChgTime;

    @Column(name = "last_chg_user_id")
    private Long lastChgUserId;

    @Column(name = "record_status_cd")
    private String recordStatusCd;

    @Column(name = "record_status_time")
    private Date recordStatusTime;

    @Column(name = "root_extension_txt")
    private String rootExtensionTxt;

    @Column(name = "status_cd")
    private Character statusCd;

    @Column(name = "status_time")
    private Date statusTime;

    @Column(name = "type_cd")
    private String typeCd;

    @Column(name = "type_desc_txt")
    private String typeDescTxt;

    @Column(name = "user_affiliation_txt")
    private String userAffiliationTxt;

    @Column(name = "valid_from_time")
    private Date validFromTime;

    @Column(name = "valid_to_time")
    private Date validToTime;

    // Constructors, getters, and setters (Lombok-generated)

    // Define relationships, if any, with other entities using JPA annotations
}
