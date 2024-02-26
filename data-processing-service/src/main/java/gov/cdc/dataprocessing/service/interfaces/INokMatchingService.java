package gov.cdc.dataprocessing.service.interfaces;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model.dto.EdxPatientMatchDT;
import gov.cdc.dataprocessing.model.classic_model.vo.PersonVO;

public interface INokMatchingService {
    EdxPatientMatchDT getMatchingNextOfKin(PersonVO personVO) throws DataProcessingException;

}
