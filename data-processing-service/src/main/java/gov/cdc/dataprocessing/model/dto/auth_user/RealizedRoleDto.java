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
 135 - for loop
 117 - naming
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201", "java:S1192", "java:S135", "java:S117"})
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
