package gov.cdc.dataprocessing.service.interfaces;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.NNDActivityLogDT;

public interface INNDActivityLogService {
    void saveNddActivityLog(NNDActivityLogDT nndActivityLogDT) throws DataProcessingException;
}
