package gov.cdc.dataprocessing.repository.nbs.odse.repos;

import gov.cdc.dataprocessing.repository.nbs.odse.model.PublicHealthCase;
import gov.cdc.dataprocessing.repository.nbs.odse.model.WAQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublicHealthCaseRepository  extends JpaRepository<PublicHealthCase, Long> {
}
