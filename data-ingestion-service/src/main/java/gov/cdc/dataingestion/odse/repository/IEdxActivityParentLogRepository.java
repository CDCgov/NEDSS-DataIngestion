package gov.cdc.dataingestion.odse.repository;

import gov.cdc.dataingestion.odse.repository.model.EdxActivityDetailLog;
import gov.cdc.dataingestion.odse.repository.model.EdxActivityLog;
import gov.cdc.dataingestion.odse.repository.model.EdxActivityLogModelView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IEdxActivityParentLogRepository extends JpaRepository<EdxActivityLog, Long> {
    @Query(value = """
            select top 1 * from EDX_activity_log
            where source_uid = :nbsSourceId 
            """,
            nativeQuery = true)
    EdxActivityLog getParentEdxActivity(@Param("nbsSourceId") Long sourceId);
}