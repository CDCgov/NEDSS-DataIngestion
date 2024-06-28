package gov.cdc.dataprocessing.service.interfaces.auth_user;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.service.model.auth_user.AuthUserProfileInfo;

public interface IAuthUserService {
    AuthUserProfileInfo getAuthUserInfo(String authUserId) throws DataProcessingException;
}
