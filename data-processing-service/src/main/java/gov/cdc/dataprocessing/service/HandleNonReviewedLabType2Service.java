package gov.cdc.dataprocessing.service;

import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.service.interfaces.IHandleNonReviewedLabType2Service;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class HandleNonReviewedLabType2Service implements IHandleNonReviewedLabType2Service {
    private static final Logger logger = LoggerFactory.getLogger(HandleNonReviewedLabType2Service.class);
    public HandleNonReviewedLabType2Service() {

    }
    public String processingNonReviewLabType2() throws DataProcessingConsumerException {
        //TODO: Adding logic here
        try {
            return "processing non review lab 2";
        } catch (Exception e) {
            throw new DataProcessingConsumerException("ERROR");
        }

    }

}
