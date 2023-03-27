package gov.cdc.dataingestion.report.integration.service;
import gov.cdc.dataingestion.report.integration.conversion.HL7ToFHIRConversion;
import gov.cdc.dataingestion.report.integration.conversion.interfaces.IHL7ToFHIRConversion;
import gov.cdc.dataingestion.report.model.HL7toFhirModel;
import io.github.linuxforhealth.hl7.HL7ToFHIRConverter;
import lombok.extern.slf4j.Slf4j;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.errors.SerializationException;
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
import org.springframework.kafka.core.KafkaTemplate;

import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class KafkaConsumerService {

    private IHL7ToFHIRConversion hl7ToFHIRConversion;
    private String HL7v2 = "HL7v2";
    public KafkaConsumerService(IHL7ToFHIRConversion hl7ToFHIRConversion) {
        this.hl7ToFHIRConversion = hl7ToFHIRConversion;
    }

    @RetryableTopic(
            attempts = "${kafka.consumer.max-retry}",
            // retry topic name, such as topic-retry-1, topic-retry-2, etc
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
            // time to wait before attempting to retry
            backoff = @Backoff(delay = 1000, multiplier = 2.0),
            // if these exceptions occur, skip retry then push message to DLQ
            exclude = {SerializationException.class, DeserializationException.class}
    )
    @KafkaListener(id = "${kafka.consumer.group-id}", topics = "${kafka.consumer.topic.fhir-conversion}")
    public void handleMessage(ConsumerRecord<String, String> consumerRecord, String message,
                              @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info("Received message: {} from topic: {}", message, topic);
        String messageType = null;
        try {

            var messageHeaders = consumerRecord.headers().toArray();
            for(int i = 0; i < messageHeaders.length; i++) {
                var header = messageHeaders[i];
                if(header.key().equalsIgnoreCase(HL7v2)) {
                    messageType = new String(header.value(), StandardCharsets.UTF_8);
                }
            }

            if (messageType.equalsIgnoreCase(HL7v2)) {
                HL7toFhirModel convertedModel = hl7ToFHIRConversion.ConvertHL7v2ToFhir(message);
                // To be implemented
                // possibly the following queue or whatever requirement is
            } else {
                throw new UnsupportedOperationException("Invalid Message");
            }




        } catch (Exception e) {
            log.info("Retry queue");
            throw new RuntimeException(e.getMessage());
        }
    }

    @DltHandler
    public void handleDlt(String message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        // Once in DLQ -- we can save message in actual db for further analyze
        // Need specification with DLT - what do we do with these messages
        log.info("Message: {} handled by dlq topic: {}", message, topic);
    }
}
