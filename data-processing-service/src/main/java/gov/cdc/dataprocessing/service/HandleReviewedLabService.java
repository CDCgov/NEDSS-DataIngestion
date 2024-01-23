package gov.cdc.dataprocessing.service;

import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.service.interfaces.IHandleReviewedLabService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class HandleReviewedLabService implements IHandleReviewedLabService {
    private static final Logger logger = LoggerFactory.getLogger(HandleReviewedLabService.class);
    public HandleReviewedLabService() {

    }

    public String processingReviewedLab() throws DataProcessingConsumerException {
        //TODO: Adding logic here
        try {
            return "processing reviewed lab";
        } catch (Exception e) {
            throw new DataProcessingConsumerException("ERROR");
        }

    }
}
