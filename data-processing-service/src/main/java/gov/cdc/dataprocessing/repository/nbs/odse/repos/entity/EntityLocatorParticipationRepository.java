package gov.cdc.dataprocessing.repository.nbs.odse.repos.entity;

import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.EntityLocatorParticipation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface EntityLocatorParticipationRepository extends JpaRepository<EntityLocatorParticipation, Long> {
    @Query("SELECT pn FROM EntityLocatorParticipation pn WHERE pn.entityUid = :parentUid")
    Optional<List<EntityLocatorParticipation>> findByParentUid(@Param("parentUid") Long parentUid);

    @Query("SELECT pn.locatorUid FROM EntityLocatorParticipation pn WHERE pn.entityUid = :entityUid")
    Optional<List<Long>> findLocatorUidsByEntityUid(@Param("entityUid") Long entityUid);

}