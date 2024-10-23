package gov.cdc.dataprocessing.service.model.auth_user;

import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUserRealizedRole;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
 6809 - TEST
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S6809"})
public class AuthUserProfileInfo {
    private AuthUser authUser;
    private Collection<AuthUserRealizedRole> authUserRealizedRoleCollection;
}
