package gov.cdc.dataprocessing.kafka.consumer;

import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.kafka.producer.KafkaHandleNonReviewedLabType1Producer;
import gov.cdc.dataprocessing.kafka.producer.KafkaHandleNonReviewedLabType2Producer;
import gov.cdc.dataprocessing.service.HandleNonReviewedLabType1Service;
import gov.cdc.dataprocessing.service.HandleNonReviewedLabType2Service;
import gov.cdc.dataprocessing.service.interfaces.IHandleNonReviewedLabType1Service;
import gov.cdc.dataprocessing.service.interfaces.IHandleNonReviewedLabType2Service;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaHandleNonReviewedLabType2Consumer {
    private static final Logger logger = LoggerFactory.getLogger(KafkaHandleNonReviewedLabType2Consumer.class);

    @Value("${kafka.topic.elr_edx_log}")
    private String edxTopic = "elr_edx_log";

    private final KafkaHandleNonReviewedLabType2Producer kafkaHandleNonReviewedLabType2Producer;
    private final IHandleNonReviewedLabType2Service handleNonReviewedLabType2Service;

    public KafkaHandleNonReviewedLabType2Consumer(
            KafkaHandleNonReviewedLabType2Producer kafkaHandleNonReviewedLabType2Producer,
            HandleNonReviewedLabType2Service handleNonReviewedLabType2Service
    ) {
        this.kafkaHandleNonReviewedLabType2Producer = kafkaHandleNonReviewedLabType2Producer;
        this.handleNonReviewedLabType2Service = handleNonReviewedLabType2Service;
    }

    @KafkaListener(
            topics = "${kafka.topic.elr_handle_non_review_lab_2}"
    )
    public void handleMessageFromType2Lab(String message,
                                          @Header(KafkaHeaders.RECEIVED_TOPIC) String topic)
            throws DataProcessingConsumerException {
        //TODO: Logic to handle goes here
        handleNonReviewedLabType2Service.processingNonReviewLabType2();
        //TODO: sending out next queue
        kafkaHandleNonReviewedLabType2Producer.sendNonReviewLab2Message(edxTopic, "data");
    }

}
