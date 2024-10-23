package gov.cdc.dataprocessing.model.container.model.auth_user;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
 6809 - TEST
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S6809"})
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
