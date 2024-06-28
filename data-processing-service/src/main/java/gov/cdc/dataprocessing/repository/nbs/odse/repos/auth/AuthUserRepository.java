package gov.cdc.dataprocessing.repository.nbs.odse.repos.auth;

import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface AuthUserRepository  extends JpaRepository<AuthUser, Long> {
//    @Query("SELECT pn FROM AuthUser pn WHERE pn.userId = :userName")
//    Optional<AuthUser> findByUserName(@Param("userName") String userName);
    @Query("SELECT data FROM AuthUser data WHERE data.userId = :userId")
    Optional<AuthUser> findAuthUserByUserId(@Param("userId") String userId);
}
