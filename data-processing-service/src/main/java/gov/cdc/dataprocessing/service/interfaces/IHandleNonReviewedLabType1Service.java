package gov.cdc.dataprocessing.service.interfaces;

import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;

public interface IHandleNonReviewedLabType1Service {
    String processingNonReviewLabType1() throws DataProcessingConsumerException;
}
