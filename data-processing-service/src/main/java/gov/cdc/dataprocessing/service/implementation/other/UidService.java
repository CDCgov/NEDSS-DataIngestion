package gov.cdc.dataprocessing.service.implementation.other;

import gov.cdc.dataprocessing.model.dto.act.ActRelationshipDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.model.container.BaseContainer;
import gov.cdc.dataprocessing.model.container.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.dto.entity.RoleDto;
import gov.cdc.dataprocessing.service.interfaces.other.IUidService;
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
     * with respective negative values in ActRelationshipDto and ParticipationDto as received from
     * the investigationProxyVO(determined in the addInvestigation method).
     * As it has also got the actualUID (determined in the addInvestigation method) it replaces them accordingly.
     */
    public void setFalseToNewForObservation(BaseContainer proxyVO, Long falseUid, Long actualUid)
    {
        Iterator<ParticipationDto> participationDTIterator;
        Iterator<ActRelationshipDto>  actRelationshipDTIterator;
        Iterator<RoleDto>  roleDtoIterator;

        ParticipationDto participationDto;
        ActRelationshipDto actRelationshipDto;
        RoleDto roleDT;

        Collection<ParticipationDto> participationColl = null;
        Collection<ActRelationshipDto>  actRelationShipColl = null;
        Collection<RoleDto>  roleColl = null;

        if (proxyVO instanceof LabResultProxyContainer)
        {
            participationColl =  ((LabResultProxyContainer) proxyVO).getTheParticipationDtoCollection();
            actRelationShipColl = ((LabResultProxyContainer) proxyVO).getTheActRelationshipDtoCollection();
            roleColl = ((LabResultProxyContainer) proxyVO).getTheRoleDtoCollection();
        }

        //TODO: MORBIDITY
//            if (proxyVO instanceof MorbidityProxyVO)
//            {
//                participationColl = (ArrayList<Object> ) ( (MorbidityProxyVO) proxyVO).
//                        getTheParticipationDtoCollection();
//                actRelationShipColl = (ArrayList<Object> ) ( (MorbidityProxyVO) proxyVO).
//                        getTheActRelationshipDtoCollection();
//                roleColl = (ArrayList<Object> ) ( (MorbidityProxyVO) proxyVO).
//                        getTheRoleDTCollection();
//            }

        if (participationColl != null)
        {
            for (participationDTIterator = participationColl.iterator(); participationDTIterator.hasNext(); )
            {
                participationDto = participationDTIterator.next();
                if (participationDto != null && falseUid != null)
                {
                    if (participationDto.getActUid().compareTo(falseUid) == 0)
                    {
                        participationDto.setActUid(actualUid);
                    }
                    if (participationDto.getSubjectEntityUid().compareTo(falseUid) == 0)
                    {
                        participationDto.setSubjectEntityUid(actualUid);
                    }
                }
            }
        }

        if (actRelationShipColl != null)
        {
            for (actRelationshipDTIterator = actRelationShipColl.iterator(); actRelationshipDTIterator.hasNext(); )
            {
                actRelationshipDto = actRelationshipDTIterator.next();
                if (actRelationshipDto.getTargetActUid().compareTo(falseUid) == 0)
                {
                    actRelationshipDto.setTargetActUid(actualUid);
                }
                if (actRelationshipDto.getSourceActUid().compareTo(falseUid) == 0)
                {
                    actRelationshipDto.setSourceActUid(actualUid);
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
        Iterator<ParticipationDto> participationIterator = null;
        Iterator<ActRelationshipDto> actRelationshipIterator = null;
        Iterator<RoleDto> roleIterator = null;

        ParticipationDto participationDto = null;
        ActRelationshipDto actRelationshipDto = null;
        RoleDto roleDto = null;

        Collection<ParticipationDto> participationColl = labResultProxyContainer.getTheParticipationDtoCollection();
        Collection<ActRelationshipDto> actRelationShipColl = labResultProxyContainer.getTheActRelationshipDtoCollection();
        Collection<RoleDto> roleColl = labResultProxyContainer.getTheRoleDtoCollection();

        if (participationColl != null) {
            for (participationIterator = participationColl.iterator(); participationIterator.hasNext(); ) {
                participationDto = participationIterator.next();
                if (participationDto.getActUid().compareTo(falseUid) == 0) {
                    participationDto.setActUid(actualUid);
                }
                if (participationDto.getSubjectEntityUid().compareTo(falseUid) == 0) {
                    participationDto.setSubjectEntityUid(actualUid);
                }
            }
        }

        if (actRelationShipColl != null) {
            for (actRelationshipIterator = actRelationShipColl.iterator(); actRelationshipIterator.hasNext(); ) {
                actRelationshipDto = actRelationshipIterator.next();
                if (actRelationshipDto.getTargetActUid().compareTo(falseUid) == 0) {
                    actRelationshipDto.setTargetActUid(actualUid);
                }
                if (actRelationshipDto.getSourceActUid().compareTo(falseUid) == 0) {
                    actRelationshipDto.setSourceActUid(actualUid);
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
