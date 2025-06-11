package gov.cdc.dataprocessing.utilities.component.notification;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.constant.enums.LocalIdClass;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.NotificationContainer;
import gov.cdc.dataprocessing.model.dto.act.ActRelationshipDto;
import gov.cdc.dataprocessing.model.dto.act.ActivityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.notification.NotificationDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template.NotificationJdbcRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.model.notification.Notification;
import gov.cdc.dataprocessing.service.implementation.uid_generator.UidPoolManager;
import gov.cdc.dataprocessing.utilities.component.act.ActIdRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.act.ActLocatorParticipationRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.act.ActRelationshipRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.act.ActRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.entity.EntityHelper;
import gov.cdc.dataprocessing.utilities.component.participation.ParticipationRepositoryUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component

public class NotificationRepositoryUtil {


    private final ActIdRepositoryUtil actIdRepositoryUtil;
    private final ActLocatorParticipationRepositoryUtil actLocatorParticipationRepositoryUtil;
    private final ActRelationshipRepositoryUtil actRelationshipRepositoryUtil;
    private final ParticipationRepositoryUtil participationRepositoryUtil;
    private final EntityHelper entityHelper;
    private  final ActRepositoryUtil actRepositoryUtil;
    private final UidPoolManager uidPoolManager;
    private final NotificationJdbcRepository notificationJdbcRepository;

    public NotificationRepositoryUtil(
                                      ActIdRepositoryUtil actIdRepositoryUtil,
                                      ActLocatorParticipationRepositoryUtil actLocatorParticipationRepositoryUtil,
                                      ActRelationshipRepositoryUtil actRelationshipRepositoryUtil,
                                      ParticipationRepositoryUtil participationRepositoryUtil,
                                      EntityHelper entityHelper,
                                      ActRepositoryUtil actRepositoryUtil,
                                      @Lazy UidPoolManager uidPoolManager,
                                      NotificationJdbcRepository notificationJdbcRepository) {
        this.actIdRepositoryUtil = actIdRepositoryUtil;
        this.actLocatorParticipationRepositoryUtil = actLocatorParticipationRepositoryUtil;
        this.actRelationshipRepositoryUtil = actRelationshipRepositoryUtil;
        this.participationRepositoryUtil = participationRepositoryUtil;
        this.entityHelper = entityHelper;
        this.actRepositoryUtil = actRepositoryUtil;
        this.uidPoolManager = uidPoolManager;
        this.notificationJdbcRepository = notificationJdbcRepository;
    }

    public NotificationContainer getNotificationContainer(Long uid) {
        NotificationContainer notificationContainer = new NotificationContainer();
        var notificationData = notificationJdbcRepository.findById(uid);
        if (notificationData == null) {
            return null;
        }
        NotificationDto notificationDto = new NotificationDto(notificationData);
        notificationDto.setItNew(false);
        notificationDto.setItDirty(false);
        notificationContainer.setTheNotificationDT(notificationDto);

        var actIdCollection = actIdRepositoryUtil.getActIdCollection(uid);
        if (!actIdCollection.isEmpty()) {
            notificationContainer.setTheActIdDTCollection(actIdCollection);
        }

        var actPatCollection =  actLocatorParticipationRepositoryUtil.getActLocatorParticipationCollection(uid);
        if (!actPatCollection.isEmpty()) {
            notificationContainer.setTheActivityLocatorParticipationDTCollection(actPatCollection);
        }

        var actReCollection =  actRelationshipRepositoryUtil.getActRelationshipCollectionFromSourceId(uid);
        if (!actReCollection.isEmpty()) {
            notificationContainer.setTheActRelationshipDTCollection(actReCollection);
        }

        var patCollection =  participationRepositoryUtil.getParticipationCollection(uid);
        if (!patCollection.isEmpty()) {
            notificationContainer.setTheParticipationDTCollection(patCollection);
        }

        notificationContainer.setItNew(false);
        notificationContainer.setItDirty(false);
        return notificationContainer;

    }


