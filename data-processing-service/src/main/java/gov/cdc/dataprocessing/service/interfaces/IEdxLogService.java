package gov.cdc.dataprocessing.service.interfaces;

import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;

public interface IEdxLogService {
    String processingLog() throws DataProcessingConsumerException;
}
