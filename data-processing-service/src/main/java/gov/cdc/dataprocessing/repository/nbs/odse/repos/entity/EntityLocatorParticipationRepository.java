package gov.cdc.dataprocessing.repository.nbs.odse.repos.entity;

import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.EntityLocatorParticipation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
 1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 2139 - exception rethrow complain
 3740 - parametrized  type for generic complaint
 1149 - replacing HashTable complaint
 112 - throwing dedicate exception complaint
 107 - max parameter complaint
 1195 - duplicate complaint
 1135 - Todos complaint
 6201 - instanceof check
 1192 - duplicate literal
 135 - for loop
 117 - naming
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201", "java:S1192", "java:S135", "java:S117"})
public interface EntityLocatorParticipationRepository extends JpaRepository<EntityLocatorParticipation, Long> {
    @Query("SELECT pn FROM EntityLocatorParticipation pn WHERE pn.entityUid = :parentUid")
    Optional<List<EntityLocatorParticipation>> findByParentUid(@Param("parentUid") Long parentUid);

    @Query("SELECT pn.locatorUid FROM EntityLocatorParticipation pn WHERE pn.entityUid = :entityUid")
    Optional<List<Long>> findLocatorUidsByEntityUid(@Param("entityUid") Long entityUid);

    @Modifying
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Query(value = "DELETE FROM EntityLocatorParticipation x WHERE x.entityUid = :entityUid AND x.locatorUid = :locatorUid", nativeQuery = false)
    void deleteLocatorById(@Param("entityUid") Long entityUid, @Param("locatorUid") Long locatorUid);

}