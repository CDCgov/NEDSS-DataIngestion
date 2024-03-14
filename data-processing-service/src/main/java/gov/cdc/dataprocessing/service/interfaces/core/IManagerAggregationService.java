package gov.cdc.dataprocessing.service.interfaces.core;

import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.ObservationDT;
import gov.cdc.dataprocessing.model.container.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.dto.EdxLabInformationDto;

public interface IManagerAggregationService {
    void serviceAggregation(LabResultProxyContainer labResult, EdxLabInformationDto edxLabInformationDto) throws DataProcessingConsumerException,
            DataProcessingException;

    void serviceAggregationAsync(LabResultProxyContainer labResult, EdxLabInformationDto edxLabInformationDto) throws DataProcessingConsumerException,
            DataProcessingException;


    void processingObservationMatching(EdxLabInformationDto edxLabInformationDto,
                                                LabResultProxyContainer labResultProxyContainer,
                                                Long aPersonUid) throws DataProcessingException;
}
