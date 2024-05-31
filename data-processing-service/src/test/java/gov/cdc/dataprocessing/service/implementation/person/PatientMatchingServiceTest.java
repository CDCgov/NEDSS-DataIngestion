package gov.cdc.dataprocessing.service.implementation.person;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.service.implementation.other.CachingValueService;
import gov.cdc.dataprocessing.utilities.component.entity.EntityHelper;
import gov.cdc.dataprocessing.utilities.component.patient.EdxPatientMatchRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.patient.PatientRepositoryUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

class PatientMatchingServiceTest {

    @Mock
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

    }

    @Test
    void getMultipleMatchFound() {
    }

    @Test
    void updateExistingPerson() {
    }
}