package gov.cdc.dataprocessing.repository.nbs.odse.repos.log;

import gov.cdc.dataprocessing.repository.nbs.odse.model.log.EdxActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;



public interface EdxActivityLogRepository extends JpaRepository<EdxActivityLog, Long> {

    @Query("SELECT eal FROM EdxActivityLog eal WHERE eal.sourceUid = :sourceUid")
    Optional<EdxActivityLog> findBySourceUid(@Param("sourceUid") Long parentUid);
}