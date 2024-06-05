package gov.cdc.dataprocessing.service.interfaces.public_health_case;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.generic_helper.StateDefinedFieldDataDto;

import java.util.List;

public interface ILdfService {
    List<StateDefinedFieldDataDto> getLDFCollection(Long busObjectUid, String conditionCode) throws DataProcessingException;
}
