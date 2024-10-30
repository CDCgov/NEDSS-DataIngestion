package gov.cdc.dataprocessing.repository.nbs.odse.model.entity;

import gov.cdc.dataprocessing.model.dto.entity.RoleDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.sql.Timestamp;


@Data
@Entity
@Table(name = "Role")
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
 1195 - duplicate complaint
 1135 - Todos complaint
 6201 - instanceof check
 1192 - duplicate literal
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201", "java:S1192"})
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
    public Role(RoleDto roleDto) {
        this.subjectEntityUid = roleDto.getSubjectEntityUid();
        this.code = roleDto.getCd();
        this.roleSeq = roleDto.getRoleSeq();
        this.addReasonCode = roleDto.getAddReasonCd();
        this.addTime = roleDto.getAddTime();
        this.addUserId = roleDto.getAddUserId();
        this.codeDescription = roleDto.getCdDescTxt();
        this.effectiveDurationAmount = roleDto.getEffectiveDurationAmt();
        this.effectiveDurationUnitCode = roleDto.getEffectiveDurationUnitCd();
        this.effectiveFromTime = roleDto.getEffectiveFromTime();
        this.effectiveToTime = roleDto.getEffectiveToTime();
        this.lastChangeReasonCode = roleDto.getLastChgReasonCd();
        this.lastChangeTime = roleDto.getLastChgTime();
        this.lastChangeUserId = roleDto.getLastChgUserId();
        this.recordStatusCode = roleDto.getRecordStatusCd();
        this.recordStatusTime = roleDto.getRecordStatusTime();
        this.scopingClassCode = roleDto.getScopingClassCd();
        this.scopingEntityUid = roleDto.getScopingEntityUid();
        this.scopingRoleCode = roleDto.getScopingRoleCd();
        this.scopingRoleSeq = roleDto.getScopingRoleSeq();
        this.statusCode = roleDto.getStatusCd();
        this.statusTime = roleDto.getStatusTime();
        this.subjectClassCode = roleDto.getSubjectClassCd();
        this.userAffiliationText = roleDto.getUserAffiliationTxt();
    }
}
