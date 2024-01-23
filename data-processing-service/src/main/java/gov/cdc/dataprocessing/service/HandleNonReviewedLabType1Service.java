package gov.cdc.dataprocessing.service;

import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.service.interfaces.IHandleNonReviewedLabType1Service;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class HandleNonReviewedLabType1Service implements IHandleNonReviewedLabType1Service {
    private static final Logger logger = LoggerFactory.getLogger(HandleNonReviewedLabType1Service.class);
    public HandleNonReviewedLabType1Service() {

    }

    public String processingNonReviewLabType1() throws DataProcessingConsumerException {
        //TODO: Adding logic here
        try {
            return "processing non rev lab 1";
        } catch (Exception e) {
            throw new DataProcessingConsumerException("ERROR");
        }

    }
}
