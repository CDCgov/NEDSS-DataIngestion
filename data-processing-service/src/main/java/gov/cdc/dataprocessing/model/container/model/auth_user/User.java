package gov.cdc.dataprocessing.model.container.model.auth_user;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class User  extends BaseContainer
{
    private static final long serialVersionUID = 1L;

    private String userID;
    private String firstName;
    private String lastName;
    private String comments;
    private String status;
    private String entryID;
    private String password;
    private Long reportingFacilityUid;
    private String userType;
    private String facilityDetails;
    private String readOnly;
    private String facilityID;
    private Long providerUid;
    private String msa;
    private String paa;
    private String adminUserTypes;
    private String paaProgramArea;
    private String jurisdictionDerivationInd;
}
