package gov.cdc.dataprocessing.service.implementation.auth_user;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.CustomAuthUserRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.auth.AuthUserRepository;
import gov.cdc.dataprocessing.service.interfaces.auth_user.IAuthUserService;
import gov.cdc.dataprocessing.service.model.auth_user.AuthUserProfileInfo;
import org.springframework.stereotype.Service;

@Service
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
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
public class AuthUserService implements IAuthUserService {
    AuthUserRepository authUserRepository;
    CustomAuthUserRepository customAuthUserRepository;

    public AuthUserService(AuthUserRepository authUserRepository,
                           CustomAuthUserRepository customAuthUserRepository) {
        this.authUserRepository = authUserRepository;
        this.customAuthUserRepository = customAuthUserRepository;
    }

    public AuthUserProfileInfo getAuthUserInfo(String authUserId) throws DataProcessingException {
        var authUser = this.authUserRepository.findAuthUserByUserId(authUserId);
        AuthUserProfileInfo authUserData;
        if (authUser.isPresent()) {
            authUserData = new AuthUserProfileInfo();
            authUserData.setAuthUser(authUser.get());
            var authUserRoleRes = this.customAuthUserRepository.getAuthUserRealizedRole(authUserId);
            authUserData.setAuthUserRealizedRoleCollection(authUserRoleRes);
        }
        else {
            throw new DataProcessingException("Auth User Not Found");
        }

        return authUserData;
    }

}
