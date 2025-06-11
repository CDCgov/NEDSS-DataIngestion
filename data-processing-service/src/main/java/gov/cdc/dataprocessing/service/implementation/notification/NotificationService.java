package gov.cdc.dataprocessing.service.implementation.notification;

import gov.cdc.dataprocessing.constant.elr.NBSBOLookup;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.model.container.model.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.container.model.NotificationContainer;
import gov.cdc.dataprocessing.model.container.model.NotificationProxyContainer;
import gov.cdc.dataprocessing.model.container.model.ObservationContainer;
import gov.cdc.dataprocessing.model.dto.act.ActRelationshipDto;
import gov.cdc.dataprocessing.model.dto.notification.NotificationDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.notification.Notification;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.notification.NotificationRepository;
import gov.cdc.dataprocessing.service.interfaces.notification.INotificationService;
import gov.cdc.dataprocessing.service.interfaces.uid_generator.IUidService;
import gov.cdc.dataprocessing.utilities.component.act.ActRelationshipRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.generic_helper.PrepareAssocModelHelper;
import gov.cdc.dataprocessing.utilities.component.generic_helper.PropertyUtil;
import gov.cdc.dataprocessing.utilities.component.notification.NotificationRepositoryUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

import static gov.cdc.dataprocessing.constant.DpConstant.OPERATION_CREATE;


@Service

public class NotificationService implements INotificationService {

    private final NotificationRepository notificationRepository;
    private final PrepareAssocModelHelper prepareAssocModelHelper;
    private final IUidService iUidService;
    private final ActRelationshipRepositoryUtil actRelationshipRepositoryUtil;

    private final NotificationRepositoryUtil notificationRepositoryUtil;

    private final PropertyUtil propertyUtil;

    public NotificationService(NotificationRepository notificationRepository,
                               PrepareAssocModelHelper prepareAssocModelHelper,
                               IUidService iUidService,
                               ActRelationshipRepositoryUtil actRelationshipRepositoryUtil,
                               NotificationRepositoryUtil notificationRepositoryUtil,
                               PropertyUtil propertyUtil) {
        this.notificationRepository = notificationRepository;
        this.prepareAssocModelHelper = prepareAssocModelHelper;
        this.iUidService = iUidService;
        this.actRelationshipRepositoryUtil = actRelationshipRepositoryUtil;
        this.notificationRepositoryUtil = notificationRepositoryUtil;
        this.propertyUtil = propertyUtil;
    }

    public NotificationDto getNotificationById(Long uid) {
        var res = notificationRepository.findById(uid);
        return res.map(NotificationDto::new).orElse(null);
    }

    public Long saveNotification(NotificationContainer notificationContainer) {
        var notification = new Notification(notificationContainer.getTheNotificationDT());
        notificationRepository.save(notification);
        return notification.getNotificationUid();
    }

    public boolean checkForExistingNotification(BaseContainer vo) throws DataProcessingException {
        if (
            //!(vo instanceof VaccinationProxyVO)
            //&& !(vo instanceof MorbidityProxyVO)
            //&&
                !(vo instanceof LabResultProxyContainer)
        ) {
            throw new DataProcessingException("NND MessageSenderHelper: VO not of type Vacc,Morb,LabReport");
        }
        long count = notificationRepository.getCountOfExistingNotifications(getRootUid(vo), getActClassCd(vo));

        return count > 0;
    }

    private String getActClassCd(BaseContainer vo)
    {
        if (vo == null)
        {
            return null;
        }

        if (vo instanceof LabResultProxyContainer) {
            // return "OBS"
            return NEDSSConstant.CLASS_CD_OBS;
        }


        return null;
    }

