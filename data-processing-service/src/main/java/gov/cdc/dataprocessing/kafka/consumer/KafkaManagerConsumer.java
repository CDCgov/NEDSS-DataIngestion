package gov.cdc.dataprocessing.kafka.consumer;

import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.service.implementation.manager.ManagerService;
import gov.cdc.dataprocessing.service.interfaces.auth_user.IAuthUserService;
import gov.cdc.dataprocessing.service.interfaces.manager.IManagerService;
import gov.cdc.dataprocessing.service.model.auth_user.AuthUserProfileInfo;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import io.micrometer.core.instrument.util.NamedThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

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
 1192 - duplicate literal
 135 - for loop
 117 - naming
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201", "java:S1192", "java:S135", "java:S117"})
public class KafkaManagerConsumer {
    private static final Logger logger = LoggerFactory.getLogger(KafkaManagerConsumer.class);

    @Value("${nbs.user}")
    private String nbsUser = "";

    private final IManagerService managerService;
    private final IAuthUserService authUserService;

    private final ExecutorService executorService;
    private final TransactionTemplate transactionTemplate;


    public KafkaManagerConsumer(
            ManagerService managerService,
            IAuthUserService authUserService,
            ExecutorService executorService,
            PlatformTransactionManager transactionManager
            ) {
        this.managerService = managerService;
        this.authUserService = authUserService;
        this.executorService = executorService;
        this.transactionTemplate = new TransactionTemplate(transactionManager);


    }

    @KafkaListener(
            topics = "${kafka.topic.elr_micro}",
            containerFactory = "kafkaListenerContainerFactoryStep1",
            batch = "true"
    )

    public void handleMessage(List<String> messages, Acknowledgment acknowledgment) {
        try {
            AuthUserProfileInfo profile = authUserService.getAuthUserInfo(nbsUser);
            AuthUtil.setGlobalAuthUser(profile);
            for (String message : messages) {


//                executorService.submit(() -> {
//                    try {
//                        Integer nbs = GSON.fromJson(message, Integer.class);
//                        managerService.processDistribution(nbs);
//                    } catch (DataProcessingConsumerException e) {
//                        log.error("Failed to process Kafka message: {}", e.getMessage());
//                    }
//                });

                while (true) {
                    try {
                        executorService.submit(() -> {
                            try {
                                Integer nbs = GSON.fromJson(message, Integer.class);
                                managerService.processDistribution(nbs);
                            } catch (DataProcessingConsumerException e) {
                                log.error("Failed to process Kafka message: {}", e.getMessage());
                            }
                        });
                        System.gc();

                        break; // success, move to next message
                    } catch (RejectedExecutionException e) {
                        // Wait a short period to retry
                        Thread.sleep(100); // small pause to let queue drain
                    }
                }

            }

            acknowledgment.acknowledge();

        } catch (Exception e) {
            log.error("Failed to process Kafka message: {}", e.getMessage());
            // Kafka will retry (no acknowledge)
        }

//        executorService.submit(() -> {
//            try {
//                AuthUserProfileInfo profile = authUserService.getAuthUserInfo(nbsUser);
//
//                for (String message : messages) {
//                    transactionTemplate.executeWithoutResult(status -> {
//                        Integer nbs = GSON.fromJson(message, Integer.class);
//                        AuthUtil.setGlobalAuthUser(profile);
//                        try {
//                            managerService.processDistribution(nbs);
//                        } catch (DataProcessingConsumerException e) {
//                            log.error("Failed to process Kafka message: {}", e.getMessage());
//                        }
//                    });
//                }
//
//                acknowledgment.acknowledge();
//
//            } catch (Exception e) {
//                log.error("Failed to process Kafka message: {}", e.getMessage());
//                // Kafka will retry (no acknowledge)
//            }
//        });
    }

}



