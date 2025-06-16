package gov.cdc.dataprocessing.utilities.component.jurisdiction;

import gov.cdc.dataprocessing.constant.enums.ObjectName;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.service.interfaces.cache.ICacheApiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProgAreaJurisdictionUtilTest {

    @Mock
    private ICacheApiService cacheApiService;

    @InjectMocks
    private ProgAreaJurisdictionUtil util;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetPAJHash_validInputs() throws DataProcessingException {
        when(cacheApiService.getSrteCacheString(ObjectName.PROGRAM_AREA_CODES_WITH_NBS_UID.name(), "PA01"))
                .thenReturn("101");
        when(cacheApiService.getSrteCacheString(ObjectName.JURISDICTION_CODE_WITH_NBS_UID.name(), "J01"))
                .thenReturn("202");

        long result = util.getPAJHash("PA01", "J01");
        assertEquals(20200000L + 101, result);
    }

    @Test
    void testGetPAJHash_invalidProgramArea() {
        long result = util.getPAJHash("", "J01");
        assertEquals(0, result);
    }

    @Test
    void testGetPAJHash_invalidJurisdiction() {
        long result = util.getPAJHash("PA01", null);
        assertEquals(0, result);
    }

    @Test
    void testGetPAJHash_exceptionInCache() throws DataProcessingException {
        when(cacheApiService.getSrteCacheString(any(), any())).thenThrow(new RuntimeException("cache error"));

        long result = util.getPAJHash("PA01", "J01");
        assertEquals(0, result);
    }

    @Test
    void testGetPAJHashList_singleJurisdiction() throws Exception {
        when(cacheApiService.getSrteCacheString(ObjectName.PROGRAM_AREA_CODES_WITH_NBS_UID.name(), "PA01"))
                .thenReturn("101");
        when(cacheApiService.getSrteCacheString(ObjectName.JURISDICTION_CODE_WITH_NBS_UID.name(), "J01"))
                .thenReturn("202");

        Collection<Object> result = util.getPAJHashList("PA01", "J01");

        assertEquals(1, result.size());
        assertTrue(result.contains(20200000L + 101));
    }

    @Test
    void testGetPAJHashList_allJurisdictions() throws Exception {
        when(cacheApiService.getSrteCacheString(ObjectName.JURISDICTION_CODE_MAP_WITH_NBS_UID_KEY_SET.name(), ""))
                .thenReturn("J01, J02");
        when(cacheApiService.getSrteCacheString(ObjectName.PROGRAM_AREA_CODES_WITH_NBS_UID.name(), "PA01"))
                .thenReturn("101");
        when(cacheApiService.getSrteCacheString(ObjectName.JURISDICTION_CODE_WITH_NBS_UID.name(), "J01"))
                .thenReturn("201");
        when(cacheApiService.getSrteCacheString(ObjectName.JURISDICTION_CODE_WITH_NBS_UID.name(), "J02"))
                .thenReturn("202");

        Collection<Object> result = util.getPAJHashList("PA01", "ALL");

        assertEquals(2, result.size());
        assertTrue(result.contains(20100000L + 101));
        assertTrue(result.contains(20200000L + 101));
    }


    @Test
    void testValidCodes_returnsCorrectHash() throws DataProcessingException {
        when(cacheApiService.getSrteCacheString(ObjectName.PROGRAM_AREA_CODES_WITH_NBS_UID.name(), "PA01"))
                .thenReturn("123");
        when(cacheApiService.getSrteCacheString(ObjectName.JURISDICTION_CODE_WITH_NBS_UID.name(), "J01"))
                .thenReturn("456");

        long result = util.getPAJHash("PA01", "J01");
        assertEquals(45600000L + 123, result);
    }

    @Test
    void testProgramAreaCodeNull_returnsZero() {
        long result = util.getPAJHash(null, "J01");
        assertEquals(0, result);
    }

    @Test
    void testProgramAreaCodeEmpty_returnsZero() {
        long result = util.getPAJHash("", "J01");
        assertEquals(0, result);
    }

    @Test
    void testJurisdictionCodeNull_returnsZero() {
        long result = util.getPAJHash("PA01", null);
        assertEquals(0, result);
    }

    @Test
    void testJurisdictionCodeEmpty_returnsZero() {
        long result = util.getPAJHash("PA01", "");
        assertEquals(0, result);
    }

    @Test
    void testCacheThrowsException_returnsZero() throws DataProcessingException {
        when(cacheApiService.getSrteCacheString(any(), any()))
                .thenThrow(new RuntimeException("Simulated failure"));

        long result = util.getPAJHash("PA01", "J01");
        assertEquals(0, result);
    }

    @Test
    void testCacheReturnsNonInteger_throwsHandled() throws DataProcessingException {
        when(cacheApiService.getSrteCacheString(ObjectName.PROGRAM_AREA_CODES_WITH_NBS_UID.name(), "PA01"))
                .thenReturn("abc"); // non-numeric
        when(cacheApiService.getSrteCacheString(ObjectName.JURISDICTION_CODE_WITH_NBS_UID.name(), "J01"))
                .thenReturn("456");

        long result = util.getPAJHash("PA01", "J01");
        assertEquals(0, result);
    }

    @Test
    void testBothCodesValidButCacheReturnsNulls_returnsZero() throws DataProcessingException {
        when(cacheApiService.getSrteCacheString(any(), any()))
                .thenReturn(null); // will cause NumberFormatException

        long result = util.getPAJHash("PA01", "J01");
        assertEquals(0, result);
    }
}
