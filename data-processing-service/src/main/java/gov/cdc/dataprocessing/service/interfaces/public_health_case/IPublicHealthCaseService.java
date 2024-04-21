package gov.cdc.dataprocessing.service.interfaces.public_health_case;

import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.PublicHealthCaseVO;

public interface IPublicHealthCaseService {
    Long setPublicHealthCase(PublicHealthCaseVO publicHealthCaseVO) throws DataProcessingException;
}
