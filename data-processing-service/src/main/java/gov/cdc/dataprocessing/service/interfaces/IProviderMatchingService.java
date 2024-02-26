package gov.cdc.dataprocessing.service.interfaces;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model.dto.EDXActivityDetailLogDT;
import gov.cdc.dataprocessing.model.classic_model.vo.PersonVO;

public interface IProviderMatchingService {
    EDXActivityDetailLogDT getMatchingProvider(PersonVO personVO) throws DataProcessingException;

}
