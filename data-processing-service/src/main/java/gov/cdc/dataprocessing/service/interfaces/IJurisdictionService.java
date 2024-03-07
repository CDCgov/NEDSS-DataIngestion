package gov.cdc.dataprocessing.service.interfaces;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.OrganizationVO;
import gov.cdc.dataprocessing.model.container.PersonContainer;

import java.util.HashMap;

public interface IJurisdictionService {
    HashMap<Object, Object> resolveLabReportJurisdiction(PersonContainer subject,
                                                         PersonContainer provider,
                                                         OrganizationVO organizationVO,
                                                         OrganizationVO organizationVO2) throws DataProcessingException;
}
