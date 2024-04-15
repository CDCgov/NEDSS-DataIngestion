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
public class KafkaManagerProducer  extends KafkaBaseProducer {
    private static final Logger logger = LoggerFactory.getLogger(KafkaManagerProducer.class);
    public KafkaManagerProducer(KafkaTemplate<String, String> kafkaTemplate) {
        super(kafkaTemplate);
    }


    @Value("${kafka.topic.elr_health_case}")
    private String phcTopic = "elr_processing_public_health_case";

    @Value("${kafka.topic.elr_handle_lab}")
    private String labHandleTopic = "elr_processing_handle_lab" ;

    @Value("${kafka.topic.elr_action_tracker}")
    private String actionTrackerTopic = "elr_action_tracker" ;
    public void sendDataPhc(String msg) {
        sendData(phcTopic, msg);
    }

    public void sendDataLabHandling(String msg) {
        sendData(labHandleTopic, msg);
    }

    public void sendDataActionTracker(String msg) {
        sendData(actionTrackerTopic, msg);
    }

    public void sendData(String topic, String msgContent) {
        String uniqueID = "DP_ELR_" + UUID.randomUUID();
        var record = createProducerRecord(topic, uniqueID, msgContent);
        // ADD HEADER if needed
        sendMessage(record);
    }
}
