package gov.cdc.dataprocessing.service.implementation.manager;

import gov.cdc.dataprocessing.cache.SrteCache;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.repository.nbs.srte.model.ConditionCode;
import gov.cdc.dataprocessing.service.interfaces.cache.ICatchingValueService;
import gov.cdc.dataprocessing.service.interfaces.manager.IManagerCacheService;
import jakarta.annotation.PostConstruct;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;

@Service
public class ManagerCacheService implements IManagerCacheService {
    private static ICatchingValueService cachingValueService;
    private final CacheManager cacheManager;

    public ManagerCacheService(ICatchingValueService cachingValueService, CacheManager cacheManager) {
        ManagerCacheService.cachingValueService = cachingValueService;
        this.cacheManager = cacheManager;
    }

    @SuppressWarnings({"java:S3776", "java:S112", "java:S2696"})
    public CompletableFuture<Void> loadAndInitCachedValueAsync() {
        return CompletableFuture.runAsync(this::loadCache);
    }

    @PostConstruct
    @Scheduled(fixedRate = 3600000) // Reload every 60 min
    public static void loadAndInitCachedValueSync() throws DataProcessingException {
        loadCacheSync();
    }

    private static void loadCacheSync() throws DataProcessingException {
        SrteCache.loincCodesMap = cachingValueService.getAOELOINCCodes(); // ObservationResultRequestHandler
        SrteCache.raceCodesMap = cachingValueService.getRaceCodes(); //HL7PatientHandler
        SrteCache.programAreaCodesMap = cachingValueService.getAllProgramAreaCodes(); // ALL
        SrteCache.jurisdictionCodeMap = cachingValueService.getAllJurisdictionCode(); // ALL
        SrteCache.jurisdictionCodeMapWithNbsUid = cachingValueService.getAllJurisdictionCodeWithNbsUid(); // ProgAreaJurisdictionutil
        SrteCache.programAreaCodesMapWithNbsUid = cachingValueService.getAllProgramAreaCodesWithNbsUid(); // ProgAreaJurisdictionutil
        SrteCache.elrXrefsList = cachingValueService.getAllElrXref(); //HL7PatientHandler
        SrteCache.coInfectionConditionCode = cachingValueService.getAllOnInfectionConditionCode(); //AutoInvestigationService
        SrteCache.conditionCodes = cachingValueService.getAllConditionCode();
        for (ConditionCode obj : SrteCache.conditionCodes) {
            SrteCache.investigationFormConditionCode.put(obj.getConditionCd(), obj.getInvestigationFormCd()); // Lower Stream
        }
        SrteCache.labResultByDescMap = cachingValueService.getLabResultDesc(); // InvestigationService
        SrteCache.snomedCodeByDescMap = cachingValueService.getAllSnomedCode(); // InvestigationService
        SrteCache.labResultWithOrganismNameIndMap = cachingValueService.getAllLabResultJoinWithLabCodingSystemWithOrganismNameInd();
        SrteCache.loinCodeWithComponentNameMap = cachingValueService.getAllLoinCodeWithComponentName(); // None
    }

    @SuppressWarnings({"java:S2696"})
    private void loadCache() {
        runWithExceptionHandling(() -> {
            if (SrteCache.loincCodesMap.isEmpty()) {
                SrteCache.loincCodesMap = cachingValueService.getAOELOINCCodes(); // ObservationResultRequestHandler
            }
        });
        runWithExceptionHandling(() -> {
            if (SrteCache.raceCodesMap.isEmpty()) {
                SrteCache.raceCodesMap = cachingValueService.getRaceCodes(); //HL7PatientHandler
            }
        });
        runWithExceptionHandling(() -> {
            if (SrteCache.programAreaCodesMap.isEmpty()) {
                SrteCache.programAreaCodesMap = cachingValueService.getAllProgramAreaCodes(); // ALL
            }
        });
        runWithExceptionHandling(() -> {
            if (SrteCache.jurisdictionCodeMap.isEmpty()) {
                SrteCache.jurisdictionCodeMap = cachingValueService.getAllJurisdictionCode(); // ALL
            }
        });
        runWithExceptionHandling(() -> {
            if (SrteCache.jurisdictionCodeMapWithNbsUid.isEmpty()) {
                SrteCache.jurisdictionCodeMapWithNbsUid = cachingValueService.getAllJurisdictionCodeWithNbsUid(); // ProgAreaJurisdictionutil
            }
        });
        runWithExceptionHandling(() -> {
            if (SrteCache.programAreaCodesMapWithNbsUid.isEmpty()) {
                SrteCache.programAreaCodesMapWithNbsUid = cachingValueService.getAllProgramAreaCodesWithNbsUid(); // ProgAreaJurisdictionutil
            }
        });
        runWithExceptionHandling(() -> {
            if (SrteCache.elrXrefsList.isEmpty()) {
                SrteCache.elrXrefsList = cachingValueService.getAllElrXref(); //HL7PatientHandler
            }
        });
        runWithExceptionHandling(() -> {
            if (SrteCache.coInfectionConditionCode.isEmpty()) {
                SrteCache.coInfectionConditionCode = cachingValueService.getAllOnInfectionConditionCode(); //AutoInvestigationService
            }
        });
        runWithExceptionHandling(() -> {
            // condCode -> Manager Cache only
            if (SrteCache.conditionCodes.isEmpty() || SrteCache.investigationFormConditionCode.isEmpty()) {
                SrteCache.conditionCodes = cachingValueService.getAllConditionCode();
                for (ConditionCode obj : SrteCache.conditionCodes) {
                    SrteCache.investigationFormConditionCode.put(obj.getConditionCd(), obj.getInvestigationFormCd()); // Lower Stream
                }
            }
        });
        runWithExceptionHandling(() -> {
            if (SrteCache.labResultByDescMap.isEmpty()) {
                SrteCache.labResultByDescMap = cachingValueService.getLabResultDesc(); // InvestigationService
            }
        });
        runWithExceptionHandling(() -> {
            if (SrteCache.snomedCodeByDescMap.isEmpty()) {
                SrteCache.snomedCodeByDescMap = cachingValueService.getAllSnomedCode(); // InvestigationService
            }
        });
        runWithExceptionHandling(() -> {
            if (SrteCache.labResultWithOrganismNameIndMap.isEmpty()) { // InvestigationService
                SrteCache.labResultWithOrganismNameIndMap = cachingValueService.getAllLabResultJoinWithLabCodingSystemWithOrganismNameInd();
            }
        });
        runWithExceptionHandling(() -> {
            if (SrteCache.loinCodeWithComponentNameMap.isEmpty()) {
                SrteCache.loinCodeWithComponentNameMap = cachingValueService.getAllLoinCodeWithComponentName(); // None
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
