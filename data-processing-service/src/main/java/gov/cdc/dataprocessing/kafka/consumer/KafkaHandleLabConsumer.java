package gov.cdc.dataprocessing.kafka.consumer;

import com.google.gson.Gson;
import gov.cdc.dataprocessing.kafka.producer.KafkaManagerProducer;
import gov.cdc.dataprocessing.service.interfaces.auth_user.IAuthUserService;
import gov.cdc.dataprocessing.service.interfaces.manager.IManagerService;
import gov.cdc.dataprocessing.service.model.phc.PublicHealthCaseFlowContainer;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

import static gov.cdc.dataprocessing.utilities.GsonUtil.GSON;

@Service
@Slf4j
public class KafkaHandleLabConsumer {
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
            e.printStackTrace();
        }
    }

}
