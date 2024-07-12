package gov.cdc.dataprocessing.utilities.component.generic_helper;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.container.model.ObservationContainer;
import gov.cdc.dataprocessing.model.container.model.PersonContainer;
import gov.cdc.dataprocessing.model.dto.lab_result.EdxLabInformationDto;
import gov.cdc.dataprocessing.model.dto.person.PersonDto;
import gov.cdc.dataprocessing.service.interfaces.person.IPersonService;
import gov.cdc.dataprocessing.service.model.person.PersonAggContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class ManagerUtilTest {
    @InjectMocks
    private ManagerUtil managerUtil;

    @Mock
    private IPersonService personService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSetPersonUIDOnUpdate() {
        Long personUid = 1L;
        LabResultProxyContainer labResultProxyContainer = new LabResultProxyContainer();
        PersonContainer personContainer = new PersonContainer();
        personContainer.setThePersonDto(new PersonDto());
        personContainer.getThePersonDto().setCdDescTxt(EdxELRConstant.ELR_PATIENT_DESC);
        Collection<PersonContainer> personCollection = new ArrayList<>();
        personCollection.add(personContainer);
        labResultProxyContainer.setThePersonContainerCollection(personCollection);

        managerUtil.setPersonUIDOnUpdate(personUid, labResultProxyContainer);

        assertTrue(personContainer.isItDirty());
        assertEquals(personUid, personContainer.getThePersonDto().getPersonUid());
    }

    @Test
    void testGetObservationWithOrderDomainCode() {
        LabResultProxyContainer labResultProxyContainer = new LabResultProxyContainer();
        ObservationContainer observationContainer = new ObservationContainer();
        observationContainer.getTheObservationDto().setObsDomainCdSt1(EdxELRConstant.ELR_ORDER_CD);
        Collection<ObservationContainer> observationCollection = new ArrayList<>();
        observationCollection.add(observationContainer);
        labResultProxyContainer.setTheObservationContainerCollection(observationCollection);

        ObservationContainer result = managerUtil.getObservationWithOrderDomainCode(labResultProxyContainer);

        assertEquals(observationContainer, result);
    }

    @Test
    void testPatientAggregation() throws DataProcessingConsumerException, DataProcessingException {
        LabResultProxyContainer labResultProxyContainer = new LabResultProxyContainer();
        EdxLabInformationDto edxLabInformationDto = new EdxLabInformationDto();
        PersonContainer personContainer = new PersonContainer();
        personContainer.setRole(EdxELRConstant.ELR_NEXT_OF_KIN);
        Collection<PersonContainer> personCollection = new ArrayList<>();
        personCollection.add(personContainer);
        labResultProxyContainer.setThePersonContainerCollection(personCollection);

        when(personService.processingNextOfKin(labResultProxyContainer, personContainer)).thenReturn(null);

        PersonAggContainer result = managerUtil.patientAggregation(labResultProxyContainer, edxLabInformationDto);

        assertNull(result.getPersonContainer());
        verify(personService, times(1)).processingNextOfKin(labResultProxyContainer, personContainer);
    }

    @Test
    void testPersonAggregationAsync() throws DataProcessingException {
        LabResultProxyContainer labResultProxyContainer = new LabResultProxyContainer();
        EdxLabInformationDto edxLabInformationDto = new EdxLabInformationDto();
        PersonContainer personContainer = new PersonContainer();
        personContainer.setRole(EdxELRConstant.ELR_NEXT_OF_KIN);
        Collection<PersonContainer> personCollection = new ArrayList<>();
        personCollection.add(personContainer);
        labResultProxyContainer.setThePersonContainerCollection(personCollection);

        when(personService.processingNextOfKin(labResultProxyContainer, personContainer)).thenReturn(null);

        PersonAggContainer result = managerUtil.personAggregationAsync(labResultProxyContainer, edxLabInformationDto);

        assertNull(result.getPersonContainer());
        verify(personService, times(1)).processingNextOfKin(labResultProxyContainer, personContainer);
    }


    @Test
    void testGetObservationWithOrderDomainCode_NullScenario() {
        // Arrange
        LabResultProxyContainer labResultProxyVO = new LabResultProxyContainer();
        labResultProxyVO.setTheObservationContainerCollection(new ArrayList<>());

        // Act
        ObservationContainer result = managerUtil.getObservationWithOrderDomainCode(labResultProxyVO);

        // Assert
        assertNull(result, "The result should be null when theObservationContainerCollection is null");
    }

    @Test
    void testPatientAggregation_ElseFlow() throws DataProcessingConsumerException, DataProcessingException {
        // Arrange
        LabResultProxyContainer labResult = new LabResultProxyContainer();
        EdxLabInformationDto edxLabInformationDto = new EdxLabInformationDto();
        Collection<PersonContainer> personContainerCollection = new ArrayList<>();

        PersonContainer patientPersonContainer = new PersonContainer();
        PersonDto patientPersonDto = new PersonDto();
        patientPersonDto.setCd(EdxELRConstant.ELR_PATIENT_CD);
        patientPersonContainer.setThePersonDto(patientPersonDto);

        PersonContainer providerPersonContainer = new PersonContainer();
        PersonDto providerPersonDto = new PersonDto();
        providerPersonDto.setCd(EdxELRConstant.ELR_PROVIDER_CD);
        providerPersonContainer.setThePersonDto(providerPersonDto);

        personContainerCollection.add(patientPersonContainer);
        personContainerCollection.add(providerPersonContainer);
        labResult.setThePersonContainerCollection(personContainerCollection);

        PersonContainer processedPatient = new PersonContainer();
        PersonContainer processedProvider = new PersonContainer();

        when(personService.processingPatient(any(), any(), eq(patientPersonContainer))).thenReturn(processedPatient);
        when(personService.processingProvider(any(), any(), eq(providerPersonContainer), anyBoolean())).thenReturn(processedProvider);

        // Act
        PersonAggContainer result = managerUtil.patientAggregation(labResult, edxLabInformationDto);

        // Assert
        assertNotNull(result);
        assertEquals(processedPatient, result.getPersonContainer());
        assertEquals(processedProvider, result.getProviderContainer());

        verify(personService, times(1)).processingPatient(any(), any(), eq(patientPersonContainer));
        verify(personService, times(1)).processingProvider(any(), any(), eq(providerPersonContainer), anyBoolean());
    }


    @Test
    void testPersonAggregationAsync_PatientProcessing() throws DataProcessingException, DataProcessingConsumerException {
        // Arrange
        LabResultProxyContainer labResult = new LabResultProxyContainer();
        EdxLabInformationDto edxLabInformationDto = new EdxLabInformationDto();
        Collection<PersonContainer> personContainerCollection = new ArrayList<>();

        PersonContainer patientPersonContainer = new PersonContainer();
        PersonDto patientPersonDto = new PersonDto();
        patientPersonDto.setCd(EdxELRConstant.ELR_PATIENT_CD);
        patientPersonContainer.setThePersonDto(patientPersonDto);

        personContainerCollection.add(patientPersonContainer);
        labResult.setThePersonContainerCollection(personContainerCollection);

        PersonContainer processedPatient = new PersonContainer();

        when(personService.processingPatient(any(), any(), eq(patientPersonContainer))).thenReturn(processedPatient);

        // Act
        PersonAggContainer result = managerUtil.personAggregationAsync(labResult, edxLabInformationDto);

        // Assert
        assertNotNull(result);
        assertEquals(processedPatient, result.getPersonContainer());
        assertNull(result.getProviderContainer());

        verify(personService, times(1)).processingPatient(any(), any(), eq(patientPersonContainer));
    }


    @Test
    void testPersonAggregationAsync_MultipleNextOfKinAndPatientProcessing() throws DataProcessingException, DataProcessingConsumerException {
        // Arrange
        LabResultProxyContainer labResult = new LabResultProxyContainer();
        EdxLabInformationDto edxLabInformationDto = new EdxLabInformationDto();
        Collection<PersonContainer> personContainerCollection = new ArrayList<>();

        PersonContainer nextOfKin1 = new PersonContainer();
        nextOfKin1.setRole(EdxELRConstant.ELR_NEXT_OF_KIN);
        PersonContainer nextOfKin2 = new PersonContainer();
        nextOfKin2.setRole(EdxELRConstant.ELR_NEXT_OF_KIN);

        PersonContainer patientPersonContainer = new PersonContainer();
        PersonDto patientPersonDto = new PersonDto();
        patientPersonDto.setCd(EdxELRConstant.ELR_PATIENT_CD);
        patientPersonContainer.setThePersonDto(patientPersonDto);

        personContainerCollection.add(nextOfKin1);
        personContainerCollection.add(nextOfKin2);
        personContainerCollection.add(patientPersonContainer);
        labResult.setThePersonContainerCollection(personContainerCollection);

        PersonContainer processedPatient = new PersonContainer();
        when(personService.processingPatient(any(), any(), eq(patientPersonContainer))).thenReturn(processedPatient);

        // Act
        PersonAggContainer result = managerUtil.personAggregationAsync(labResult, edxLabInformationDto);

        // Assert
        assertNotNull(result);
        assertEquals(processedPatient, result.getPersonContainer());
        assertNull(result.getProviderContainer());

        verify(personService, times(1)).processingNextOfKin(any(), eq(nextOfKin1));
        verify(personService, times(1)).processingNextOfKin(any(), eq(nextOfKin2));
        verify(personService, times(1)).processingPatient(any(), any(), eq(patientPersonContainer));
    }

    @Test
    void testPersonAggregationAsync_NextOfKinException() throws DataProcessingException {
        // Arrange
        LabResultProxyContainer labResult = new LabResultProxyContainer();
        PersonContainer personContainer = new PersonContainer();
        personContainer.setRole(EdxELRConstant.ELR_NEXT_OF_KIN);
        personContainer.setThePersonDto(new PersonDto());
        labResult.setThePersonContainerCollection(Collections.singletonList(personContainer));
        doThrow(new RuntimeException("Test Exception")).when(personService).processingNextOfKin(any(), any());

        // Act & Assert
        DataProcessingException exception = assertThrows(DataProcessingException.class, () -> {
            managerUtil.personAggregationAsync(labResult, new EdxLabInformationDto());
        });

        assertTrue(exception.getMessage().contains("Error processing lab results"));
    }

    @Test
    void testPersonAggregationAsync_NextOfKinDataProcessingException() throws DataProcessingException {
        // Arrange
        LabResultProxyContainer labResult = new LabResultProxyContainer();
        PersonContainer personContainer = new PersonContainer();
        personContainer.setRole(EdxELRConstant.ELR_NEXT_OF_KIN);
        personContainer.setThePersonDto(new PersonDto());
        labResult.setThePersonContainerCollection(Collections.singletonList(personContainer));
        doThrow(new DataProcessingException("Test Data Processing Exception")).when(personService).processingNextOfKin(any(), any());

        // Act & Assert
        DataProcessingException exception = assertThrows(DataProcessingException.class, () -> {
            managerUtil.personAggregationAsync(labResult, new EdxLabInformationDto());
        });

        assertTrue(exception.getMessage().contains("Test Data Processing Exception"));
    }

    @Test
    void testPersonAggregationAsync_PatientDataProcessingConsumerException() throws DataProcessingException, DataProcessingConsumerException {
        // Arrange
        LabResultProxyContainer labResult = new LabResultProxyContainer();
        PersonContainer personContainer = new PersonContainer();
        PersonDto personDto = new PersonDto();
        personDto.setCd(EdxELRConstant.ELR_PATIENT_CD);
        personContainer.setThePersonDto(personDto);
        labResult.setThePersonContainerCollection(Collections.singletonList(personContainer));
        when(personService.processingPatient(any(), any(), any())).thenThrow(new DataProcessingConsumerException("Test Data Processing Consumer Exception"));

        // Act & Assert
        DataProcessingException exception = assertThrows(DataProcessingException.class, () -> {
            managerUtil.personAggregationAsync(labResult, new EdxLabInformationDto());
        });

        assertTrue(exception.getMessage().contains("Error processing lab results"));
    }


    @Test
    void testPersonAggregationAsync_ProviderDataProcessingConsumerException() throws DataProcessingException, DataProcessingConsumerException {
        // Arrange
        LabResultProxyContainer labResult = new LabResultProxyContainer();
        PersonContainer personContainer = new PersonContainer();
        PersonDto personDto = new PersonDto();
        personDto.setCd(EdxELRConstant.ELR_PROVIDER_CD);
        personContainer.setThePersonDto(personDto);
        labResult.setThePersonContainerCollection(Collections.singletonList(personContainer));
        when(personService.processingProvider(any(), any(), any(), anyBoolean())).thenThrow(new DataProcessingConsumerException("Test Data Processing Consumer Exception"));

        // Act & Assert
        DataProcessingException exception = assertThrows(DataProcessingException.class, () -> {
            managerUtil.personAggregationAsync(labResult, new EdxLabInformationDto());
        });

        assertTrue(exception.getMessage().contains("Error processing lab results"));
    }

    @Test
    void testPersonAggregationAsync_ProviderDataProcessingException() throws DataProcessingException, DataProcessingConsumerException {
        // Arrange
        LabResultProxyContainer labResult = new LabResultProxyContainer();
        PersonContainer personContainer = new PersonContainer();
        PersonDto personDto = new PersonDto();
        personDto.setCd(EdxELRConstant.ELR_PROVIDER_CD);
        personContainer.setThePersonDto(personDto);
        labResult.setThePersonContainerCollection(Collections.singletonList(personContainer));
        when(personService.processingProvider(any(), any(), any(), anyBoolean())).thenThrow(new DataProcessingException("Test Data Processing Exception"));

        // Act & Assert
        DataProcessingException exception = assertThrows(DataProcessingException.class, () -> {
            managerUtil.personAggregationAsync(labResult, new EdxLabInformationDto());
        });

        assertTrue(exception.getMessage().contains("Test Data Processing Exception"));
    }

    @Test
    void testPersonAggregationAsync_ProviderProcessing() throws DataProcessingException, DataProcessingConsumerException {
        // Arrange
        LabResultProxyContainer labResult = new LabResultProxyContainer();
        PersonContainer personContainer = new PersonContainer();
        PersonDto personDto = new PersonDto();
        personDto.setCd(EdxELRConstant.ELR_PROVIDER_CD);
        personContainer.setThePersonDto(personDto);
        labResult.setThePersonContainerCollection(Collections.singletonList(personContainer));
        PersonContainer expectedProviderContainer = new PersonContainer();
        when(personService.processingProvider(any(), any(), any(), anyBoolean())).thenReturn(expectedProviderContainer);

        // Act
        PersonAggContainer result = managerUtil.personAggregationAsync(labResult, new EdxLabInformationDto());

        // Assert
        assertNotNull(result);
    }
}
