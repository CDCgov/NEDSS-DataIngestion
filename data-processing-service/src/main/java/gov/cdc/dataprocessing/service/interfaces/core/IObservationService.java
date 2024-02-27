package gov.cdc.dataprocessing.service.interfaces.core;

import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;

public interface IObservationService {
    Object processingObservation() throws DataProcessingConsumerException;
}
