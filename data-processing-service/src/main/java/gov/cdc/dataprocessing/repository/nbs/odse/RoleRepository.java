package gov.cdc.dataprocessing.repository.nbs.odse;

import gov.cdc.dataprocessing.repository.nbs.odse.model.EntityLocatorParticipation;
import gov.cdc.dataprocessing.repository.nbs.odse.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    @Query("SELECT pn FROM Role pn WHERE pn.subjectEntityUid = :parentUid")
    Optional<List<Role>> findByParentUid(@Param("parentUid") Long parentUid);
}