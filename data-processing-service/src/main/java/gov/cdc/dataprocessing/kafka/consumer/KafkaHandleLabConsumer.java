package gov.cdc.dataprocessing.kafka.consumer;

import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.kafka.producer.KafkaManagerProducer;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.service.implementation.auth.SessionProfileService;
import gov.cdc.dataprocessing.service.interfaces.manager.IManagerService;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaHandleLabConsumer {
    @Value("${kafka.topic.elr_edx_log}")
    private String logTopic = "elr_edx_log";

    private final KafkaManagerProducer kafkaManagerProducer;
    private final IManagerService managerService;
    private final SessionProfileService sessionProfileService;


    public KafkaHandleLabConsumer(KafkaManagerProducer kafkaManagerProducer,
                                  IManagerService managerService, SessionProfileService sessionProfileService) {
        this.kafkaManagerProducer = kafkaManagerProducer;
        this.managerService = managerService;
        this.sessionProfileService = sessionProfileService;

        AuthUser profile = this.sessionProfileService.getSessionProfile("data-processing");
        AuthUtil.setGlobalAuthUser(profile);
    }

    @KafkaListener(
            topics = "${kafka.topic.elr_handle_lab}"
    )
    public void handleMessage(String message)
            throws DataProcessingConsumerException {
        try {

            AuthUser profile = this.sessionProfileService.getSessionProfile("data-processing");
            AuthUtil.setGlobalAuthUser(profile);

            managerService.initiatingLabProcessing(message);
        }
        catch (Exception e)
        {
            kafkaManagerProducer.sendData(logTopic, "result");
        }
    }
}
