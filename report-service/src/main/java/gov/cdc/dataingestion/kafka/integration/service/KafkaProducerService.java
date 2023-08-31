package gov.cdc.dataingestion.kafka.integration.service;

import com.google.gson.Gson;
import gov.cdc.dataingestion.constant.enums.EnumKafkaOperation;
import gov.cdc.dataingestion.conversion.repository.model.HL7ToFHIRModel;
import gov.cdc.dataingestion.constant.enums.EnumElrDltStatus;
import gov.cdc.dataingestion.exception.ConversionPrepareException;
import gov.cdc.dataingestion.constant.TopicPreparationType;
import gov.cdc.dataingestion.validation.repository.model.ValidatedELRModel;
import gov.cdc.dataingestion.constant.KafkaHeaderValue;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class KafkaProducerService {
    private final String prefixMessagePreparation = "PREP_";
    private final String xmlMessageKeyPrefix = "XML_";
    private final String fhirMessageKeyPrefix = "FHIR_";
    private final String validMessageKeyPrefix = "VALID_";
    private final String hl7MessageKeyPrefix = "HL7_";
    private final String dltMessageKeyPrefix = "DLT_";


    private final KafkaTemplate<String, String> kafkaTemplate;
    public KafkaProducerService( KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessageFromController(String msg, String topic, String msgType, Integer dltOccurrence, Boolean validationActive) {
        String uniqueID = msgType + "_" + UUID.randomUUID();
        var record = new ProducerRecord<>(topic, uniqueID, msg);
        record.headers().add(KafkaHeaderValue.MessageType, msgType.getBytes());
        record.headers().add(KafkaHeaderValue.DltOccurrence, dltOccurrence.toString().getBytes());
        record.headers().add(KafkaHeaderValue.MessageOperation, EnumKafkaOperation.INJECTION.name().getBytes());
        record.headers().add(KafkaHeaderValue.MessageValidationActive, validationActive.toString().getBytes());

        sendMessage(record);
    }

    public void sendMessageFromDltController(
            String msg, String topic, String msgType, Integer dltOccurrence) {
        String uniqueID = msgType + "_" + UUID.randomUUID();
        var record = new ProducerRecord<>(topic, uniqueID, msg);
        record.headers().add(KafkaHeaderValue.MessageType, msgType.getBytes());
        record.headers().add(KafkaHeaderValue.DltOccurrence, dltOccurrence.toString().getBytes());
        record.headers().add(KafkaHeaderValue.MessageOperation, EnumKafkaOperation.REINJECTION.name().getBytes());
        sendMessage(record);
    }


    @Deprecated
    public void sendMessageFromCSVController(List<List<String>> msg, String topic, String msgType) {
        String uniqueID = msgType + "_" + UUID.randomUUID();
        Gson gson = new Gson();
        String json = gson.toJson(msg);

        var record = new ProducerRecord<>(topic, uniqueID, json);
        record.headers().add(KafkaHeaderValue.MessageType, msgType.getBytes());
        sendMessage(record);
    }

    public void sendMessageAfterValidatingMessage(ValidatedELRModel msg, String topic, Integer dltOccurrence) {
        String uniqueID =  validMessageKeyPrefix + msg.getMessageType() + "_" + UUID.randomUUID();
        var record = new ProducerRecord<>(topic, uniqueID, msg.getId());
        record.headers().add(KafkaHeaderValue.MessageType, msg.getMessageType().getBytes());
        record.headers().add(KafkaHeaderValue.MessageVersion, msg.getMessageVersion().getBytes());
        record.headers().add(KafkaHeaderValue.DltOccurrence, dltOccurrence.toString().getBytes());
        record.headers().add(KafkaHeaderValue.MessageOperation, EnumKafkaOperation.INJECTION.name().getBytes());
        sendMessage(record);
    }

    public void sendMessagePreparationTopic(ValidatedELRModel msg, String topic, TopicPreparationType topicType, Integer dltOccurrence) throws ConversionPrepareException {

        String uniqueId;
        if (topicType == TopicPreparationType.XML) {
            uniqueId =  prefixMessagePreparation + xmlMessageKeyPrefix + msg.getMessageType() + "_" + UUID.randomUUID();
        }
        else if (topicType == TopicPreparationType.FHIR) {
            uniqueId =  prefixMessagePreparation +  fhirMessageKeyPrefix + msg.getMessageType() + "_" + UUID.randomUUID();
        }
        else {
            throw new ConversionPrepareException("Unsupported Topic");
        }
        sendMessageHelper(topic, dltOccurrence, uniqueId, msg.getId(), msg.getMessageType(), msg.getMessageVersion());
    }

    private void sendMessageHelper(String topic, Integer dltOccurrence, String uniqueId, String messageOriginId, String messageType, String messageVersion) {
        var record = new ProducerRecord<>(topic, uniqueId, messageOriginId);
        record.headers().add(KafkaHeaderValue.MessageType, messageType.getBytes());
        record.headers().add(KafkaHeaderValue.MessageVersion, messageVersion.getBytes());
        record.headers().add(KafkaHeaderValue.DltOccurrence, dltOccurrence.toString().getBytes());
        record.headers().add(KafkaHeaderValue.MessageOperation, EnumKafkaOperation.INJECTION.name().getBytes());
        sendMessage(record);
    }

    public void sendMessageAfterConvertedToFhirMessage(HL7ToFHIRModel msg, String topic, Integer dltOccurrence) {
        String uniqueID = fhirMessageKeyPrefix + UUID.randomUUID();
        var record = new ProducerRecord<>(topic, uniqueID, msg.getId());
        record.headers().add(KafkaHeaderValue.DltOccurrence, dltOccurrence.toString().getBytes());
        record.headers().add(KafkaHeaderValue.MessageOperation, EnumKafkaOperation.INJECTION.name().getBytes());
        sendMessage(record);
    }


    public void sendMessageAfterConvertedToXml(String xmlMsg, String topic, Integer dltOccurrence) {
        String uniqueID = xmlMessageKeyPrefix + UUID.randomUUID();
        var record = new ProducerRecord<>(topic, uniqueID, xmlMsg);
        record.headers().add(KafkaHeaderValue.DltOccurrence, dltOccurrence.toString().getBytes());
        record.headers().add(KafkaHeaderValue.MessageOperation, EnumKafkaOperation.INJECTION.name().getBytes());
        sendMessage(record);
    }

    public void sendMessageAfterCheckingDuplicateHL7(ValidatedELRModel msg, String validatedElrDuplicateTopic, Integer dltOccurrence) {
        String uniqueID = hl7MessageKeyPrefix + UUID.randomUUID();
        var record = new ProducerRecord<>(validatedElrDuplicateTopic, uniqueID, msg.getRawId());
        record.headers().add(KafkaHeaderValue.DltOccurrence, dltOccurrence.toString().getBytes());
        record.headers().add(KafkaHeaderValue.MessageOperation, EnumKafkaOperation.INJECTION.name().getBytes());
        sendMessage(record);
    }




    private void sendMessage(ProducerRecord<String, String> record) {
        kafkaTemplate.send(record);
    }


}