package gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template;


import gov.cdc.dataprocessing.repository.nbs.odse.model.generic_helper.LocalUidGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LocalUidJdbcRepositoryTest {

    @Mock
    private NamedParameterJdbcTemplate jdbcTemplateOdse;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private LocalUidJdbcRepository repoSpy;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(jdbcTemplateOdse.getJdbcTemplate()).thenReturn(jdbcTemplate);
        repoSpy = spy(new LocalUidJdbcRepository(jdbcTemplateOdse));
    }

    @Test
    void getLocalUID_shouldPopulateAllFieldsFromProcedureResult() {
        // Arrange
        String className = "TEST_CLASS";
        int count = 3;

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("uidPrefixCd", "ABC");
        resultMap.put("uidSuffixCd", "XYZ");
        resultMap.put("fromseedValueNbr", "123456");

        // Mock the internal SimpleJdbcCall
        SimpleJdbcCall mockCall = mock(SimpleJdbcCall.class);
        when(mockCall.withProcedureName("GetUid")).thenReturn(mockCall);
        when(mockCall.execute(anyMap())).thenReturn(resultMap);

        // Spy and override the instantiation line
        doReturn(mockCall).when(repoSpy).createJdbcCall();

        // Act
        LocalUidGenerator result = repoSpy.getLocalUID(className, count);

        // Assert
        assertNotNull(result);
        assertEquals("TEST_CLASS", result.getClassNameCd());
        assertEquals("ABC", result.getUidPrefixCd());
        assertEquals("XYZ", result.getUidSuffixCd());
        assertEquals(123456L, result.getSeedValueNbr());
    }

    @Test
    void createJdbcCall_shouldReturnNonNullCall() {
        when(jdbcTemplateOdse.getJdbcTemplate()).thenReturn(jdbcTemplate);

        SimpleJdbcCall call = repoSpy.createJdbcCall();

        assertNotNull(call);
    }

}