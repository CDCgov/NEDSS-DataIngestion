package gov.cdc.dataprocessing.service.interfaces.person;

import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.container.model.PersonContainer;
import gov.cdc.dataprocessing.model.dto.lab_result.EdxLabInformationDto;

public interface IPersonService {
    PersonContainer processingPatient(LabResultProxyContainer labResultProxyContainer, EdxLabInformationDto edxLabInformationDto, PersonContainer personContainer) throws DataProcessingConsumerException, DataProcessingException;

    PersonContainer processingNextOfKin(LabResultProxyContainer labResultProxyContainer, PersonContainer personContainer) throws DataProcessingException;

    PersonContainer processingProvider(LabResultProxyContainer labResultProxyContainer, EdxLabInformationDto edxLabInformationDto, PersonContainer personContainer, boolean orderingProviderIndicator) throws DataProcessingConsumerException, DataProcessingException;

    Long getMatchedPersonUID(LabResultProxyContainer matchedlabResultProxyVO);

    void updatePersonELRUpdate(LabResultProxyContainer labResultProxyVO, LabResultProxyContainer matchedLabResultProxyVO);
}
