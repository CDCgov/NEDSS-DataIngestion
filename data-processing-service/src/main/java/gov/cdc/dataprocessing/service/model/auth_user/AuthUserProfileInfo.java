package gov.cdc.dataprocessing.service.model.auth_user;

import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUserRealizedRole;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
public class AuthUserProfileInfo {
    private AuthUser authUser;
    private Collection<AuthUserRealizedRole> authUserRealizedRoleCollection;
}
