package gov.cdc.dataprocessing.kafka.consumer;

import com.google.gson.Gson;
import gov.cdc.dataprocessing.exception.EdxLogException;
import gov.cdc.dataprocessing.model.dto.log.EDXActivityLogDto;
import gov.cdc.dataprocessing.service.interfaces.log.IEdxLogService;
import gov.cdc.dataprocessing.service.interfaces.manager.IManagerService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class KafkaEdxLogConsumer {
    private static final Logger logger = LoggerFactory.getLogger(KafkaEdxLogConsumer.class);
    private final IManagerService managerService;
    private final IEdxLogService edxLogService;

    public KafkaEdxLogConsumer(IManagerService managerService,
                               IEdxLogService edxLogService) {
        this.managerService = managerService;
        this.edxLogService = edxLogService;
    }

    @KafkaListener(
            topics = "${kafka.topic.elr_edx_log}"
    )

    public void handleMessage(List<String> messages,
                              @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) throws EdxLogException {
        Gson gson = new Gson();
        logger.info("UNPROCESSED BATCH SIZE: " + messages.size());
        for (String message : messages) {
            EDXActivityLogDto edxActivityLogDto = gson.fromJson(message, EDXActivityLogDto.class);
            edxLogService.saveEdxActivityLogs(edxActivityLogDto);
        }
    }
//    public void handleMessage(String message,
//                              @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) throws EdxLogException {
//        Gson gson = new Gson();
//        EDXActivityLogDto edxActivityLogDto = gson.fromJson(message, EDXActivityLogDto.class);
//        edxLogService.saveEdxActivityLogs(edxActivityLogDto);
//    }
}
