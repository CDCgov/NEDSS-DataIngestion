package gov.cdc.dataprocessing.repository.nbs.odse.repos.stored_proc;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class ProgAreaSnomeCodeStoredProcRepositoryTest {
    @Mock
    private EntityManager entityManager;

    @Mock
    private StoredProcedureQuery storedProcedureQuery;

    @InjectMocks
    private ProgAreaSnomeCodeStoredProcRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetSnomed() throws DataProcessingException {
        String code = "testCode";
        String type = "testType";
        String clia = "testClia";

        when(entityManager.createStoredProcedureQuery(anyString())).thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.registerStoredProcedureParameter(anyString(), eq(String.class), eq(ParameterMode.IN))).thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.registerStoredProcedureParameter(anyString(), eq(Integer.class), eq(ParameterMode.OUT))).thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.setParameter(anyString(), anyString())).thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.getOutputParameterValue("loinc_snomed")).thenReturn("testLoincSnomed");
        when(storedProcedureQuery.getOutputParameterValue("count")).thenReturn(123);

        Map<String, Object> result = repository.getSnomed(code, type, clia);

        assertEquals("testLoincSnomed", result.get("LOINC"));
        assertEquals(123, result.get("COUNT"));
    }

    @Test
    void testGetSnomed_ThrowsException() {
        String code = "testCode";
        String type = "testType";
        String clia = "testClia";

        when(entityManager.createStoredProcedureQuery(anyString())).thenThrow(new RuntimeException("Database error"));

        assertThrows(DataProcessingException.class, () -> repository.getSnomed(code, type, clia));
    }

    @Test
    void testGetProgAreaCd() throws DataProcessingException {
        String code = "testCode";
        String type = "testType";
        String clia = "testClia";

        when(entityManager.createStoredProcedureQuery(anyString())).thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.registerStoredProcedureParameter(anyString(), eq(String.class), eq(ParameterMode.IN))).thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.registerStoredProcedureParameter(anyString(), eq(Integer.class), eq(ParameterMode.OUT))).thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.setParameter(anyString(), anyString())).thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.getOutputParameterValue("prog_area")).thenReturn("testProgArea");
        when(storedProcedureQuery.getOutputParameterValue("count")).thenReturn(456);

        Map<String, Object> result = repository.getProgAreaCd(code, type, clia);

        assertEquals("testProgArea", result.get("PROGRAM"));
        assertEquals(456, result.get("COUNT"));
    }

    @Test
    void testGetProgAreaCd_ThrowsException() {
        String code = "testCode";
        String type = "testType";
        String clia = "testClia";

        when(entityManager.createStoredProcedureQuery(anyString())).thenThrow(new RuntimeException("Database error"));

        assertThrows(DataProcessingException.class, () -> repository.getProgAreaCd(code, type, clia));
    }
}
