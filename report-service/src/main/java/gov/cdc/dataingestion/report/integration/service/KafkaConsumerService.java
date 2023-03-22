package gov.cdc.dataingestion.report.integration.service;
import gov.cdc.dataingestion.report.integration.conversion.HL7ToFHIRConversion;
import gov.cdc.dataingestion.report.integration.conversion.interfaces.IHL7ToFHIRConversion;
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

@Component
public class KafkaConsumerService {

    IHL7ToFHIRConversion hl7ToFHIRConversion = new HL7ToFHIRConversion(new HL7ToFHIRConverter());
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
        String messageType = null;
        try {
            var hl7String = hl7ToFHIRConversion.ConvertHL7v2ToFhir(message);

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @DltHandler
    public void handleDlt(String message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        // Once in DLQ -- we can save message in actual db for further analyze
        // To be implemented
    }
}
