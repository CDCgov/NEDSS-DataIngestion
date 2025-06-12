package gov.cdc.dataprocessing.service.implementation.uid_generator;

import gov.cdc.dataprocessing.constant.enums.LocalIdClass;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.uid.LocalUidGeneratorDto;
import gov.cdc.dataprocessing.model.dto.uid.LocalUidModel;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.locator.LocalUidGeneratorRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class UidPoolManager {
    private static final Logger logger = LoggerFactory.getLogger(UidPoolManager.class);
    private final LocalUidGeneratorRepository localUidGeneratorRepository;
    protected final Map<String, Queue<LocalUidModel>> uidPools = new ConcurrentHashMap<>();
    private final Map<String, AtomicBoolean> refillInProgress = new ConcurrentHashMap<>();

    @Value("${uid.pool_size}")
    private int POOL_SIZE = 5000;
    @Value("${uid.min_pool_size}")
    private int LOW_WATERMARK = 1000;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Autowired
    public UidPoolManager(LocalUidGeneratorRepository localUidGeneratorRepository) {
        this.localUidGeneratorRepository = localUidGeneratorRepository;
    }

    @PostConstruct
    public void initializePools() throws DataProcessingException {
        for (LocalIdClass idClass : LocalIdClass.values()) {
            preloadPool(idClass, false);
            preloadPool(idClass, true);
        }
        logger.info("All UID pools initialized.");
    }

    @Scheduled(fixedDelay = 60000)
    public void periodicRefill() {
        for (Map.Entry<String, Queue<LocalUidModel>> entry : uidPools.entrySet()) {
            String key = entry.getKey();
            Queue<LocalUidModel> pool = entry.getValue();
            boolean gaApplied = key.startsWith(LocalIdClass.GA.name() + "|");
            LocalIdClass idClass = gaApplied
                    ? LocalIdClass.valueOf(key.split("\\|")[1])
                    : LocalIdClass.valueOf(key);

            if (pool.size() < LOW_WATERMARK) {
                triggerAsyncRefill(idClass, gaApplied, key);
            }
        }
    }

    public LocalUidModel getNextUid(LocalIdClass idClass, boolean gaApplied) throws DataProcessingException {
        String key = getKey(idClass, gaApplied);
        uidPools.putIfAbsent(key, new ConcurrentLinkedQueue<>());
        refillInProgress.putIfAbsent(key, new AtomicBoolean(false));

        Queue<LocalUidModel> pool = uidPools.get(key);

        if (pool.isEmpty()) {
            triggerAsyncRefill(idClass, gaApplied, key);
            getNextUid(idClass, gaApplied);
        }

        if (pool.size() < LOW_WATERMARK) {
            triggerAsyncRefill(idClass, gaApplied, key);
        }

        LocalUidModel uidModel = pool.poll();
        if (uidModel == null) {
            throw new DataProcessingException("UID pool exhausted unexpectedly.");
        }
        return uidModel;
    }

    private void triggerAsyncRefill(LocalIdClass idClass, boolean gaApplied, String key) {
        if (refillInProgress.get(key).compareAndSet(false, true)) {
            executorService.submit(() -> {
                try {
                    preloadPool(idClass, gaApplied);
                } catch (Exception ignored) {
                    // IGNORE THIS EXCEPTION
                } finally {
                    refillInProgress.get(key).set(false);
                }
            });
        }
    }

    private void preloadPool(LocalIdClass idClass, boolean gaApplied) throws DataProcessingException {
        String key = getKey(idClass, gaApplied);
        uidPools.putIfAbsent(key, new ConcurrentLinkedQueue<>());
        refillInProgress.putIfAbsent(key, new AtomicBoolean(false));

        Queue<LocalUidModel> pool = uidPools.get(key);

        var localStartSeed = localUidGeneratorRepository.reserveBatchAndGetStartSeed(idClass.name(), POOL_SIZE);
        var gaStartSeed = gaApplied ? localUidGeneratorRepository.reserveBatchAndGetStartSeed(LocalIdClass.GA.name(), POOL_SIZE) : null;

        if (localStartSeed == null) {
            throw new DataProcessingException("Failed to reserve UID batch from DB for " + idClass.name());
        }

        for (int i = 0; i < POOL_SIZE; i++) {
            LocalUidModel model = new LocalUidModel();

            LocalUidGeneratorDto localDto = new LocalUidGeneratorDto();
            localDto.setSeedValueNbr(localStartSeed.getSeedValueNbr() + i);
            localDto.setClassNameCd(localStartSeed.getClassNameCd());
            localDto.setCounter(POOL_SIZE);
            localDto.setUsedCounter(1);
            localDto.setUidPrefixCd(localStartSeed.getUidPrefixCd());
            localDto.setUidSuffixCd(localStartSeed.getUidSuffixCd());

            model.setClassTypeUid(localDto);

            if (gaApplied && gaStartSeed != null) {
                LocalUidGeneratorDto gaDto = new LocalUidGeneratorDto();
                gaDto.setSeedValueNbr(gaStartSeed.getSeedValueNbr() + i);
                gaDto.setClassNameCd(gaStartSeed.getClassNameCd());
                gaDto.setCounter(POOL_SIZE);
                gaDto.setUsedCounter(1);
                gaDto.setUidPrefixCd(gaStartSeed.getUidPrefixCd());
                gaDto.setUidSuffixCd(gaStartSeed.getUidSuffixCd());
                model.setGaTypeUid(gaDto);
            }

            model.setPrimaryClassName(idClass.name());
            pool.offer(model);
        }
    }

    private String getKey(LocalIdClass idClass, boolean gaApplied) {
        return gaApplied ? (LocalIdClass.GA.name() + "|" + idClass.name()) : idClass.name();
    }
}
