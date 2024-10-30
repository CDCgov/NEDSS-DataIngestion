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
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 2139 - exception rethrow complain
 3740 - parametrized  type for generic complaint
 1149 - replacing HashTable complaint
 112 - throwing dedicate exception complaint
 107 - max parameter complaint
 1195 - duplicate complaint
 1135 - Todos complaint
 6201 - instanceof check
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201"})
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

    @KafkaListener(
            topics = "${kafka.topic.elr_health_case}"
    )
    public void handleMessageForPublicHealthCase(String message) {
        try {
            var profile = authUserService.getAuthUserInfo(nbsUser);
            AuthUtil.setGlobalAuthUser(profile);
            PublicHealthCaseFlowContainer publicHealthCaseFlowContainer = GSON.fromJson(message, PublicHealthCaseFlowContainer.class);
            managerService.initiatingInvestigationAndPublicHealthCase(publicHealthCaseFlowContainer);
        } catch (Exception e) {
            logger.error("KafkaPublicHealthCaseConsumer.handleMessageForPublicHealthCase: {}", e.getMessage());
        }
    }
}
