package gov.cdc.dataprocessing.repository.nbs.odse.repos.locator;

import gov.cdc.dataprocessing.repository.nbs.odse.model.locator.PhysicalLocator;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PhysicalLocatorRepository extends JpaRepository<PhysicalLocator, Long> {
  @Query(
      value = "SELECT x FROM PhysicalLocator x WHERE x.physicalLocatorUid IN :uids",
      nativeQuery = false)
  Optional<List<PhysicalLocator>> findByPhysicalLocatorUids(@Param("uids") List<Long> uids);
}