    private Long getRootUid(BaseContainer vo)
    {
        if (vo == null)
        {
            return null;
        }

        if (vo instanceof LabResultProxyContainer) {
        // the root Lab observation UID out of the observation collection
        Collection<ObservationContainer> obsColl =
                ((LabResultProxyContainer) vo).getTheObservationContainerCollection();
            for (ObservationContainer observationContainer : obsColl) {
                String obsDomainCdSt1 =
                        observationContainer.getTheObservationDto().getObsDomainCdSt1();
                String obsCtrlCdDisplayForm =
                        observationContainer.getTheObservationDto().getCtrlCdDisplayForm();
                if (obsDomainCdSt1 != null
                        && obsDomainCdSt1.equalsIgnoreCase(
                        NEDSSConstant.ORDERED_TEST_OBS_DOMAIN_CD)
                        && obsCtrlCdDisplayForm != null
                        && obsCtrlCdDisplayForm.equalsIgnoreCase(
                        NEDSSConstant.LAB_REPORT)) {
                    return observationContainer
                            .getTheObservationDto()
                            .getObservationUid();
                }
            }

        }
        return null;
    }



    @SuppressWarnings("java:S3776")

    public Long setNotificationProxy(NotificationProxyContainer notificationProxyVO) throws DataProcessingException
    {

        Long notificationUid = null;
        String permissionFlag;
        Collection<Object> act2 = new ArrayList<>();

        if (notificationProxyVO == null)
        {
            throw new DataProcessingException("notificationproxyVO is null ");
        }
        permissionFlag = OPERATION_CREATE;



        NotificationContainer notifVO = notificationProxyVO.getTheNotificationContainer();

        if (notifVO == null)
        {
            throw new DataProcessingException("notificationVO is null ");
        }

        NotificationDto notifDT = notifVO.getTheNotificationDT();
        notifDT.setProgAreaCd(notificationProxyVO.getThePublicHealthCaseContainer().getThePublicHealthCaseDto().getProgAreaCd());
        notifDT.setJurisdictionCd(notificationProxyVO.getThePublicHealthCaseContainer().getThePublicHealthCaseDto().getJurisdictionCd());

        if (permissionFlag.equals(OPERATION_CREATE))
        {
            notifDT.setCaseConditionCd(notificationProxyVO.getThePublicHealthCaseContainer().getThePublicHealthCaseDto().getCd());
        }

        if ((notifVO.isItDirty()) || (notifVO.isItNew()))
        {
            String boLookup = NBSBOLookup.NOTIFICATION;
            String triggerCd = "";
            if (permissionFlag.equals(OPERATION_CREATE))
            {
                triggerCd = NEDSSConstant.NOT_CR_APR;
            }
            if (permissionFlag.equals("CREATENEEDSAPPROVAL"))
            {
                triggerCd = NEDSSConstant.NOT_CR_PEND_APR;
            }
            String tableName = "Notification";
            String moduleCd = NEDSSConstant.BASE;

            if(notifVO.isItNew() && propertyUtil.isHIVProgramArea(notifDT.getProgAreaCd()))
            {
                triggerCd = NEDSSConstant.NOT_HIV;// for HIV, notification is always created as completed
            }
            if(notifVO.isItDirty() && propertyUtil.isHIVProgramArea(notifDT.getProgAreaCd()))
            {
                triggerCd = NEDSSConstant.NOT_HIV_EDIT;// for HIV, notification always stay as completed
            }



            notifDT = (NotificationDto) prepareAssocModelHelper.prepareVO(notifDT, boLookup, triggerCd, tableName, moduleCd, notifDT.getVersionCtrlNbr());

            if (notifDT.getCd() == null || notifDT.getCd().isEmpty())
            {
                notifDT.setCd(NEDSSConstant.CLASS_CD_NOTIFICATION);
            }

            notifVO.setTheNotificationDT(notifDT);


            Long falseUid;
            Long realUid;

            // Create Act Here
            realUid = notificationRepositoryUtil.setNotification(notifVO);
            notificationUid = realUid;
            falseUid = notifVO.getTheNotificationDT().getNotificationUid();

            if (notifVO.isItNew())
            {
                ActRelationshipDto actRelDT;
                actRelDT = iUidService.setFalseToNewForNotification(notificationProxyVO, falseUid, realUid);
                notifDT.setNotificationUid(realUid);

                notifVO.setTheNotificationDT(notifDT);
                notificationProxyVO.setTheNotificationContainer(notifVO);
                act2.add(actRelDT);
                notificationProxyVO.setTheActRelationshipDTCollection(act2);
                actRelationshipRepositoryUtil.storeActRelationship(actRelDT);
            }

        } // end of if new or dirty
        return notificationUid;
    } // end of setNotificationProxy



}
