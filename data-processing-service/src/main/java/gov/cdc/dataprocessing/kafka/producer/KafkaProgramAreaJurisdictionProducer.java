package gov.cdc.dataprocessing.kafka.producer;

import gov.cdc.dataprocessing.kafka.producer.share.KafkaBaseProducer;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class KafkaProgramAreaJurisdictionProducer extends KafkaBaseProducer {
    private static final Logger logger = LoggerFactory.getLogger(KafkaProgramAreaJurisdictionProducer.class);

    public KafkaProgramAreaJurisdictionProducer(KafkaTemplate<String, String> kafkaTemplate) {
        super(kafkaTemplate);
    }

    public void sendProgAreaMessage(String topic, String msgContent) {
        String uniqueID =  UUID.randomUUID().toString();
        var record = createProducerRecord(topic, uniqueID, msgContent);
        // ADD HEADER if needed
        sendMessage(record);
    }
}
