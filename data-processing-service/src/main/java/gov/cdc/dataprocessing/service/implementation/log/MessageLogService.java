package gov.cdc.dataprocessing.service.implementation.log;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.log.MessageLogDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.log.MessageLog;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.log.MessageLogRepository;
import gov.cdc.dataprocessing.service.interfaces.log.IMessageLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 2139 - exception rethrow complain
 3740 - parametrized  type for generic complaint
 1149 - replacing HashTable complaint
 112 - throwing dedicate exception complaint
 107 - max parameter complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107"})
public class MessageLogService implements IMessageLogService {
    private final MessageLogRepository messageLogRepository;

    public MessageLogService(MessageLogRepository messageLogRepository) {
        this.messageLogRepository = messageLogRepository;
    }

    @Transactional
    public void saveMessageLog(Collection<MessageLogDto> messageLogDtoCollection) throws DataProcessingException {
        try{
            if(messageLogDtoCollection !=null)
            {
                for (MessageLogDto messageLogDto : messageLogDtoCollection) {
                    MessageLog msg = new MessageLog(messageLogDto);
                    messageLogRepository.save(msg);
                }
            }
        }catch(Exception ex){
            throw new DataProcessingException(ex.getMessage(), ex);
        }
    }
}
