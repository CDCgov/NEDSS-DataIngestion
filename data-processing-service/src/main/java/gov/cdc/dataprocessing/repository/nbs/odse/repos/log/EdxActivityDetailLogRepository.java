package gov.cdc.dataprocessing.repository.nbs.odse.repos.log;

import gov.cdc.dataprocessing.repository.nbs.odse.model.log.EdxActivityDetailLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
 1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809"})
public interface EdxActivityDetailLogRepository extends JpaRepository<EdxActivityDetailLog, Long> {
    @Query(value = "SELECT edx_activity_detail_log_uid FROM NBS_ODSE.dbo.EDX_activity_detail_log WHERE edx_activity_log_uid = :edxActivityLogUid AND log_comment = :logComment", nativeQuery = true)
    List<Integer> findIdsByEdxActivityLogUidAndLogComment(@Param("edxActivityLogUid") Long edxActivityLogUid, @Param("logComment") String logComment);
}
