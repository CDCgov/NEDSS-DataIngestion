package gov.cdc.dataprocessing.service.interfaces;

import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;

public interface IHandleNonReviewedLabType2Service {
    String processingNonReviewLabType2() throws DataProcessingConsumerException;
}
