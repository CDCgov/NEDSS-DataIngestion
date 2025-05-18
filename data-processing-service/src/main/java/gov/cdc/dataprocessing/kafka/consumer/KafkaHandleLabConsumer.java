package gov.cdc.dataprocessing.kafka.consumer;

import com.google.gson.Gson;
import gov.cdc.dataprocessing.service.interfaces.auth_user.IAuthUserService;
import gov.cdc.dataprocessing.service.interfaces.manager.IManagerService;
import gov.cdc.dataprocessing.service.model.auth_user.AuthUserProfileInfo;
import gov.cdc.dataprocessing.service.model.phc.PublicHealthCaseFlowContainer;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

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
public class KafkaHandleLabConsumer {
    private static final Logger logger = LoggerFactory.getLogger(KafkaHandleLabConsumer.class); //NOSONAR

    @Value("${nbs.user}")
    private String nbsUser = "";

    private final IManagerService managerService;
    private final IAuthUserService authUserService;
    private static final Queue<PublicHealthCaseFlowContainer> pendingMessages = new ConcurrentLinkedQueue<>();


//    @Value("${feature.thread-enabled}")
    private boolean threadEnabled = false;
    @Value("${feature.thread-pool-size}")
    private Integer poolSize = 1;

    public KafkaHandleLabConsumer(
                                  IManagerService managerService,
                                  IAuthUserService authUserService) {
        this.managerService = managerService;
        this.authUserService = authUserService;
    }

    @KafkaListener(
            topics = "${kafka.topic.elr_handle_lab}",
            containerFactory = "kafkaListenerContainerFactoryStep3",
            batch = "true"
    )
    public void handleMessage(List<String> messages, Acknowledgment acknowledgment) {
        try {
            AuthUserProfileInfo profile = authUserService.getAuthUserInfo(nbsUser);
            AuthUtil.setGlobalAuthUser(profile);
            Gson GSON = new Gson();
            for (String message : messages) {

                PublicHealthCaseFlowContainer nbs = GSON.fromJson(message, PublicHealthCaseFlowContainer.class);
                pendingMessages.add(nbs);
            }

            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Failed to process Kafka message: {}", e.getMessage());
            // Do not ack, Kafka will retry
        }
    }

//    @Scheduled(fixedDelay = 30000) // every 10000 = 10 seconds
//    public void processPendingMessage() {
//        try {
//            PublicHealthCaseFlowContainer publicHealthCaseFlowContainer = GSON.fromJson(message, PublicHealthCaseFlowContainer.class);
//            managerService.initiatingLabProcessing(publicHealthCaseFlowContainer);
//            acknowledgment.acknowledge();
//        } catch (Exception e) {
//            logger.error("KafkaHandleLabConsumer.handleMessage: {}", e.getMessage());
//        }
//    }
//
//    @Scheduled(fixedDelay = 30000) // every 10000 = 10 seconds
//    public void processPendingMessages() {
//        logger.info("BATCH SIZE for STEP 3: {}", pendingMessages.size());
//        if (pendingMessages.isEmpty()) return;
//
//        if (threadEnabled) {
//            Semaphore concurrencyLimiter = new Semaphore(poolSize); // Same as Hikari max pool size
//            int batchSize = 50;
//
//            while (true) {
//                List<PublicHealthCaseFlowContainer> batch = new ArrayList<>(batchSize);
//                PublicHealthCaseFlowContainer nbs;
//                while (batch.size() < batchSize && (nbs = pendingMessages.poll()) != null) {
//                    batch.add(nbs);
//                }
//                if (batch.isEmpty()) break;
//
//                concurrencyLimiter.acquireUninterruptibly();
//                Thread.startVirtualThread(() -> {
//                    try {
////                        managerService.processDataByBatch(batch);
//                        for (PublicHealthCaseFlowContainer id : batch) {
//                            try {
//                                managerService.initiatingLabProcessing(id);
//                            } catch (Exception e) {
//                                log.error("Error processing NBS {}: {}", id, e.getMessage(), e);
//                            }
//                        }
//                    } finally {
//                        concurrencyLimiter.release();
//                    }
//                });
//            }
//        }
//        else
//        {
//            // Single-threaded fallback
//            while (!pendingMessages.isEmpty()) {
//                PublicHealthCaseFlowContainer nbs = pendingMessages.poll();
//                if (nbs == null) continue;
//
//                try {
//                    managerService.initiatingLabProcessing(nbs);
//                } catch (Exception e) {
//                    log.error("Single-threaded error: {}", e.getMessage(), e);
//                }
//            }
//        }
//    }

}
