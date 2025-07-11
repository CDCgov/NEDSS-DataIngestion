package gov.cdc.dataprocessing.service.implementation.uid_generator;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.model.container.model.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.container.model.NotificationProxyContainer;
import gov.cdc.dataprocessing.model.container.model.PageActProxyContainer;
import gov.cdc.dataprocessing.model.container.model.PamProxyContainer;
import gov.cdc.dataprocessing.model.dto.act.ActRelationshipDto;
import gov.cdc.dataprocessing.model.dto.entity.RoleDto;
import gov.cdc.dataprocessing.model.dto.nbs.NbsActEntityDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.service.interfaces.uid_generator.IUidService;
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
    @SuppressWarnings({"java:S3776","java:S6541"})
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

        if (proxyVO instanceof LabResultProxyContainer labresultproxycontainer)
        {
            participationColl =  labresultproxycontainer.getTheParticipationDtoCollection();
            actRelationShipColl = labresultproxycontainer.getTheActRelationshipDtoCollection();
            roleColl = labresultproxycontainer.getTheRoleDtoCollection();
        }

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
                if (falseUid != null && actRelationshipDto.getTargetActUid().compareTo(falseUid) == 0)
                {
                    actRelationshipDto.setTargetActUid(actualUid);
                }
                if (falseUid != null && actRelationshipDto.getSourceActUid().compareTo(falseUid) == 0)
                {
                    actRelationshipDto.setSourceActUid(actualUid);
                }
            }

        }

        if (roleColl != null && !roleColl.isEmpty())
        {
            for (roleDtoIterator = roleColl.iterator(); roleDtoIterator.hasNext(); )
            {
                roleDT =  roleDtoIterator.next();

                if (falseUid != null && roleDT.getSubjectEntityUid().compareTo(falseUid) == 0)
                {
                    roleDT.setSubjectEntityUid(actualUid);

                }
                if (roleDT.getScopingEntityUid() != null && falseUid != null && roleDT.getScopingEntityUid().compareTo(falseUid) == 0)
                {
                    roleDT.setScopingEntityUid(actualUid);
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
    @SuppressWarnings("java:S3776")
    public void setFalseToNewPersonAndOrganization(LabResultProxyContainer labResultProxyContainer, Long falseUid, Long actualUid)
    {
        Iterator<ParticipationDto> participationIterator;
        Iterator<ActRelationshipDto> actRelationshipIterator;
        Iterator<RoleDto> roleIterator;

        ParticipationDto participationDto;
        ActRelationshipDto actRelationshipDto ;
        RoleDto roleDto;

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
                if (roleDto.getScopingEntityUid() != null && roleDto.getScopingEntityUid().compareTo(falseUid) == 0) {
                    roleDto.setScopingEntityUid(actualUid);
                }

            }
        }
    }


    /**
     * Converts negative UIDs to positive UIDs
     */
    @SuppressWarnings("java:S3776")
    public void setFalseToNewForPageAct(PageActProxyContainer pageProxyVO, Long falseUid, Long actualUid) {

        ParticipationDto participationDT;
        ActRelationshipDto actRelationshipDT;
        NbsActEntityDto pamCaseEntityDT;
        Collection<ParticipationDto> participationColl = pageProxyVO.getTheParticipationDtoCollection();
        Collection<ActRelationshipDto> actRelationShipColl = pageProxyVO.getPublicHealthCaseContainer().getTheActRelationshipDTCollection();
        Collection<NbsActEntityDto> pamCaseEntityColl = pageProxyVO.getPageVO().getActEntityDTCollection();


        Iterator<ParticipationDto> anIteratorPat;
        if (participationColl != null) {
            for (anIteratorPat = participationColl.iterator(); anIteratorPat
                    .hasNext();) {
                participationDT = anIteratorPat.next();
                if (participationDT.getActUid().compareTo(falseUid) == 0) {
                    participationDT.setActUid(actualUid);
                }
                if (participationDT.getSubjectEntityUid().compareTo(falseUid) == 0) {
                    participationDT.setSubjectEntityUid(actualUid);

                }
            }
        }

        Iterator<ActRelationshipDto> anIteratorActRe;
        if (actRelationShipColl != null) {
            for (anIteratorActRe = actRelationShipColl.iterator(); anIteratorActRe
                    .hasNext();) {
                actRelationshipDT =  anIteratorActRe.next();

                if (actRelationshipDT.getTargetActUid().compareTo(falseUid) == 0) {
                    actRelationshipDT.setTargetActUid(actualUid);
                }
                if (actRelationshipDT.getSourceActUid().compareTo(falseUid) == 0) {
                    actRelationshipDT.setSourceActUid(actualUid);
                }
                logger.debug("ActRelationShipDT: falseUid {} actualUid: {}",falseUid, actualUid);
            }
        }

        Iterator<NbsActEntityDto> anIteratorNbsActEntity;
        if (pamCaseEntityColl != null) {
            for (anIteratorNbsActEntity = pamCaseEntityColl.iterator(); anIteratorNbsActEntity
                    .hasNext();) {
                pamCaseEntityDT =  anIteratorNbsActEntity.next();
                if (pamCaseEntityDT.getEntityUid().compareTo(falseUid) == 0) {
                    pamCaseEntityDT.setEntityUid(actualUid);
                }
            }
        }
    }

    @SuppressWarnings("java:S3776")
    public void setFalseToNewForPam(PamProxyContainer pamProxyVO, Long falseUid, Long actualUid) {
        ParticipationDto participationDT;
        ActRelationshipDto actRelationshipDT;
        NbsActEntityDto pamCaseEntityDT;
        Collection<ParticipationDto>  participationColl = pamProxyVO.getTheParticipationDTCollection();
        Collection<ActRelationshipDto>  actRelationShipColl = pamProxyVO
                .getPublicHealthCaseContainer().getTheActRelationshipDTCollection();
        Collection<NbsActEntityDto>  pamCaseEntityColl = pamProxyVO.getPamVO().getActEntityDTCollection();

        Iterator<ParticipationDto>  anIteratorPat;
        if (participationColl != null) {
            for (anIteratorPat = participationColl.iterator(); anIteratorPat.hasNext();) {
                participationDT =  anIteratorPat.next();
                if (participationDT.getActUid().compareTo(falseUid) == 0) {
                    participationDT.setActUid(actualUid);
                }
                if (participationDT.getSubjectEntityUid().compareTo(falseUid) == 0) {
                    participationDT.setSubjectEntityUid(actualUid);
                }
            }
        }

        Iterator<ActRelationshipDto>  anIteratorAct;

        if (actRelationShipColl != null) {
            for (anIteratorAct = actRelationShipColl.iterator(); anIteratorAct.hasNext();) {
                actRelationshipDT = anIteratorAct.next();

                if (actRelationshipDT.getTargetActUid().compareTo(falseUid) == 0) {
                    actRelationshipDT.setTargetActUid(actualUid);
                }
                if (actRelationshipDT.getSourceActUid().compareTo(falseUid) == 0) {
                    actRelationshipDT.setSourceActUid(actualUid);
                }
            }
        }

        Iterator<NbsActEntityDto>  anIteratorActEntity;

        if (pamCaseEntityColl != null) {
            for (anIteratorActEntity = pamCaseEntityColl.iterator(); anIteratorActEntity.hasNext();) {
                pamCaseEntityDT =  anIteratorActEntity.next();
                if (pamCaseEntityDT.getEntityUid().compareTo(falseUid) == 0) {
                    pamCaseEntityDT.setEntityUid(actualUid);
                }
            }

        }
    }

    public ActRelationshipDto setFalseToNewForNotification(NotificationProxyContainer notificationProxyVO, Long falseUid, Long actualUid)  {

        Iterator<Object> anIterator;
        ActRelationshipDto actRelationshipDT = null;
        Collection<Object> actRelationShipColl = notificationProxyVO.getTheActRelationshipDTCollection();

        if (actRelationShipColl != null)
        {

            for (anIterator = actRelationShipColl.iterator(); anIterator.hasNext();)
            {
                actRelationshipDT = (ActRelationshipDto) anIterator.next();
                actRelationshipDT.setSourceActUid(actualUid);
            }
        }
        return actRelationshipDT;
    }
}
