package gov.cdc.dataprocessing.service.implementation.uid_generator;

import gov.cdc.dataprocessing.constant.enums.LocalIdClass;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.uid.LocalUidGeneratorDto;
import gov.cdc.dataprocessing.model.dto.uid.LocalUidModel;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.locator.LocalUidGeneratorRepository;
import gov.cdc.dataprocessing.service.interfaces.uid_generator.IOdseIdGeneratorWCacheService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class UidPoolManager {

    private final IOdseIdGeneratorWCacheService uidGeneratorService;
    private final LocalUidGeneratorRepository localUidGeneratorRepository;
    private final Map<Boolean, Queue<LocalUidModel>> personUidPool = new HashMap<>();
    private final Map<Boolean, ReentrantLock> poolLocks = new HashMap<>();
    private static final int POOL_SIZE = 5000;
    private final LocalIdClass localIdClass = LocalIdClass.PERSON; // or make dynamic

    @Autowired
    public UidPoolManager(IOdseIdGeneratorWCacheService uidGeneratorService, LocalUidGeneratorRepository localUidGeneratorRepository) {
        this.uidGeneratorService = uidGeneratorService;
        this.localUidGeneratorRepository = localUidGeneratorRepository;
        personUidPool.put(true, new ArrayDeque<>());
        personUidPool.put(false, new ArrayDeque<>());
        poolLocks.put(true, new ReentrantLock());
        poolLocks.put(false, new ReentrantLock());
    }

    @PostConstruct
    public void initializePools() {
        try {
            System.out.println("Person UID pool initialized.");
            preloadPool(true);
            preloadPool(false);
            System.out.println("Person UID pool completed.");
        } catch (Exception e) {
            throw new IllegalStateException("Failed to preload person UID pools at startup", e);
        }
    }

    public LocalUidModel getNextUid(boolean gaApplied) throws DataProcessingException {
        Queue<LocalUidModel> pool = personUidPool.get(gaApplied);
        ReentrantLock lock = poolLocks.get(gaApplied);

        lock.lock();
        try {
            if (pool.isEmpty()) {
                preloadPool(gaApplied);
            }
            LocalUidModel uidModel = pool.poll();
            if (uidModel == null) {
                throw new DataProcessingException("Person UID pool exhausted unexpectedly.");
            }
            return uidModel;
        } finally {
            lock.unlock();
        }
    }

    private void preloadPool(boolean gaApplied) throws DataProcessingException {
        Queue<LocalUidModel> pool = personUidPool.get(gaApplied);

        long startSeed = localUidGeneratorRepository
                .reserveBatchAndGetStartSeed(localIdClass.name(), POOL_SIZE);

        long gaStartSeed = gaApplied
                ? localUidGeneratorRepository
                .reserveBatchAndGetStartSeed(LocalIdClass.GA.name(), POOL_SIZE)
                : 0;

        for (int i = 0; i < POOL_SIZE; i++) {
            LocalUidModel model = new LocalUidModel();

            var localDto = new LocalUidGeneratorDto();
            localDto.setSeedValueNbr(startSeed + i);
            localDto.setClassNameCd(localIdClass.name());
            localDto.setCounter(POOL_SIZE);
            localDto.setUsedCounter(1);

            model.setClassTypeUid(localDto);

            if (gaApplied) {
                var gaDto = new LocalUidGeneratorDto();
                gaDto.setSeedValueNbr(gaStartSeed + i);
                gaDto.setClassNameCd(LocalIdClass.GA.name());
                gaDto.setCounter(POOL_SIZE);
                gaDto.setUsedCounter(1);
                model.setGaTypeUid(gaDto);
            }

            model.setPrimaryClassName(localIdClass.name());
            pool.offer(model);
        }
    }

//    private void preloadPool(boolean gaApplied) throws DataProcessingException {
//        Queue<LocalUidModel> pool = personUidPool.get(gaApplied);
//        for (int i = 0; i < POOL_SIZE; i++) {
//            pool.offer(uidGeneratorService.getValidLocalUid(localIdClass, gaApplied));
//        }
//    }
}