package gov.cdc.dataprocessing.service.interfaces.person;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.PersonContainer;
import gov.cdc.dataprocessing.model.dto.matching.EdxPatientMatchDto;

public interface INokMatchingService {
    EdxPatientMatchDto getMatchingNextOfKin(PersonContainer personContainer) throws DataProcessingException;

}
