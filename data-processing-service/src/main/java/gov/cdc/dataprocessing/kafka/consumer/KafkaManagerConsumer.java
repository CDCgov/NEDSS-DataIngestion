package gov.cdc.dataprocessing.kafka.consumer;

import gov.cdc.dataprocessing.constant.KafkaCustomHeader;
import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.kafka.producer.KafkaManagerProducer;
import gov.cdc.dataprocessing.service.implementation.manager.ManagerService;
import gov.cdc.dataprocessing.service.interfaces.manager.IManagerService;
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
public class KafkaManagerConsumer {
    private static final Logger logger = LoggerFactory.getLogger(KafkaManagerConsumer.class);


    @Value("${kafka.topic.elr_edx_log}")
    private String logTopic = "elr_edx_log";

    @Value("${kafka.topic.elr_health_case}")
    private String healthCaseTopic = "elr_processing_public_health_case";

    private final KafkaManagerProducer kafkaManagerProducer;
    private final IManagerService managerService;

    public KafkaManagerConsumer(
            KafkaManagerProducer kafkaManagerProducer,
            ManagerService managerService
    ) {
        this.kafkaManagerProducer = kafkaManagerProducer;
        this.managerService = managerService;
    }

    @KafkaListener(
            topics = "${kafka.topic.elr_micro}"
    )
    public void handleMessage(String message,
                              @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                              @Header(KafkaCustomHeader.DATA_TYPE) String dataType)
            throws DataProcessingConsumerException {
        //TODO: Logic to handle goes here
        Object result = new Object();
        try {
            result = managerService.processDistribution(dataType,message);

            //TODO: Send out result to next step
            kafkaManagerProducer.sendData(healthCaseTopic, "result");
        } catch (DataProcessingConsumerException e) {
            //TODO: Error occurred mid way, send result to edx logging
            kafkaManagerProducer.sendData(logTopic, "result");
        }
    }
}
