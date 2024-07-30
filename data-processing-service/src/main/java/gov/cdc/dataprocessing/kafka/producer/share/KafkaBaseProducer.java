package gov.cdc.dataprocessing.kafka.producer.share;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaBaseProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    public KafkaBaseProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    protected ProducerRecord<String, String> createProducerRecord(String topic, String msgKey, String msgContent) {
        return new ProducerRecord<>(topic, msgKey, msgContent);
    }
    protected void sendMessage(ProducerRecord<String, String> prodRecord) {
        kafkaTemplate.send(prodRecord);
    }

    protected void sendMessageTransactional(ProducerRecord<String, String> prodRecord) {
        kafkaTemplate.executeInTransaction(operations -> {
            operations.send(prodRecord);
            return "OK";
        });
    }

}
