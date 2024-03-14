package gov.cdc.dataprocessing.service.interfaces.organization;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.OrganizationContainer;
import gov.cdc.dataprocessing.model.dto.log.EDXActivityDetailLogDto;

public interface IOrganizationMatchingService {
    EDXActivityDetailLogDto getMatchingOrganization(
            OrganizationContainer organizationContainer)
            throws DataProcessingException;
}