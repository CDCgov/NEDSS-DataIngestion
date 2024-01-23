package gov.cdc.dataprocessing.service.interfaces;

import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;

public interface IHandleReviewedLabService {
    String processingReviewedLab() throws DataProcessingConsumerException;
}
