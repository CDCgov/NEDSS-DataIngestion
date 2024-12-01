package gov.cdc.dataprocessing.service.interfaces.observation;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.dto.lab_result.EdxLabInformationDto;
import gov.cdc.dataprocessing.model.dto.observation.ObservationDto;

public interface IObservationMatchingService {
    void processMatchedProxyVO(LabResultProxyContainer labResultProxyVO,
                               LabResultProxyContainer matchedlabResultProxyVO,
                               EdxLabInformationDto edxLabInformationDT);
    ObservationDto checkingMatchingObservation(EdxLabInformationDto edxLabInformationDto) throws DataProcessingException;

}
