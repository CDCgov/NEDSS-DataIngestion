package gov.cdc.dataprocessing.utilities.component.generic_helper;

import gov.cdc.dataprocessing.constant.elr.DataTables;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.RootDtoInterface;
import gov.cdc.dataprocessing.model.dto.observation.ObservationDto;
import gov.cdc.dataprocessing.model.dto.organization.OrganizationDto;
import gov.cdc.dataprocessing.model.dto.person.PersonDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConcurrentCheckTest {
    @InjectMocks
    private ConcurrentCheck concurrentCheck;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testDataConcurrenceCheckPersonWithMatchingVersion() throws DataProcessingException {
        PersonDto personDto = mock(PersonDto.class);
        when(personDto.getVersionCtrlNbr()).thenReturn(1);

        boolean result = concurrentCheck.dataConcurrenceCheck(personDto, "Person", 1);

        assertTrue(result);
    }

    @Test
    void testDataConcurrenceCheckPersonWithNonMatchingVersion() throws DataProcessingException {
        PersonDto personDto = mock(PersonDto.class);
        when(personDto.getVersionCtrlNbr()).thenReturn(2);

        boolean result = concurrentCheck.dataConcurrenceCheck(personDto, "Person", 1);

        assertFalse(result);
    }

    @Test
    void testDataConcurrenceCheckPersonWithNullVersion() throws DataProcessingException {
        PersonDto personDto = mock(PersonDto.class);
        when(personDto.getVersionCtrlNbr()).thenReturn(null);

        boolean result = concurrentCheck.dataConcurrenceCheck(personDto, "Person", null);

        assertTrue(result);
        verify(personDto).setVersionCtrlNbr(1);
    }

    @Test
    void testDataConcurrenceCheckOrganizationWithMatchingVersion() throws DataProcessingException {
        OrganizationDto organizationDto = mock(OrganizationDto.class);
        when(organizationDto.getVersionCtrlNbr()).thenReturn(1);

        boolean result = concurrentCheck.dataConcurrenceCheck(organizationDto, DataTables.ORGANIZATION_TABLE, 1);

        assertTrue(result);
    }

    @Test
    void testDataConcurrenceCheckOrganizationWithNonMatchingVersion() throws DataProcessingException {
        OrganizationDto organizationDto = mock(OrganizationDto.class);
        when(organizationDto.getVersionCtrlNbr()).thenReturn(2);

        boolean result = concurrentCheck.dataConcurrenceCheck(organizationDto, DataTables.ORGANIZATION_TABLE, 1);

        assertFalse(result);
    }

    @Test
    void testDataConcurrenceCheckObservationWithMatchingVersion() throws DataProcessingException {
        ObservationDto observationDto = mock(ObservationDto.class);
        when(observationDto.getVersionCtrlNbr()).thenReturn(1);

        boolean result = concurrentCheck.dataConcurrenceCheck(observationDto, "Observation", 1);

        assertTrue(result);
    }

    @Test
    void testDataConcurrenceCheckObservationWithNonMatchingVersion() throws DataProcessingException {
        ObservationDto observationDto = mock(ObservationDto.class);
        when(observationDto.getVersionCtrlNbr()).thenReturn(2);

        boolean result = concurrentCheck.dataConcurrenceCheck(observationDto, "Observation", 1);

        assertFalse(result);
    }

    @Test
    void testDataConcurrenceCheckWithException() {
        RootDtoInterface rootDto = mock(RootDtoInterface.class);
        doThrow(new RuntimeException("Test Exception")).when(rootDto).getVersionCtrlNbr();

        assertThrows(RuntimeException.class, () -> {
            concurrentCheck.dataConcurrenceCheck(rootDto, "Person", 1);
        });
    }

    @Test
    void testDataConcurrenceCheck_1() throws DataProcessingException {
        PersonDto personDto = mock(PersonDto.class);
        when(personDto.getVersionCtrlNbr()).thenReturn(2);

        when(personDto.isReentrant()).thenReturn(true);

        boolean result = concurrentCheck.dataConcurrenceCheck(personDto, "Person", 1);

        assertTrue(result);
    }

    @Test
    void testDataConcurrenceCheckOrganization_1() throws DataProcessingException {
        OrganizationDto organizationDto = mock(OrganizationDto.class);
        when(organizationDto.getVersionCtrlNbr()).thenReturn(null);

        boolean result = concurrentCheck.dataConcurrenceCheck(organizationDto, DataTables.ORGANIZATION_TABLE, 1);

        assertFalse(result);
    }

    @Test
    void testDataConcurrenceCheckObs_1() throws DataProcessingException {
        ObservationDto organizationDto = mock(ObservationDto.class);
        when(organizationDto.getVersionCtrlNbr()).thenReturn(null);

        boolean result = concurrentCheck.dataConcurrenceCheck(organizationDto, "Observation", 1);

        assertFalse(result);
    }
}
