package gov.cdc.dataprocessing.kafka.consumer;

import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.kafka.producer.KafkaOrganizationProducer;
import gov.cdc.dataprocessing.kafka.producer.KafkaPatientProducer;
import gov.cdc.dataprocessing.service.OrganizationService;
import gov.cdc.dataprocessing.service.PatientService;
import gov.cdc.dataprocessing.service.interfaces.IOrganizationService;
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
public class KafkaOrganizationConsumer {
    private static final Logger logger = LoggerFactory.getLogger(KafkaOrganizationConsumer.class);

    @Value("${kafka.topic.elr_org}")
    private String orgTopic = "elr_processing_org";

    private final KafkaOrganizationProducer kafkaOrganizationProducer;
    private final IOrganizationService organizationService;

    public KafkaOrganizationConsumer(
            KafkaOrganizationProducer kafkaOrganizationProducer,
            OrganizationService organizationService
    ) {
        this.kafkaOrganizationProducer = kafkaOrganizationProducer;
        this.organizationService = organizationService;
    }

    @KafkaListener(
            topics = "${kafka.topic.elr_provider}"
    )
    public void handleMessageFromProvider(String message,
                                     @Header(KafkaHeaders.RECEIVED_TOPIC) String topic)
            throws DataProcessingConsumerException {
        //TODO: Logic to handle goes here
        organizationService.processingOrganization();
        //TODO: sending out next queue
        kafkaOrganizationProducer.sendOrganizationMessage(orgTopic, "data");
    }
}
