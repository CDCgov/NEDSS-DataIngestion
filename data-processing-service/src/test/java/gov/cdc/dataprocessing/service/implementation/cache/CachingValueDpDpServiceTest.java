
package gov.cdc.dataprocessing.service.implementation.cache;

import gov.cdc.dataprocessing.constant.enums.ObjectName;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template.CodeValueJdbcRepository;
import gov.cdc.dataprocessing.repository.nbs.srte.model.CodeValueGeneral;
import gov.cdc.dataprocessing.repository.nbs.srte.model.StateCode;
import gov.cdc.dataprocessing.service.interfaces.cache.ICacheApiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CachingValueDpDpServiceTest {

    @Mock
    private CodeValueJdbcRepository codeValueJdbcRepository;
    @Mock
    private ICacheApiService cacheApiService;

    @InjectMocks
    private CachingValueDpDpService cachingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindToCode() throws DataProcessingException {
        when(cacheApiService.getSrteCacheString(ObjectName.FIND_TO_CODE.name(), "A~B~C")).thenReturn("X");
        String result = cachingService.findToCode("A", "B", "C");
        assertEquals("X", result);
    }

    @Test
    void testGetCodeDescTxtForCd() throws DataProcessingException {
        when(cacheApiService.getSrteCacheString(ObjectName.GET_CODE_DESC_TXT_FOR_CD.name(), "code~set")).thenReturn("description");
        String result = cachingService.getCodeDescTxtForCd("code", "set");
        assertEquals("description", result);
    }

    @Test
    void testGetCountyCdByDesc() throws DataProcessingException {
        when(cacheApiService.getSrteCacheString(ObjectName.GET_COUNTY_CD_BY_DESC.name(), "county~state")).thenReturn("countyCode");
        String result = cachingService.getCountyCdByDesc("county", "state");
        assertEquals("countyCode", result);
    }

    @Test
    void testFindStateCodeByStateNm() {
        StateCode stateCode = new StateCode();
        when(cacheApiService.getSrteCacheObject(ObjectName.FIND_STATE_CODE_BY_STATE_NM.name(), "NY")).thenReturn(stateCode);
        StateCode result = cachingService.findStateCodeByStateNm("NY");
        assertEquals(stateCode, result);
    }

    @Test
    void testGetCodedValueWithKey() throws DataProcessingException {
        when(cacheApiService.getSrteCacheString(ObjectName.CODED_VALUE.name(), "type~key")).thenReturn("value");
        String result = cachingService.getCodedValue("type", "key");
        assertEquals("value", result);
    }

    @Test
    void testCheckCodedValue() throws DataProcessingException {
        when(cacheApiService.getSrteCacheBool(ObjectName.CODED_VALUE.name(), "type~key")).thenReturn(true);
        boolean result = cachingService.checkCodedValue("type", "key");
        assertTrue(result);
    }

    @Test
    void testGetCodedValuesCallRepos() throws DataProcessingException {
        when(cacheApiService.getSrteCacheString(ObjectName.GET_CODED_VALUES_CALL_REPOS.name(), "type")).thenReturn("repos");
        String result = cachingService.getCodedValuesCallRepos("type");
        assertEquals("repos", result);
    }

    @Test
    void testGetGeneralCodedValueNonEmpty() {
        CodeValueGeneral general = new CodeValueGeneral();
        when(codeValueJdbcRepository.findCodeValuesByCodeSetNm("code")).thenReturn(List.of(general));
        List<CodeValueGeneral> result = cachingService.getGeneralCodedValue("code");
        assertEquals(1, result.size());
    }

    @Test
    void testGetGeneralCodedValueEmpty() {
        when(codeValueJdbcRepository.findCodeValuesByCodeSetNm("code")).thenReturn(new ArrayList<>());
        List<CodeValueGeneral> result = cachingService.getGeneralCodedValue("code");
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetCodedValueByCodeOnly() throws DataProcessingException {
        when(cacheApiService.getSrteCacheString(ObjectName.GET_CODED_VALUE.name(), "val")).thenReturn("result");
        String result = cachingService.getCodedValue("val");
        assertEquals("result", result);
    }
}
