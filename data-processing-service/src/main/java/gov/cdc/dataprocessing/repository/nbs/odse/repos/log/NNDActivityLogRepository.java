package gov.cdc.dataprocessing.repository.nbs.odse.repos.log;

import gov.cdc.dataprocessing.repository.nbs.odse.model.log.NNDActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;



public interface NNDActivityLogRepository extends JpaRepository<NNDActivityLog, Long> {
}
