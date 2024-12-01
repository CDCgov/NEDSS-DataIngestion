package gov.cdc.nbs.mpidatasyncer.repository.syncer;

import gov.cdc.nbs.mpidatasyncer.entity.syncer.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LogRepository extends JpaRepository<Log, Long> {
  List<Log> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
}
