package gov.cdc.dataprocessing.utilities.auth;

import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUserRealizedRole;
import gov.cdc.dataprocessing.service.model.auth_user.AuthUserProfileInfo;

import java.util.Collection;

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
public class AuthUtil {
    public static AuthUser authUser;
    public static Collection<AuthUserRealizedRole> authUserRealizedRoleCollection;

    public static void setGlobalAuthUser(AuthUserProfileInfo user) {
        authUser = user.getAuthUser();
        authUserRealizedRoleCollection = user.getAuthUserRealizedRoleCollection();
    }
}
