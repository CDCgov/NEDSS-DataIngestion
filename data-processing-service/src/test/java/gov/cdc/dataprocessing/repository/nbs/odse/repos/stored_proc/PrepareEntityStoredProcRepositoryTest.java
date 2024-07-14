package gov.cdc.dataprocessing.repository.nbs.odse.repos.stored_proc;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.repository.nbs.odse.model.generic_helper.PrepareEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class PrepareEntityStoredProcRepositoryTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private StoredProcedureQuery storedProcedureQuery;

    @InjectMocks
    private PrepareEntityStoredProcRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetPrepareEntity() throws DataProcessingException {
        String businessTriggerCd = "triggerCd";
        String moduleCd = "moduleCd";
        Long uid = 1L;
        String tableName = "tableName";

        when(entityManager.createStoredProcedureQuery(anyString())).thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.registerStoredProcedureParameter(anyString(), eq(String.class), eq(ParameterMode.IN))).thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.registerStoredProcedureParameter(anyString(), eq(Timestamp.class), eq(ParameterMode.OUT))).thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.setParameter(anyString(), any())).thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.getOutputParameterValue("localId")).thenReturn("localId");
        when(storedProcedureQuery.getOutputParameterValue("addUserId")).thenReturn("123");
        when(storedProcedureQuery.getOutputParameterValue("addUserTime")).thenReturn(new Timestamp(System.currentTimeMillis()));
        when(storedProcedureQuery.getOutputParameterValue("recordStatusState")).thenReturn("recordStatus");
        when(storedProcedureQuery.getOutputParameterValue("objectStatusState")).thenReturn("objectStatus");

        PrepareEntity result = repository.getPrepareEntity(businessTriggerCd, moduleCd, uid, tableName);

        assertEquals("localId", result.getLocalId());
        assertEquals(123L, result.getAddUserId());
        assertEquals("recordStatus", result.getRecordStatusState());
        assertEquals("objectStatus", result.getObjectStatusState());
    }

    @Test
    void testGetPrepareEntity_ThrowsException() {
        String businessTriggerCd = "triggerCd";
        String moduleCd = "moduleCd";
        Long uid = 1L;
        String tableName = "tableName";

        when(entityManager.createStoredProcedureQuery(anyString())).thenThrow(new RuntimeException("Database error"));

        assertThrows(DataProcessingException.class, () -> repository.getPrepareEntity(businessTriggerCd, moduleCd, uid, tableName));
    }
}
