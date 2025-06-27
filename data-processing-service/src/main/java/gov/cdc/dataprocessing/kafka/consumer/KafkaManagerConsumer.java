package gov.cdc.dataprocessing.kafka.consumer;

import com.google.gson.Gson;
import gov.cdc.dataprocessing.cache.DpStatic;
import gov.cdc.dataprocessing.cache.OdseCache;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.service.implementation.manager.ManagerService;
import gov.cdc.dataprocessing.service.implementation.uid_generator.UidPoolManager;
import gov.cdc.dataprocessing.service.interfaces.auth_user.IAuthUserService;
import gov.cdc.dataprocessing.service.interfaces.lookup_data.ILookupService;
import gov.cdc.dataprocessing.service.interfaces.manager.IManagerService;
import gov.cdc.dataprocessing.service.interfaces.manager.IManagerTransactionService;
import gov.cdc.dataprocessing.service.model.auth_user.AuthUserProfileInfo;
import gov.cdc.dataprocessing.service.model.phc.PublicHealthCaseFlowContainer;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.sql.QueryHelper;
import jakarta.annotation.PostConstruct;
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
public class KafkaManagerConsumer {
    private static final Logger logger = LoggerFactory.getLogger(KafkaManagerConsumer.class);
    private static final Queue<Integer> pendingMessages = new ConcurrentLinkedQueue<>();
    @Value("${feature.thread-pool-size}")
    private Integer poolSize = 1;
    @Value("${nbs.user}")
    private String nbsUser = "";
    @Value("${feature.thread-batch-size}")
    private Integer batchSize = 50;

    @Value("${feature.thread-enabled}")
    private boolean threadEnabled = false;


    private final IManagerService managerService;
    private final IManagerTransactionService managerTransactionService;
    private final IAuthUserService authUserService;
    private final QueryHelper queryHelper;
    private final ILookupService lookupService;
    private final UidPoolManager uidPoolManager;


    public KafkaManagerConsumer(
            ManagerService managerService, IManagerTransactionService managerTransactionService,
            IAuthUserService authUserService,
            QueryHelper queryHelper,
            ILookupService lookupService, UidPoolManager uidPoolManager
    ) {
        this.managerService = managerService;
        this.managerTransactionService = managerTransactionService;
        this.authUserService = authUserService;
        this.queryHelper = queryHelper;
        this.lookupService = lookupService;
        this.uidPoolManager = uidPoolManager;
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

    @KafkaListener(
            topics = "${kafka.topic.elr_unprocessed}",
            containerFactory = "kafkaListenerContainerFactoryStep1",
            batch = "true"
    )
    public void handleMessage(List<String> messages, Acknowledgment acknowledgment) {
        try {
            AuthUserProfileInfo profile = authUserService.getAuthUserInfo(nbsUser);
            AuthUtil.setGlobalAuthUser(profile);
            List<Integer> ids = new ArrayList<>();
            for (String message : messages) {
                Integer nbs = GSON.fromJson(message, Integer.class);
                ids.add(nbs);
                pendingMessages.add(nbs);
            }

            // Update the status to RTI processing for tracking
            managerService.updateNbsInterfaceStatus(ids);
            logger.debug("[KafkaManagerConsumer] pending {} messages", messages.size());
            acknowledgment.acknowledge();
        }
        catch (Exception e)
        {
            log.error("Failed to process Kafka message: {}", e.getMessage());
            // Do not ack, Kafka will retry
        }
    }

    @KafkaListener(
            topics = "${kafka.topic.elr_reprocessing_locking}",
            containerFactory = "kafkaListenerContainerFactoryDltStep1"
    )
    public void handleDltMessageUnifiedForLockingException(String message, Acknowledgment acknowledgment)  {
        if (!pendingMessages.isEmpty()) {
            log.info("Skipping due to active processing. Will be retried.");
            return;
        }

        try {
            // First, try parsing as Integer
            try {
                Integer id = Integer.valueOf(message);
                var result = managerService.processingELR(id, false);
                if (result != null) {
                    managerService.handlingWdsAndLab(result, false);
                }
            }
            catch (NumberFormatException ex) {
                // If it's not an integer, try parsing as JSON object
                Gson consumerGson = new Gson();
                PublicHealthCaseFlowContainer phc = consumerGson.fromJson(message, PublicHealthCaseFlowContainer.class);
                managerService.handlingWdsAndLab(phc, false);
            }

            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Failed to process handleDltMessageUnifiedForLockingException: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(
            topics = "${kafka.topic.elr_reprocessing_data_integrity}",
            containerFactory = "kafkaListenerContainerFactoryDltStep1"
    )
    public void handleDltMessageUnifiedForDataIntegrityException(String message, Acknowledgment acknowledgment) throws DataProcessingException {
        if (!pendingMessages.isEmpty()) {
            log.info("Skipping due to active processing. Will be retried.");
            return;
        }

        if (DpStatic.isUuidPoolInitialized()) {
            // Reinitialize the uuid pool
            uidPoolManager.initializePools();
            DpStatic.setUuidPoolInitialized(false);
        }


        try {
            // First, try parsing as Integer
            try {
                Integer id = Integer.valueOf(message);
                var result = managerService.processingELR(id, true);
                if (result != null) {
                    managerService.handlingWdsAndLab(result, true);
                }
            }
            catch (NumberFormatException ex) {
                // If it's not an integer, try parsing as JSON object
                Gson consumerGson = new Gson();
                PublicHealthCaseFlowContainer phc = consumerGson.fromJson(message, PublicHealthCaseFlowContainer.class);
                managerService.handlingWdsAndLab(phc, true);
            }

            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Failed to process handleDltMessageUnifiedForLockingException: {}", e.getMessage(), e);
        }
    }

    @Scheduled(fixedDelayString = "${processor.delay_ms:30000}")
    public void processPendingMessages() {
        logger.debug("BATCH SIZE: {}", pendingMessages.size());
        if (pendingMessages.isEmpty()) return;

        if (threadEnabled) {
            Semaphore concurrencyLimiter = new Semaphore(poolSize); // Same as Hikari max pool size

            while (true) {
                List<Integer> batch = new ArrayList<>(batchSize);
                Integer nbs;
                while (batch.size() < batchSize && (nbs = pendingMessages.poll()) != null) {
                    batch.add(nbs);
                }
                if (batch.isEmpty()) break;

                concurrencyLimiter.acquireUninterruptibly();
                Thread.startVirtualThread(() -> {
                    try {
                        for (Integer id : batch) {
                            try {
                                managerTransactionService.processWithTransactionSeparation(id);
                            } catch (Exception e) {
                                log.error("Error processing NBS {}: {}", id, e.getMessage(), e);
                            }

                        }
                    } finally {
                        concurrencyLimiter.release();
                    }
                });
            }
        }
        else
        {
            // Single-threaded fallback
            while (!pendingMessages.isEmpty()) {
                Integer nbs = pendingMessages.poll();
                if (nbs == null) continue;

                try {
                    var result = managerService.processingELR(nbs, false);
                    if (result != null) {
                        managerService.handlingWdsAndLab(result, false);
                    }
                } catch (Exception e) {
                    log.error("Single-threaded error: {}", e.getMessage(), e);
                }
            }
        }
    }



}



