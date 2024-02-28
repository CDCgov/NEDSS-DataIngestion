package gov.cdc.dataprocessing.service.interfaces.core;

import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;

public interface ILabProcessingService {
    Object processingLabResult() throws DataProcessingConsumerException;
}
