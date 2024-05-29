package gov.cdc.dataprocessing.utilities.auth;

import gov.cdc.dataprocessing.repository.nbs.odse.model.AuthUserRealizedRole;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.service.model.auth_user.AuthUserProfileInfo;

import java.util.Collection;

public class AuthUtil {
    public static AuthUser authUser;
    public static Collection<AuthUserRealizedRole> authUserRealizedRoleCollection;

    public static void setGlobalAuthUser(AuthUserProfileInfo user) {
        authUser = user.getAuthUser();
        authUserRealizedRoleCollection = user.getAuthUserRealizedRoleCollection();
    }
}
