package gov.cdc.dataprocessing.service.interfaces.core;

import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.EdxLabInformationDto;
import gov.cdc.dataprocessing.model.container.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.container.PersonContainer;

public interface IPatientService {
    PersonContainer processingPatient(LabResultProxyContainer labResultProxyContainer, EdxLabInformationDto edxLabInformationDto, PersonContainer personContainer) throws DataProcessingConsumerException, DataProcessingException;
    PersonContainer processingNextOfKin(LabResultProxyContainer labResultProxyContainer, PersonContainer personContainer) throws DataProcessingException;
    PersonContainer processingProvider(LabResultProxyContainer labResultProxyContainer, EdxLabInformationDto edxLabInformationDto, PersonContainer personContainer, boolean orderingProviderIndicator) throws DataProcessingConsumerException, DataProcessingException;
}
