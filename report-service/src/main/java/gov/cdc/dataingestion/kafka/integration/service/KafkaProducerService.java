package gov.cdc.dataingestion.kafka.integration.service;

import com.google.gson.Gson;
import gov.cdc.dataingestion.conversion.repository.model.HL7ToFHIRModel;
import gov.cdc.dataingestion.validation.repository.model.ValidatedELRModel;
import gov.cdc.dataingestion.validation.model.constant.KafkaHeaderValue;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class KafkaProducerService {
    private String xmlMessageKeyPrefix = "XML_";

    private String fhirMessageKeyPrefix = "FHIR_";
    private String validMessageKeyPrefix = "VALID_";
    private String hl7MessageKeyPrefix = "HL7_";

    private KafkaTemplate<String, String> kafkaTemplate;
    public KafkaProducerService( KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessageFromController(String msg, String topic, String msgType) {
        String uniqueID = msgType + "_" + UUID.randomUUID();
        var record = new ProducerRecord<>(topic, uniqueID, msg);
        record.headers().add(KafkaHeaderValue.MessageType, msgType.getBytes());
        sendMessage(record);
    }


    public void sendMessageFromCSVController(List<List<String>> msg, String topic, String msgType) {
        String uniqueID = msgType + "_" + UUID.randomUUID();
        Gson gson = new Gson();
        String json = gson.toJson(msg);

        var record = new ProducerRecord<>(topic, uniqueID, json);
        record.headers().add(KafkaHeaderValue.MessageType, msgType.getBytes());
        sendMessage(record);
    }

    public void sendMessageAfterValidatingMessage(ValidatedELRModel msg, String topic) {
        String uniqueID =  validMessageKeyPrefix + msg.getMessageType() + "_" + UUID.randomUUID();
        var record = new ProducerRecord<>(topic, uniqueID, msg.getId());
        record.headers().add(KafkaHeaderValue.MessageType, msg.getMessageType().toString().getBytes());
        record.headers().add(KafkaHeaderValue.MessageVersion, msg.getMessageVersion().getBytes());
        sendMessage(record);
    }

    public void sendMessageAfterConvertedToFhirMessage(HL7ToFHIRModel msg, String topic) {
        String uniqueID = fhirMessageKeyPrefix + UUID.randomUUID();
        var record = new ProducerRecord<>(topic, uniqueID, msg.getId());
        sendMessage(record);
    }

    public void sendMessageAfterConvertedToXml(String xmlMsg, String topic) {
        String uniqueID = xmlMessageKeyPrefix + UUID.randomUUID();
        var record = new ProducerRecord<>(topic, uniqueID, xmlMsg);
        sendMessage(record);
    }

    public void sendMessageAfterCheckingDuplicateHL7(ValidatedELRModel msg, String validatedElrDuplicateTopic) {
        String uniqueID = hl7MessageKeyPrefix + UUID.randomUUID();
        var record = new ProducerRecord<>(validatedElrDuplicateTopic, uniqueID, msg.getRawId());
        sendMessage(record);
    }

    private void sendMessage(ProducerRecord<String, String> record) {
        kafkaTemplate.send(record);
    }


}