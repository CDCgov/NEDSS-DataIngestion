package gov.cdc.dataingestion.kafka.integration.service;

import com.google.gson.Gson;
import gov.cdc.dataingestion.constant.KafkaHeaderValue;
import gov.cdc.dataingestion.constant.TopicPreparationType;
import gov.cdc.dataingestion.constant.enums.EnumKafkaOperation;
import gov.cdc.dataingestion.exception.ConversionPrepareException;
import gov.cdc.dataingestion.validation.repository.model.ValidatedELRModel;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class KafkaProducerService {
    private static final String PREFIX_MSG_PREP = "PREP_";
    private static final String PREFIX_MSG_XML = "XML_";
    private static final String PREFIX_MSG_FHIR = "FHIR_";
    private static final String PREFIX_MSG_VALID = "VALID_";
    private static final String PREFIX_MSG_HL7 = "HL7_";


    private final KafkaTemplate<String, String> kafkaTemplate;
    public KafkaProducerService( KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessageFromController(String msg,
                                          String topic,
                                          String msgType,
                                          Integer dltOccurrence,
                                          Boolean validationActive,
                                          String version) {
        String uniqueID = msgType + "_" + UUID.randomUUID();
        var prodRecord = new ProducerRecord<>(topic, uniqueID, msg);
        prodRecord.headers().add(KafkaHeaderValue.MESSAGE_TYPE, msgType.getBytes());
        prodRecord.headers().add(KafkaHeaderValue.DLT_OCCURRENCE, dltOccurrence.toString().getBytes());
        prodRecord.headers().add(KafkaHeaderValue.MESSAGE_OPERATION, EnumKafkaOperation.INJECTION.name().getBytes());
        prodRecord.headers().add(KafkaHeaderValue.MESSAGE_VALIDATION_ACTIVE, validationActive.toString().getBytes());

        boolean dataProcessingApplied = version.equals("2");
        prodRecord.headers().add(KafkaHeaderValue.DATA_PROCESSING_ENABLE, Boolean.toString(dataProcessingApplied).getBytes());

        sendMessage(prodRecord);
    }

    public void sendElrXmlMessageFromController(String msgId,
                                          String topic,
                                          String msgType,
                                          Integer dltOccurrence,
                                          String payload, String version) {
        String uniqueID = msgType + "_" + msgId;
        var prodRecord = new ProducerRecord<>(topic, uniqueID, payload);
        prodRecord.headers().add(KafkaHeaderValue.MESSAGE_TYPE, msgType.getBytes());
        prodRecord.headers().add(KafkaHeaderValue.DLT_OCCURRENCE, dltOccurrence.toString().getBytes());
        prodRecord.headers().add(KafkaHeaderValue.MESSAGE_OPERATION, EnumKafkaOperation.INJECTION.name().getBytes());

        boolean dataProcessingApplied = version.equals("2");
        prodRecord.headers().add(KafkaHeaderValue.DATA_PROCESSING_ENABLE, Boolean.toString(dataProcessingApplied).getBytes());

        sendMessage(prodRecord);
    }

    public void sendMessageFromDltController(
            String msg, String topic, String msgType, Integer dltOccurrence) {
        String uniqueID = msgType + "_" + UUID.randomUUID();
        var prodRecord = new ProducerRecord<>(topic, uniqueID, msg);
        prodRecord.headers().add(KafkaHeaderValue.MESSAGE_TYPE, msgType.getBytes());
        prodRecord.headers().add(KafkaHeaderValue.DLT_OCCURRENCE, dltOccurrence.toString().getBytes());
        prodRecord.headers().add(KafkaHeaderValue.MESSAGE_OPERATION, EnumKafkaOperation.REINJECTION.name().getBytes());
        prodRecord.headers().add(KafkaHeaderValue.MESSAGE_VALIDATION_ACTIVE, "true".getBytes());
        prodRecord.headers().add(KafkaHeaderValue.DATA_PROCESSING_ENABLE, "false".getBytes());
        sendMessage(prodRecord);
    }


    @Deprecated
    @SuppressWarnings("java:S1133")
    public void sendMessageFromCSVController(List<List<String>> msg, String topic, String msgType) {
        String uniqueID = msgType + "_" + UUID.randomUUID();
        Gson gson = new Gson();
        String json = gson.toJson(msg);

        var prodRecord = new ProducerRecord<>(topic, uniqueID, json);
        prodRecord.headers().add(KafkaHeaderValue.MESSAGE_TYPE, msgType.getBytes());
        sendMessage(prodRecord);
    }

    public void sendMessageAfterValidatingMessage(ValidatedELRModel msg, String topic, Integer dltOccurrence, String dataProcessingEnable) {
        String uniqueID =  PREFIX_MSG_VALID + msg.getMessageType() + "_" + UUID.randomUUID();
        var prodRecord = new ProducerRecord<>(topic, uniqueID, msg.getId());
        prodRecord.headers().add(KafkaHeaderValue.MESSAGE_TYPE, msg.getMessageType().getBytes());
        prodRecord.headers().add(KafkaHeaderValue.MESSAGE_VERSION, msg.getMessageVersion().getBytes());
        prodRecord.headers().add(KafkaHeaderValue.DLT_OCCURRENCE, dltOccurrence.toString().getBytes());
        prodRecord.headers().add(KafkaHeaderValue.MESSAGE_OPERATION, EnumKafkaOperation.INJECTION.name().getBytes());
        prodRecord.headers().add(KafkaHeaderValue.DATA_PROCESSING_ENABLE, dataProcessingEnable.getBytes());

        sendMessage(prodRecord);
    }
    @SuppressWarnings({"java:S6880"})
    public void sendMessagePreparationTopic(ValidatedELRModel msg, String topic, TopicPreparationType topicType, Integer dltOccurrence, String dataProcessingEnable) throws ConversionPrepareException {

        String uniqueId;
        if (topicType == TopicPreparationType.XML) {
            uniqueId =  PREFIX_MSG_PREP + PREFIX_MSG_XML + msg.getMessageType() + "_" + UUID.randomUUID();
        }
        else if (topicType == TopicPreparationType.FHIR) {
            uniqueId =  PREFIX_MSG_PREP +  PREFIX_MSG_FHIR + msg.getMessageType() + "_" + UUID.randomUUID();
        }
        else {
            throw new ConversionPrepareException("Unsupported Topic");
        }
        sendMessageHelper(topic, dltOccurrence, uniqueId, msg.getId(), msg.getMessageType(), msg.getMessageVersion(), dataProcessingEnable);
    }



    private void sendMessageHelper(String topic, Integer dltOccurrence, String uniqueId,
                                   String messageOriginId, String messageType, String messageVersion,
                                   String dataProcessingEnable) {
        var prodRecord = new ProducerRecord<>(topic, uniqueId, messageOriginId);
        prodRecord.headers().add(KafkaHeaderValue.MESSAGE_TYPE, messageType.getBytes());
        prodRecord.headers().add(KafkaHeaderValue.MESSAGE_VERSION, messageVersion.getBytes());
        prodRecord.headers().add(KafkaHeaderValue.DLT_OCCURRENCE, dltOccurrence.toString().getBytes());
        prodRecord.headers().add(KafkaHeaderValue.MESSAGE_OPERATION, EnumKafkaOperation.INJECTION.name().getBytes());
        prodRecord.headers().add(KafkaHeaderValue.DATA_PROCESSING_ENABLE, dataProcessingEnable.getBytes());

        sendMessage(prodRecord);
    }

    public void sendMessageDlt(String msgShort, String msg, String topic, Integer dltOccurrence,
                               String stackTrace, String originalTopic) {
        String uniqueID = "DLT_" + UUID.randomUUID();
        var prodRecord = new ProducerRecord<>(topic, uniqueID, msg);
        prodRecord.headers().add(KafkaHeaderValue.DLT_OCCURRENCE, dltOccurrence.toString().getBytes());
        prodRecord.headers().add(KafkaHeaders.EXCEPTION_STACKTRACE, stackTrace.getBytes());
        prodRecord.headers().add(KafkaHeaders.EXCEPTION_MESSAGE, msgShort.getBytes());
        prodRecord.headers().add(KafkaHeaders.ORIGINAL_TOPIC, originalTopic.getBytes());
        sendMessage(prodRecord);
    }



    public void sendMessageAfterConvertedToXml(String xmlMsg, String topic, Integer dltOccurrence) {
        String uniqueID = PREFIX_MSG_XML + UUID.randomUUID();
        var prodRecord = new ProducerRecord<>(topic, uniqueID, xmlMsg);
        prodRecord.headers().add(KafkaHeaderValue.DLT_OCCURRENCE, dltOccurrence.toString().getBytes());
        prodRecord.headers().add(KafkaHeaderValue.MESSAGE_OPERATION, EnumKafkaOperation.INJECTION.name().getBytes());
        prodRecord.headers().add(KafkaHeaderValue.DATA_TYPE, "ELR".getBytes());
        sendMessage(prodRecord);
    }

    public void sendMessageAfterCheckingDuplicateHL7(ValidatedELRModel msg, String validatedElrDuplicateTopic, Integer dltOccurrence) {
        String uniqueID = PREFIX_MSG_HL7 + UUID.randomUUID();
        var prodRecord = new ProducerRecord<>(validatedElrDuplicateTopic, uniqueID, msg.getRawId());
        prodRecord.headers().add(KafkaHeaderValue.DLT_OCCURRENCE, dltOccurrence.toString().getBytes());
        prodRecord.headers().add(KafkaHeaderValue.MESSAGE_OPERATION, EnumKafkaOperation.INJECTION.name().getBytes());
        sendMessage(prodRecord);
    }




    private void sendMessage(ProducerRecord<String, String> prodRecord) {
        kafkaTemplate.send(prodRecord);
    }


}