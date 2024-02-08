package gov.cdc.dataprocessing.service.interfaces;

import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model.dt.EdxLabInformationDT;
import gov.cdc.dataprocessing.model.classic_model.vo.LabResultProxyVO;
import gov.cdc.dataprocessing.model.classic_model.vo.PersonVO;

public interface IPatientService {
    Object processingPatient(LabResultProxyVO labResultProxyVO, EdxLabInformationDT edxLabInformationDT, PersonVO personVO) throws DataProcessingConsumerException, DataProcessingException;
    Object processingNextOfKin() throws DataProcessingConsumerException;
    Object processingProvider() throws DataProcessingConsumerException;
}
