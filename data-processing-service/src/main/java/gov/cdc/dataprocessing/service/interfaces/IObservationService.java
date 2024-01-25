package gov.cdc.dataprocessing.service.interfaces;

import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;

public interface IObservationService {
    Object processingObservation() throws DataProcessingConsumerException;
}
