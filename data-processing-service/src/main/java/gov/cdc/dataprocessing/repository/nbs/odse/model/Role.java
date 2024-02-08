package gov.cdc.dataprocessing.repository.nbs.odse.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "Role")
public class Role {

    @Id
    @Column(name = "subject_entity_uid", nullable = false)
    private Long subjectEntityUid;

    @Column(name = "cd", length = 40, nullable = false)
    private String code;

    @Column(name = "role_seq", nullable = false)
    private Long roleSeq;

    @Column(name = "add_reason_cd", length = 20)
    private String addReasonCode;

    @Column(name = "add_time")
    private Date addTime;

    @Column(name = "add_user_id")
    private Long addUserId;

    @Column(name = "cd_desc_txt", length = 100)
    private String codeDescription;

    @Column(name = "effective_duration_amt", length = 20)
    private String effectiveDurationAmount;

    @Column(name = "effective_duration_unit_cd", length = 20)
    private String effectiveDurationUnitCode;

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

    @Column(name = "scoping_class_cd", length = 10)
    private String scopingClassCode;

    @Column(name = "scoping_entity_uid")
    private Long scopingEntityUid;

    @Column(name = "scoping_role_cd", length = 20)
    private String scopingRoleCode;

    @Column(name = "scoping_role_seq")
    private Short scopingRoleSeq;

    @Column(name = "status_cd", length = 1, nullable = false)
    private Character statusCode;

    @Column(name = "status_time")
    private Date statusTime;

    @Column(name = "subject_class_cd", length = 10)
    private String subjectClassCode;

    @Column(name = "user_affiliation_txt", length = 20)
    private String userAffiliationText;
//
//    @ManyToOne
//    @JoinColumn(name = "subject_entity_uid", referencedColumnName = "entity_uid", insertable = false, updatable = false)
//    private Entity entity;

}
