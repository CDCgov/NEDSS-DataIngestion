package gov.cdc.dataprocessing.service.interfaces.matching;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.EDXActivityDetailLogDT;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.OrganizationVO;

public interface IOrganizationMatchingService {
    EDXActivityDetailLogDT getMatchingOrganization(
            OrganizationVO organizationVO)
            throws DataProcessingException;
}