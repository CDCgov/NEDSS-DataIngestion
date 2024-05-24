package gov.cdc.dataprocessing.service.implementation.person;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.PersonContainer;
import gov.cdc.dataprocessing.model.dto.matching.EdxPatientMatchDto;
import gov.cdc.dataprocessing.service.implementation.other.CachingValueService;
import gov.cdc.dataprocessing.service.implementation.person.base.MatchingBaseService;
import gov.cdc.dataprocessing.service.interfaces.person.IPatientMatchingService;
import gov.cdc.dataprocessing.utilities.component.entity.EntityHelper;
import gov.cdc.dataprocessing.utilities.component.patient.EdxPatientMatchRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.patient.PatientRepositoryUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

class PatientMatchingServiceTest {

    //@Mock
    private EdxPatientMatchRepositoryUtil edxPatientMatchRepositoryUtilMock;
    @Mock
    private EntityHelper entityHelperMock;
    @Mock
    private PatientRepositoryUtil patientRepositoryUtilMock;
    @Mock
    private CachingValueService cachingValueServiceMock;

    @InjectMocks
    private PatientMatchingService patientMatchingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(edxPatientMatchRepositoryUtilMock);
        Mockito.reset(entityHelperMock);
        Mockito.reset(patientRepositoryUtilMock);
        Mockito.reset(cachingValueServiceMock);
    }

    @Test
    void getMatchingPatient() throws DataProcessingException {
        PersonContainer personContainer=new PersonContainer();
        personContainer.getThePersonDto().setPersonUid(123L);
        personContainer.getThePersonDto().setCd("TEST_CD");
        personContainer.setLocalIdentifier("123");

        EdxPatientMatchDto edxPatientMatchDto = new EdxPatientMatchDto();
        edxPatientMatchDto.setPatientUid(123L);
        edxPatientMatchDto.setMatchStringHashCode(1234567L);
        edxPatientMatchDto.setTypeCd("TEST_TYPE_CD");
        edxPatientMatchDto.setMatchString("TEST_MATCH_STRING");

        //when(patientMatchingService.getEdxPatientMatchRepositoryUtil().getEdxPatientMatchOnMatchString("TEST_TYPE_CD","TEST_MATCH_STRING")).thenReturn(edxPatientMatchDto);

//        PatientMatchingService patientMatchingServiceSpy = Mockito.spy(new PatientMatchingService(edxPatientMatchRepositoryUtilMock, entityHelperMock, patientRepositoryUtilMock, cachingValueServiceMock));
        //when(patientMatchingServiceSpy.getEdxPatientMatchRepositoryUtil().getEdxPatientMatchOnMatchString("TEST_TYPE_CD","TEST_MATCH_STRING")).thenReturn(edxPatientMatchDto);

        //when(matchingBaseService.getEdxPatientMatchRepositoryUtil().getEdxPatientMatchOnMatchString("TEST_TYPE_CD","TEST_MATCH_STRING")).thenReturn(edxPatientMatchDto);

       patientMatchingService.getMatchingPatient(personContainer);
        //System.out.println("edxPatientMatchDto1"+edxPatientMatchDto1);
    }

    @Test
    void getMultipleMatchFound() {
    }

    @Test
    void updateExistingPerson() {
    }
}