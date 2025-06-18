package gov.cdc.dataprocessing.service.interfaces.log;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.log.MessageLogDto;

import java.util.Collection;



public interface IMessageLogService {
    void saveMessageLog(Collection<MessageLogDto> messageLogDtoCollection) throws DataProcessingException;
}
