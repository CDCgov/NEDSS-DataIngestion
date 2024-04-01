package gov.cdc.dataprocessing.service.interfaces.observation;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.observation.ObservationDto;
import gov.cdc.dataprocessing.model.container.LabResultProxyContainer;

public interface IObservationService {
    ObservationDto processingLabResultContainer(LabResultProxyContainer labResultProxyContainer) throws DataProcessingException;
    LabResultProxyContainer getObservationToLabResultContainer(Long observationUid) throws DataProcessingException;

}
