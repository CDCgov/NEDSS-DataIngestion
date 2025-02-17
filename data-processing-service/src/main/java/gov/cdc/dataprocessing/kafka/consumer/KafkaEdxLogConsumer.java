package gov.cdc.dataprocessing.kafka.consumer;

import gov.cdc.dataprocessing.model.dto.log.EDXActivityLogDto;
import gov.cdc.dataprocessing.service.interfaces.log.IEdxLogService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

import static gov.cdc.dataprocessing.utilities.GsonUtil.GSON;

@Service
@Slf4j
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
 1195 - duplicate complaint
 1135 - Todos complaint
 6201 - instanceof check
 1192 - duplicate literal
 135 - for loop
 117 - naming
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201", "java:S1192", "java:S135", "java:S117"})
public class KafkaEdxLogConsumer {
    private static final Logger logger = LoggerFactory.getLogger(KafkaEdxLogConsumer.class); //NOSONAR
    private final IEdxLogService edxLogService;

    public KafkaEdxLogConsumer(IEdxLogService edxLogService) {
        this.edxLogService = edxLogService;
    }

//    @RetryableTopic(
//            attempts = "3", // Number of attempts including the first try
//            backoff = @Backoff(delay = 1000, multiplier = 2.0), // Exponential backoff configuration
//            dltStrategy = DltStrategy.FAIL_ON_ERROR, // Strategy on how to handle messages that fail all retries
//            dltTopicSuffix = "dlt" // Suffix for the dead letter topic
//    )
    @KafkaListener(
            topics = "${kafka.topic.elr_edx_log}",
            containerFactory = "kafkaListenerContainerFactory4"
    )
    public void handleMessage(String message){
        try {
            EDXActivityLogDto edxActivityLogDto = GSON.fromJson(message, EDXActivityLogDto.class);
            edxLogService.saveEdxActivityLogs(edxActivityLogDto);
//            acknowledgment.acknowledge();
        } catch (Exception e) {
            logger.error("KafkaEdxLogConsumer.handleMessage: {}", e.getMessage());
        }

    }

}
