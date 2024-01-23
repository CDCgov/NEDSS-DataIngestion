package gov.cdc.dataprocessing.service.interfaces;

import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;

public interface IProgramAreaJurisdictionService {
    String processingProgramArea() throws DataProcessingConsumerException;
    String processingJurisdiction() throws DataProcessingConsumerException;
}
