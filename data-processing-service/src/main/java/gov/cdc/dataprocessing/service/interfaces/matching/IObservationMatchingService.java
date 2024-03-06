package gov.cdc.dataprocessing.service.interfaces.matching;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.ObservationDT;
import gov.cdc.dataprocessing.model.container.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.dto.EdxLabInformationDto;

public interface IObservationMatchingService {
    ObservationDT matchingObservation(EdxLabInformationDto edxLabInformationDto) throws DataProcessingException;
    void processMatchedProxyVO(LabResultProxyContainer labResultProxyVO,
                                      LabResultProxyContainer matchedlabResultProxyVO,
                                      EdxLabInformationDto edxLabInformationDT);

}
