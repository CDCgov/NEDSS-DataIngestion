package gov.cdc.dataprocessing.service;

import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.service.interfaces.IObservationService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ObservationService implements IObservationService {
    private static final Logger logger = LoggerFactory.getLogger(ObservationService.class);

    public ObservationService() {

    }

    public Object processingObservation() throws DataProcessingConsumerException {
        //TODO: Adding Observation logic here
        try {
            return "processing observation";
        } catch (Exception e) {
            throw new DataProcessingConsumerException("ERROR", "Data");
        }

    }
}
