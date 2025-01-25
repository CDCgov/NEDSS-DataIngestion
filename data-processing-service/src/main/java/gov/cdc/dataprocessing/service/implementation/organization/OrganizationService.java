package gov.cdc.dataprocessing.service.implementation.organization;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.container.model.OrganizationContainer;
import gov.cdc.dataprocessing.model.dto.log.EDXActivityDetailLogDto;
import gov.cdc.dataprocessing.service.interfaces.organization.IOrganizationMatchingService;
import gov.cdc.dataprocessing.service.interfaces.organization.IOrganizationService;
import gov.cdc.dataprocessing.service.interfaces.uid_generator.IUidService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
@Slf4j
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 2139 - exception rethrow complain
 3740 - parametrized  type for generic complaint
 1149 - replacing HashTable complaint
 112 - throwing dedicate exception complaint
 107 - max parameter complaint
 1195 - duplicate complaint
 1135 - Todos complaint
 6201 - instanceof check
 1192 - duplicate literal
 135 - for loop
 117 - naming
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201", "java:S1192", "java:S135", "java:S117"})
public class OrganizationService implements IOrganizationService {

    private final IOrganizationMatchingService iOrganizationMatchingService;

    private final IUidService uidService;

    public OrganizationService(IOrganizationMatchingService iOrganizationMatchingService,
                               IUidService uidService) {
        this.iOrganizationMatchingService = iOrganizationMatchingService;
        this.uidService = uidService;
    }
    @SuppressWarnings("java:S3776")
    public OrganizationContainer processingOrganization(LabResultProxyContainer labResultProxyContainer) throws DataProcessingConsumerException, DataProcessingException {

        OrganizationContainer orderingFacilityVO = null;
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

    }

}
