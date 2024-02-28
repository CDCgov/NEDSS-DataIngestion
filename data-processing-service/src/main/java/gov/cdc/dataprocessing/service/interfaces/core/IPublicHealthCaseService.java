package gov.cdc.dataprocessing.service.interfaces.core;

import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;

public interface IPublicHealthCaseService {
    Object processingPublicHealthCase() throws DataProcessingConsumerException;
    Object processingAutoInvestigation() throws DataProcessingConsumerException;
}
