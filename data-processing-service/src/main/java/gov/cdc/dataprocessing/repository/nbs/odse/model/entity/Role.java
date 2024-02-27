package gov.cdc.dataprocessing.repository.nbs.odse.model.entity;

import gov.cdc.dataprocessing.model.classic_model.dto.RoleDT;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;


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
    private Timestamp addTime;

    @Column(name = "add_user_id")
    private Long addUserId;

    @Column(name = "cd_desc_txt", length = 100)
    private String codeDescription;

    @Column(name = "effective_duration_amt", length = 20)
    private String effectiveDurationAmount;

    @Column(name = "effective_duration_unit_cd", length = 20)
    private String effectiveDurationUnitCode;

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

    @Column(name = "scoping_class_cd", length = 10)
    private String scopingClassCode;

    @Column(name = "scoping_entity_uid")
    private Long scopingEntityUid;

    @Column(name = "scoping_role_cd", length = 20)
    private String scopingRoleCode;

    @Column(name = "scoping_role_seq")
    private Integer scopingRoleSeq;

    @Column(name = "status_cd", length = 1, nullable = false)
    private String statusCode;

    @Column(name = "status_time")
    private Timestamp statusTime;

    @Column(name = "subject_class_cd", length = 10)
    private String subjectClassCode;

    @Column(name = "user_affiliation_txt", length = 20)
    private String userAffiliationText;
//
//    @ManyToOne
//    @JoinColumn(name = "subject_entity_uid", referencedColumnName = "entity_uid", insertable = false, updatable = false)
//    private Entity entity;

    public Role() {

    }
    public Role(RoleDT roleDT) {
        this.subjectEntityUid = roleDT.getSubjectEntityUid();
        this.code = roleDT.getCd();
        this.roleSeq = roleDT.getRoleSeq();
        this.addReasonCode = roleDT.getAddReasonCd();
        this.addTime = roleDT.getAddTime();
        this.addUserId = roleDT.getAddUserId();
        this.codeDescription = roleDT.getCdDescTxt();
        this.effectiveDurationAmount = roleDT.getEffectiveDurationAmt();
        this.effectiveDurationUnitCode = roleDT.getEffectiveDurationUnitCd();
        this.effectiveFromTime = roleDT.getEffectiveFromTime();
        this.effectiveToTime = roleDT.getEffectiveToTime();
        this.lastChangeReasonCode = roleDT.getLastChgReasonCd();
        this.lastChangeTime = roleDT.getLastChgTime();
        this.lastChangeUserId = roleDT.getLastChgUserId();
        this.recordStatusCode = roleDT.getRecordStatusCd();
        this.recordStatusTime = roleDT.getRecordStatusTime();
        this.scopingClassCode = roleDT.getScopingClassCd();
        this.scopingEntityUid = roleDT.getScopingEntityUid();
        this.scopingRoleCode = roleDT.getScopingRoleCd();
        this.scopingRoleSeq = roleDT.getScopingRoleSeq();
        this.statusCode = roleDT.getStatusCd();
        this.statusTime = roleDT.getStatusTime();
        this.subjectClassCode = roleDT.getSubjectClassCd();
        this.userAffiliationText = roleDT.getUserAffiliationTxt();
    }
}
