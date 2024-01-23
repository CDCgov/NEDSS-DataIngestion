package gov.cdc.dataprocessing.service.interfaces;

import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;

public interface IOrganizationService {
    String processingOrganization() throws DataProcessingConsumerException;
}
