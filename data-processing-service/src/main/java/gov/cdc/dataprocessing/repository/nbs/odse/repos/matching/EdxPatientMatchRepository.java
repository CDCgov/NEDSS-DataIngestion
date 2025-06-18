package gov.cdc.dataprocessing.repository.nbs.odse.repos.matching;

import gov.cdc.dataprocessing.repository.nbs.odse.model.matching.EdxPatientMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository


public interface EdxPatientMatchRepository extends JpaRepository<EdxPatientMatch, Long> {
}