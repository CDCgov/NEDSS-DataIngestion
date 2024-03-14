package gov.cdc.dataprocessing.service.implementation.core;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.ActRelationshipDT;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.ParticipationDT;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.AbstractVO;
import gov.cdc.dataprocessing.model.container.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.dto.entity.RoleDto;
import gov.cdc.dataprocessing.service.interfaces.core.IUidService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Iterator;

@Service
public class UidService implements IUidService {
    private static final Logger logger = LoggerFactory.getLogger(UidService.class);

    /**
     * This method checks for the negative uid value for any ACT & ENTITY DT then compare them
     * with respective negative values in ActRelationshipDT and ParticipationDT as received from
     * the investigationProxyVO(determined in the addInvestigation method).
     * As it has also got the actualUID (determined in the addInvestigation method) it replaces them accordingly.
     */
    public void setFalseToNewForObservation(AbstractVO proxyVO, Long falseUid, Long actualUid)
    {
        Iterator<ParticipationDT> participationDTIterator;
        Iterator<ActRelationshipDT>  actRelationshipDTIterator;
        Iterator<RoleDto>  roleDtoIterator;

        ParticipationDT participationDT;
        ActRelationshipDT actRelationshipDT;
        RoleDto roleDT;

        Collection<ParticipationDT> participationColl = null;
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


    /**
     * This method update uid for items in the following collection
     * Participation collection
     * Act Relationship collection
     * Role collection
     * - This is crucial in Observation Flow
     * */
    public void setFalseToNewPersonAndOrganization(LabResultProxyContainer labResultProxyContainer, Long falseUid, Long actualUid)
    {
        Iterator<ParticipationDT> participationIterator = null;
        Iterator<ActRelationshipDT> actRelationshipIterator = null;
        Iterator<RoleDto> roleIterator = null;

        ParticipationDT participationDT = null;
        ActRelationshipDT actRelationshipDT = null;
        RoleDto roleDto = null;

        Collection<ParticipationDT> participationColl = labResultProxyContainer.getTheParticipationDTCollection();
        Collection<ActRelationshipDT> actRelationShipColl = labResultProxyContainer.getTheActRelationshipDTCollection();
        Collection<RoleDto> roleColl = labResultProxyContainer.getTheRoleDtoCollection();

        if (participationColl != null) {
            for (participationIterator = participationColl.iterator(); participationIterator.hasNext(); ) {
                participationDT = participationIterator.next();
                if (participationDT.getActUid().compareTo(falseUid) == 0) {
                    participationDT.setActUid(actualUid);
                }
                if (participationDT.getSubjectEntityUid().compareTo(falseUid) == 0) {
                    participationDT.setSubjectEntityUid(actualUid);
                }
            }
        }

        if (actRelationShipColl != null) {
            for (actRelationshipIterator = actRelationShipColl.iterator(); actRelationshipIterator.hasNext(); ) {
                actRelationshipDT = actRelationshipIterator.next();
                if (actRelationshipDT.getTargetActUid().compareTo(falseUid) == 0) {
                    actRelationshipDT.setTargetActUid(actualUid);
                }
                if (actRelationshipDT.getSourceActUid().compareTo(falseUid) == 0) {
                    actRelationshipDT.setSourceActUid(actualUid);
                }
            }
        }

        if (roleColl != null) {
            for (roleIterator = roleColl.iterator(); roleIterator.hasNext(); ) {
                roleDto = roleIterator.next();
                if (roleDto.getSubjectEntityUid().compareTo(falseUid) == 0) {
                    roleDto.setSubjectEntityUid(actualUid);
                }
                if (roleDto.getScopingEntityUid() != null) {
                    if (roleDto.getScopingEntityUid().compareTo(falseUid) == 0) {
                        roleDto.setScopingEntityUid(actualUid);
                    }
                }

            }
        }
    }

}
