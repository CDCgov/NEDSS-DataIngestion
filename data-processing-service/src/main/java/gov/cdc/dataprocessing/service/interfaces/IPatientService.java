package gov.cdc.dataprocessing.service.interfaces;

import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;

public interface IPatientService {
    Object processingPatient() throws DataProcessingConsumerException;
    Object processingNextOfKin() throws DataProcessingConsumerException;
    Object processingProvider() throws DataProcessingConsumerException;
}
