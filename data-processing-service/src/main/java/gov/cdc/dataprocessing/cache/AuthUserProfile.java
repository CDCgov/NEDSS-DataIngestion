package gov.cdc.dataprocessing.cache;


import gov.cdc.dataprocessing.service.model.auth_user.AuthUserProfileInfo;

public class AuthUserProfile {
    private AuthUserProfile() {
        // for SONARQ
    }
    public static AuthUserProfileInfo authUserProfileInfo; // NOSONAR
}