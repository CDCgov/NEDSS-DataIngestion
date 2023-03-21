package gov.cdc.dataingestion.consumer.validationservice.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.errors.SerializationException;
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
@Slf4j
public class KafkaConsumerService {

    @RetryableTopic(
            attempts = "5",
            // retry topic name, such as topic-retry-1, topic-retry-2, etc
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
            // time to wait before attempting to retry
            backoff = @Backoff(delay = 1000, multiplier = 2.0),
            // if these exceptions occur, skip retry then push message to DLQ
            exclude = {SerializationException.class, DeserializationException.class}
    )
    @KafkaListener(id = "${spring.kafka.consumer.group-id}", topics = "${topic}")
    public void handleMessage(ConsumerRecord<String, String> consumerRecord, String message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info("Received message: {} from topic: {}", message, topic);
        try {

        } catch (Exception e) {
            System.out.println(e.getMessage());
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
