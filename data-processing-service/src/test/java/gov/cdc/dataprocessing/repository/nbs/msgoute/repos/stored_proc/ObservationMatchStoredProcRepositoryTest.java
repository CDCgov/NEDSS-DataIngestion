package gov.cdc.dataprocessing.repository.nbs.msgoute.repos.stored_proc;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.ObservationContainer;
import gov.cdc.dataprocessing.model.dto.lab_result.EdxLabInformationDto;
import gov.cdc.dataprocessing.model.dto.observation.ObservationDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
class ObservationMatchStoredProcRepositoryTest {
    @Mock
    private EntityManager entityManager;

    @Mock
    private StoredProcedureQuery storedProcedureQuery;

    @InjectMocks
    private ObservationMatchStoredProcRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetMatchedObservation() throws DataProcessingException {
        EdxLabInformationDto edxLabInformationDto = new EdxLabInformationDto();
        ObservationContainer observationContainer = new ObservationContainer();
        ObservationDto observationDto = new ObservationDto();
        observationDto.setEffectiveFromTime(new Timestamp(System.currentTimeMillis()));
        observationDto.setCd("testCode");
        observationContainer.setTheObservationDto(observationDto);
        edxLabInformationDto.setRootObservationContainer(observationContainer);
        edxLabInformationDto.setSendingFacilityClia("testClia");
        edxLabInformationDto.setFillerNumber("testFiller");

        when(entityManager.createStoredProcedureQuery(anyString())).thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.registerStoredProcedureParameter(anyString(), eq(String.class), eq(ParameterMode.IN))).thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.registerStoredProcedureParameter(anyString(), eq(Timestamp.class), eq(ParameterMode.IN))).thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.registerStoredProcedureParameter(anyString(), eq(Integer.class), eq(ParameterMode.IN))).thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.registerStoredProcedureParameter(anyString(), eq(Long.class), eq(ParameterMode.OUT))).thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.setParameter(anyString(), any())).thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.getOutputParameterValue("Observation_uid")).thenReturn(123L);

        Long result = repository.getMatchedObservation(edxLabInformationDto);

        assertEquals(123L, result);
        verify(entityManager).createStoredProcedureQuery("GetObservationMatch_SP");
        verify(storedProcedureQuery).registerStoredProcedureParameter("fillerNbr", String.class, ParameterMode.IN);
        verify(storedProcedureQuery).registerStoredProcedureParameter("labCLIA", String.class, ParameterMode.IN);
        verify(storedProcedureQuery).registerStoredProcedureParameter("orderedTestCd", String.class, ParameterMode.IN);
        verify(storedProcedureQuery).registerStoredProcedureParameter("specimenCollectionDate", Timestamp.class, ParameterMode.IN);
        verify(storedProcedureQuery).registerStoredProcedureParameter("numberOfGoBackYears", Integer.class, ParameterMode.IN);
        verify(storedProcedureQuery).registerStoredProcedureParameter("Observation_uid", Long.class, ParameterMode.OUT);
        verify(storedProcedureQuery).setParameter("fillerNbr", "testFiller");
        verify(storedProcedureQuery).setParameter("labCLIA", "testClia");
        verify(storedProcedureQuery).setParameter("orderedTestCd", "testCode");
        verify(storedProcedureQuery).setParameter("specimenCollectionDate", observationDto.getEffectiveFromTime());
        verify(storedProcedureQuery).setParameter("numberOfGoBackYears", 2);
        verify(storedProcedureQuery).execute();
    }

    @Test
    void testGetMatchedObservation_NullValues() throws DataProcessingException {
        EdxLabInformationDto edxLabInformationDto = new EdxLabInformationDto();
        ObservationContainer observationContainer = new ObservationContainer();
        ObservationDto observationDto = new ObservationDto();
        observationContainer.setTheObservationDto(observationDto);
        edxLabInformationDto.setRootObservationContainer(observationContainer);

        Long result = repository.getMatchedObservation(edxLabInformationDto);

        assertNull(result);
    }

    @Test
    void testGetMatchedObservation_ThrowsException() {
        EdxLabInformationDto edxLabInformationDto = new EdxLabInformationDto();
        ObservationContainer observationContainer = new ObservationContainer();
        ObservationDto observationDto = new ObservationDto();
        observationDto.setEffectiveFromTime(new Timestamp(System.currentTimeMillis()));
        observationDto.setCd("testCode");
        observationContainer.setTheObservationDto(observationDto);
        edxLabInformationDto.setRootObservationContainer(observationContainer);
        edxLabInformationDto.setSendingFacilityClia("testClia");
        edxLabInformationDto.setFillerNumber("testFiller");

        when(entityManager.createStoredProcedureQuery(anyString())).thenThrow(new RuntimeException("Database error"));

        assertThrows(DataProcessingException.class, () -> repository.getMatchedObservation(edxLabInformationDto));
    }
}
