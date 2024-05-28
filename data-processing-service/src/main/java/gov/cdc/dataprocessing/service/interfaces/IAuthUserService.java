package gov.cdc.dataprocessing.service.interfaces;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.service.model.AuthUserProfileInfo;

public interface IAuthUserService {
    AuthUserProfileInfo getAuthUserInfo(String authUserId) throws DataProcessingException;
    AuthUser getSessionProfile(String userName);
}
