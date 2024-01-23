package gov.cdc.dataprocessing.kafka.consumer;

import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.kafka.producer.KafkaPatientProducer;
import gov.cdc.dataprocessing.kafka.producer.KafkaPublicHealthCaseProducer;
import gov.cdc.dataprocessing.service.PatientService;
import gov.cdc.dataprocessing.service.PublicHealthCaseService;
import gov.cdc.dataprocessing.service.interfaces.IPatientService;
import gov.cdc.dataprocessing.service.interfaces.IPublicHealthCaseService;
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
public class KafkaPublicHealthCaseConsumer {
    private static final Logger logger = LoggerFactory.getLogger(KafkaPublicHealthCaseConsumer.class);

    @Value("${kafka.topic.elr_health_case}")
    private String caseTopic = "elr_processing_public_health_case";
    @Value("${kafka.topic.elr_auto_investigation}")
    private String investigationTopic = "elr_processing_auto_investigation";

    private final KafkaPublicHealthCaseProducer kafkaPublicHealthCaseProducer;
    private final IPublicHealthCaseService publicHealthCaseService;

    public KafkaPublicHealthCaseConsumer(
            KafkaPublicHealthCaseProducer kafkaPublicHealthCaseProducer,
            PublicHealthCaseService publicHealthCaseService
    ) {
        this.kafkaPublicHealthCaseProducer = kafkaPublicHealthCaseProducer;
        this.publicHealthCaseService = publicHealthCaseService;
    }

    @KafkaListener(
            topics = "${kafka.topic.elr_process_lab}"
    )
    public void handleMessageFromLab(String message,
                                     @Header(KafkaHeaders.RECEIVED_TOPIC) String topic)
            throws DataProcessingConsumerException {
        //TODO: Logic to handle goes here
        publicHealthCaseService.processingPublicHealthCase();
        //TODO: sending out next queue
        kafkaPublicHealthCaseProducer.sendPublicHealthCaseMessage(caseTopic, "data");
    }

    @KafkaListener(
            topics = "${kafka.topic.elr_health_case}"
    )
    public void handleMessageFromCase(String message,
                                     @Header(KafkaHeaders.RECEIVED_TOPIC) String topic)
            throws DataProcessingConsumerException {
        //TODO: Logic to handle goes here
        publicHealthCaseService.processingAutoInvestigation();
        //TODO: sending out next queue
        kafkaPublicHealthCaseProducer.sendPublicHealthCaseMessage(investigationTopic, "data");
    }
}
