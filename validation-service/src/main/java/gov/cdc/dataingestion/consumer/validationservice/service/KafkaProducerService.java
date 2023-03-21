package gov.cdc.dataingestion.consumer.validationservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaProducerService {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    public void sendMessage(String msg) {
        kafkaTemplate.send("Topic name goes here", msg);
    }
}
