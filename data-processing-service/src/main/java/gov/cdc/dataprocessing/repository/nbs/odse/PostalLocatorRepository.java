package gov.cdc.dataprocessing.repository.nbs.odse;

import gov.cdc.dataprocessing.repository.nbs.odse.model.PhysicalLocator;
import gov.cdc.dataprocessing.repository.nbs.odse.model.PostalLocator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostalLocatorRepository extends JpaRepository<PostalLocator, Long> {
}
