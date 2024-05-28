package gov.cdc.dataprocessing.service.implementation;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.CustomAuthUserRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.auth.AuthUserRepository;
import gov.cdc.dataprocessing.service.interfaces.IAuthUserService;
import gov.cdc.dataprocessing.service.model.AuthUserProfileInfo;
import org.springframework.stereotype.Service;

@Service
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
        AuthUserProfileInfo authUserData = null;
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


    //DUMMY
    public AuthUser getSessionProfile(String userName) {
        var profile = this.authUserRepository.findAuthUserByUserId(userName);
        if (profile.isPresent()) {
            return profile.get();
        } else {
            // this is for debug and development only
            AuthUser authUser = new AuthUser();
            authUser.setAuthUserUid(123L);
            authUser.setUserId("data-processing");
            return authUser;
        }
    }
}
