package gov.cdc.dataprocessing.service.interfaces.jurisdiction;

import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.container.ObservationContainer;
import gov.cdc.dataprocessing.repository.nbs.srte.model.ProgramAreaCode;

import java.util.Collection;
import java.util.List;

public interface IProgramAreaService {
    Object processingProgramArea() throws DataProcessingConsumerException;
    Object processingJurisdiction() throws DataProcessingConsumerException;
    void getProgramArea(Collection<ObservationContainer> resultTests, ObservationContainer orderTest, String clia) throws DataProcessingException;
    List<ProgramAreaCode> getAllProgramAreaCode();
    String deriveProgramAreaCd(LabResultProxyContainer labResultProxyVO, ObservationContainer orderTest) throws DataProcessingException;
}
