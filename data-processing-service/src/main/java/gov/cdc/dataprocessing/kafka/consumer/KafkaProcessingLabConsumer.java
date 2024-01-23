package gov.cdc.dataprocessing.kafka.consumer;

import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.kafka.producer.KafkaPatientProducer;
import gov.cdc.dataprocessing.kafka.producer.KafkaProcessingLabProducer;
import gov.cdc.dataprocessing.service.LabProcessingService;
import gov.cdc.dataprocessing.service.PatientService;
import gov.cdc.dataprocessing.service.interfaces.ILabProcessingService;
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
public class KafkaProcessingLabConsumer {
    private static final Logger logger = LoggerFactory.getLogger(KafkaProcessingLabConsumer.class);

    @Value("${kafka.topic.elr_process_lab}")
    private String labTopic = "elr_processing_lab_result";

    private final KafkaProcessingLabProducer kafkaProcessingLabProducer;
    private final ILabProcessingService labProcessingService;

    public KafkaProcessingLabConsumer(
            KafkaProcessingLabProducer kafkaProcessingLabProducer,
            LabProcessingService labProcessingService
    ) {
        this.kafkaProcessingLabProducer = kafkaProcessingLabProducer;
        this.labProcessingService = labProcessingService;
    }

    @KafkaListener(
            topics = "${kafka.topic.elr_jurisdiction}"
    )
    public void handleMessageFromJurisdiction(String message,
                                     @Header(KafkaHeaders.RECEIVED_TOPIC) String topic)
            throws DataProcessingConsumerException {
        //TODO: Logic to handle goes here
        labProcessingService.processingLabResult();
        //TODO: sending out next queue
        kafkaProcessingLabProducer.sendLabResultMessage(labTopic, "data");
    }

}
