package gov.cdc.dataprocessing.service.interfaces.organization;

import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.model.container.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.container.OrganizationContainer;

public interface IOrganizationService {
    OrganizationContainer processingOrganization(LabResultProxyContainer labResultProxyContainer) throws DataProcessingConsumerException;
}
