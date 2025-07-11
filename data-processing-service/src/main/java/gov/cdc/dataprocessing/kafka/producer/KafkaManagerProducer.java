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
    @Value("${kafka.topic.elr_reprocessing_locking}")
    private String dlt_reprocess_locking = "dp_elr_reprocess_locking";

    @Value("${kafka.topic.elr_reprocessing_data_integrity}")
    private String dlt_reprocess_data_integrity = "dp_elr_reprocess_data_integrity";

    @Value("${kafka.topic.elr_health_case}")
    private String phcTopic = "elr_processing_public_health_case";

    @Value("${kafka.topic.elr_handle_lab}")
    private String labHandleTopic = "dp_elr_processing_handle_lab" ;

    @Value("${kafka.topic.elr_nnd}")
    private String nndTopic = "dp_elr_handle_nnd" ;

    @Value("${kafka.topic.elr_action_tracker}")
    private String actionTrackerTopic = "elr_action_tracker" ;

    @Value("${kafka.topic.elr_edx_log}")
    private String edxLogTopic = "elr_edx_log";

    public KafkaManagerProducer(KafkaTemplate<String, String> kafkaTemplate) {
        super(kafkaTemplate);
    }

    public void sendDltForLocking(String message) {
        sendData(dlt_reprocess_locking, message);
    }
    public void sendDltForDataIntegrity(String message) {
        sendData(dlt_reprocess_data_integrity, message);
    }



    public void sendDataPhc(String msg) {
        sendData(phcTopic, msg);
    }

    public void sendDataLabHandling(String msg) {
        sendData(labHandleTopic, msg);
    }

    public void sendNNDHandling(String msg) {
        sendData(nndTopic, msg);
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
