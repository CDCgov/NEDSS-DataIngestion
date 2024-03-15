package gov.cdc.dataprocessing.service.interfaces.other;

import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;

public interface IHandleLabService {
    Object processingReviewedLab() throws DataProcessingConsumerException;
    Object processingNonReviewLabWithAct() throws DataProcessingConsumerException;
    Object processingNonReviewLabWithoutAct() throws DataProcessingConsumerException;
}
