package gov.cdc.dataprocessing.service.interfaces.organization;

import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.OrganizationContainer;
import gov.cdc.dataprocessing.model.container.LabResultProxyContainer;

public interface IOrganizationService {
    OrganizationContainer processingOrganization(LabResultProxyContainer labResultProxyContainer) throws DataProcessingConsumerException;
    OrganizationContainer testloadObject(long orguid, long actid) throws DataProcessingException;
}
