package gov.cdc.dataprocessing.service.interfaces.person;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.PersonContainer;
import gov.cdc.dataprocessing.model.dto.matching.EdxPatientMatchDto;

import java.io.IOException;

public interface IPatientMatchingService {
    EdxPatientMatchDto getMatchingPatient(PersonContainer personContainer,boolean isNbs)
        throws DataProcessingException, IOException, InterruptedException;
    boolean getMultipleMatchFound();
    Long updateExistingPerson(PersonContainer personContainer, String businessTriggerCd) throws DataProcessingException;
}
