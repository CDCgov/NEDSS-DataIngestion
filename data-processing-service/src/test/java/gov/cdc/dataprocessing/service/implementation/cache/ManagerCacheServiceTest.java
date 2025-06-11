
package gov.cdc.dataprocessing.service.implementation.cache;

import gov.cdc.dataprocessing.cache.cache_model.SrteCache;
import gov.cdc.dataprocessing.constant.enums.ObjectName;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.repository.nbs.srte.model.ConditionCode;
import gov.cdc.dataprocessing.service.interfaces.cache.ICatchingValueService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ManagerCacheServiceTest {

    @Mock
    private ICatchingValueService cachingValueService;

    @InjectMocks
    private ManagerCacheService managerCacheService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    

    @Test
    void testGetCacheProgramAreaCode() throws DataProcessingException {
        SrteCache.programAreaCodesMap.put("P1", "Desc1");
        String result = managerCacheService.getCache(ObjectName.PROGRAM_AREA_CODES, "P1");
        assertEquals("Desc1", result);
    }

    @Test
    void testGetCacheFindToCode() throws DataProcessingException {
        when(cachingValueService.findToCode("set1", "code1", "set2")).thenReturn("result");
        String result = managerCacheService.getCache(ObjectName.FIND_TO_CODE, "set1~code1~set2");
        assertEquals("result", result);
    }

    @Test
    void testGetCacheGetCodeDescTxtForCd() throws DataProcessingException {
        when(cachingValueService.getCodeDescTxtForCd("c1", "set1")).thenReturn("desc");
        String result = managerCacheService.getCache(ObjectName.GET_CODE_DESC_TXT_FOR_CD, "c1~set1");
        assertEquals("desc", result);
    }

    @Test
    void testGetCacheGetCountyCdByDesc() throws DataProcessingException {
        when(cachingValueService.getCountyCdByDesc("county", "state")).thenReturn("countyCd");
        String result = managerCacheService.getCache(ObjectName.GET_COUNTY_CD_BY_DESC, "county~state");
        assertEquals("countyCd", result);
    }

    @Test
    void testGetCacheGetCodedValue() throws DataProcessingException {
        HashMap<String, String> codedValueMap = new HashMap<>();
        codedValueMap.put("key1", "value1");
        when(cachingValueService.getCodedValue("key1")).thenReturn(codedValueMap);
        String result = managerCacheService.getCache(ObjectName.GET_CODED_VALUE, "key1");
        assertEquals("value1", result);
    }

    @Test
    void testContainKeyTrue() throws DataProcessingException {
        SrteCache.loincCodesMap.put("L1", "code");
        assertTrue(managerCacheService.containKey(ObjectName.LOINC_CODES, "L1"));
    }

    @Test
    void testContainKeyFalse() throws DataProcessingException {
        SrteCache.loincCodesMap.clear();
        assertFalse(managerCacheService.containKey(ObjectName.LOINC_CODES, "NotExist"));
    }

    @Test
    void testLoadCache() throws DataProcessingException {
        when(cachingValueService.getAOELOINCCodes()).thenReturn(new HashMap<>());
        when(cachingValueService.getRaceCodes()).thenReturn(new HashMap<>());
        when(cachingValueService.getAllProgramAreaCodes()).thenReturn(new HashMap<>());
        when(cachingValueService.getAllJurisdictionCode()).thenReturn(new HashMap<>());
        when(cachingValueService.getAllJurisdictionCodeWithNbsUid()).thenReturn(new HashMap<>());
        when(cachingValueService.getAllProgramAreaCodesWithNbsUid()).thenReturn(new HashMap<>());
        when(cachingValueService.getAllElrXref()).thenReturn(List.of());
        when(cachingValueService.getAllOnInfectionConditionCode()).thenReturn(new HashMap<>());
        when(cachingValueService.getAllConditionCode()).thenReturn(List.of());
        when(cachingValueService.getLabResultDesc()).thenReturn(new HashMap<>());
        when(cachingValueService.getAllSnomedCode()).thenReturn(new HashMap<>());
        when(cachingValueService.getAllLabResultJoinWithLabCodingSystemWithOrganismNameInd()).thenReturn(new HashMap<>());
        when(cachingValueService.getAllLoinCodeWithComponentName()).thenReturn(new HashMap<>());

        managerCacheService.loadCache();
    }
}
