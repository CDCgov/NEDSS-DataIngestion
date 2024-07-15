package gov.cdc.dataprocessing.service.interfaces.observation;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.dto.observation.ObservationDto;

public interface IObservationService {
    ObservationDto processingLabResultContainer(LabResultProxyContainer labResultProxyContainer) throws DataProcessingException;

    LabResultProxyContainer getObservationToLabResultContainer(Long observationUid) throws DataProcessingException;


    /**
     * Available for updating Observation in Mark As Reviewed Flow
     */
    boolean processObservation(Long observationUid) throws DataProcessingException;

    void setLabInvAssociation(Long labUid, Long investigationUid) throws DataProcessingException;

}
