package gov.cdc.dataprocessing.utilities.component.patient;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.model.container.model.PersonContainer;
import gov.cdc.dataprocessing.model.dto.observation.ObservationDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.service.implementation.person.PatientMatchingService;
import gov.cdc.dataprocessing.service.implementation.person.ProviderMatchingService;
import gov.cdc.dataprocessing.service.interfaces.uid_generator.IUidService;
import gov.cdc.dataprocessing.service.model.auth_user.AuthUserProfileInfo;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.observation.ObservationUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class PersonUtilTest {
    @Mock
    AuthUtil authUtil;
    @Mock
    private ObservationUtil observationUtil;
    @Mock
    private PatientRepositoryUtil patientRepositoryUtil;
    @Mock
    private PatientMatchingService patientMatchingService;
    @Mock
    private ProviderMatchingService providerMatchingService;
    @Mock
    private IUidService uidService;
    @InjectMocks
    private PersonUtil personUtil;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        AuthUserProfileInfo userInfo = new AuthUserProfileInfo();
        AuthUser user = new AuthUser();
        user.setAuthUserUid(1L);
        user.setUserType(NEDSSConstant.SEC_USERTYPE_EXTERNAL);
        userInfo.setAuthUser(user);

        AuthUtil.setGlobalAuthUser(userInfo);
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(observationUtil, patientRepositoryUtil, patientMatchingService,
                providerMatchingService, uidService, authUtil);
    }

    @Test
    void processLabPersonContainerCollection_Test() {
        var personContainerCollection = new ArrayList<PersonContainer>();
        boolean morbidityApplied = true;
        BaseContainer dataContainer = new BaseContainer();


        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            personUtil.processLabPersonContainerCollection(personContainerCollection, morbidityApplied, dataContainer);
        });

        assertNotNull(thrown);
    }

    @Test
    void processLabPersonContainerCollection_Test_2() throws DataProcessingException {
        var personContainerCollection = new ArrayList<PersonContainer>();
        boolean morbidityApplied = true;
        BaseContainer dataContainer = new BaseContainer();

        var perCon = new PersonContainer();
        personContainerCollection.add(null);
        personContainerCollection.add(perCon);

        var obsDto = new ObservationDto();
        when(observationUtil.getRootObservationDto(any())).thenReturn(obsDto);

        DataProcessingException res = assertThrows(DataProcessingException.class, () -> {
            personUtil.processLabPersonContainerCollection(personContainerCollection, morbidityApplied, dataContainer);
        });
        assertNotNull(res);
    }

    @Test
    void processLabPersonContainerCollection_Test_3() throws DataProcessingException {
        var personContainerCollection = new ArrayList<PersonContainer>();
        boolean morbidityApplied = true;
        BaseContainer dataContainer = new BaseContainer();

        var perCon = new PersonContainer();
        perCon.getThePersonDto().setCd("PAT");
        perCon.setRole(null);
        perCon.setItNew(true);
        perCon.getThePersonDto().setPersonUid(-1L);
        personContainerCollection.add(null);
        personContainerCollection.add(perCon);

        var obsDto = new ObservationDto();
        obsDto.setElectronicInd("Y");
        when(observationUtil.getRootObservationDto(any())).thenReturn(obsDto);

        // setPersonForObservationFlow
        when(patientMatchingService.updateExistingPerson(any(), any())).thenReturn(11L);


        var res = personUtil.processLabPersonContainerCollection(personContainerCollection, morbidityApplied, dataContainer);

        assertNotNull(res);
    }


    @Test
    void processLabPersonContainerCollection_Test_4() throws DataProcessingException {
        var personContainerCollection = new ArrayList<PersonContainer>();
        boolean morbidityApplied = true;
        BaseContainer dataContainer = new BaseContainer();

        var perCon = new PersonContainer();
        perCon.getThePersonDto().setCd("PRV");
        perCon.setRole(null);
        perCon.setItNew(true);
        perCon.getThePersonDto().setPersonUid(-1L);
        personContainerCollection.add(null);
        personContainerCollection.add(perCon);

        var obsDto = new ObservationDto();
        obsDto.setElectronicInd("Y");
        when(observationUtil.getRootObservationDto(any())).thenReturn(obsDto);

        // setPersonForObservationFlow
        when(providerMatchingService.setProvider(any(), any())).thenReturn(11L);


        var res = personUtil.processLabPersonContainerCollection(personContainerCollection, morbidityApplied, dataContainer);

        assertNull(res);
    }

    @Test
    void processLabPersonContainerCollection_Test_5() throws DataProcessingException {
        var personContainerCollection = new ArrayList<PersonContainer>();
        boolean morbidityApplied = true;
        BaseContainer dataContainer = new BaseContainer();

        var perCon = new PersonContainer();
        perCon.getThePersonDto().setCd("PRV");
        perCon.setRole(null);
        perCon.setItNew(true);
        perCon.getThePersonDto().setPersonUid(-1L);
        personContainerCollection.add(null);
        personContainerCollection.add(perCon);

        var obsDto = new ObservationDto();
        obsDto.setElectronicInd("Y");
        when(observationUtil.getRootObservationDto(any())).thenReturn(obsDto);

        // setPersonForObservationFlow
        when(providerMatchingService.setProvider(any(), any())).thenThrow(new RuntimeException("TEST"));


        DataProcessingException res = assertThrows(DataProcessingException.class, () -> {
            personUtil.processLabPersonContainerCollection(personContainerCollection, morbidityApplied, dataContainer);
        });
        assertNotNull(res);
    }

    @Test
    void processLabPersonContainerCollection_Test_6() throws DataProcessingException {
        var personContainerCollection = new ArrayList<PersonContainer>();
        boolean morbidityApplied = true;
        BaseContainer dataContainer = new BaseContainer();

        var perCon = new PersonContainer();
        perCon.getThePersonDto().setCd("BLAH");
        perCon.setRole(null);
        perCon.setItNew(true);
        perCon.getThePersonDto().setPersonUid(-1L);
        personContainerCollection.add(null);
        personContainerCollection.add(perCon);

        var obsDto = new ObservationDto();
        obsDto.setElectronicInd("Y");
        when(observationUtil.getRootObservationDto(any())).thenReturn(obsDto);


        DataProcessingException res = assertThrows(DataProcessingException.class, () -> {
            personUtil.processLabPersonContainerCollection(personContainerCollection, morbidityApplied, dataContainer);
        });
        assertNotNull(res);
    }


    @Test
    void processLabPersonContainerCollection_Test_7() throws DataProcessingException {
        var personContainerCollection = new ArrayList<PersonContainer>();
        boolean morbidityApplied = true;
        BaseContainer dataContainer = new BaseContainer();

        var perCon = new PersonContainer();
        perCon.getThePersonDto().setCd("PAT");
        perCon.setRole("PAT");
        perCon.setItNew(true);
        perCon.getThePersonDto().setPersonUid(10L);
        personContainerCollection.add(null);
        personContainerCollection.add(perCon);

        var obsDto = new ObservationDto();
        obsDto.setElectronicInd("Y");
        when(observationUtil.getRootObservationDto(any())).thenReturn(obsDto);


        var res = personUtil.processLabPersonContainerCollection(personContainerCollection, morbidityApplied, dataContainer);

        assertNotNull(res);
    }
}
