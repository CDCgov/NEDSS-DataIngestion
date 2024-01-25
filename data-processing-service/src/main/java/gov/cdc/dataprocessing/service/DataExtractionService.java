package gov.cdc.dataprocessing.service;

import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.service.interfaces.IDataExtractionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DataExtractionService implements IDataExtractionService {
    public DataExtractionService () {

    }

    public Object parsingDataToObject(String data) throws DataProcessingConsumerException {
        Object result = new Object();
        try {
            // TODO: add data extraction logic
            result = new Object();
            return result;
        }catch (Exception e) {
            throw new DataProcessingConsumerException(e.getMessage(), "Data");
        }
    }
}
