package gov.cdc.dataprocessing.repository.nbs.odse.repos.act;

import gov.cdc.dataprocessing.repository.nbs.odse.model.act.Act;
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActRelationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActRepository extends JpaRepository<Act, Long> {
}
