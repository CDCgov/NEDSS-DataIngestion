package gov.cdc.dataprocessing.kafka.consumer;

import gov.cdc.dataprocessing.service.interfaces.auth_user.IAuthUserService;
import gov.cdc.dataprocessing.service.interfaces.manager.IManagerService;
import gov.cdc.dataprocessing.service.model.phc.PublicHealthCaseFlowContainer;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static gov.cdc.dataprocessing.utilities.GsonUtil.GSON;

@Service
@Slf4j
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
 1118 - Private constructor complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118"})
public class KafkaHandleLabConsumer {
    private static final Logger logger = LoggerFactory.getLogger(KafkaHandleLabConsumer.class); //NOSONAR

    @Value("${nbs.user}")
    private String nbsUser = "";

    private final IManagerService managerService;
    private final IAuthUserService authUserService;


    public KafkaHandleLabConsumer(
                                  IManagerService managerService,
                                  IAuthUserService authUserService) {
        this.managerService = managerService;
        this.authUserService = authUserService;
    }

    @KafkaListener(
            topics = "${kafka.topic.elr_handle_lab}"
    )
    public void handleMessage(String message) {
        try {
            var auth = authUserService.getAuthUserInfo(nbsUser);
            AuthUtil.setGlobalAuthUser(auth);
            PublicHealthCaseFlowContainer publicHealthCaseFlowContainer = GSON.fromJson(message, PublicHealthCaseFlowContainer.class);
            managerService.initiatingLabProcessing(publicHealthCaseFlowContainer);
        } catch (Exception e) {
            logger.error("KafkaHandleLabConsumer.handleMessage: {}", e.getMessage());
        }
    }

}
