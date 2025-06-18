package gov.cdc.dataprocessing.service.interfaces.jurisdiction;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.container.model.ObservationContainer;
import gov.cdc.dataprocessing.repository.nbs.srte.model.ProgramAreaCode;

import java.util.Collection;
import java.util.List;


public interface IProgramAreaService {
    void getProgramArea(Collection<ObservationContainer> observationResults, ObservationContainer observationRequest, String clia) throws DataProcessingException;
    List<ProgramAreaCode> getAllProgramAreaCode();
    String deriveProgramAreaCd(LabResultProxyContainer labResultProxyVO, ObservationContainer orderTest) throws DataProcessingException;
}
