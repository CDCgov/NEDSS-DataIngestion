package gov.cdc.dataprocessing.service.interfaces;

import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model.dt.EdxLabInformationDT;
import gov.cdc.dataprocessing.model.classic_model.vo.LabResultProxyVO;
import gov.cdc.dataprocessing.model.classic_model.vo.PersonVO;

public interface IPatientService {
    PersonVO processingPatient(LabResultProxyVO labResultProxyVO, EdxLabInformationDT edxLabInformationDT, PersonVO personVO) throws DataProcessingConsumerException, DataProcessingException;
    PersonVO processingNextOfKin(LabResultProxyVO labResultProxyVO, PersonVO personVO) throws DataProcessingException;
    PersonVO processingProvider(LabResultProxyVO labResultProxyVO, EdxLabInformationDT edxLabInformationDT, PersonVO personVO, boolean orderingProviderIndicator) throws DataProcessingConsumerException;
}