    public Long setNotification(NotificationContainer notificationContainer) throws DataProcessingException
    {
        Long notificationUid;

        Collection<ActivityLocatorParticipationDto> alpDTCol = notificationContainer.getTheActivityLocatorParticipationDTCollection();
        Collection<ActRelationshipDto> arDTCol = notificationContainer.getTheActRelationshipDTCollection();
        Collection<ParticipationDto> pDTCol = notificationContainer.getTheParticipationDTCollection();

        if (alpDTCol != null)
        {
            var col1 = entityHelper.iterateALPDTActivityLocatorParticipation(alpDTCol);
            notificationContainer.setTheActivityLocatorParticipationDTCollection(col1);
        }

        if (arDTCol != null)
        {
            var col2 = entityHelper.iterateARDTActRelationship(arDTCol);
            notificationContainer.setTheActRelationshipDTCollection(col2);
        }

        if (pDTCol != null)
        {
            var col3 = entityHelper.iteratePDTForParticipation(pDTCol);
            notificationContainer.setTheParticipationDTCollection(col3);
        }

        if (notificationContainer.isItNew())
        {
            notificationUid = createNotification(notificationContainer);
        }
        else
        {
            var  notification = getNotificationContainer(notificationContainer.getTheNotificationDT().getNotificationUid());
            updateNotification(notification);
            notificationUid = notification.getTheNotificationDT().getNotificationUid();
        }
        return notificationUid;
    }

    private Long createNotification(NotificationContainer notificationContainer) throws DataProcessingException {
        var uidData = uidPoolManager.getNextUid(LocalIdClass.NOTIFICATION, true);
        var uid = uidData.getGaTypeUid().getSeedValueNbr();
        var localId = uidData.getClassTypeUid().getUidPrefixCd() + uidData.getClassTypeUid().getSeedValueNbr() + uidData.getClassTypeUid().getUidSuffixCd();

        actRepositoryUtil.insertActivityId(uid,NEDSSConstant.NOTIFICATION_CLASS_CODE, NEDSSConstant.EVENT_MOOD_CODE);

        Notification notification = new Notification(notificationContainer.getTheNotificationDT());
        notification.setNotificationUid(uid);
        notification.setLocalId(localId);

        notificationJdbcRepository.insertNotification(notification);
        notificationContainer.getTheNotificationDT().setItDirty(false);
        notificationContainer.getTheNotificationDT().setItNew(false);
        notificationContainer.getTheNotificationDT().setItDelete(false);

        notificationContainer.getTheNotificationDT().setNotificationUid(uid);

        notificationContainer.setItNew(true);
        notificationContainer.setItDirty(false);

        return uid;
    }

    private Long updateNotification(NotificationContainer notificationContainer) {
        var uid = notificationContainer.getTheNotificationDT().getNotificationUid();
        var localId = notificationContainer.getTheNotificationDT().getLocalId();

        actRepositoryUtil.updateActivityId(uid,NEDSSConstant.NOTIFICATION_CLASS_CODE, NEDSSConstant.EVENT_MOOD_CODE);

        Notification notification = new Notification(notificationContainer.getTheNotificationDT());
        notification.setNotificationUid(uid);
        notification.setLocalId(localId);

        notificationJdbcRepository.updateNotification(notification);
        notificationContainer.getTheNotificationDT().setItDirty(false);
        notificationContainer.getTheNotificationDT().setItNew(false);
        notificationContainer.getTheNotificationDT().setItDelete(false);

        notificationContainer.getTheNotificationDT().setNotificationUid(uid);

        actIdRepositoryUtil.insertActIdCollection(uid, notificationContainer.getTheActIdDTCollection());

        actLocatorParticipationRepositoryUtil.insertActLocatorParticipationCollection(uid, notificationContainer.getTheActivityLocatorParticipationDTCollection());

        notificationContainer.setItNew(false);
        notificationContainer.setItDirty(false);

        return uid;
    }

}
