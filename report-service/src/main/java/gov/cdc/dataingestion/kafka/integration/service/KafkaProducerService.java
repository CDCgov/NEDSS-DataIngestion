package gov.cdc.dataingestion.kafka.integration.service;

import com.google.gson.Gson;
import gov.cdc.dataingestion.constant.enums.EnumKafkaOperation;
import gov.cdc.dataingestion.conversion.repository.model.HL7ToFHIRModel;
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
    private static final String prefixMessagePreparation = "PREP_";
    private static final String xmlMessageKeyPrefix = "XML_";
    private static final String fhirMessageKeyPrefix = "FHIR_";
    private static final String validMessageKeyPrefix = "VALID_";
    private static final String hl7MessageKeyPrefix = "HL7_";


    private final KafkaTemplate<String, String> kafkaTemplate;
    public KafkaProducerService( KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessageFromController(String msg, String topic, String msgType, Integer dltOccurrence, Boolean validationActive) {
        String uniqueID = msgType + "_" + UUID.randomUUID();
        var prodRecord = new ProducerRecord<>(topic, uniqueID, msg);
        prodRecord.headers().add(KafkaHeaderValue.MESSAGE_TYPE, msgType.getBytes());
        prodRecord.headers().add(KafkaHeaderValue.DLT_OCCURRENCE, dltOccurrence.toString().getBytes());
        prodRecord.headers().add(KafkaHeaderValue.MESSAGE_OPERATION, EnumKafkaOperation.INJECTION.name().getBytes());
        prodRecord.headers().add(KafkaHeaderValue.MESSAGE_VALIDATION_ACTIVE, validationActive.toString().getBytes());

        sendMessage(prodRecord);
    }

    public void sendMessageFromDltController(
            String msg, String topic, String msgType, Integer dltOccurrence) {
        String uniqueID = msgType + "_" + UUID.randomUUID();
        var prodRecord = new ProducerRecord<>(topic, uniqueID, msg);
        prodRecord.headers().add(KafkaHeaderValue.MESSAGE_TYPE, msgType.getBytes());
        prodRecord.headers().add(KafkaHeaderValue.DLT_OCCURRENCE, dltOccurrence.toString().getBytes());
        prodRecord.headers().add(KafkaHeaderValue.MESSAGE_OPERATION, EnumKafkaOperation.REINJECTION.name().getBytes());
        sendMessage(prodRecord);
    }


    @Deprecated
    public void sendMessageFromCSVController(List<List<String>> msg, String topic, String msgType) {
        String uniqueID = msgType + "_" + UUID.randomUUID();
        Gson gson = new Gson();
        String json = gson.toJson(msg);

        var prodRecord = new ProducerRecord<>(topic, uniqueID, json);
        prodRecord.headers().add(KafkaHeaderValue.MESSAGE_TYPE, msgType.getBytes());
        sendMessage(prodRecord);
    }

    public void sendMessageAfterValidatingMessage(ValidatedELRModel msg, String topic, Integer dltOccurrence) {
        String uniqueID =  validMessageKeyPrefix + msg.getMessageType() + "_" + UUID.randomUUID();
        var prodRecord = new ProducerRecord<>(topic, uniqueID, msg.getId());
        prodRecord.headers().add(KafkaHeaderValue.MESSAGE_TYPE, msg.getMessageType().getBytes());
        prodRecord.headers().add(KafkaHeaderValue.MESSAGE_VERSION, msg.getMessageVersion().getBytes());
        prodRecord.headers().add(KafkaHeaderValue.DLT_OCCURRENCE, dltOccurrence.toString().getBytes());
        prodRecord.headers().add(KafkaHeaderValue.MESSAGE_OPERATION, EnumKafkaOperation.INJECTION.name().getBytes());
        sendMessage(prodRecord);
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
        var prodRecord = new ProducerRecord<>(topic, uniqueId, messageOriginId);
        prodRecord.headers().add(KafkaHeaderValue.MESSAGE_TYPE, messageType.getBytes());
        prodRecord.headers().add(KafkaHeaderValue.MESSAGE_VERSION, messageVersion.getBytes());
        prodRecord.headers().add(KafkaHeaderValue.DLT_OCCURRENCE, dltOccurrence.toString().getBytes());
        prodRecord.headers().add(KafkaHeaderValue.MESSAGE_OPERATION, EnumKafkaOperation.INJECTION.name().getBytes());
        sendMessage(prodRecord);
    }

    public void sendMessageAfterConvertedToFhirMessage(HL7ToFHIRModel msg, String topic, Integer dltOccurrence) {
        String uniqueID = fhirMessageKeyPrefix + UUID.randomUUID();
        var prodRecord = new ProducerRecord<>(topic, uniqueID, msg.getId());
        prodRecord.headers().add(KafkaHeaderValue.DLT_OCCURRENCE, dltOccurrence.toString().getBytes());
        prodRecord.headers().add(KafkaHeaderValue.MESSAGE_OPERATION, EnumKafkaOperation.INJECTION.name().getBytes());
        sendMessage(prodRecord);
    }


    public void sendMessageAfterConvertedToXml(String xmlMsg, String topic, Integer dltOccurrence) {
        String uniqueID = xmlMessageKeyPrefix + UUID.randomUUID();
        var prodRecord = new ProducerRecord<>(topic, uniqueID, xmlMsg);
        prodRecord.headers().add(KafkaHeaderValue.DLT_OCCURRENCE, dltOccurrence.toString().getBytes());
        prodRecord.headers().add(KafkaHeaderValue.MESSAGE_OPERATION, EnumKafkaOperation.INJECTION.name().getBytes());
        sendMessage(prodRecord);
    }

    public void sendMessageAfterCheckingDuplicateHL7(ValidatedELRModel msg, String validatedElrDuplicateTopic, Integer dltOccurrence) {
        String uniqueID = hl7MessageKeyPrefix + UUID.randomUUID();
        var prodRecord = new ProducerRecord<>(validatedElrDuplicateTopic, uniqueID, msg.getRawId());
        prodRecord.headers().add(KafkaHeaderValue.DLT_OCCURRENCE, dltOccurrence.toString().getBytes());
        prodRecord.headers().add(KafkaHeaderValue.MESSAGE_OPERATION, EnumKafkaOperation.INJECTION.name().getBytes());
        sendMessage(prodRecord);
    }




    private void sendMessage(ProducerRecord<String, String> prodRecord) {
        kafkaTemplate.send(prodRecord);
    }


}