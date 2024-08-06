package gov.cdc.dataprocessing.service.interfaces.manager;

import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.service.model.phc.PublicHealthCaseFlowContainer;

public interface IManagerService {
    void processDistribution(Integer data) throws DataProcessingConsumerException;
    void initiatingInvestigationAndPublicHealthCase(PublicHealthCaseFlowContainer data) throws DataProcessingException;
    void initiatingLabProcessing(PublicHealthCaseFlowContainer data)  throws DataProcessingConsumerException;
}
