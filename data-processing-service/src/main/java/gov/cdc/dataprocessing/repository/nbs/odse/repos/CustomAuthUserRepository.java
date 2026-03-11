package gov.cdc.dataprocessing.repository.nbs.odse.repos;

import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUserRealizedRole;
import java.util.Collection;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomAuthUserRepository {
  Collection<AuthUserRealizedRole> getAuthUserRealizedRole(String userId);
}
