package gov.cdc.dataprocessing.utilities.component;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.constant.enums.LocalIdClass;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.NotificationVO;
import gov.cdc.dataprocessing.model.dto.act.ActRelationshipDto;
import gov.cdc.dataprocessing.model.dto.act.ActivityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.notification.NotificationDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.notification.Notification;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.notification.NotificationRepository;
import gov.cdc.dataprocessing.service.implementation.other.OdseIdGeneratorService;
import gov.cdc.dataprocessing.utilities.component.entity.EntityHelper;
import jakarta.transaction.Transactional;
import org.checkerframework.checker.units.qual.N;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class NotificationRepositoryUtil {
    private final NotificationRepository notificationRepository;


    private final ActIdRepositoryUtil actIdRepositoryUtil;
    private final ActLocatorParticipationRepositoryUtil actLocatorParticipationRepositoryUtil;
    private final ActRelationshipRepositoryUtil actRelationshipRepositoryUtil;
    private final ParticipationRepositoryUtil participationRepositoryUtil;
    private final EntityHelper entityHelper;
    private  final ActRepositoryUtil actRepositoryUtil;
    private final OdseIdGeneratorService odseIdGeneratorService;

    public NotificationRepositoryUtil(NotificationRepository notificationRepository,
                                      ActIdRepositoryUtil actIdRepositoryUtil,
                                      ActLocatorParticipationRepositoryUtil actLocatorParticipationRepositoryUtil,
                                      ActRelationshipRepositoryUtil actRelationshipRepositoryUtil,
                                      ParticipationRepositoryUtil participationRepositoryUtil,
                                      EntityHelper entityHelper,
                                      ActRepositoryUtil actRepositoryUtil,
                                      OdseIdGeneratorService odseIdGeneratorService) {
        this.notificationRepository = notificationRepository;
        this.actIdRepositoryUtil = actIdRepositoryUtil;
        this.actLocatorParticipationRepositoryUtil = actLocatorParticipationRepositoryUtil;
        this.actRelationshipRepositoryUtil = actRelationshipRepositoryUtil;
        this.participationRepositoryUtil = participationRepositoryUtil;
        this.entityHelper = entityHelper;
        this.actRepositoryUtil = actRepositoryUtil;
        this.odseIdGeneratorService = odseIdGeneratorService;
    }

    public NotificationVO getNotificationContainer(Long uid) {
        NotificationVO notificationContainer = new NotificationVO();
        var notificationData = notificationRepository.findById(uid);
        if (!notificationData.isPresent()) {
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
    public Long setNotification(NotificationVO notificationVO) throws DataProcessingException
    {
        Long notificationUid = -1L;

        try
        {
            Collection<ActivityLocatorParticipationDto> alpDTCol = notificationVO.getTheActivityLocatorParticipationDTCollection();
            Collection<ActRelationshipDto> arDTCol = notificationVO.getTheActRelationshipDTCollection();
            Collection<ParticipationDto> pDTCol = notificationVO.getTheParticipationDTCollection();

            if (alpDTCol != null)
            {
                var col1 = entityHelper.iterateALPDTActivityLocatorParticipation(alpDTCol);
                notificationVO.setTheActivityLocatorParticipationDTCollection(col1);
            }

            if (arDTCol != null)
            {
                var col2 = entityHelper.iterateARDTActRelationship(arDTCol);
                notificationVO.setTheActRelationshipDTCollection(col2);
            }

            if (pDTCol != null)
            {
                var col3 = entityHelper.iteratePDTForParticipation(pDTCol);
                notificationVO.setTheParticipationDTCollection(col3);
            }

            if (notificationVO.isItNew())
            {
                notificationUid = createNotification(notificationVO);
            }
            else
            {
                var  notification = getNotificationContainer(notificationVO.getTheNotificationDT().getNotificationUid());
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

    private Long createNotification(NotificationVO notificationVO) throws DataProcessingException {
        var uidData = odseIdGeneratorService.getLocalIdAndUpdateSeed(LocalIdClass.NOTIFICATION);
        var uid = uidData.getSeedValueNbr();
        var localId = uidData.getUidPrefixCd() + uid + uidData.getUidSuffixCd();

        actRepositoryUtil.insertActivityId(uid,NEDSSConstant.NOTIFICATION_CLASS_CODE, NEDSSConstant.EVENT_MOOD_CODE);

        Notification notification = new Notification(notificationVO.getTheNotificationDT());
        notification.setNotificationUid(uid);
        notification.setLocalId(localId);

        notificationRepository.save(notification);
        notificationVO.getTheNotificationDT().setItDirty(false);
        notificationVO.getTheNotificationDT().setItNew(false);
        notificationVO.getTheNotificationDT().setItDelete(false);

        notificationVO.getTheNotificationDT().setNotificationUid(uid);

      //  actIdRepositoryUtil.insertActIdCollection(uid, notificationVO.getTheActIdDTCollection());

      //  actLocatorParticipationRepositoryUtil.insertActLocatorParticipationCollection(uid, notificationVO.getTheActivityLocatorParticipationDTCollection());

        notificationVO.setItNew(true);
        notificationVO.setItDirty(false);

        return uid;
    }

    private Long updateNotification(NotificationVO notificationVO) throws DataProcessingException {
        var uid = notificationVO.getTheUpdatedNotificationDT().getNotificationUid();
        var localId = notificationVO.getTheNotificationDT().getLocalId();

        actRepositoryUtil.insertActivityId(uid,NEDSSConstant.NOTIFICATION_CLASS_CODE, NEDSSConstant.EVENT_MOOD_CODE);

        Notification notification = new Notification(notificationVO.getTheNotificationDT());
        notification.setNotificationUid(uid);
        notification.setLocalId(localId);

        notificationRepository.save(notification);
        notificationVO.getTheNotificationDT().setItDirty(false);
        notificationVO.getTheNotificationDT().setItNew(false);
        notificationVO.getTheNotificationDT().setItDelete(false);

        notificationVO.getTheNotificationDT().setNotificationUid(uid);

        actIdRepositoryUtil.insertActIdCollection(uid, notificationVO.getTheActIdDTCollection());

        actLocatorParticipationRepositoryUtil.insertActLocatorParticipationCollection(uid, notificationVO.getTheActivityLocatorParticipationDTCollection());

        notificationVO.setItNew(false);
        notificationVO.setItDirty(false);

        return uid;
    }

}
