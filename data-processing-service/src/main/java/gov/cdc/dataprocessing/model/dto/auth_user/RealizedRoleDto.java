package gov.cdc.dataprocessing.model.dto.auth_user;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUserRealizedRole;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SuppressWarnings("all")
public  class RealizedRoleDto extends BaseContainer
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
