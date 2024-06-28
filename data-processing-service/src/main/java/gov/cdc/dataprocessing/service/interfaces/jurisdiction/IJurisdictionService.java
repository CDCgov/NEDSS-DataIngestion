package gov.cdc.dataprocessing.service.interfaces.jurisdiction;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.model.container.model.ObservationContainer;
import gov.cdc.dataprocessing.model.container.model.OrganizationContainer;
import gov.cdc.dataprocessing.model.container.model.PersonContainer;
import gov.cdc.dataprocessing.model.dto.observation.ObservationDto;
import gov.cdc.dataprocessing.repository.nbs.srte.model.JurisdictionCode;

import java.util.List;

public interface IJurisdictionService {

    String deriveJurisdictionCd(BaseContainer proxyVO, ObservationDto rootObsDT) throws DataProcessingException;
    void assignJurisdiction(PersonContainer patientContainer, PersonContainer providerContainer, OrganizationContainer organizationContainer,
                            ObservationContainer observationRequest) throws DataProcessingException;

    List<JurisdictionCode> getJurisdictionCode();
}
