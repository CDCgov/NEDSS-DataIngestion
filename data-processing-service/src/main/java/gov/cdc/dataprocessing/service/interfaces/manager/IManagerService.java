package gov.cdc.dataprocessing.service.interfaces.manager;

import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.exception.EdxLogException;

public interface IManagerService {
    Object processDistribution(String eventType, String data) throws DataProcessingConsumerException;
    Object processingHealthCase(String data) throws DataProcessingConsumerException;
    Object processingHandleLab(String data)  throws DataProcessingConsumerException;
    void processingEdxLog(String data) throws DataProcessingConsumerException, EdxLogException;

}
