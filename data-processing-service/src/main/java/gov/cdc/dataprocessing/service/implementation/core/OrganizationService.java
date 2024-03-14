package gov.cdc.dataprocessing.service.implementation.core;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.EDXActivityDetailLogDT;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.OrganizationVO;
import gov.cdc.dataprocessing.model.container.LabResultProxyContainer;
import gov.cdc.dataprocessing.service.interfaces.core.IOrganizationService;
import gov.cdc.dataprocessing.service.interfaces.core.IUidService;
import gov.cdc.dataprocessing.service.interfaces.matching.IOrganizationMatchingService;
import gov.cdc.dataprocessing.utilities.component.organization.OrganizationRepositoryUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@Slf4j
public class OrganizationService implements IOrganizationService {
    private static final Logger logger = LoggerFactory.getLogger(OrganizationService.class);

    private static IOrganizationMatchingService iOrganizationMatchingService;
    private final OrganizationRepositoryUtil organizationRepositoryUtil;

    private final IUidService uidService;

    public OrganizationService(IOrganizationMatchingService iOrganizationMatchingService,
                               OrganizationRepositoryUtil organizationRepositoryUtil,
                               IUidService uidService) {
        this.iOrganizationMatchingService = iOrganizationMatchingService;
        this.organizationRepositoryUtil = organizationRepositoryUtil;
        this.uidService = uidService;
    }

    public OrganizationVO testloadObject(long orguid, long actid) throws DataProcessingException {
        OrganizationVO organizationVO = organizationRepositoryUtil.loadObject(orguid, actid);
        return organizationVO;
    }

    public OrganizationVO processingOrganization(LabResultProxyContainer labResultProxyContainer) throws DataProcessingConsumerException {

        OrganizationVO orderingFacilityVO = null;
        try {
            Collection<OrganizationVO> orgColl = labResultProxyContainer.getTheOrganizationVOCollection();
            if (orgColl != null && !orgColl.isEmpty()) {
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
                        uidService.setFalseToNewPersonAndOrganization(labResultProxyContainer, falseUid, orgUid);
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

}
