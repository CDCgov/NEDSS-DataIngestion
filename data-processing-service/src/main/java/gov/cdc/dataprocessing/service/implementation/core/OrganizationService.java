package gov.cdc.dataprocessing.service.implementation.core;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.ActRelationshipDT;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.EDXActivityDetailLogDT;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.ParticipationDT;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.OrganizationVO;
import gov.cdc.dataprocessing.model.container.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.dto.entity.RoleDto;
import gov.cdc.dataprocessing.service.interfaces.core.IOrganizationService;
import gov.cdc.dataprocessing.service.interfaces.matching.IOrganizationMatchingService;
import gov.cdc.dataprocessing.utilities.component.organization.OrganizationRepositoryUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Iterator;

@Service
@Slf4j
public class OrganizationService implements IOrganizationService {
    private static final Logger logger = LoggerFactory.getLogger(OrganizationService.class);

    private static IOrganizationMatchingService iOrganizationMatchingService;
    private final OrganizationRepositoryUtil organizationRepositoryUtil;
    public OrganizationService(IOrganizationMatchingService iOrganizationMatchingService,OrganizationRepositoryUtil organizationRepositoryUtil) {
        this.iOrganizationMatchingService = iOrganizationMatchingService;
        this.organizationRepositoryUtil=organizationRepositoryUtil;
    }
    public OrganizationVO testloadObject(long orguid, long actid) throws DataProcessingException {
        OrganizationVO organizationVO=organizationRepositoryUtil.loadObject(orguid,actid);
       return organizationVO;
    }
    public OrganizationVO processingOrganization(LabResultProxyContainer labResultProxyContainer) throws DataProcessingConsumerException {
        OrganizationVO orderingFacilityVO=null;
        try {
            Collection<OrganizationVO> orgColl = labResultProxyContainer.getTheOrganizationVOCollection();
            if (orgColl != null) {
                for (OrganizationVO organizationVO : orgColl) {
                    Long orgUid;
                    if (organizationVO.getRole() != null && organizationVO.getRole().equalsIgnoreCase(EdxELRConstant.ELR_SENDING_FACILITY_CD) && labResultProxyContainer.getSendingFacilityUid() != null) {
                        orgUid = labResultProxyContainer.getSendingFacilityUid();
                    } else {
//                        EdxMatchingCriteriaUtil util = new EdxMatchingCriteriaUtil();
//                        EDXActivityDetailLogDT eDXActivityDetailLogDT = new EDXActivityDetailLogDT();
//                        eDXActivityDetailLogDT = util.getMatchingOrganization(
//                                organizationVO, nbsSecurityObj);

                        EDXActivityDetailLogDT eDXActivityDetailLogDT = iOrganizationMatchingService.getMatchingOrganization(organizationVO);
                        orgUid = Long.parseLong(eDXActivityDetailLogDT.getRecordId());
                    }
                    Long falseUid = organizationVO.getTheOrganizationDT()
                            .getOrganizationUid();
                    //match found!!!!
                    if (orgUid > 0) {
                        setFalseToNew(labResultProxyContainer, falseUid, orgUid);
                        // /organizationVO
                        // =getOrganization(orgUid,nbsSecurityObj);
                        organizationVO.setItNew(false);
                        organizationVO.setItDirty(false);
                        organizationVO.getTheOrganizationDT().setItNew(false);
                        organizationVO.getTheOrganizationDT().setItDirty(false);
                    }
                    if (organizationVO.getRole() != null && organizationVO.getRole().equalsIgnoreCase(EdxELRConstant.ELR_OP_CD)) {
                        orderingFacilityVO = organizationVO;
                    }

                    organizationVO.getTheOrganizationDT().setOrganizationUid(orgUid);
                }
            }
            return orderingFacilityVO;
        } catch (Exception e) {
            throw new DataProcessingConsumerException("ERROR", "Data");
        }
    }

    /**
     * NOTE: Not sure what this for -- copied from PatientService..to be moved to common file.
     */
    private void setFalseToNew(LabResultProxyContainer labResultProxyContainer, Long falseUid, Long actualUid) throws DataProcessingException {

        try {
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
                    participationDT = (ParticipationDT) participationIterator.next();
                    logger.debug("(participationDT.getAct() comparedTo falseUid)"
                            + (participationDT.getActUid().compareTo(falseUid)));
                    if (participationDT.getActUid().compareTo(falseUid) == 0) {
                        participationDT.setActUid(actualUid);
                    }

                    if (participationDT.getSubjectEntityUid().compareTo(falseUid) == 0) {
                        participationDT.setSubjectEntityUid(actualUid);
                    }
                }
                logger.debug("participationDT.getSubjectEntityUid()"
                        + participationDT.getSubjectEntityUid());
            }

            if (actRelationShipColl != null) {
                for (actRelationshipIterator = actRelationShipColl.iterator(); actRelationshipIterator
                        .hasNext(); ) {
                    actRelationshipDT = (ActRelationshipDT) actRelationshipIterator.next();

                    if (actRelationshipDT.getTargetActUid().compareTo(falseUid) == 0) {
                        actRelationshipDT.setTargetActUid(actualUid);
                    }
                    if (actRelationshipDT.getSourceActUid().compareTo(falseUid) == 0) {
                        actRelationshipDT.setSourceActUid(actualUid);
                    }
                    logger.debug("ActRelationShipDT: falseUid "
                            + falseUid.toString() + " actualUid: " + actualUid);
                }
            }

            if (roleColl != null) {
                for (roleIterator = roleColl.iterator(); roleIterator.hasNext(); ) {
                    roleDto = (RoleDto) roleIterator.next();
                    if (roleDto.getSubjectEntityUid().compareTo(falseUid) == 0) {
                        roleDto.setSubjectEntityUid(actualUid);
                    }
                    if (roleDto.getScopingEntityUid() != null) {
                        if (roleDto.getScopingEntityUid().compareTo(falseUid) == 0) {
                            roleDto.setScopingEntityUid(actualUid);
                        }
                        logger.debug("\n\n\n(roleDT.getSubjectEntityUid() compared to falseUid)  "
                                + roleDto.getSubjectEntityUid().compareTo(
                                falseUid));
                        logger.debug("\n\n\n(roleDT.getScopingEntityUid() compared to falseUid)  "
                                + roleDto.getScopingEntityUid().compareTo(
                                falseUid));
                    }

                }
            }

        } catch (Exception e) {
            logger.error("HL7CommonLabUtil.setFalseToNew thrown for falseUid:"
                    + falseUid + "For actualUid :" + actualUid);
            throw new DataProcessingException("HL7CommonLabUtil.setFalseToNew thrown for falseUid:" + falseUid + "For actualUid :" + actualUid);
        }
    }
}
