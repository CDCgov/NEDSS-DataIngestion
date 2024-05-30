package gov.cdc.dataprocessing.service.implementation.log;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.log.MessageLogDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.log.MessageLog;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.log.MessageLogRepository;
import gov.cdc.dataprocessing.service.interfaces.log.IMessageLogService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
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
                java.util.Iterator<MessageLogDto> it = messageLogDtoCollection.iterator();
                while(it.hasNext()){
                    MessageLog msg = new MessageLog(it.next());
                    messageLogRepository.save(msg);
                }
            }
        }catch(Exception ex){
            throw new DataProcessingException(ex.toString());
        }
    }
}
