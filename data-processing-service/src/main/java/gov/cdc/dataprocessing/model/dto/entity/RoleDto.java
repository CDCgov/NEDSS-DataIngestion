package gov.cdc.dataprocessing.model.dto.entity;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.Role;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 6541 - brain method complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541"})
public class RoleDto extends BaseContainer {
    private Long roleSeq;
    private String addReasonCd;
    private Timestamp addTime;
    private Long addUserId;
    private String cd;
    private String cdDescTxt;
    //private String classCd;
    private String effectiveDurationAmt;
    private String effectiveDurationUnitCd;
    private Timestamp effectiveFromTime;
    private Timestamp effectiveToTime;
    private String lastChgReasonCd;
    private Timestamp lastChgTime;
    private Long lastChgUserId;
    private String recordStatusCd;
    private Timestamp recordStatusTime;
    private String scopingRoleCd;
    private String statusCd;
    private Timestamp statusTime;
    private String userAffiliationTxt;
    private Long scopingEntityUid;
    private Integer scopingRoleSeq;
    private Long subjectEntityUid;
    private String scopingClassCd;
    private String subjectClassCd;

    public RoleDto() {

    }
    public RoleDto(Role role) {
        this.roleSeq = role.getRoleSeq();
        this.addReasonCd = role.getAddReasonCode();
        this.addTime = role.getAddTime();
        this.addUserId = role.getAddUserId();
        this.cd = role.getCode();
        this.cdDescTxt = role.getCodeDescription();
        this.effectiveDurationAmt = role.getEffectiveDurationAmount();
        this.effectiveDurationUnitCd = role.getEffectiveDurationUnitCode();
        this.effectiveFromTime = role.getEffectiveFromTime();
        this.effectiveToTime = role.getEffectiveToTime();
        this.lastChgReasonCd = role.getLastChangeReasonCode();
        this.lastChgTime = role.getLastChangeTime();
        this.lastChgUserId = role.getLastChangeUserId();
        this.recordStatusCd = role.getRecordStatusCode();
        this.recordStatusTime = role.getRecordStatusTime();
        this.scopingRoleCd = role.getScopingRoleCode();
        this.statusCd = role.getStatusCode();
        this.statusTime = role.getStatusTime();
        this.userAffiliationTxt = role.getUserAffiliationText();
        this.scopingEntityUid = role.getScopingEntityUid();
        this.scopingRoleSeq = role.getScopingRoleSeq();
        this.subjectEntityUid = role.getSubjectEntityUid();
        this.scopingClassCd = role.getScopingClassCode();
        this.subjectClassCd = role.getSubjectClassCode();
    }


}
