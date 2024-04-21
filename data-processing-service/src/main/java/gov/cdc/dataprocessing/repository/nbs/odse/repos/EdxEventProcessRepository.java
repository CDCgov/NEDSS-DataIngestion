package gov.cdc.dataprocessing.repository.nbs.odse.repos;

import gov.cdc.dataprocessing.repository.nbs.odse.model.DsmAlgorithm;
import gov.cdc.dataprocessing.repository.nbs.odse.model.EdxEventProcess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EdxEventProcessRepository  extends JpaRepository<EdxEventProcess, Long> {
}
