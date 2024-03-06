package gov.cdc.dataprocessing.service.interfaces;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.MessageLogDT;

import java.util.Collection;

public interface IMessageLogService {
    void saveMessageLog(Collection<MessageLogDT> messageLogDTCollection) throws DataProcessingException;
}
