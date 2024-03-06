package gov.cdc.dataprocessing.service.implementation;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.MessageLogDT;
import gov.cdc.dataprocessing.repository.nbs.odse.model.log.MessageLog;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.log.MessageLogRepository;
import gov.cdc.dataprocessing.service.interfaces.IMessageLogService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class MessageLogService implements IMessageLogService {
    private static final Logger logger = LoggerFactory.getLogger(MessageLogService.class);

    private final MessageLogRepository messageLogRepository;

    public MessageLogService(MessageLogRepository messageLogRepository) {
        this.messageLogRepository = messageLogRepository;
    }

    @Transactional
    public void saveMessageLog(Collection<MessageLogDT> messageLogDTCollection) throws DataProcessingException {
        try{
            if(messageLogDTCollection!=null)
            {
                java.util.Iterator<MessageLogDT> it = messageLogDTCollection.iterator();
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
