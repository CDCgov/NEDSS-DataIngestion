package gov.cdc.dataprocessing.service.interfaces;

import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;

public interface IOrganizationService {
    Object processingOrganization() throws DataProcessingConsumerException;
}
