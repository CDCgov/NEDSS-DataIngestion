package gov.cdc.dataprocessing.kafka.consumer;

import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.kafka.producer.KafkaManagerProducer;
import gov.cdc.dataprocessing.service.interfaces.core.IManagerService;
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

    @Value("${kafka.topic.elr_handle_lab}")
    private String handleLabTopic = "elr_processing_handle_lab";
    @Value("${kafka.topic.elr_edx_log}")
    private String logTopic = "elr_edx_log";
    private final KafkaManagerProducer kafkaManagerProducer;
    private final IManagerService managerService;

    public KafkaPublicHealthCaseConsumer(
            KafkaManagerProducer kafkaManagerProducer,
            IManagerService managerService) {

        this.kafkaManagerProducer = kafkaManagerProducer;
        this.managerService = managerService;
    }

    @KafkaListener(
            topics = "${kafka.topic.elr_health_case}"
    )
    public void handleMessageForPublicHealthCase(String message,
                                     @Header(KafkaHeaders.RECEIVED_TOPIC) String topic)
            throws DataProcessingConsumerException {
        //TODO: Logic to handle goes here
        Object result = new Object();
        try {
            result = managerService.processingHealthCase("data");

            //TODO: Send out result to next step
            kafkaManagerProducer.sendData(handleLabTopic, "result");
        } catch (DataProcessingConsumerException e) {
            //TODO: Error occurred mid way, send result to edx logging
            kafkaManagerProducer.sendData(logTopic, "result");
        }
    }

}
