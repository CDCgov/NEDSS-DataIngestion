package gov.cdc.dataingestion.rti.repository;

import gov.cdc.dataingestion.rti.repository.model.RtiLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RtiLogRepository extends JpaRepository<RtiLog, String> {
}
