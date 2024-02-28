package gov.cdc.dataprocessing.utilities.auth;

import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;

public class AuthUtil {
    public static AuthUser authUser;

    public static void setGlobalAuthUser(AuthUser user) {
        authUser = user;
    }
}
