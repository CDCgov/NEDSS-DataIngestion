package gov.cdc.dataprocessing.service.interfaces.manager;

import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.exception.EdxLogException;

public interface IManagerService {
    void processDistribution(String eventType, String data) throws DataProcessingConsumerException;
    void initiatingInvestigationAndPublicHealthCase(String data) throws DataProcessingException;
    void initiatingLabProcessing(String data)  throws DataProcessingConsumerException;
}
