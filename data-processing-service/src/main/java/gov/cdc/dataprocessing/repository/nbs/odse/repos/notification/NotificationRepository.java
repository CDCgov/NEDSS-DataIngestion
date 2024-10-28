package gov.cdc.dataprocessing.repository.nbs.odse.repos.notification;

import gov.cdc.dataprocessing.repository.nbs.odse.model.notification.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


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
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("SELECT COUNT(*) " +
            "FROM ActRelationship ar, Notification n " +
            "WHERE ar.targetActUid IN " +
            "(SELECT targetActUid " +
            "FROM ActRelationship " +
            "WHERE ar.sourceActUid = :sourceActUid " +
            "AND ar.sourceClassCd = :sourceClassCd " +
            "AND ar.targetClassCd = 'CASE' " +
            "AND ar.recordStatusCd = 'ACTIVE') " +
            "AND ar.typeCd = 'Notification' " +
            "AND n.notificationUid = ar.sourceActUid " +
            "AND (n.recordStatusCd IN ('APPROVED', 'PEND_APPR') OR n.autoResendInd = 'T')")
    long getCountOfExistingNotifications(@Param("sourceActUid") Long sourceActUid, @Param("sourceClassCd") String sourceClassCd);
}
