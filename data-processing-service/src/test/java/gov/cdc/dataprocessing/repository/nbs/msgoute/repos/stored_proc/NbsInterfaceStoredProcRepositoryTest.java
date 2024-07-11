package gov.cdc.dataprocessing.repository.nbs.msgoute.repos.stored_proc;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class NbsInterfaceStoredProcRepositoryTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private StoredProcedureQuery storedProcedureQuery;

    @InjectMocks
    private NbsInterfaceStoredProcRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUpdateSpecimenCollDateSP() throws DataProcessingException {
        Long nbsInterfaceUid = 1L;
        Timestamp specimenCollectionDate = new Timestamp(System.currentTimeMillis());

        when(entityManager.createStoredProcedureQuery(anyString())).thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.registerStoredProcedureParameter(anyString(), eq(Integer.class), eq(ParameterMode.IN))).thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.registerStoredProcedureParameter(anyString(), eq(Timestamp.class), eq(ParameterMode.IN))).thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.setParameter(anyString(), any())).thenReturn(storedProcedureQuery);

        repository.updateSpecimenCollDateSP(nbsInterfaceUid, specimenCollectionDate);

        verify(entityManager).createStoredProcedureQuery("UpdateSpecimenCollDate_SP");
        verify(storedProcedureQuery).registerStoredProcedureParameter("NBSInterfaceUid", Integer.class, ParameterMode.IN);
        verify(storedProcedureQuery).registerStoredProcedureParameter("specimentCollectionDate", Timestamp.class, ParameterMode.IN);
        verify(storedProcedureQuery).setParameter("NBSInterfaceUid", nbsInterfaceUid);
        verify(storedProcedureQuery).setParameter("specimentCollectionDate", specimenCollectionDate);
        verify(storedProcedureQuery).execute();
    }

    @Test
    void testUpdateSpecimenCollDateSP_ThrowsException() {
        Long nbsInterfaceUid = 1L;
        Timestamp specimenCollectionDate = new Timestamp(System.currentTimeMillis());

        when(entityManager.createStoredProcedureQuery(anyString())).thenThrow(new RuntimeException("Database error"));

        assertThrows(DataProcessingException.class, () -> repository.updateSpecimenCollDateSP(nbsInterfaceUid, specimenCollectionDate));
    }
}
