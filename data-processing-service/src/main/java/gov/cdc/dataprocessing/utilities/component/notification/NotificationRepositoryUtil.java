package gov.cdc.dataprocessing.utilities.component.notification;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.constant.enums.LocalIdClass;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.NotificationContainer;
import gov.cdc.dataprocessing.model.dto.act.ActRelationshipDto;
import gov.cdc.dataprocessing.model.dto.act.ActivityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.notification.NotificationDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.notification.Notification;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.notification.NotificationRepository;
import gov.cdc.dataprocessing.service.interfaces.uid_generator.IOdseIdGeneratorWCacheService;
import gov.cdc.dataprocessing.utilities.component.act.ActIdRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.act.ActLocatorParticipationRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.act.ActRelationshipRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.act.ActRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.entity.EntityHelper;
import gov.cdc.dataprocessing.utilities.component.participation.ParticipationRepositoryUtil;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Component
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 2139 - exception rethrow complain
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139"})
public class NotificationRepositoryUtil {
    private final NotificationRepository notificationRepository;


    private final ActIdRepositoryUtil actIdRepositoryUtil;
    private final ActLocatorParticipationRepositoryUtil actLocatorParticipationRepositoryUtil;
    private final ActRelationshipRepositoryUtil actRelationshipRepositoryUtil;
    private final ParticipationRepositoryUtil participationRepositoryUtil;
    private final EntityHelper entityHelper;
    private  final ActRepositoryUtil actRepositoryUtil;
    private final IOdseIdGeneratorWCacheService odseIdGeneratorService;

    public NotificationRepositoryUtil(NotificationRepository notificationRepository,
                                      ActIdRepositoryUtil actIdRepositoryUtil,
                                      ActLocatorParticipationRepositoryUtil actLocatorParticipationRepositoryUtil,
                                      ActRelationshipRepositoryUtil actRelationshipRepositoryUtil,
                                      ParticipationRepositoryUtil participationRepositoryUtil,
                                      EntityHelper entityHelper,
                                      ActRepositoryUtil actRepositoryUtil,
                                      IOdseIdGeneratorWCacheService odseIdGeneratorService) {
        this.notificationRepository = notificationRepository;
        this.actIdRepositoryUtil = actIdRepositoryUtil;
        this.actLocatorParticipationRepositoryUtil = actLocatorParticipationRepositoryUtil;
        this.actRelationshipRepositoryUtil = actRelationshipRepositoryUtil;
        this.participationRepositoryUtil = participationRepositoryUtil;
        this.entityHelper = entityHelper;
        this.actRepositoryUtil = actRepositoryUtil;
        this.odseIdGeneratorService = odseIdGeneratorService;
    }

    public NotificationContainer getNotificationContainer(Long uid) {
        NotificationContainer notificationContainer = new NotificationContainer();
        var notificationData = notificationRepository.findById(uid);
        if (notificationData.isEmpty()) {
            return null;
        }
        NotificationDto notificationDto = new NotificationDto(notificationData.get());
        notificationDto.setItNew(false);
        notificationDto.setItDirty(false);
        notificationContainer.setTheNotificationDT(notificationDto);

        var actIdCollection = actIdRepositoryUtil.GetActIdCollection(uid);
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


    @Transactional
    public Long setNotification(NotificationContainer notificationContainer) throws DataProcessingException
    {
        Long notificationUid;

        try
        {
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
        }
        catch (Exception e)
        {
            throw new DataProcessingException(e.getMessage(),e);
        }
        return notificationUid;
    }

    private Long createNotification(NotificationContainer notificationContainer) throws DataProcessingException {
        var uidData = odseIdGeneratorService.getValidLocalUid(LocalIdClass.NOTIFICATION, true);
        var uid = uidData.getGaTypeUid().getSeedValueNbr();
        var localId = uidData.getClassTypeUid().getUidPrefixCd() + uidData.getClassTypeUid().getSeedValueNbr() + uidData.getClassTypeUid().getUidSuffixCd();

        actRepositoryUtil.insertActivityId(uid,NEDSSConstant.NOTIFICATION_CLASS_CODE, NEDSSConstant.EVENT_MOOD_CODE);

        Notification notification = new Notification(notificationContainer.getTheNotificationDT());
        notification.setNotificationUid(uid);
        notification.setLocalId(localId);

        notificationRepository.save(notification);
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

        actRepositoryUtil.insertActivityId(uid,NEDSSConstant.NOTIFICATION_CLASS_CODE, NEDSSConstant.EVENT_MOOD_CODE);

        Notification notification = new Notification(notificationContainer.getTheNotificationDT());
        notification.setNotificationUid(uid);
        notification.setLocalId(localId);

        notificationRepository.save(notification);
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
