package gov.cdc.dataprocessing.kafka.consumer;

import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.kafka.producer.KafkaHandleReviewedLabProducer;
import gov.cdc.dataprocessing.kafka.producer.KafkaPatientProducer;
import gov.cdc.dataprocessing.service.HandleReviewedLabService;
import gov.cdc.dataprocessing.service.PatientService;
import gov.cdc.dataprocessing.service.interfaces.IHandleReviewedLabService;
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
public class KafkaHandleReviewedLabConsumer {
    private static final Logger logger = LoggerFactory.getLogger(KafkaHandleReviewedLabConsumer.class);

    @Value("${kafka.topic.elr_handle_lab}")
    private String handleLabTopic = "elr_processing_handle_lab";
    @Value("${kafka.topic.elr_handle_reviewed_lab}")
    private String reviewedLabTopic = "elr_processing_handle_reviewed_lab";
    @Value("${kafka.topic.elr_handle_non_review_lab_1}")
    private String type1LabTopic = "elr_processing_handle_non_lab_1";
    @Value("${kafka.topic.elr_handle_non_review_lab_2}")
    private String type2LabTopic = "elr_processing_handle_non_lab_2";
    @Value("${kafka.topic.elr_edx_log}")
    private String edxTopic = "elr_edx_log";

    private final KafkaHandleReviewedLabProducer kafkaHandleReviewedLabProducer;
    private final IHandleReviewedLabService handleReviewedLabService;

    public KafkaHandleReviewedLabConsumer(
            KafkaHandleReviewedLabProducer kafkaHandleReviewedLabProducer,
            HandleReviewedLabService handleReviewedLabService
    ) {
        this.kafkaHandleReviewedLabProducer = kafkaHandleReviewedLabProducer;
        this.handleReviewedLabService = handleReviewedLabService;
    }

    @KafkaListener(
            topics = "${kafka.topic.elr_auto_investigation}"
    )
    public void handleMessageFromHealthInvestigation(String message,
                                     @Header(KafkaHeaders.RECEIVED_TOPIC) String topic)
            throws DataProcessingConsumerException {
        //TODO: Logic to handle goes here
        //TODO: sending out next queue
        kafkaHandleReviewedLabProducer.sendReviewedLabMessage(handleLabTopic, "data");

    }

    @KafkaListener(
            topics = "${kafka.topic.elr_handle_lab}"
    )
    public void handleMessage(String message,
                             @Header(KafkaHeaders.RECEIVED_TOPIC) String topic)
            throws DataProcessingConsumerException {
        //TODO: Logic to handle goes here
        //TODO: sending out next queue
        boolean reviewed = true;
        String destinationTopic = "";
        if (reviewed) {
            destinationTopic = reviewedLabTopic;
        } else {
            boolean actObjIsNotNull = true;
            if (actObjIsNotNull) {
                destinationTopic = type1LabTopic;
            } else {
                destinationTopic = type2LabTopic;
            }
        }

        if (destinationTopic.isEmpty()) {
        //TODO: follow original logic and handle this
        } else {
            kafkaHandleReviewedLabProducer.sendReviewedLabMessage(destinationTopic, "data");
        }

    }


    @KafkaListener(
            topics = "${kafka.topic.elr_handle_reviewed_lab}"
    )
    public void handleMessageFromSelectedLabResult(String message,
                                     @Header(KafkaHeaders.RECEIVED_TOPIC) String topic)
            throws DataProcessingConsumerException {
        //TODO: Logic to handle goes here
        handleReviewedLabService.processingReviewedLab();
        //TODO: sending out next queue
        kafkaHandleReviewedLabProducer.sendReviewedLabMessage(edxTopic, "data");
    }
}
