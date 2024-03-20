package gov.cdc.dataprocessing.service.interfaces.jurisdiction;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.ObservationContainer;
import gov.cdc.dataprocessing.model.container.OrganizationContainer;
import gov.cdc.dataprocessing.model.container.PersonContainer;
import gov.cdc.dataprocessing.model.dto.observation.ObservationDto;
import gov.cdc.dataprocessing.model.container.BaseContainer;
import gov.cdc.dataprocessing.repository.nbs.srte.model.JurisdictionCode;

import java.util.List;

public interface IJurisdictionService {

    String deriveJurisdictionCd(BaseContainer proxyVO, ObservationDto rootObsDT) throws DataProcessingException;
    void assignJurisdiction(PersonContainer subjectVO, PersonContainer providerVO, OrganizationContainer orderingFacilityVO, ObservationContainer orderTestVO) throws DataProcessingException;

    List<JurisdictionCode> getJurisdictionCode();
}
