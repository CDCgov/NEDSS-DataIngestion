package gov.cdc.dataprocessing.repository.nbs.odse;

import gov.cdc.dataprocessing.repository.nbs.odse.model.Role;
import gov.cdc.dataprocessing.repository.nbs.odse.model.TeleLocator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeleLocatorRepository  extends JpaRepository<TeleLocator, Long> {
}
