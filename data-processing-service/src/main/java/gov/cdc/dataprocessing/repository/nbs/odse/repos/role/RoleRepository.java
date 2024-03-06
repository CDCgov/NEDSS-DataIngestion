package gov.cdc.dataprocessing.repository.nbs.odse.repos.role;

import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    @Query("SELECT pn FROM Role pn WHERE pn.subjectEntityUid = :parentUid")
    Optional<List<Role>> findByParentUid(@Param("parentUid") Long parentUid);

    @Query("SELECT data FROM Role data WHERE data.scopingEntityUid = :entityUid AND data.scopingClassCode = 'PAT' AND data.subjectClassCode IN ('PROV', 'CON')")
    Optional<Collection<Role>> findRoleScopedToPatient(@Param("entityUid") Long entityUid);
}