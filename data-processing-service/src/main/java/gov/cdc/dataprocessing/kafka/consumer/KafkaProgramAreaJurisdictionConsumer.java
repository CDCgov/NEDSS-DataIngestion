package gov.cdc.dataprocessing.kafka.consumer;

import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.kafka.producer.KafkaOrganizationProducer;
import gov.cdc.dataprocessing.kafka.producer.KafkaPatientProducer;
import gov.cdc.dataprocessing.kafka.producer.KafkaProgramAreaJurisdictionProducer;
import gov.cdc.dataprocessing.service.PatientService;
import gov.cdc.dataprocessing.service.ProgramAreaJurisdictionService;
import gov.cdc.dataprocessing.service.interfaces.IPatientService;
import gov.cdc.dataprocessing.service.interfaces.IProgramAreaJurisdictionService;
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
public class KafkaProgramAreaJurisdictionConsumer {
    private static final Logger logger = LoggerFactory.getLogger(KafkaProgramAreaJurisdictionConsumer.class);

    @Value("${kafka.topic.elr_prog_area}")
    private String progAreaTopic = "elr_processing_program_area";
    @Value("${kafka.topic.elr_jurisdiction}")
    private String jurisdictionTopic = "elr_processing_jurisdiction";

    private final KafkaProgramAreaJurisdictionProducer kafkaProgramAreaJurisdictionProducer;
    private final IProgramAreaJurisdictionService programAreaJurisdictionService;

    public KafkaProgramAreaJurisdictionConsumer(
            KafkaProgramAreaJurisdictionProducer kafkaProgramAreaJurisdictionProducer,
            ProgramAreaJurisdictionService programAreaJurisdictionService
    ) {
        this.kafkaProgramAreaJurisdictionProducer = kafkaProgramAreaJurisdictionProducer;
        this.programAreaJurisdictionService = programAreaJurisdictionService;
    }


    @KafkaListener(
            topics = "${kafka.topic.elr_org}"
    )
    public void handleMessageFromOrg(String message,
                                     @Header(KafkaHeaders.RECEIVED_TOPIC) String topic)
            throws DataProcessingConsumerException {
        //TODO: Logic to handle goes here
        programAreaJurisdictionService.processingProgramArea();
        //TODO: sending out next queue
        kafkaProgramAreaJurisdictionProducer.sendProgAreaMessage(progAreaTopic, "data");
    }

    @KafkaListener(
            topics = "${kafka.topic.elr_prog_area}"
    )
    public void handleMessageFromProgArea(String message,
                                     @Header(KafkaHeaders.RECEIVED_TOPIC) String topic)
            throws DataProcessingConsumerException {
        //TODO: Logic to handle goes here
        programAreaJurisdictionService.processingJurisdiction();
        //TODO: sending out next queue
        kafkaProgramAreaJurisdictionProducer.sendProgAreaMessage(jurisdictionTopic, "data");
    }

}
