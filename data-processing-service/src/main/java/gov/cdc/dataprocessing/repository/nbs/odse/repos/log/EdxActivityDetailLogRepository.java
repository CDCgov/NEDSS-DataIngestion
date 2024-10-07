package gov.cdc.dataprocessing.repository.nbs.odse.repos.log;

import gov.cdc.dataprocessing.repository.nbs.odse.model.log.EdxActivityDetailLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EdxActivityDetailLogRepository extends JpaRepository<EdxActivityDetailLog, Long> {
    @Query(value = "SELECT edx_activity_detail_log_uid FROM NBS_ODSE.dbo.EDX_activity_detail_log WHERE edx_activity_log_uid = :edxActivityLogUid AND log_comment = :logComment", nativeQuery = true)
    List<Integer> findIdsByEdxActivityLogUidAndLogComment(@Param("edxActivityLogUid") Long edxActivityLogUid, @Param("logComment") String logComment);
}
