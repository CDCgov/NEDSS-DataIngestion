package gov.cdc.dataprocessing.service.interfaces.jurisdiction;

import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;

public interface IProgramAreaJurisdictionService {
    Object processingProgramArea() throws DataProcessingConsumerException;
    Object processingJurisdiction() throws DataProcessingConsumerException;
}
