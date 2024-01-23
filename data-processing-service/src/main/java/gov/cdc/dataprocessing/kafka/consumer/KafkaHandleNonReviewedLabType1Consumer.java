package gov.cdc.dataprocessing.kafka.consumer;

import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.kafka.producer.KafkaHandleNonReviewedLabType1Producer;
import gov.cdc.dataprocessing.kafka.producer.KafkaPatientProducer;
import gov.cdc.dataprocessing.service.HandleNonReviewedLabType1Service;
import gov.cdc.dataprocessing.service.PatientService;
import gov.cdc.dataprocessing.service.interfaces.IHandleNonReviewedLabType1Service;
import gov.cdc.dataprocessing.service.interfaces.IPatientService;
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
public class KafkaHandleNonReviewedLabType1Consumer {
    private static final Logger logger = LoggerFactory.getLogger(KafkaHandleNonReviewedLabType1Consumer.class);

    @Value("${kafka.topic.elr_edx_log}")
    private String edxTopic = "elr_edx_log";

    private final KafkaHandleNonReviewedLabType1Producer kafkaHandleNonReviewedLabType1Producer;
    private final IHandleNonReviewedLabType1Service handleNonReviewedLabType1Service;

    public KafkaHandleNonReviewedLabType1Consumer(
            KafkaHandleNonReviewedLabType1Producer kafkaHandleNonReviewedLabType1Producer,
            HandleNonReviewedLabType1Service handleNonReviewedLabType1Service
    ) {
        this.kafkaHandleNonReviewedLabType1Producer = kafkaHandleNonReviewedLabType1Producer;
        this.handleNonReviewedLabType1Service = handleNonReviewedLabType1Service;
    }

    @KafkaListener(
            topics = "${kafka.topic.elr_handle_non_review_lab_1}"
    )
    public void handleMessageFromType1Lab(String message,
                                     @Header(KafkaHeaders.RECEIVED_TOPIC) String topic)
            throws DataProcessingConsumerException {
        //TODO: Logic to handle goes here
        handleNonReviewedLabType1Service.processingNonReviewLabType1();
        //TODO: sending out next queue
        kafkaHandleNonReviewedLabType1Producer.sendNonReviewLab1Message(edxTopic, "data");
    }

}
