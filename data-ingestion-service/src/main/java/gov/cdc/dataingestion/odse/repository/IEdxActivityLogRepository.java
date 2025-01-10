package gov.cdc.dataingestion.odse.repository;

import gov.cdc.dataingestion.odse.repository.model.EdxActivityDetailLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IEdxActivityLogRepository extends JpaRepository<EdxActivityDetailLog, Long> {
    @Query(value = """
            select eadl.* from NBS_ODSE.dbo.EDX_activity_detail_log eadl,NBS_ODSE.dbo.EDX_activity_log eal 
            where eal.source_uid = :nbsSourceId and eadl.edx_activity_log_uid =eal.edx_activity_log_uid
            """,
            nativeQuery = true)
    List<EdxActivityDetailLog> getEdxActivityLogDetailsBySourceId(@Param("nbsSourceId") Long sourceId);
}
