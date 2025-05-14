package gov.cdc.dataprocessing.kafka.consumer;

import gov.cdc.dataprocessing.cache.OdseCache;
import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.service.implementation.manager.ManagerService;
import gov.cdc.dataprocessing.service.interfaces.auth_user.IAuthUserService;
import gov.cdc.dataprocessing.service.interfaces.lookup_data.ILookupService;
import gov.cdc.dataprocessing.service.interfaces.manager.IManagerService;
import gov.cdc.dataprocessing.service.model.auth_user.AuthUserProfileInfo;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.sql.QueryHelper;
import io.micrometer.core.instrument.util.NamedThreadFactory;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
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
    private static final Queue<Integer> pendingMessages = new ConcurrentLinkedQueue<>();
    @Value("${feature.thread-pool-size}")
    private Integer poolSize = 1;
    @Value("${nbs.user}")
    private String nbsUser = "";

    @Value("${feature.thread-enabled}")
    private boolean threadEnabled = false;


    private final IManagerService managerService;
    private final IAuthUserService authUserService;
    private final QueryHelper queryHelper;
    private final ILookupService lookupService;

    private final ExecutorService executorService;
    private final TransactionTemplate transactionTemplate;


    public KafkaManagerConsumer(
            ManagerService managerService,
            IAuthUserService authUserService, QueryHelper queryHelper, ILookupService lookupService,
            ExecutorService executorService,
            PlatformTransactionManager transactionManager
            ) {
        this.managerService = managerService;
        this.authUserService = authUserService;
        this.queryHelper = queryHelper;
        this.lookupService = lookupService;
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
                Integer nbs = GSON.fromJson(message, Integer.class);
                pendingMessages.add(nbs);
            }

            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Failed to process Kafka message: {}", e.getMessage());
            // Do not ack, Kafka will retry
        }
    }


    @Scheduled(fixedDelay = 30000) // every 10 seconds
//    public void processPendingMessages() {
//        if (pendingMessages.isEmpty()) return;
//        try {
//            Semaphore concurrencyLimiter = new Semaphore(poolSize); // limit to 10 virtual threads
//            while (!pendingMessages.isEmpty()) {
//                Integer nbs = pendingMessages.poll();
//                if (nbs == null) continue;
//
//                Runnable task = () -> {
//                    try {
//                        managerService.processDistribution(nbs);
//                    } catch (DataProcessingConsumerException e) {
//                        log.error("Failed to process: {}", e.getMessage());
//                        // Optionally re-add to queue or log to DLQ
//                    } finally {
//                        if (threadEnabled) {
//                            concurrencyLimiter.release();
//                            System.gc();
//                        }
//                    }
//                };
//
//                if (threadEnabled) {
////                    concurrencyLimiter.acquire(); // block if 10 tasks are already running
//                    concurrencyLimiter.acquireUninterruptibly(); // cap parallel threads
//                    Thread.startVirtualThread(task);
//                } else {
//                    task.run();
//                    System.gc(); // optional; consider removing
//                }
//            }
//
//            if (threadEnabled) {
//                concurrencyLimiter.acquire(poolSize); // Wait for all virtual threads to finish
//                concurrencyLimiter.release(poolSize); // Reset for next scheduled run
//            }
//
//        } catch (Exception e) {
//            log.error("Scheduled processing failed: {}", e.getMessage());
//        } finally {
//            if (!threadEnabled) {
//                System.gc(); // optional; consider removing
//            }
//        }
//    }
    public void processPendingMessages() {
        if (pendingMessages.isEmpty()) return;

        if (threadEnabled) {
            Semaphore concurrencyLimiter = new Semaphore(poolSize); // e.g. 10
            int batchSize = 100;
            List<Integer> batch = new ArrayList<>(batchSize);

            while (!pendingMessages.isEmpty()) {
                batch.clear();
                for (int i = 0; i < batchSize && !pendingMessages.isEmpty(); i++) {
                    Integer nbs = pendingMessages.poll();
                    if (nbs != null) batch.add(nbs);
                }
                if (batch.isEmpty()) continue;

                concurrencyLimiter.acquireUninterruptibly();

                Thread.startVirtualThread(() -> {
                    try {
                        for (Integer nbs : batch) {
                            managerService.processDistribution(nbs);
                        }
                    } catch (Exception e) {
                        log.error("Error in batch processing: {}", e.getMessage());
                    } finally {
                        concurrencyLimiter.release();
                    }
                });
            }
        } else {
            // Single-threaded fallback — sequentially process each record
            while (!pendingMessages.isEmpty()) {
                Integer nbs = pendingMessages.poll();
                if (nbs == null) continue;

                try {
                    managerService.processDistribution(nbs);
                } catch (DataProcessingConsumerException e) {
                    log.error("Single-threaded error: {}", e.getMessage());
                }
            }
        }
    }

    @PostConstruct
    public void init() throws DataProcessingException {
        // Ensure this runs first at startup
        AuthUserProfileInfo profile = authUserService.getAuthUserInfo(nbsUser);
        AuthUtil.setGlobalAuthUser(profile);

        OdseCache.OWNER_LIST_HASHED_PA_J = queryHelper.getHashedPAJList(false);
        OdseCache.GUEST_LIST_HASHED_PA_J = queryHelper.getHashedPAJList(true);

        OdseCache.DMB_QUESTION_MAP = lookupService.getDMBQuestionMapAfterPublish();

        logger.info("Completed Initializing");

    }


    @Scheduled(fixedDelay = 1800000) // every 30 min
    public void populateAuthUser() throws DataProcessingException {
        AuthUserProfileInfo profile = authUserService.getAuthUserInfo(nbsUser);
        AuthUtil.setGlobalAuthUser(profile);
        logger.info("Completed populateAuthUser");
    }



    @Scheduled(fixedDelay = 3600000) // every 1 hr
    public void populateHashPAJList() throws DataProcessingException {
        logger.info("Started populateHashPAJList");
        OdseCache.OWNER_LIST_HASHED_PA_J =  queryHelper.getHashedPAJList(false);
        OdseCache.GUEST_LIST_HASHED_PA_J = queryHelper.getHashedPAJList(true);
        logger.info("Completed populateHashPAJList");
    }

    @Scheduled(fixedDelay = 3600000) // every 1 hr
    public void populateDMBQuestionMap() {
        logger.info("Started populateDMBQuestionMap");
        OdseCache.DMB_QUESTION_MAP = lookupService.getDMBQuestionMapAfterPublish();
        logger.info("Completed populateDMBQuestionMap");
    }


}



