package gov.cdc.dataprocessing.repository.nbs.odse.repos.auth;

import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.EntityId;
import gov.cdc.dataprocessing.repository.nbs.odse.model.person.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface AuthUserRepository  extends JpaRepository<AuthUser, Long> {
    @Query("SELECT pn FROM AuthUser pn WHERE pn.userId = :userName")
    Optional<AuthUser> findByUserName(@Param("userName") String userName);
}
