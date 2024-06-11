package gov.cdc.dataprocessing.kafka.consumer;

import gov.cdc.dataprocessing.kafka.producer.KafkaManagerProducer;
import gov.cdc.dataprocessing.service.interfaces.auth_user.IAuthUserService;
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
public class KafkaPublicHealthCaseConsumer {
    private static final Logger logger = LoggerFactory.getLogger(KafkaPublicHealthCaseConsumer.class);

    @Value("${kafka.topic.elr_handle_lab}")
    private String handleLabTopic = "elr_processing_handle_lab";
    @Value("${kafka.topic.elr_edx_log}")
    private String logTopic = "elr_edx_log";
    private final KafkaManagerProducer kafkaManagerProducer;
    private final IManagerService managerService;
    private final IAuthUserService authUserService;

    public KafkaPublicHealthCaseConsumer(
            KafkaManagerProducer kafkaManagerProducer,
            IManagerService managerService, IAuthUserService authUserService) {

        this.kafkaManagerProducer = kafkaManagerProducer;
        this.managerService = managerService;
        this.authUserService = authUserService;

    }

    @KafkaListener(
            topics = "${kafka.topic.elr_health_case}"
    )
    public void handleMessageForPublicHealthCase(String message,
                                     @Header(KafkaHeaders.RECEIVED_TOPIC) String topic)
    {
        try {

            var profile = this.authUserService.getAuthUserInfo("superuser");
            AuthUtil.setGlobalAuthUser(profile);
            managerService.initiatingInvestigationAndPublicHealthCase(message);
        }
        catch (Exception e)
        {
             e.printStackTrace();
        }
    }

}
