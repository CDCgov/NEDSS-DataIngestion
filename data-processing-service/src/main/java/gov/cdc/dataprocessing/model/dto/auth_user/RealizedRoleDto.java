package gov.cdc.dataprocessing.model.dto.auth_user;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUserRealizedRole;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
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
public class RealizedRoleDto extends BaseContainer
{
    private static final long serialVersionUID = 1L;
    private String roleName;
    private String programAreaCode;
    private String jurisdictionCode;
    private String oldProgramAreaCode;
    private String oldJurisdictionCode;
    private boolean guest;
    private boolean readOnly = true; // make sure that the default access for permissionset is readyonly
    private int seqNum =0;

    private String recordStatus = "";
    private String guestString ="N";


    public RealizedRoleDto() {

    }

    public RealizedRoleDto(AuthUserRealizedRole role) {
        roleName = role.getAuthRoleNm();
        programAreaCode = role.getProgAreaCd();
        jurisdictionCode = role.getJurisdictionCd();
//        recordStatus = role.getRecordStatusCd();

    }
}
