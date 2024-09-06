package gov.cdc.nbsDedup.utilities.auth;


import gov.cdc.nbsDedup.nbs.odse.model.auth.AuthUser;
import gov.cdc.nbsDedup.nbs.odse.model.auth.AuthUserRealizedRole;
import gov.cdc.nbsDedup.service.model.auth_user.AuthUserProfileInfo;

import java.util.Collection;

public class AuthUtil {
    public static AuthUser authUser;
    public static Collection<AuthUserRealizedRole> authUserRealizedRoleCollection;

    public static void setGlobalAuthUser(AuthUserProfileInfo user) {
        authUser = user.getAuthUser();
        authUserRealizedRoleCollection = user.getAuthUserRealizedRoleCollection();
    }
}
