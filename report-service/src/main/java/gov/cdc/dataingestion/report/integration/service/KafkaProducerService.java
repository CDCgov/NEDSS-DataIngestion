package gov.cdc.dataingestion.report.integration.service;

import gov.cdc.dataingestion.report.repository.model.HL7toFhirModel;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class KafkaProducerService {
    private String MessageKeyPrefix = "FHIR_";
    private KafkaTemplate<String, String> kafkaTemplate;
    public KafkaProducerService( KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessageAfterConvertedToFhirMessage(HL7toFhirModel msg, String topic) {
        String uniqueID = MessageKeyPrefix + UUID.randomUUID();
        var record = new ProducerRecord<>(topic, uniqueID, msg.getId());
        sendMessage(record);
    }

    private void sendMessage(ProducerRecord<String, String> record) {
        kafkaTemplate.send(record);
    }
}
