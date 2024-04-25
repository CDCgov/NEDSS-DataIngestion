package gov.cdc.dataprocessing.kafka.consumer;

import gov.cdc.dataprocessing.constant.KafkaCustomHeader;
import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.kafka.producer.KafkaManagerProducer;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.service.implementation.auth.SessionProfileService;
import gov.cdc.dataprocessing.service.implementation.manager.ManagerService;
import gov.cdc.dataprocessing.service.interfaces.manager.IManagerService;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
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
    private final SessionProfileService sessionProfileService;

    public KafkaManagerConsumer(
            KafkaManagerProducer kafkaManagerProducer,
            ManagerService managerService,
            SessionProfileService sessionProfileService) {
        this.kafkaManagerProducer = kafkaManagerProducer;
        this.managerService = managerService;
        this.sessionProfileService = sessionProfileService;

    }

    @KafkaListener(
            topics = "${kafka.topic.elr_micro}"
    )
    public void handleMessage(String message,
                              @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                              @Header(KafkaCustomHeader.DATA_TYPE) String dataType)
            throws DataProcessingConsumerException {
        try {
            AuthUser profile = this.sessionProfileService.getSessionProfile("data-processing");
            AuthUtil.setGlobalAuthUser(profile);
            managerService.processDistribution(dataType,message);
            kafkaManagerProducer.sendData(healthCaseTopic, "result");
        } catch (DataProcessingConsumerException e) {
            kafkaManagerProducer.sendData(logTopic, "result");
        }
    }
}
