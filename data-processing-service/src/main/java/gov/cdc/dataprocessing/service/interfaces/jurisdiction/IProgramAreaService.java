package gov.cdc.dataprocessing.service.interfaces.jurisdiction;

import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.ObservationContainer;

import java.util.Collection;

public interface IProgramAreaService {
    Object processingProgramArea() throws DataProcessingConsumerException;
    Object processingJurisdiction() throws DataProcessingConsumerException;
    void getProgramArea(Collection<ObservationContainer> resultTests, ObservationContainer orderTest, String clia) throws DataProcessingException;
}
