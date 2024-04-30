package gov.cdc.dataprocessing.repository.nbs.odse.repos.log;

import gov.cdc.dataprocessing.repository.nbs.odse.model.log.EdxActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EdxActivityLogRepository extends JpaRepository<EdxActivityLog, Long> {
}
