package gov.cdc.dataprocessing.repository.nbs.odse.repos.notification;

import gov.cdc.dataprocessing.repository.nbs.odse.model.notification.Notification;
import gov.cdc.dataprocessing.repository.nbs.odse.model.observation.ObservationInterp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
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
