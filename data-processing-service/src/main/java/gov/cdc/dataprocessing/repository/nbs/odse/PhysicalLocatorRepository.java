package gov.cdc.dataprocessing.repository.nbs.odse;

import gov.cdc.dataprocessing.repository.nbs.odse.model.PhysicalLocator;
import gov.cdc.dataprocessing.repository.nbs.odse.model.TeleLocator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhysicalLocatorRepository extends JpaRepository<PhysicalLocator, Long> {
}
