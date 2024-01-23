package gov.cdc.dataprocessing.kafka.consumer;

import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.kafka.producer.KafkaPatientProducer;
import gov.cdc.dataprocessing.service.PatientService;
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
public class KafkaPatientConsumer {
    private static final Logger logger = LoggerFactory.getLogger(KafkaPatientConsumer.class);

    @Value("${kafka.topic.elr_patient}")
    private String patientTopic = "elr_processing_patient";
    @Value("${kafka.topic.elr_nok}")
    private String nokTopic = "elr_processing_nok";
    @Value("${kafka.topic.elr_provider")
    private String providerTopic = "elr_processing_provider";

    private final KafkaPatientProducer kafkaPatientProducer;
    private final IPatientService patientService;

    public KafkaPatientConsumer(
            KafkaPatientProducer kafkaPatientProducer,
            PatientService patientService
    ) {
        this.kafkaPatientProducer = kafkaPatientProducer;
        this.patientService = patientService;
    }

    @KafkaListener(
            topics = "${kafka.topic.elr_obs}"
    )
    public void handleMessageFromObs(String message,
                              @Header(KafkaHeaders.RECEIVED_TOPIC) String topic)
            throws DataProcessingConsumerException {
        //TODO: Logic to handle goes here
        patientService.processingPatient();
        //TODO: sending out next queue
        kafkaPatientProducer.sendPatientMessage(patientTopic, "data");
    }

    @KafkaListener(
            topics = "${kafka.topic.elr_patient}"
    )
    public void handleMessageFromPatient(String message,
                                     @Header(KafkaHeaders.RECEIVED_TOPIC) String topic)
            throws DataProcessingConsumerException {
        //TODO: Logic to handle goes here
        patientService.processingNextOfKin();
        //TODO: sending out next queue
        kafkaPatientProducer.sendPatientMessage(nokTopic, "data");
    }

    @KafkaListener(
            topics = "${kafka.topic.elr_nok}"
    )
    public void handleMessageFromNok(String message,
                                         @Header(KafkaHeaders.RECEIVED_TOPIC) String topic)
            throws DataProcessingConsumerException {
        //TODO: Logic to handle goes here
        patientService.processingProvider();
        //TODO: sending out next queue
        kafkaPatientProducer.sendPatientMessage(providerTopic, "data");
    }
}
