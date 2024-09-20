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

import static gov.cdc.dataprocessing.utilities.GsonUtil.GSON;

@Service
@Slf4j
public class KafkaEdxLogConsumer {
    private static final Logger logger = LoggerFactory.getLogger(KafkaEdxLogConsumer.class); //NOSONAR
    private final IEdxLogService edxLogService;

    public KafkaEdxLogConsumer(IEdxLogService edxLogService) {
        this.edxLogService = edxLogService;
    }

    @KafkaListener(
            topics = "${kafka.topic.elr_edx_log}"
    )

    public void handleMessage(String message){
        try {
            EDXActivityLogDto edxActivityLogDto = GSON.fromJson(message, EDXActivityLogDto.class);
            edxLogService.saveEdxActivityLogs(edxActivityLogDto);
        } catch (Exception e) {
            logger.error("KafkaEdxLogConsumer.handleMessage: {}", e.getMessage());
        }

    }

}
