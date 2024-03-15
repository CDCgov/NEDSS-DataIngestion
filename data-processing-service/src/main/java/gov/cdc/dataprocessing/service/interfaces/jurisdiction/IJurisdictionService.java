package gov.cdc.dataprocessing.service.interfaces.jurisdiction;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.observation.ObservationDto;
import gov.cdc.dataprocessing.model.container.BaseContainer;

public interface IJurisdictionService {

    String deriveJurisdictionCd(BaseContainer proxyVO, ObservationDto rootObsDT) throws DataProcessingException;
}
