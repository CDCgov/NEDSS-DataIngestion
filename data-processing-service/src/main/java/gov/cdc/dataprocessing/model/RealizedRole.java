package gov.cdc.dataprocessing.model;

import gov.cdc.dataprocessing.model.container.BaseContainer;
import gov.cdc.dataprocessing.repository.nbs.odse.model.AuthUserRealizedRole;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RealizedRole extends BaseContainer
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


    public RealizedRole() {

    }

    public RealizedRole(AuthUserRealizedRole role) {
        roleName = role.getAuthRoleNm();
        programAreaCode = role.getProgAreaCd();
        jurisdictionCode = role.getJurisdictionCd();
//        recordStatus = role.getRecordStatusCd();

    }
}
