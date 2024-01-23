package gov.cdc.dataprocessing.service.interfaces;

import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;

public interface IPublicHealthCaseService {
    String processingPublicHealthCase() throws DataProcessingConsumerException;
    String processingAutoInvestigation() throws DataProcessingConsumerException;
}
