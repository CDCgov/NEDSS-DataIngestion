package gov.cdc.dataprocessing.service.implementation.auth;

import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.auth.AuthUserRepository;
import gov.cdc.dataprocessing.service.interfaces.auth.ISessionProfileService;
import org.springframework.stereotype.Service;

@Service
public class SessionProfileService implements ISessionProfileService {
    private final AuthUserRepository authUserRepository;

    public SessionProfileService(AuthUserRepository authUserRepository) {
        this.authUserRepository = authUserRepository;
    }

    public AuthUser getSessionProfile(String userName) {
        var profile = this.authUserRepository.findByUserName(userName);
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
