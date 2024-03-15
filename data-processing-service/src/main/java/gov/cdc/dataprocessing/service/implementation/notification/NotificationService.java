package gov.cdc.dataprocessing.service.implementation.notification;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.ObservationContainer;
import gov.cdc.dataprocessing.model.container.BaseContainer;
import gov.cdc.dataprocessing.model.container.LabResultProxyContainer;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.notification.NotificationRepository;
import gov.cdc.dataprocessing.service.interfaces.notification.INotificationService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Iterator;

@Service
public class NotificationService implements INotificationService {
    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
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
            return null;

        /*Both lab and morb class codes are OBS */
//        if (vo instanceof MorbidityProxyVO) {
//            // return "OBS"
//            return NEDSSConstants.CLASS_CD_OBS;
//        }

        if (vo instanceof LabResultProxyContainer) {
            // return "OBS"
            return NEDSSConstant.CLASS_CD_OBS;
        }

//        else if (vo instanceof VaccinationProxyVO) {
//            // return "INTV"
//            return NEDSSConstants.CLASS_CD_INTV;
//        }
        return null;
    }

    private Long getRootUid(BaseContainer vo)
    {
        if (vo == null)
            return null;

//        // for now only implement three
//        if (vo instanceof MorbidityProxyVO) {
//            // the root Morb observation UID out of the observation collection
//            Collection<ObservationContainer>  obsColl =
//                    ((MorbidityProxyVO) vo).getTheObservationContainerCollection();
//            Iterator<ObservationContainer> iter = obsColl.iterator();
//            while (iter.hasNext()) {
//                ObservationContainer observationVO = (ObservationContainer) iter.next();
//                String ctrlCdDisplayForm =
//                        observationVO.getTheObservationDto().getCtrlCdDisplayForm();
//                if (ctrlCdDisplayForm != null
//                        && ctrlCdDisplayForm.equalsIgnoreCase(
//                        NEDSSConstants.MOB_CTRLCD_DISPLAY))
//                    return observationVO
//                            .getTheObservationDto()
//                            .getObservationUid();
//            }
//        } else

            if (vo instanceof LabResultProxyContainer) {
            // the root Lab observation UID out of the observation collection
            Collection<ObservationContainer> obsColl =
                    ((LabResultProxyContainer) vo).getTheObservationContainerCollection();
            Iterator<ObservationContainer> iter = obsColl.iterator();
            while (iter.hasNext()) {
                ObservationContainer observationContainer = (ObservationContainer) iter.next();
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
//            else if (vo instanceof VaccinationProxyVO) {
//            // the vaccination UID
//            return ((VaccinationProxyVO) vo)
//                    .theInterventionVO
//                    .getTheInterventionDT()
//                    .getInterventionUid();
//        }
        return null;
    }



}
