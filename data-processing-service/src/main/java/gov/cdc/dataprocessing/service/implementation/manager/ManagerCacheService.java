package gov.cdc.dataprocessing.service.implementation.manager;

import gov.cdc.dataprocessing.cache.SrteCache;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.repository.nbs.srte.model.ConditionCode;
import gov.cdc.dataprocessing.service.interfaces.cache.ICatchingValueService;
import gov.cdc.dataprocessing.service.interfaces.manager.IManagerCacheService;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;

@Service
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
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740"})
public class ManagerCacheService implements IManagerCacheService {
    private final ICatchingValueService cachingValueService;
    private final CacheManager cacheManager;

    public ManagerCacheService(ICatchingValueService cachingValueService, CacheManager cacheManager) {
        this.cachingValueService = cachingValueService;
        this.cacheManager = cacheManager;
    }

    @SuppressWarnings({"java:S3776", "java:S112", "java:S2696"})
    public CompletableFuture<Void> loadAndInitCachedValueAsync() {
        return CompletableFuture.runAsync(this::loadCache);
    }

    @SuppressWarnings({"java:S2696", "java:S3776"})
    private void loadCache() {
        runWithExceptionHandling(() -> {
            if (SrteCache.loincCodesMap.isEmpty()) {
                SrteCache.loincCodesMap = cachingValueService.getAOELOINCCodes();
            }
        });
        runWithExceptionHandling(() -> {
            if (SrteCache.raceCodesMap.isEmpty()) {
                SrteCache.raceCodesMap = cachingValueService.getRaceCodes();
            }
        });
        runWithExceptionHandling(() -> {
            if (SrteCache.programAreaCodesMap.isEmpty()) {
                SrteCache.programAreaCodesMap = cachingValueService.getAllProgramAreaCodes();
            }
        });
        runWithExceptionHandling(() -> {
            if (SrteCache.jurisdictionCodeMap.isEmpty()) {
                SrteCache.jurisdictionCodeMap = cachingValueService.getAllJurisdictionCode();
            }
        });
        runWithExceptionHandling(() -> {
            if (SrteCache.jurisdictionCodeMapWithNbsUid.isEmpty()) {
                SrteCache.jurisdictionCodeMapWithNbsUid = cachingValueService.getAllJurisdictionCodeWithNbsUid();
            }
        });
        runWithExceptionHandling(() -> {
            if (SrteCache.programAreaCodesMapWithNbsUid.isEmpty()) {
                SrteCache.programAreaCodesMapWithNbsUid = cachingValueService.getAllProgramAreaCodesWithNbsUid();
            }
        });
        runWithExceptionHandling(() -> {
            if (SrteCache.elrXrefsList.isEmpty()) {
                SrteCache.elrXrefsList = cachingValueService.getAllElrXref();
            }
        });
        runWithExceptionHandling(() -> {
            if (SrteCache.coInfectionConditionCode.isEmpty()) {
                SrteCache.coInfectionConditionCode = cachingValueService.getAllOnInfectionConditionCode();
            }
        });
        runWithExceptionHandling(() -> {
            if (SrteCache.conditionCodes.isEmpty() || SrteCache.investigationFormConditionCode.isEmpty()) {
                SrteCache.conditionCodes = cachingValueService.getAllConditionCode();
                for (ConditionCode obj : SrteCache.conditionCodes) {
                    SrteCache.investigationFormConditionCode.put(obj.getConditionCd(), obj.getInvestigationFormCd());
                }
            }
        });
        runWithExceptionHandling(() -> {
            if (SrteCache.labResultByDescMap.isEmpty()) {
                SrteCache.labResultByDescMap = cachingValueService.getLabResultDesc();
            }
        });
        runWithExceptionHandling(() -> {
            if (SrteCache.snomedCodeByDescMap.isEmpty()) {
                SrteCache.snomedCodeByDescMap = cachingValueService.getAllSnomedCode();
            }
        });
        runWithExceptionHandling(() -> {
            if (SrteCache.labResultWithOrganismNameIndMap.isEmpty()) {
                SrteCache.labResultWithOrganismNameIndMap = cachingValueService.getAllLabResultJoinWithLabCodingSystemWithOrganismNameInd();
            }
        });
        runWithExceptionHandling(() -> {
            if (SrteCache.loinCodeWithComponentNameMap.isEmpty()) {
                SrteCache.loinCodeWithComponentNameMap = cachingValueService.getAllLoinCodeWithComponentName();
            }
        });

        loadCachedValues();
    }

    private void loadCachedValues() {
        Cache cache = cacheManager.getCache("srte");
        if (cache == null) {
            return;
        }
        retrieveAndSetCacheValue(cache, "loincCodes", SrteCache.loincCodesMap);
        retrieveAndSetCacheValue(cache, "raceCodes", SrteCache.raceCodesMap);
        retrieveAndSetCacheValue(cache, "programAreaCodes", SrteCache.programAreaCodesMap);
        retrieveAndSetCacheValue(cache, "jurisdictionCode", SrteCache.jurisdictionCodeMap);
        retrieveAndSetCacheValue(cache, "programAreaCodesWithNbsUid", SrteCache.programAreaCodesMapWithNbsUid);
        retrieveAndSetCacheValue(cache, "jurisdictionCodeWithNbsUid", SrteCache.jurisdictionCodeMapWithNbsUid);
        retrieveAndSetCacheValue(cache, "elrXref", SrteCache.elrXrefsList);
        retrieveAndSetCacheValue(cache, "coInfectionConditionCode", SrteCache.coInfectionConditionCode);
        retrieveAndSetCacheValue(cache, "conditionCode", SrteCache.conditionCodes);
        retrieveAndSetCacheValue(cache, "labResulDesc", SrteCache.labResultByDescMap);
        retrieveAndSetCacheValue(cache, "snomedCodeByDesc", SrteCache.snomedCodeByDescMap);
        retrieveAndSetCacheValue(cache, "labResulDescWithOrgnismName", SrteCache.labResultWithOrganismNameIndMap);
        retrieveAndSetCacheValue(cache, "loinCodeWithComponentName", SrteCache.loinCodeWithComponentNameMap);
    }

    @SuppressWarnings("unchecked")
    private <T> void retrieveAndSetCacheValue(Cache cache, String key, T target) {
        Cache.ValueWrapper valueWrapper = cache.get(key);
        if (valueWrapper != null) {
            Object cachedObject = valueWrapper.get();
            if (cachedObject != null) {
                if (cachedObject instanceof TreeMap && target instanceof TreeMap) {
                    ((TreeMap<Object, Object>) target).putAll((TreeMap<Object, Object>) cachedObject);
                } else if (cachedObject instanceof List && target instanceof List) {
                    ((List<Object>) target).addAll((List<Object>) cachedObject);
                }
            }
        }
    }

    private void runWithExceptionHandling(ExceptionalRunnable runnable) {
        try {
            runnable.run();
        } catch (DataProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @FunctionalInterface
    private interface ExceptionalRunnable {
        void run() throws DataProcessingException;
    }
}
