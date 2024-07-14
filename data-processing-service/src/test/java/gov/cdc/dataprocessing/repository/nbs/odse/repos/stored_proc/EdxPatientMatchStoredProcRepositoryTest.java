package gov.cdc.dataprocessing.repository.nbs.odse.repos.stored_proc;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.matching.EdxEntityMatchDto;
import gov.cdc.dataprocessing.model.dto.matching.EdxPatientMatchDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class EdxPatientMatchStoredProcRepositoryTest {
    @Mock
    private EntityManager entityManager;

    @Mock
    private StoredProcedureQuery storedProcedureQuery;

    @InjectMocks
    private EdxPatientMatchStoredProcRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetEdxPatientMatch() throws DataProcessingException {
        String typeCd = "testType";
        String matchString = "testMatch";

        when(entityManager.createStoredProcedureQuery(anyString())).thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.registerStoredProcedureParameter(anyString(), eq(String.class), eq(ParameterMode.IN))).thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.registerStoredProcedureParameter(anyString(), eq(Long.class), eq(ParameterMode.OUT))).thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.setParameter(anyString(), anyString())).thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.getOutputParameterValue("Patient_uid")).thenReturn(123L);
        when(storedProcedureQuery.getOutputParameterValue("match_string_hashcode")).thenReturn(456L);

        EdxPatientMatchDto result = repository.getEdxPatientMatch(typeCd, matchString);

        assertEquals(123L, result.getPatientUid());
        assertEquals(456L, result.getMatchStringHashCode());
        assertEquals(typeCd, result.getTypeCd());
        assertEquals(matchString, result.getMatchString());
    }

    @Test
    void testGetEdxPatientMatch_ThrowsException() {
        String typeCd = "testType";
        String matchString = "testMatch";

        when(entityManager.createStoredProcedureQuery(anyString())).thenThrow(new RuntimeException("Database error"));

        assertThrows(DataProcessingException.class, () -> repository.getEdxPatientMatch(typeCd, matchString));
    }

    @Test
    void testGetEdxEntityMatch() throws DataProcessingException {
        String typeCd = "testType";
        String matchString = "testMatch";

        when(entityManager.createStoredProcedureQuery(anyString())).thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.registerStoredProcedureParameter(anyString(), eq(String.class), eq(ParameterMode.IN))).thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.registerStoredProcedureParameter(anyString(), eq(Long.class), eq(ParameterMode.OUT))).thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.setParameter(anyString(), anyString())).thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.getOutputParameterValue("out_entity_uid")).thenReturn(123L);

        EdxEntityMatchDto result = repository.getEdxEntityMatch(typeCd, matchString);

        assertEquals(123L, result.getEntityUid());
        assertEquals(typeCd, result.getTypeCd());
        assertEquals(matchString, result.getMatchString());
    }

    @Test
    void testGetEdxEntityMatch_ThrowsException() {
        String typeCd = "testType";
        String matchString = "testMatch";

        when(entityManager.createStoredProcedureQuery(anyString())).thenThrow(new RuntimeException("Database error"));

        assertThrows(DataProcessingException.class, () -> repository.getEdxEntityMatch(typeCd, matchString));
    }
}
