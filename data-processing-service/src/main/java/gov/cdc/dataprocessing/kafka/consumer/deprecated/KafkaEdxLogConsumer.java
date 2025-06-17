package gov.cdc.dataprocessing.kafka.consumer.deprecated;

import gov.cdc.dataprocessing.model.dto.log.EDXActivityLogDto;
import gov.cdc.dataprocessing.service.interfaces.log.IEdxLogService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

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
            topics = "${kafka.topic.elr_edx_log}",
            containerFactory = "kafkaListenerContainerFactoryStep4"
    )
    public void handleMessage(String message, Acknowledgment acknowledgment) {
        try {
            EDXActivityLogDto edxActivityLogDto = GSON.fromJson(message, EDXActivityLogDto.class);
            edxLogService.saveEdxActivityLogs(edxActivityLogDto);
            acknowledgment.acknowledge();
        } catch (Exception e) {
            logger.error("KafkaEdxLogConsumer.handleMessage: {}", e.getMessage());
        }

    }

}
