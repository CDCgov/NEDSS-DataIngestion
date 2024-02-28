package gov.cdc.dataprocessing.repository.nbs.odse.model.other_move_as_needed;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "Act_relationship")
public class ActRelationship {
    @EmbeddedId
    private ActRelationshipId id;

    @Column(name = "add_reason_cd")
    private String addReasonCd;

    @Column(name = "add_time")
    private Date addTime;

    @Column(name = "add_user_id")
    private Long addUserId;

    @Column(name = "duration_amt")
    private String durationAmt;

    @Column(name = "duration_unit_cd")
    private String durationUnitCd;

    @Column(name = "from_time")
    private Date fromTime;

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

    @Column(name = "sequence_nbr")
    private Short sequenceNbr;

    @Column(name = "source_class_cd")
    private String sourceClassCd;

    @Column(name = "status_cd")
    private Character statusCd;

    @Column(name = "status_time")
    private Date statusTime;

    @Column(name = "target_class_cd")
    private String targetClassCd;

    @Column(name = "to_time")
    private Date toTime;

    @Column(name = "type_desc_txt")
    private String typeDescTxt;

    @Column(name = "user_affiliation_txt")
    private String userAffiliationTxt;

    // Relationships if needed
}
