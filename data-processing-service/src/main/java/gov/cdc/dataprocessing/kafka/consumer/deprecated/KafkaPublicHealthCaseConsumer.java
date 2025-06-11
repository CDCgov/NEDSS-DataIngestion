package gov.cdc.dataprocessing.kafka.consumer.deprecated;

import gov.cdc.dataprocessing.service.interfaces.auth_user.IAuthUserService;
import gov.cdc.dataprocessing.service.interfaces.manager.IManagerService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaPublicHealthCaseConsumer {
    private static final Logger logger = LoggerFactory.getLogger(KafkaPublicHealthCaseConsumer.class); // NOSONAR

    private final IManagerService managerService;
    private final IAuthUserService authUserService;


    @Value("${nbs.user}")
    private String nbsUser = "";

    public KafkaPublicHealthCaseConsumer(
            IManagerService managerService, IAuthUserService authUserService) {

        this.managerService = managerService;
        this.authUserService = authUserService;

    }

//    @KafkaListener(
//            topics = "${kafka.topic.elr_health_case}",
//            containerFactory = "kafkaListenerContainerFactoryStep2"
//    )
//    public void handleMessageForPublicHealthCase(String message, Acknowledgment acknowledgment) {
//        try {
//            var profile = authUserService.getAuthUserInfo(nbsUser);
//            AuthUtil.setGlobalAuthUser(profile);
//            PublicHealthCaseFlowContainer publicHealthCaseFlowContainer = GSON.fromJson(message, PublicHealthCaseFlowContainer.class);
//            managerService.initiatingInvestigationAndPublicHealthCase(publicHealthCaseFlowContainer);
//            acknowledgment.acknowledge();
//        } catch (Exception e) {
//            logger.error("KafkaPublicHealthCaseConsumer.handleMessageForPublicHealthCase: {}", e.getMessage());
//        }
//    }
}
