package gov.cdc.dataprocessing.repository.nbs.odse.repos;

import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUserRealizedRole;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface CustomAuthUserRepository {
    Collection<AuthUserRealizedRole> getAuthUserRealizedRole(String userId);
}
