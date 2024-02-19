package gov.cdc.dataprocessing.repository.nbs.odse;

import gov.cdc.dataprocessing.repository.nbs.odse.model.EdxEntityMatch;
import gov.cdc.dataprocessing.repository.nbs.odse.model.EdxPatientMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface EdxEntityMatchRepository  extends JpaRepository<EdxEntityMatch, Long> {

}