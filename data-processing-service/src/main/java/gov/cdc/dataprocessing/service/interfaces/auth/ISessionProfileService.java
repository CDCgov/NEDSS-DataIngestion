package gov.cdc.dataprocessing.service.interfaces.auth;

import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;

public interface ISessionProfileService {
    AuthUser getSessionProfile(String userName);
}
