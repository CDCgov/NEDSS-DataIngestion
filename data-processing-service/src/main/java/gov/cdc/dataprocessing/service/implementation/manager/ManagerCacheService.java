package gov.cdc.dataprocessing.service.implementation.manager;

import gov.cdc.dataprocessing.cache.SrteCache;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.repository.nbs.srte.model.ConditionCode;
import gov.cdc.dataprocessing.repository.nbs.srte.model.ElrXref;
import gov.cdc.dataprocessing.service.interfaces.cache.ICatchingValueService;
import gov.cdc.dataprocessing.service.interfaces.manager.IManagerCacheService;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;

@Service
public class ManagerCacheService implements IManagerCacheService {
    private final ICatchingValueService cachingValueService;
    private final CacheManager cacheManager;


    public ManagerCacheService(ICatchingValueService cachingValueService,
                               CacheManager cacheManager) {
        this.cachingValueService = cachingValueService;
        this.cacheManager = cacheManager;
    }

    @SuppressWarnings({"java:S3776","java:S1488", "java:S112", "java:S2696"})
    public CompletableFuture<Void> loadAndInitCachedValueAsync() {
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            if (SrteCache.loincCodesMap.isEmpty()) {
                try {
                    cachingValueService.getAOELOINCCodes();
                } catch (DataProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        }).thenRun(() -> {
            if (SrteCache.raceCodesMap.isEmpty()) {
                try {
                    cachingValueService.getRaceCodes();
                } catch (DataProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        }).thenRun(() -> {
            if (SrteCache.programAreaCodesMap.isEmpty()) {
                try {
                    cachingValueService.getAllProgramAreaCodes();
                } catch (DataProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        }).thenRun(() -> {
            if (SrteCache.jurisdictionCodeMap.isEmpty()) {
                try {
                    cachingValueService.getAllJurisdictionCode();
                } catch (DataProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        }).thenRun(() -> {
            if (SrteCache.jurisdictionCodeMapWithNbsUid.isEmpty()) {
                try {
                    cachingValueService.getAllJurisdictionCodeWithNbsUid();
                } catch (DataProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        }).thenRun(() -> {
            if (SrteCache.programAreaCodesMapWithNbsUid.isEmpty()) {
                try {
                    cachingValueService.getAllProgramAreaCodesWithNbsUid();
                } catch (DataProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        }).thenRun(() -> {
            if (SrteCache.elrXrefsList.isEmpty()) {
                try {
                    cachingValueService.getAllElrXref();
                } catch (DataProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        }).thenRun(() -> {
            if (SrteCache.coInfectionConditionCode.isEmpty()) {
                try {
                    cachingValueService.getAllOnInfectionConditionCode();
                } catch (DataProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        }).thenRun(() -> {
            if (SrteCache.conditionCodes.isEmpty() || SrteCache.investigationFormConditionCode.isEmpty()) {
                try {
                    cachingValueService.getAllConditionCode();
                } catch (DataProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        }).thenRun(() -> {
            if (SrteCache.labResultByDescMap.isEmpty()) {
                try {
                    cachingValueService.getLabResultDesc();
                } catch (DataProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        }).thenRun(() -> {
            if (SrteCache.snomedCodeByDescMap.isEmpty()) {
                try {
                    cachingValueService.getAllSnomedCode();
                } catch (DataProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        }).thenRun(() -> {
            if (SrteCache.labResultWithOrganismNameIndMap.isEmpty()) {
                try {
                    cachingValueService.getAllLabResultJoinWithLabCodingSystemWithOrganismNameInd();
                } catch (DataProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        }).thenRun(() -> {
            if (SrteCache.loinCodeWithComponentNameMap.isEmpty()) {
                try {
                    cachingValueService.getAllLoinCodeWithComponentName();
                } catch (DataProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        }).thenRun(() -> {
            // Retrieve cached values using Cache.ValueWrapper
            var cache = cacheManager.getCache("srte");
            if (cache != null) {
                Cache.ValueWrapper valueWrapper;
                valueWrapper = cache.get("loincCodes");
                if (valueWrapper != null) {
                    Object cachedObject = valueWrapper.get();
                    if (cachedObject instanceof TreeMap) {
                        SrteCache.loincCodesMap = (TreeMap<String, String>) cachedObject;
                    }
                }

                valueWrapper = cache.get("raceCodes");
                if (valueWrapper != null) {
                    Object cachedObject = valueWrapper.get();
                    if (cachedObject instanceof TreeMap) {
                        SrteCache.raceCodesMap = (TreeMap<String, String>) cachedObject;
                    }
                }

                valueWrapper = cache.get("programAreaCodes");
                if (valueWrapper != null) {
                    Object cachedObject = valueWrapper.get();
                    if (cachedObject instanceof TreeMap) {
                        SrteCache.programAreaCodesMap = (TreeMap<String, String>) cachedObject;
                    }
                }

                valueWrapper = cache.get("jurisdictionCode");
                if (valueWrapper != null) {
                    Object cachedObject = valueWrapper.get();
                    if (cachedObject instanceof TreeMap) {
                        SrteCache.jurisdictionCodeMap = (TreeMap<String, String>) cachedObject;
                    }
                }

                valueWrapper = cache.get("programAreaCodesWithNbsUid");
                if (valueWrapper != null) {
                    Object cachedObject = valueWrapper.get();
                    if (cachedObject instanceof TreeMap) {
                        SrteCache.programAreaCodesMapWithNbsUid = (TreeMap<String, Integer>) cachedObject;
                    }
                }

                valueWrapper = cache.get("jurisdictionCodeWithNbsUid");
                if (valueWrapper != null) {
                    Object cachedObject = valueWrapper.get();
                    if (cachedObject instanceof TreeMap) {
                        SrteCache.jurisdictionCodeMapWithNbsUid = (TreeMap<String, Integer>) cachedObject;
                    }
                }

                valueWrapper = cache.get("elrXref");
                if (valueWrapper != null) {
                    Object cachedObject = valueWrapper.get();
                    if (cachedObject instanceof List) {
                        SrteCache.elrXrefsList = (List<ElrXref>) cachedObject;
                    }
                }


                valueWrapper = cache.get("coInfectionConditionCode");
                if (valueWrapper != null) {
                    Object cachedObject = valueWrapper.get();
                    if (cachedObject instanceof List) {
                        SrteCache.coInfectionConditionCode = (TreeMap<String, String>) cachedObject;
                    }
                }

                valueWrapper = cache.get("conditionCode");
                if (valueWrapper != null) {
                    Object cachedObject = valueWrapper.get();
                    if (cachedObject instanceof List) {
                        SrteCache.conditionCodes = (List<ConditionCode>) cachedObject;

                        // Populate Code for Investigation Form
                        for (ConditionCode obj : SrteCache.conditionCodes) {
                            SrteCache.investigationFormConditionCode.put(obj.getConditionCd(), obj.getInvestigationFormCd());
                        }

                    }
                }

                valueWrapper = cache.get("labResulDesc");
                if (valueWrapper != null) {
                    Object cachedObject = valueWrapper.get();
                    if (cachedObject instanceof List) {
                        SrteCache.labResultByDescMap = (TreeMap<String, String>) cachedObject;
                    }
                }

                valueWrapper = cache.get("snomedCodeByDesc");
                if (valueWrapper != null) {
                    Object cachedObject = valueWrapper.get();
                    if (cachedObject instanceof List) {
                        SrteCache.snomedCodeByDescMap = (TreeMap<String, String>) cachedObject;
                    }
                }

                valueWrapper = cache.get("labResulDescWithOrgnismName");
                if (valueWrapper != null) {
                    Object cachedObject = valueWrapper.get();
                    if (cachedObject instanceof List) {
                        SrteCache.labResultWithOrganismNameIndMap = (TreeMap<String, String>) cachedObject;
                    }
                }

                valueWrapper = cache.get("loinCodeWithComponentName");
                if (valueWrapper != null) {
                    Object cachedObject = valueWrapper.get();
                    if (cachedObject instanceof List) {
                        SrteCache.loinCodeWithComponentNameMap = (TreeMap<String, String>) cachedObject;
                    }
                }
            }
        });

        return future;
    }

}
