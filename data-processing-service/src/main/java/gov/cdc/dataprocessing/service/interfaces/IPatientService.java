package gov.cdc.dataprocessing.service.interfaces;

import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;

public interface IPatientService {
    String processingPatient() throws DataProcessingConsumerException;
    String processingNextOfKin() throws DataProcessingConsumerException;
    String processingProvider() throws DataProcessingConsumerException;
}
