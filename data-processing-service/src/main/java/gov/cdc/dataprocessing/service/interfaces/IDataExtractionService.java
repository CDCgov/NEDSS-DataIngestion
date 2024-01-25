package gov.cdc.dataprocessing.service.interfaces;

import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;

public interface IDataExtractionService {
    Object parsingDataToObject(String data) throws DataProcessingConsumerException;
}
