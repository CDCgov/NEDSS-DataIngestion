package gov.cdc.nbsDedup.service.model.auth_user;


import gov.cdc.nbsDedup.nbs.odse.model.auth.AuthUser;
import gov.cdc.nbsDedup.nbs.odse.model.auth.AuthUserRealizedRole;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
public class AuthUserProfileInfo {
    private AuthUser authUser;
    private Collection<AuthUserRealizedRole> authUserRealizedRoleCollection;
}
