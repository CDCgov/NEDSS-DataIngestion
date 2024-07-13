package gov.cdc.dataprocessing.service.implementation.organization;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.model.container.model.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.container.model.OrganizationContainer;
import gov.cdc.dataprocessing.model.dto.log.EDXActivityDetailLogDto;
import gov.cdc.dataprocessing.service.interfaces.organization.IOrganizationMatchingService;
import gov.cdc.dataprocessing.service.interfaces.organization.IOrganizationService;
import gov.cdc.dataprocessing.service.interfaces.uid_generator.IUidService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@Slf4j
public class OrganizationService implements IOrganizationService {

    private static IOrganizationMatchingService iOrganizationMatchingService;

    private final IUidService uidService;

    public OrganizationService(IOrganizationMatchingService iOrganizationMatchingService,
                               IUidService uidService) {
        this.iOrganizationMatchingService = iOrganizationMatchingService;
        this.uidService = uidService;
    }

    public OrganizationContainer processingOrganization(LabResultProxyContainer labResultProxyContainer) throws DataProcessingConsumerException {

        OrganizationContainer orderingFacilityVO = null;
        try {
            Collection<OrganizationContainer> orgColl = labResultProxyContainer.getTheOrganizationContainerCollection();
            if (orgColl != null && !orgColl.isEmpty()) {
                for (OrganizationContainer organizationContainer : orgColl) {

                    long orgUid;
                    if (organizationContainer.getRole() != null && organizationContainer.getRole().equalsIgnoreCase(EdxELRConstant.ELR_SENDING_FACILITY_CD) && labResultProxyContainer.getSendingFacilityUid() != null) {
                        orgUid = labResultProxyContainer.getSendingFacilityUid();
                    }
                    else
                    {
                        EDXActivityDetailLogDto eDXActivityDetailLogDto = iOrganizationMatchingService.getMatchingOrganization(organizationContainer);
                        orgUid = Long.parseLong(eDXActivityDetailLogDto.getRecordId());
                    }
                    Long falseUid = organizationContainer.getTheOrganizationDto().getOrganizationUid();
                    //match found!!!!
                    if (orgUid > 0) {
                        uidService.setFalseToNewPersonAndOrganization(labResultProxyContainer, falseUid, orgUid);
                        organizationContainer.setItNew(false);
                        organizationContainer.setItDirty(false);
                        organizationContainer.getTheOrganizationDto().setItNew(false);
                        organizationContainer.getTheOrganizationDto().setItDirty(false);
                    }
                    if (organizationContainer.getRole() != null && organizationContainer.getRole().equalsIgnoreCase(EdxELRConstant.ELR_OP_CD)) {
                        orderingFacilityVO = organizationContainer;
                    }

                    organizationContainer.getTheOrganizationDto().setOrganizationUid(orgUid);
                }
            }
            return orderingFacilityVO;
        } catch (Exception e) {
            throw new DataProcessingConsumerException(e.getMessage());
        }
    }

}
