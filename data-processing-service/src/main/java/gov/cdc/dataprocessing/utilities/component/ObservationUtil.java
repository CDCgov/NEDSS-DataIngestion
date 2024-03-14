package gov.cdc.dataprocessing.utilities.component;


import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.ActRelationshipDT;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.ObservationDT;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.ParticipationDT;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.AbstractVO;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.ObservationVO;
import gov.cdc.dataprocessing.model.container.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.dto.entity.RoleDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

@Component
public class ObservationUtil {


    public ObservationUtil() {
    }

    public Long getUid(Collection<ParticipationDT> participationDTCollection,
                        Collection<ActRelationshipDT> actRelationshipDTCollection,
                        String uidListType, String uidClassCd, String uidTypeCd,
                        String uidActClassCd, String uidRecordStatusCd) throws DataProcessingException {
        Long anUid = null;
        try {
            if (participationDTCollection != null) {
                for (ParticipationDT partDT : participationDTCollection) {
                    if (
                            (
                                    (
                                            partDT.getSubjectClassCd() != null
                                                    && partDT.getSubjectClassCd().equalsIgnoreCase(uidClassCd)
                                    )
                                            && (partDT.getTypeCd() != null
                                            && partDT.getTypeCd().equalsIgnoreCase(uidTypeCd))
                                            && (partDT.getActClassCd() != null
                                            && partDT.getActClassCd().equalsIgnoreCase(uidActClassCd))
                                            && (partDT.getRecordStatusCd() != null
                                            && partDT.getRecordStatusCd().equalsIgnoreCase(uidRecordStatusCd))
                            )
                    )
                    {
                        anUid = partDT.getSubjectEntityUid();
                    }
                }
            }
            else if (actRelationshipDTCollection != null) {
                for (ActRelationshipDT actRelDT : actRelationshipDTCollection) {
                    if (
                            (
                                    actRelDT.getSourceClassCd() != null
                                            && actRelDT.getSourceClassCd().equalsIgnoreCase(uidClassCd)
                            )
                                    && (
                                    actRelDT.getTypeCd() != null
                                            && actRelDT.getTypeCd().equalsIgnoreCase(uidTypeCd)
                            )
                                    && (
                                    actRelDT.getTargetClassCd() != null
                                            && actRelDT.getTargetClassCd().equalsIgnoreCase(uidActClassCd)
                            )
                                    && (
                                    actRelDT.getRecordStatusCd() != null
                                            && actRelDT.getRecordStatusCd().equalsIgnoreCase(uidRecordStatusCd)
                            )

                    ) {
                        if (uidListType.equalsIgnoreCase(NEDSSConstant.ACT_UID_LIST_TYPE)) {
                            anUid = actRelDT.getTargetActUid();
                        } else if (uidListType.equalsIgnoreCase(NEDSSConstant.SOURCE_ACT_UID_LIST_TYPE)) {
                            anUid = actRelDT.getSourceActUid();
                        }
                    }
                }
            }

        } catch (Exception ex) {
            throw new DataProcessingException("Error while retrieving a " + uidListType + " uid. " + ex.toString(), ex);
        }

        return anUid;
    }


    /**
     * Description:
     * Root OBS are one of these following
     *  - Ctrl Code Display Form = LabReport;
     *  - Obs Domain Code St 1 = Order;
     *  - Ctrl Code Display Form = MorbReport;

     *  Original Name: getRootDT
     **/
    public ObservationDT getRootObservationDto(AbstractVO proxyVO) throws DataProcessingException {
        try {
            ObservationVO rootVO = getRootObservationContainer(proxyVO);
            if (rootVO != null)
            {
                return rootVO.getTheObservationDT();
            }
            return null;
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);

        }
    }

    /**
     * Description:
     * Root OBS are one of these following
     *  - Ctrl Code Display Form = LabReport;
     *  - Obs Domain Code St 1 = Order;
     *  - Ctrl Code Display Form = MorbReport;
     * Original Name: getRootObservationVO
     **/
    public ObservationVO getRootObservationContainer(AbstractVO proxy) throws DataProcessingException
    {
        Collection<ObservationVO>  obsColl = null;
        boolean isLabReport = false;

        if (proxy instanceof LabResultProxyContainer)
        {
            obsColl = ( (LabResultProxyContainer) proxy).getTheObservationVOCollection();
            isLabReport = true;
        }
//            if (proxy instanceof MorbidityProxyVO)
//            {
//                obsColl = ( (MorbidityProxyVO) proxy).getTheObservationVOCollection();
//            }

        ObservationVO rootVO = getRootObservationContainerFromObsCollection(obsColl, isLabReport);

        if( rootVO == null)
        {
            throw new DataProcessingException("Expected the proxyVO containing a root observation(e.g., ordered test)");
        }
        return rootVO;

    }


    /**
     * Description:
     * Root OBS are one of these following
     *  - Ctrl Code Display Form = LabReport;
     *  - Obs Domain Code St 1 = Order;
     *  - Ctrl Code Display Form = MorbReport;
     **/
    private ObservationVO getRootObservationContainerFromObsCollection(Collection<ObservationVO> obsColl, boolean isLabReport) {
        if(obsColl == null){
            return null;
        }

        Iterator<ObservationVO> iterator;
        for (iterator = obsColl.iterator(); iterator.hasNext(); )
        {
            ObservationVO observationVO = iterator.next();
            if (
                observationVO.getTheObservationDT() != null
                && (
                        (
                            observationVO.getTheObservationDT().getCtrlCdDisplayForm() != null
                            && observationVO.getTheObservationDT().getCtrlCdDisplayForm().equalsIgnoreCase(NEDSSConstant.LAB_CTRLCD_DISPLAY)
                        )
                        || (
                            observationVO.getTheObservationDT().getObsDomainCdSt1() != null
                            && observationVO.getTheObservationDT().getObsDomainCdSt1().equalsIgnoreCase(NEDSSConstant.ORDERED_TEST_OBS_DOMAIN_CD)
                            && isLabReport
                        ) || (
                            observationVO.getTheObservationDT().getCtrlCdDisplayForm() != null &&
                            observationVO.getTheObservationDT().getCtrlCdDisplayForm().equalsIgnoreCase(NEDSSConstant.MOB_CTRLCD_DISPLAY)
                        )
                )
            )
            {
                return observationVO;
            }
        }
        return null;

    }


}
