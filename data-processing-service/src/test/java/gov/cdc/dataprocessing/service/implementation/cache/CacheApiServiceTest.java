
package gov.cdc.dataprocessing.service.implementation.cache;

import gov.cdc.dataprocessing.constant.enums.ObjectName;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class CacheApiServiceTest {

    @Mock
    private ManagerCacheService managerCacheService;

    @InjectMocks
    private CacheApiService cacheApiService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetSrteCacheString() throws DataProcessingException {
        when(managerCacheService.getCache(ObjectName.PROGRAM_AREA_CODES, "key")).thenReturn("value");
        String result = cacheApiService.getSrteCacheString("PROGRAM_AREA_CODES", "key");
        assertEquals("value", result);
    }

    @Test
    void testGetSrteCacheObject() {
        Object expected = new Object();
        when(managerCacheService.getCacheObject(ObjectName.CONDITION_CODE, "key")).thenReturn(expected);
        Object result = cacheApiService.getSrteCacheObject("CONDITION_CODE", "key");
        assertEquals(expected, result);
    }

    @Test
    void testGetSrteCacheBool() throws DataProcessingException {
        when(managerCacheService.containKey(ObjectName.LOINC_CODES, "key")).thenReturn(true);
        Boolean result = cacheApiService.getSrteCacheBool("LOINC_CODES", "key");
        assertTrue(result);
    }
}
