package gov.cdc.dataprocessing.kafka.consumer;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.service.implementation.manager.ManagerService;
import gov.cdc.dataprocessing.service.interfaces.auth_user.IAuthUserService;
import gov.cdc.dataprocessing.service.interfaces.manager.IManagerService;
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
public class KafkaManagerConsumer {
    private static final Logger logger = LoggerFactory.getLogger(KafkaManagerConsumer.class);


    @Value("${kafka.topic.elr_edx_log}")
    private String logTopic = "elr_edx_log";

    @Value("${kafka.topic.elr_health_case}")
    private String healthCaseTopic = "elr_processing_public_health_case";

    @Value("${nbs.user}")
    private String nbsUser = "";


    private final IManagerService managerService;
    private final IAuthUserService authUserService;

    public KafkaManagerConsumer(
            ManagerService managerService,
            IAuthUserService authUserService) {
        this.managerService = managerService;
        this.authUserService = authUserService;

    }

    @KafkaListener(
            topics = "${kafka.topic.elr_micro}"
    )
    public void handleMessage(String messages)
            throws DataProcessingException {
        var profile = authUserService.getAuthUserInfo(nbsUser);
        AuthUtil.setGlobalAuthUser(profile);

        try {
            var nbs = GSON.fromJson(messages, Integer.class);
            managerService.processDistribution(nbs);
        } catch (Exception e) {
            log.error("KafkaManagerConsumer.handleMessage: {}", e.getMessage());
        }

    }

}
