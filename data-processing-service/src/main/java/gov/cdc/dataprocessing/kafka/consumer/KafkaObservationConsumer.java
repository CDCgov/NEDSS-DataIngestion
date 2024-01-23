package gov.cdc.dataprocessing.kafka.consumer;

import gov.cdc.dataingestion.hl7.helper.integration.exception.DiHL7Exception;
import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.kafka.producer.KafkaObservationProducer;
import gov.cdc.dataprocessing.service.ObservationService;
import gov.cdc.dataprocessing.service.interfaces.IObservationService;
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
public class KafkaObservationConsumer {
    private static final Logger logger = LoggerFactory.getLogger(KafkaObservationConsumer.class);

    @Value("${kafka.topic.elr_obs}")
    private String obsTopic = "elr_processing_obs";
    private final KafkaObservationProducer kafkaObservationProducer;
    private final IObservationService observationService;

    public KafkaObservationConsumer(
            KafkaObservationProducer kafkaObservationProducer,
            ObservationService observationService
    ) {
        this.kafkaObservationProducer = kafkaObservationProducer;
        this.observationService = observationService;
    }

    @KafkaListener(
            topics = "${kafka.topic.elr_micro}"
    )
    public void handleMessage(String message,
              @Header(KafkaHeaders.RECEIVED_TOPIC) String topic)
            throws DataProcessingConsumerException {
        //TODO: Logic to handle goes here
        observationService.processingObservation();
        //TODO: sending out next queue
        kafkaObservationProducer.sendObservationMessage(obsTopic, "data");
    }
}
