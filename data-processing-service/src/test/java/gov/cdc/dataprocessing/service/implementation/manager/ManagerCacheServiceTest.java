package gov.cdc.dataprocessing.service.implementation.manager;

import gov.cdc.dataprocessing.cache.SrteCache;
import gov.cdc.dataprocessing.repository.nbs.srte.model.ConditionCode;
import gov.cdc.dataprocessing.repository.nbs.srte.model.ElrXref;
import gov.cdc.dataprocessing.service.interfaces.cache.ICatchingValueService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ManagerCacheServiceTest {

    @Mock
    private ICatchingValueService catchingValueService;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache cache;

    @Mock
    private Cache.ValueWrapper valueWrapper;

    @InjectMocks
    private ManagerCacheService managerCacheService;

    @BeforeEach
    void setUp() {
        // Initialize the SrteCache to ensure it is empty before each test
        SrteCache.loincCodesMap = new TreeMap<>();
        SrteCache.raceCodesMap = new TreeMap<>();
        SrteCache.programAreaCodesMap = new TreeMap<>();
        SrteCache.jurisdictionCodeMap = new TreeMap<>();
        SrteCache.jurisdictionCodeMapWithNbsUid = new TreeMap<>();
        SrteCache.programAreaCodesMapWithNbsUid = new TreeMap<>();
        SrteCache.elrXrefsList = new ArrayList<>();
        SrteCache.coInfectionConditionCode = new TreeMap<>();
        SrteCache.conditionCodes = new ArrayList<>();
        SrteCache.investigationFormConditionCode = new TreeMap<>();
        SrteCache.labResultByDescMap = new TreeMap<>();
        SrteCache.snomedCodeByDescMap = new TreeMap<>();
        SrteCache.labResultWithOrganismNameIndMap = new TreeMap<>();
        SrteCache.loinCodeWithComponentNameMap = new TreeMap<>();
    }

    @Test
    void testLoadAndInitCachedValueAsync_WithCacheValues() throws ExecutionException, InterruptedException {
        // Mock behavior for CacheManager and Cache
        when(cacheManager.getCache("srte")).thenReturn(cache);

        // Mock behavior for ValueWrapper to simulate existing cache values
        when(cache.get("loincCodes")).thenReturn(valueWrapper);
        when(valueWrapper.get()).thenReturn(new TreeMap<String, String>());
        when(cache.get("raceCodes")).thenReturn(valueWrapper);
        when(valueWrapper.get()).thenReturn(new TreeMap<String, String>());
        when(cache.get("programAreaCodes")).thenReturn(valueWrapper);
        when(valueWrapper.get()).thenReturn(new TreeMap<String, String>());
        when(cache.get("jurisdictionCode")).thenReturn(valueWrapper);
        when(valueWrapper.get()).thenReturn(new TreeMap<String, String>());
        when(cache.get("programAreaCodesWithNbsUid")).thenReturn(valueWrapper);
        when(valueWrapper.get()).thenReturn(new TreeMap<String, Integer>());
        when(cache.get("jurisdictionCodeWithNbsUid")).thenReturn(valueWrapper);
        when(valueWrapper.get()).thenReturn(new TreeMap<String, Integer>());

        when(cache.get("elrXref")).thenReturn(valueWrapper);
        when(valueWrapper.get()).thenReturn(new ArrayList<ElrXref>());

        when(cache.get("coInfectionConditionCode")).thenReturn(valueWrapper);
        when(valueWrapper.get()).thenReturn(new TreeMap<String, String>());
        when(cache.get("conditionCode")).thenReturn(valueWrapper);
        when(valueWrapper.get()).thenReturn(new ArrayList<ConditionCode>());
        when(cache.get("labResulDesc")).thenReturn(valueWrapper);
        when(valueWrapper.get()).thenReturn(new TreeMap<String, String>());
        when(cache.get("snomedCodeByDesc")).thenReturn(valueWrapper);
        when(valueWrapper.get()).thenReturn(new TreeMap<String, String>());
        when(cache.get("labResulDescWithOrgnismName")).thenReturn(valueWrapper);
        when(valueWrapper.get()).thenReturn(new TreeMap<String, String>());
        when(cache.get("loinCodeWithComponentName")).thenReturn(valueWrapper);
        when(valueWrapper.get()).thenReturn(new TreeMap<String, String>());

        // Call the method under test
        CompletableFuture<Void> future = managerCacheService.loadAndInitCachedValueAsync();

        // Wait for the CompletableFuture to complete
        future.get();

        // Verify interactions with the mocked dependencies
        verify(cacheManager, times(1)).getCache("srte");
        verify(cache, times(13)).get(anyString());
    }
}
