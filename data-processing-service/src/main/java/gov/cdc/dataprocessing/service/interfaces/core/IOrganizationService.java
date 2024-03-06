package gov.cdc.dataprocessing.service.interfaces.core;

import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.OrganizationVO;
import gov.cdc.dataprocessing.model.container.LabResultProxyContainer;

public interface IOrganizationService {
    OrganizationVO processingOrganization(LabResultProxyContainer labResultProxyContainer) throws DataProcessingConsumerException;
}
