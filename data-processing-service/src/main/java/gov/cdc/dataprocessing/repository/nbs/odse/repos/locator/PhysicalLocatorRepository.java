package gov.cdc.dataprocessing.repository.nbs.odse.repos.locator;

import gov.cdc.dataprocessing.repository.nbs.odse.model.locator.PhysicalLocator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
 6809 - Calling transactional method with This. complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809"})
public interface PhysicalLocatorRepository extends JpaRepository<PhysicalLocator, Long> {
    @Query(value = "SELECT x FROM PhysicalLocator x WHERE x.physicalLocatorUid IN :uids", nativeQuery = false)
    Optional<List<PhysicalLocator>> findByPhysicalLocatorUids(@Param("uids") List<Long> uids);
}
