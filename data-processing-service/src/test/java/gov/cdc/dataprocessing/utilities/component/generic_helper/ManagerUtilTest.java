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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

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
    public void testSetPersonUIDOnUpdate() {
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
    public void testGetObservationWithOrderDomainCode() {
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
    public void testPatientAggregation() throws DataProcessingConsumerException, DataProcessingException {
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
    public void testPersonAggregationAsync() throws DataProcessingException {
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
}
