package gov.cdc.dataprocessing.service.interfaces.person;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.PersonContainer;
import gov.cdc.dataprocessing.model.dto.log.EDXActivityDetailLogDto;

public interface IProviderMatchingService {
    EDXActivityDetailLogDto getMatchingProvider(PersonContainer personContainer) throws DataProcessingException;
    Long setProvider(PersonContainer personContainer, String businessTriggerCd) throws DataProcessingException;

}
