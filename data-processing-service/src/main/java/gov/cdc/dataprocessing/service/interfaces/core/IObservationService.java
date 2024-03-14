package gov.cdc.dataprocessing.service.interfaces.core;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.ObservationDT;
import gov.cdc.dataprocessing.model.container.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.dto.EdxLabInformationDto;

public interface IObservationService {
    ObservationDT sendLabResultToProxy(LabResultProxyContainer labResultProxyContainer) throws DataProcessingException;
    LabResultProxyContainer getObservationToLabResultContainer(Long observationUid) throws DataProcessingException;

}
