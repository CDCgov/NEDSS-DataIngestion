package gov.cdc.dataprocessing.service.interfaces.log;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.log.NNDActivityLogDto;

public interface INNDActivityLogService {
    void saveNddActivityLog(NNDActivityLogDto nndActivityLogDto) throws DataProcessingException;
}
