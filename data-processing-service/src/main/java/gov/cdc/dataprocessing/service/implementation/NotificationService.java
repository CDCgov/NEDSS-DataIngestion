package gov.cdc.dataprocessing.service.implementation;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.ObservationDT;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.AbstractVO;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.ObservationVO;
import gov.cdc.dataprocessing.model.container.LabResultProxyContainer;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.notification.NotificationRepository;
import gov.cdc.dataprocessing.service.interfaces.INotificationService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

@Service
public class NotificationService implements INotificationService {
    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public boolean checkForExistingNotification(AbstractVO vo) throws DataProcessingException {
        if (
            //!(vo instanceof VaccinationProxyVO)
            //&& !(vo instanceof MorbidityProxyVO)
            //&&
                !(vo instanceof LabResultProxyContainer)
        ) {
            throw new DataProcessingException("NND MessageSenderHelper: VO not of type Vacc,Morb,LabReport");
        }
        long count = notificationRepository.getCountOfExistingNotifications(getRootUid(vo), getActClassCd(vo));

        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }

    private String getActClassCd(AbstractVO vo)
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

    private Long getRootUid(AbstractVO vo)
    {
        if (vo == null)
            return null;

//        // for now only implement three
//        if (vo instanceof MorbidityProxyVO) {
//            // the root Morb observation UID out of the observation collection
//            Collection<ObservationVO>  obsColl =
//                    ((MorbidityProxyVO) vo).getTheObservationVOCollection();
//            Iterator<ObservationVO> iter = obsColl.iterator();
//            while (iter.hasNext()) {
//                ObservationVO observationVO = (ObservationVO) iter.next();
//                String ctrlCdDisplayForm =
//                        observationVO.getTheObservationDT().getCtrlCdDisplayForm();
//                if (ctrlCdDisplayForm != null
//                        && ctrlCdDisplayForm.equalsIgnoreCase(
//                        NEDSSConstants.MOB_CTRLCD_DISPLAY))
//                    return observationVO
//                            .getTheObservationDT()
//                            .getObservationUid();
//            }
//        } else

            if (vo instanceof LabResultProxyContainer) {
            // the root Lab observation UID out of the observation collection
            Collection<ObservationVO> obsColl =
                    ((LabResultProxyContainer) vo).getTheObservationVOCollection();
            Iterator<ObservationVO> iter = obsColl.iterator();
            while (iter.hasNext()) {
                ObservationVO observationVO = (ObservationVO) iter.next();
                String obsDomainCdSt1 =
                        observationVO.getTheObservationDT().getObsDomainCdSt1();
                String obsCtrlCdDisplayForm =
                        observationVO.getTheObservationDT().getCtrlCdDisplayForm();
                if (obsDomainCdSt1 != null
                        && obsDomainCdSt1.equalsIgnoreCase(
                        NEDSSConstant.ORDERED_TEST_OBS_DOMAIN_CD)
                        && obsCtrlCdDisplayForm != null
                        && obsCtrlCdDisplayForm.equalsIgnoreCase(
                        NEDSSConstant.LAB_REPORT)) {
                    return observationVO
                            .getTheObservationDT()
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
