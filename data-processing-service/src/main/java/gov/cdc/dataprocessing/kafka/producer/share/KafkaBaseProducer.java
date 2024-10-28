package gov.cdc.dataprocessing.kafka.producer.share;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
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

//    protected void sendMessageTransactional(ProducerRecord<String, String> prodRecord) {
//        kafkaTemplate.executeInTransaction(operations -> {
//            operations.send(prodRecord);
//            return "OK";
//        });
//    }

}
