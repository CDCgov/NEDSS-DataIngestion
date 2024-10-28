package gov.cdc.dataprocessing.kafka.producer;

import gov.cdc.dataprocessing.kafka.producer.share.KafkaBaseProducer;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
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
 6541 - brain method complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541"})
public class KafkaManagerProducer  extends KafkaBaseProducer {
    private static final Logger logger = LoggerFactory.getLogger(KafkaManagerProducer.class);


    @Value("${kafka.topic.elr_health_case}")
    private String phcTopic = "elr_processing_public_health_case";

    @Value("${kafka.topic.elr_handle_lab}")
    private String labHandleTopic = "elr_processing_handle_lab" ;

    @Value("${kafka.topic.elr_action_tracker}")
    private String actionTrackerTopic = "elr_action_tracker" ;

    @Value("${kafka.topic.elr_edx_log}")
    private String edxLogTopic = "elr_edx_log";

    @Value("${kafka.topic.elr_micro_transaction}")
    private String unprocessedTopic = "elr_unprocessed_transaction";

    public KafkaManagerProducer(KafkaTemplate<String, String> kafkaTemplate) {
        super(kafkaTemplate);
    }

    public void sendDataPhc(String msg) {
        sendData(phcTopic, msg);
    }

    public void sendDataLabHandling(String msg) {
        sendData(labHandleTopic, msg);
    }

    public void sendDataActionTracker(String msg) {
        sendData(actionTrackerTopic, msg);
    }

    public void sendDataEdxActivityLog(String msgContent) {
        String uniqueID = "DP_LOG_" + UUID.randomUUID();
        var record = createProducerRecord(edxLogTopic, uniqueID, msgContent);
        sendMessage(record);
    }


    private void sendData(String topic, String msgContent) {
        String uniqueID = "DP_ELR_" + UUID.randomUUID();
        var record = createProducerRecord(topic, uniqueID, msgContent);
        sendMessage(record);
    }
}
