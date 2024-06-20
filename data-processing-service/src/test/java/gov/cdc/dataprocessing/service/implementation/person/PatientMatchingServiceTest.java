package gov.cdc.dataprocessing.service.implementation.person;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.PersonContainer;
import gov.cdc.dataprocessing.model.dto.matching.EdxPatientMatchDto;
import gov.cdc.dataprocessing.service.implementation.cache.CachingValueService;
import gov.cdc.dataprocessing.service.implementation.person.base.PatientMatchingBaseService;
import gov.cdc.dataprocessing.utilities.component.entity.EntityHelper;
import gov.cdc.dataprocessing.utilities.component.generic_helper.PrepareAssocModelHelper;
import gov.cdc.dataprocessing.utilities.component.patient.EdxPatientMatchRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.patient.PatientRepositoryUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class PatientMatchingServiceTest {

    @Mock
    private EdxPatientMatchRepositoryUtil edxPatientMatchRepositoryUtil;
    @Mock
    private EntityHelper entityHelper;
    @Mock
    private PatientRepositoryUtil patientRepositoryUtil;
    @Mock
    private CachingValueService cachingValueService;
    @Mock
    private PrepareAssocModelHelper prepareAssocModelHelper;

    @InjectMocks
    private PatientMatchingService patientMatchingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(edxPatientMatchRepositoryUtil);
        Mockito.reset(entityHelper);
        Mockito.reset(patientRepositoryUtil);
        Mockito.reset(cachingValueService);
        Mockito.reset(prepareAssocModelHelper);
    }

    @Test
    void getMatchingPatient() throws DataProcessingException {
        PersonContainer personContainer=new PersonContainer();
        personContainer.thePersonDto.setPersonUid(123L);
        personContainer.setLocalIdentifier("123");
        personContainer.setRole(EdxELRConstant.ELR_PATIENT_ROLE_CD);

        EdxPatientMatchDto edxPatientMatchFoundDT = new EdxPatientMatchDto();
        edxPatientMatchFoundDT.setMultipleMatch(true);
        when(edxPatientMatchRepositoryUtil.getEdxPatientMatchOnMatchString(any(),any())).thenReturn(edxPatientMatchFoundDT);
        //call test method
        patientMatchingService.getMatchingPatient(personContainer);
    }

    @Test
    void getMatchingPatient_throw_exp_nullpointer() throws DataProcessingException {
        PersonContainer personContainer=new PersonContainer();
        personContainer.thePersonDto.setPersonUid(123L);
        personContainer.setLocalIdentifier("123");
        personContainer.setRole(EdxELRConstant.ELR_PATIENT_ROLE_CD);

        EdxPatientMatchDto edxPatientMatchFoundDT = new EdxPatientMatchDto();
        edxPatientMatchFoundDT.setMultipleMatch(true);
        when(edxPatientMatchRepositoryUtil.getEdxPatientMatchOnMatchString(any(),any())).thenReturn(edxPatientMatchFoundDT);
        //call test method
        //patientMatchingService.getMatchingPatient(personContainer);
        assertThrows(DataProcessingException.class, () -> patientMatchingService.getMatchingPatient(personContainer));
    }

    @Test
    void getMultipleMatchFound() {
    }

    @Test
    void updateExistingPerson() {
    }
}