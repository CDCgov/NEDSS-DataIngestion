package gov.cdc.dataingestion.consumer.validationservice.service;

import ca.uhn.hl7v2.DefaultHapiContext;
import gov.cdc.dataingestion.consumer.validationservice.integration.CsvValidator;
import gov.cdc.dataingestion.consumer.validationservice.integration.HL7v2Validator;
import gov.cdc.dataingestion.consumer.validationservice.model.MessageModel;
import gov.cdc.dataingestion.consumer.validationservice.model.constant.KafkaHeaderValue;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.errors.SerializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class KafkaConsumerService {

    @Value("${kafka.consumer.topic}")
    private String validatedTopic = "";
    @Autowired
    KafkaProducerService kafkaProducerService;
    HL7v2Validator hl7v2Validator = new HL7v2Validator(new DefaultHapiContext());
    CsvValidator csvValidator = new CsvValidator();

    @RetryableTopic(
            attempts = "${kafka.consumer.max-retry}",
            // retry topic name, such as topic-retry-1, topic-retry-2, etc
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
            // time to wait before attempting to retry
            backoff = @Backoff(delay = 1000, multiplier = 2.0),
            // if these exceptions occur, skip retry then push message to DLQ
            exclude = {SerializationException.class, DeserializationException.class}
    )
    @KafkaListener(id = "${kafka.consumer.group-id}", topics = "#{'${kafka.topics}'.split(',')}")
    public void handleMessage(ConsumerRecord<String, String> consumerRecord, String message,
                              @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info("Received message: {} from topic: {}", message, topic);
        String messageType = null;
        try {
            var messageHeaders = consumerRecord.headers().toArray();
            for(int i = 0; i < messageHeaders.length; i++) {
                var header = messageHeaders[i];
                if(header.key().equalsIgnoreCase(KafkaHeaderValue.MessageType)) {
                    messageType = new String(header.value(), StandardCharsets.UTF_8);
                }
            }

            switch (messageType) {
                case KafkaHeaderValue.MessageType_HL7v2:
                    MessageModel hl7ValidatedModel = hl7v2Validator.MessageValidation(message);
                    kafkaProducerService.sendMessageAfterValidatingMessage(hl7ValidatedModel, validatedTopic);
                    // push this to another consumer for further queue down the line
                    break;
                case KafkaHeaderValue.MessageType_CSV:

                    MessageModel csvValidatedModel = csvValidator.ValidateCSVAgainstCVSSchema(message);
                    kafkaProducerService.sendMessageAfterValidatingMessage(csvValidatedModel, validatedTopic);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            log.info("Retry queue");
            // run time error then -- do retry
            throw new RuntimeException(e.getMessage());
        }
    }

    @DltHandler
    public void handleDlt(String message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        // Once in DLQ -- we can save message in actual db for further analyze
        log.info("Message: {} handled by dlq topic: {}", message, topic);
    }
}
