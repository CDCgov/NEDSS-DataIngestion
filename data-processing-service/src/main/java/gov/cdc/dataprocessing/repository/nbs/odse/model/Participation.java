package gov.cdc.dataprocessing.repository.nbs.odse.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "Participation")
public class Participation {

    @Id
    @Column(name = "subject_entity_uid", nullable = false)
    private Long subjectEntityUid;

    @Id
    @Column(name = "act_uid", nullable = false)
    private Long actUid;

    @Id
    @Column(name = "type_cd", length = 50, nullable = false)
    private String typeCode;

    @Column(name = "act_class_cd", length = 10)
    private String actClassCode;

    @Column(name = "add_reason_cd", length = 20)
    private String addReasonCode;

    @Column(name = "add_time")
    private Date addTime;

    @Column(name = "add_user_id")
    private Long addUserId;

    @Column(name = "awareness_cd", length = 20)
    private String awarenessCode;

    @Column(name = "awareness_desc_txt", length = 100)
    private String awarenessDescription;

    @Column(name = "cd", length = 40)
    private String code;

    @Column(name = "duration_amt", length = 20)
    private String durationAmount;

    @Column(name = "duration_unit_cd", length = 20)
    private String durationUnitCode;

    @Column(name = "from_time")
    private Date fromTime;

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

    @Column(name = "role_seq")
    private Long roleSeq;

    @Column(name = "status_cd", length = 1)
    private Character statusCode;

    @Column(name = "status_time")
    private Date statusTime;

    @Column(name = "subject_class_cd", length = 10)
    private String subjectClassCode;

    @Column(name = "to_time")
    private Date toTime;

    @Column(name = "type_desc_txt", length = 100)
    private String typeDescription;

    @Column(name = "user_affiliation_txt", length = 20)
    private String userAffiliationText;

//    @ManyToOne
//    @JoinColumn(name = "act_uid", referencedColumnName = "actUid", insertable = false, updatable = false)
//    private Act act;
//
//    @ManyToOne
//    @JoinColumn(name = "subject_entity_uid", referencedColumnName = "entityUid", insertable = false, updatable = false)
//    private Entity entity;
//
//    @ManyToOne
//    @JoinColumns({
//            @JoinColumn(name = "subject_entity_uid", referencedColumnName = "subjectEntityUid", insertable = false, updatable = false),
//            @JoinColumn(name = "role_seq", referencedColumnName = "roleSeq", insertable = false, updatable = false),
//            @JoinColumn(name = "cd", referencedColumnName = "code", insertable = false, updatable = false)
//    })
//    private Role role;

}
