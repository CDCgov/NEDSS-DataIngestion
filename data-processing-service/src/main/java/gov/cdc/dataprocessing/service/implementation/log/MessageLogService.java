package gov.cdc.dataprocessing.service.implementation.log;

import gov.cdc.dataprocessing.model.dto.log.MessageLogDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.log.MessageLog;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.log.MessageLogRepository;
import gov.cdc.dataprocessing.service.interfaces.log.IMessageLogService;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service

public class MessageLogService implements IMessageLogService {
    private final MessageLogRepository messageLogRepository;

    public MessageLogService(MessageLogRepository messageLogRepository) {
        this.messageLogRepository = messageLogRepository;
    }

    public void saveMessageLog(Collection<MessageLogDto> messageLogDtoCollection) {
        if(messageLogDtoCollection !=null)
        {
            for (MessageLogDto messageLogDto : messageLogDtoCollection) {
                MessageLog msg = new MessageLog(messageLogDto);
                messageLogRepository.save(msg);
            }
        }

    }
}
