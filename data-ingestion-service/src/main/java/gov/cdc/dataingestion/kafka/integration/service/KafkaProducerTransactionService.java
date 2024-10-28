package gov.cdc.dataingestion.kafka.integration.service;

import gov.cdc.dataingestion.constant.KafkaHeaderValue;
import gov.cdc.dataingestion.constant.enums.EnumKafkaOperation;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
/**
 1118 - require constructor complaint
 125 - comment complaint
 6126 - String block complaint
 * */
@SuppressWarnings({"java:S1118","java:S125", "java:S6126"})
public class KafkaProducerTransactionService {
    private static final String PREFIX_MSG_XML = "XML_";

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProducerTransactionService(@Qualifier("transactionalKafkaTemplate") KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    public void sendMessageAfterConvertedToXml(String xmlMsg, String topic, Integer dltOccurrence) {
        String uniqueID = PREFIX_MSG_XML + UUID.randomUUID();
        var prodRecord = new ProducerRecord<>(topic, uniqueID, xmlMsg);
        prodRecord.headers().add(KafkaHeaderValue.DLT_OCCURRENCE, dltOccurrence.toString().getBytes());
        prodRecord.headers().add(KafkaHeaderValue.MESSAGE_OPERATION, EnumKafkaOperation.INJECTION.name().getBytes());
        prodRecord.headers().add(KafkaHeaderValue.DATA_TYPE, "ELR".getBytes());
        sendMessage(prodRecord);
    }

    private void sendMessage(ProducerRecord<String, String> prodRecord) {
        kafkaTemplate.send(prodRecord);
    }
}
