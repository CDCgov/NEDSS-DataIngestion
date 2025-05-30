package gov.cdc.dataprocessing.kafka.consumer;

import gov.cdc.dataprocessing.service.implementation.manager.LabService;
import gov.cdc.dataprocessing.service.interfaces.auth_user.IAuthUserService;
import gov.cdc.dataprocessing.service.interfaces.manager.IManagerService;
import gov.cdc.dataprocessing.service.model.phc.NndKafkaContainer;
import gov.cdc.dataprocessing.service.model.phc.PublicHealthCaseFlowContainer;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

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


    private final IManagerService managerService;
    private final IAuthUserService authUserService;
    private final LabService labService;

    private static final Queue<PublicHealthCaseFlowContainer> pendingMessages = new ConcurrentLinkedQueue<>();
    private static final Queue<NndKafkaContainer> pendingNndMessages = new ConcurrentLinkedQueue<>();

    @Value("${feature.thread-pool-size}")
    private Integer poolSize = 1;
    @Value("${nbs.user}")
    private String nbsUser = "";
    @Value("${feature.thread-batch-size}")
    private Integer batchSize = 50;

    @Value("${feature.thread-enabled}")
    private boolean threadEnabled = false;
    public KafkaHandleLabConsumer(
            IManagerService managerService,
            IAuthUserService authUserService,
            LabService labService
    ) {
        this.managerService = managerService;
        this.authUserService = authUserService;
        this.labService = labService;
    }

//    @KafkaListener(
//            topics = "${kafka.topic.elr_handle_lab}",
//            containerFactory = "kafkaListenerContainerFactoryStep2",
//            batch = "true"
//    )
//    public void handleMessage(List<String> messages, Acknowledgment acknowledgment) {
//        try {
//            AuthUserProfileInfo profile = authUserService.getAuthUserInfo(nbsUser);
//            AuthUtil.setGlobalAuthUser(profile);
//            Gson GSON = new Gson();
//            for (String message : messages) {
//
//                PublicHealthCaseFlowContainer nbs = GSON.fromJson(message, PublicHealthCaseFlowContainer.class);
//                pendingMessages.add(nbs);
//            }
//
//            acknowledgment.acknowledge();
//        } catch (Exception e) {
//            log.error("Failed to process Kafka message: {}", e.getMessage());
//            // Do not ack, Kafka will retry
//        }
//    }
//
//    @Scheduled(fixedDelayString = "${processor.delay_ms:30000}")
//    public void processPendingMessages() {
//        logger.debug("BATCH SIZE: {}", pendingMessages.size());
//        if (pendingMessages.isEmpty()) return;
//
//        if (threadEnabled) {
//            Semaphore concurrencyLimiter = new Semaphore(poolSize); // Same as Hikari max pool size
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
//                        for (PublicHealthCaseFlowContainer data : batch) {
//                            try {
//                                managerService.initiatingLabProcessing(data);
//                            } catch (Exception e) {
//                                log.error("Error processing NBS {}: {}", data, e.getMessage(), e);
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
//                    logger.info("Start Step 2");
//                    managerService.initiatingLabProcessing(nbs);
//                    logger.info("Completed Step 2");
//                } catch (Exception e) {
//                    log.error("Single-threaded error: {}", e.getMessage(), e);
//                }
//            }
//        }
//    }
//
//
//
//    @KafkaListener(
//            topics = "${kafka.topic.elr_nnd}",
//            containerFactory = "kafkaListenerContainerFactoryStep3",
//            batch = "true"
//    )
//    public void handleMessageForNnd(List<String> messages, Acknowledgment acknowledgment) {
//        try {
//            AuthUserProfileInfo profile = authUserService.getAuthUserInfo(nbsUser);
//            AuthUtil.setGlobalAuthUser(profile);
//            Gson GSON = new Gson();
//            for (String message : messages) {
//
//                NndKafkaContainer nbs = GSON.fromJson(message, NndKafkaContainer.class);
//                pendingNndMessages.add(nbs);
//            }
//
//            acknowledgment.acknowledge();
//        } catch (Exception e) {
//            log.error("Failed to process Kafka message: {}", e.getMessage());
//            // Do not ack, Kafka will retry
//        }
//    }
//
//    @Scheduled(fixedDelayString = "${processor.delay_ms:30000}")
//    public void processNndPendingMessages() {
//        logger.debug("BATCH SIZE: {}", pendingNndMessages.size());
//        if (pendingNndMessages.isEmpty()) return;
//
//        if (threadEnabled) {
//            Semaphore concurrencyLimiter = new Semaphore(poolSize); // Same as Hikari max pool size
//
//            while (true) {
//                List<NndKafkaContainer> batch = new ArrayList<>(batchSize);
//                NndKafkaContainer nbs;
//                while (batch.size() < batchSize && (nbs = pendingNndMessages.poll()) != null) {
//                    batch.add(nbs);
//                }
//                if (batch.isEmpty()) break;
//
//                concurrencyLimiter.acquireUninterruptibly();
//                Thread.startVirtualThread(() -> {
//                    try {
//                        for (NndKafkaContainer data : batch) {
//                            try {
//                                labService.handleNndNotification(data.getPublicHealthCaseContainer(), data.getEdxLabInformationDto());
//                            } catch (Exception e) {
//                                log.error("Error processing NBS {}: {}", data, e.getMessage(), e);
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
//            while (!pendingNndMessages.isEmpty()) {
//                NndKafkaContainer nbs = pendingNndMessages.poll();
//                if (nbs == null) continue;
//
//                try {
//                    logger.info("Start Step 3");
//                    labService.handleNndNotification(nbs.getPublicHealthCaseContainer(), nbs.getEdxLabInformationDto());
//                    logger.info("Completed Step 3");
//                } catch (Exception e) {
//                    log.error("Single-threaded error: {}", e.getMessage(), e);
//                }
//            }
//        }
//    }




}
