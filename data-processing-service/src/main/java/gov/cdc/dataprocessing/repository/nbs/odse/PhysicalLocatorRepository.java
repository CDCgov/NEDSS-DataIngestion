package gov.cdc.dataprocessing.repository.nbs.odse;

import gov.cdc.dataprocessing.repository.nbs.odse.model.PhysicalLocator;
import gov.cdc.dataprocessing.repository.nbs.odse.model.PostalLocator;
import gov.cdc.dataprocessing.repository.nbs.odse.model.TeleLocator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhysicalLocatorRepository extends JpaRepository<PhysicalLocator, Long> {
    @Query(value = "SELECT x FROM PhysicalLocator x WHERE x.physicalLocatorUid IN :uids", nativeQuery = false)
    List<PhysicalLocator> findByPhysicalLocatorUids(@Param("uids") List<Long> uids);
}
