package gov.cdc.dataingestion.odse.repository;

import gov.cdc.dataingestion.odse.repository.model.EdxActivityDetailLog;
import gov.cdc.dataingestion.odse.repository.model.EdxActivityLogModelView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IEdxActivityLogRepository extends JpaRepository<EdxActivityDetailLog, Long> {
    @Query(value = """
            select eadl.record_id as recordId ,eadl.record_type as recordType ,eadl.log_type as logType,eadl.log_comment as logComment,
            eal.record_status_time as recordStatusTime from NBS_ODSE.dbo.EDX_activity_detail_log eadl,NBS_ODSE.dbo.EDX_activity_log eal 
            where eal.source_uid = :sourceId and eadl.edx_activity_log_uid =eal.edx_activity_log_uid
            """,
            nativeQuery = true)
    List<EdxActivityLogModelView> getEdxActivityLogDetailsBySourceId(Long sourceId);
}
