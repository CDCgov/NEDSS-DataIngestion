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


    public ObservationDT getRootDT(AbstractVO proxyVO) throws DataProcessingException {
        try {
            ObservationVO rootVO = getRootObservationVO(proxyVO);
            if (rootVO != null)
            {
                return rootVO.getTheObservationDT();
            }
            return null;
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);

        }
    }

    public ObservationVO getRootObservationVO(AbstractVO proxy) throws DataProcessingException
    {
        try {
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

            ObservationVO rootVO = getRootObservationVO(obsColl, isLabReport);

            if( rootVO != null)
            {
                return rootVO;
            }
            throw new DataProcessingException("Expected the proxyVO containing a root observation(e.g., ordered test)");
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    /**
     * This method checks for the negative uid value for any ACT & ENTITY DT then compare them
     * with respective negative values in ActRelationshipDT and ParticipationDT as received from
     * the investigationProxyVO(determined in the addInvestigation method).
     * As it has also got the actualUID (determined in the addInvestigation method) it replaces them accordingly.
     */
    public void setFalseToNew(AbstractVO proxyVO, Long falseUid, Long actualUid)
    {
        Iterator<ParticipationDT>  participationDTIterator;
        Iterator<ActRelationshipDT>  actRelationshipDTIterator;
        Iterator<RoleDto>  roleDtoIterator;


        ParticipationDT participationDT;
        ActRelationshipDT actRelationshipDT;
        RoleDto roleDT;

        Collection<ParticipationDT>  participationColl = null;
        Collection<ActRelationshipDT>  actRelationShipColl = null;
        Collection<RoleDto>  roleColl = null;

        if (proxyVO instanceof LabResultProxyContainer)
        {
            participationColl =  ((LabResultProxyContainer) proxyVO).getTheParticipationDTCollection();
            actRelationShipColl = ((LabResultProxyContainer) proxyVO).getTheActRelationshipDTCollection();
            roleColl = ((LabResultProxyContainer) proxyVO).getTheRoleDtoCollection();
        }

        //TODO: MORBIDITY
//            if (proxyVO instanceof MorbidityProxyVO)
//            {
//                participationColl = (ArrayList<Object> ) ( (MorbidityProxyVO) proxyVO).
//                        getTheParticipationDTCollection();
//                actRelationShipColl = (ArrayList<Object> ) ( (MorbidityProxyVO) proxyVO).
//                        getTheActRelationshipDTCollection();
//                roleColl = (ArrayList<Object> ) ( (MorbidityProxyVO) proxyVO).
//                        getTheRoleDTCollection();
//            }

        if (participationColl != null)
        {
            for (participationDTIterator = participationColl.iterator(); participationDTIterator.hasNext(); )
            {
                participationDT = participationDTIterator.next();
                if (participationDT != null && falseUid != null)
                {
                    if (participationDT.getActUid().compareTo(falseUid) == 0)
                    {
                        participationDT.setActUid(actualUid);
                    }
                    if (participationDT.getSubjectEntityUid().compareTo(falseUid) == 0)
                    {
                        participationDT.setSubjectEntityUid(actualUid);
                    }
                }
            }
        }

        if (actRelationShipColl != null)
        {
            for (actRelationshipDTIterator = actRelationShipColl.iterator(); actRelationshipDTIterator.hasNext(); )
            {
                actRelationshipDT = actRelationshipDTIterator.next();
                if (actRelationshipDT.getTargetActUid().compareTo(falseUid) == 0)
                {
                    actRelationshipDT.setTargetActUid(actualUid);
                }
                if (actRelationshipDT.getSourceActUid().compareTo(falseUid) == 0)
                {
                    actRelationshipDT.setSourceActUid(actualUid);
                }
            }

        }

        if (roleColl != null && roleColl.size() != 0)
        {
            for (roleDtoIterator = roleColl.iterator(); roleDtoIterator.hasNext(); )
            {
                roleDT =  roleDtoIterator.next();

                if (roleDT.getSubjectEntityUid().compareTo(falseUid) == 0)
                {
                    roleDT.setSubjectEntityUid(actualUid);

                }
                if (roleDT.getScopingEntityUid() != null)
                {
                    if (roleDT.getScopingEntityUid().compareTo(falseUid) == 0)
                    {
                        roleDT.setScopingEntityUid(actualUid);
                    }
                }

            }
        }
    }


    private ObservationVO getRootObservationVO(Collection<ObservationVO> obsColl, boolean isLabReport) throws DataProcessingException {
        try {
            if(obsColl == null){
                return null;
            }

            Iterator<ObservationVO> iterator;
            for (iterator = obsColl.iterator(); iterator.hasNext(); )
            {
                ObservationVO observationVO = iterator.next();
                if (observationVO.getTheObservationDT() != null &&
                        ( (observationVO.getTheObservationDT().getCtrlCdDisplayForm() != null &&
                                observationVO.getTheObservationDT().getCtrlCdDisplayForm().
                                        equalsIgnoreCase(NEDSSConstant.LAB_CTRLCD_DISPLAY))
                                ||
                                (observationVO.getTheObservationDT().getObsDomainCdSt1() != null &&
                                        observationVO.getTheObservationDT().getObsDomainCdSt1().
                                                equalsIgnoreCase(NEDSSConstant.ORDERED_TEST_OBS_DOMAIN_CD) && isLabReport)
                                ||
                                (observationVO.getTheObservationDT().getCtrlCdDisplayForm() != null &&
                                        observationVO.getTheObservationDT().getCtrlCdDisplayForm().
                                                equalsIgnoreCase(NEDSSConstant.MOB_CTRLCD_DISPLAY))))
                {
                    return observationVO;
                }
            }
            return null;
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);

        }
    }


}
