package gov.cdc.dataprocessing.repository.nbs.odse.repos;

import gov.cdc.dataprocessing.repository.nbs.odse.model.EntityGroup;
import gov.cdc.dataprocessing.repository.nbs.odse.model.lookup.LookupQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EntityGroupRepository  extends JpaRepository<EntityGroup, Long> {
}
