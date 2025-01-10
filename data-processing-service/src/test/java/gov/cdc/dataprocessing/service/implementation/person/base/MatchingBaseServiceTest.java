package gov.cdc.dataprocessing.service.implementation.person.base;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.PersonContainer;
import gov.cdc.dataprocessing.model.dto.entity.EntityIdDto;
import gov.cdc.dataprocessing.model.dto.person.PersonNameDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.service.implementation.cache.CachingValueService;
import gov.cdc.dataprocessing.service.model.auth_user.AuthUserProfileInfo;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.entity.EntityHelper;
import gov.cdc.dataprocessing.utilities.component.generic_helper.PrepareAssocModelHelper;
import gov.cdc.dataprocessing.utilities.component.patient.EdxPatientMatchRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.patient.PatientRepositoryUtil;
import gov.cdc.dataprocessing.utilities.model.Coded;
import gov.cdc.dataprocessing.utilities.time.TimeStampUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MatchingBaseServiceTest {
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
    @Spy
    private MatchingBaseService matchingBaseService;
    @Mock
    AuthUtil authUtil;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        AuthUserProfileInfo userInfo = new AuthUserProfileInfo();
        AuthUser user = new AuthUser();
        user.setAuthUserUid(1L);
        user.setUserType(NEDSSConstant.SEC_USERTYPE_EXTERNAL);
        userInfo.setAuthUser(user);

        authUtil.setGlobalAuthUser(userInfo);
    }
    @Mock
    private PersonContainer personContainer;
    @Mock
    private Coded coded;

    @AfterEach
    void tearDown() {
        Mockito.reset(edxPatientMatchRepositoryUtil, coded, entityHelper,patientRepositoryUtil,cachingValueService,prepareAssocModelHelper, personContainer, authUtil);
    }


    @Test
    void testGetIdentifier_NoEntityIdDtoCollection() throws DataProcessingException {
        when(personContainer.getTheEntityIdDtoCollection()).thenReturn(null);

        List<String> identifiers = matchingBaseService.getIdentifier(personContainer);

        assertEquals(0, identifiers.size());
    }

    @Test
    void testGetIdentifier_EmptyEntityIdDtoCollection() throws DataProcessingException {
        when(personContainer.getTheEntityIdDtoCollection()).thenReturn(Arrays.asList());

        List<String> identifiers = matchingBaseService.getIdentifier(personContainer);

        assertEquals(0, identifiers.size());
    }

    @Test
    void getIdentifier_Test() throws DataProcessingException {
        var entityCol = new ArrayList<EntityIdDto>();
        var entity = new EntityIdDto();
        entity.setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);
        entity.setTypeCd(EdxELRConstant.ELR_SS_TYPE);
        entity.setRootExtensionTxt("ROOT");
        entity.setTypeCd(EdxELRConstant.ELR_SS_TYPE);
        entity.setAssigningAuthorityCd("TEST");
        entityCol.add(entity);
        when(personContainer.getTheEntityIdDtoCollection()).thenReturn(entityCol);

        matchingBaseService.getIdentifier(personContainer);

        verify(personContainer, times(3)).getTheEntityIdDtoCollection();
    }

    @Test
    void getIdentifier_Test_2()  {
        when(personContainer.getTheEntityIdDtoCollection()).thenThrow(new RuntimeException());

        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            matchingBaseService.getIdentifier(personContainer);
        });

        assertNotNull(thrown);
    }

    @Test
    void getNameStr_Test() {
        PersonContainer personContainer1 = new PersonContainer();

        var nameCol = new ArrayList<PersonNameDto>();
        var name = new PersonNameDto();
        name.setNmUseCd("L");
        name.setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);
        name.setAsOfDate(TimeStampUtil.getCurrentTimeStampPlusOneDay("UTC"));
        name.setLastNm("TEST");
        name.setFirstNm("TEST");
        nameCol.add(name);
        name = new PersonNameDto();
        name.setNmUseCd("L");
        name.setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);
        name.setAsOfDate(TimeStampUtil.getCurrentTimeStamp("UTC"));
        name.setLastNm("TEST");
        name.setFirstNm("TEST");
        nameCol.add(name);

        personContainer1.setThePersonNameDtoCollection(nameCol);
        personContainer1.getThePersonDto().setCd(NEDSSConstant.PAT);


        var res = matchingBaseService.getNamesStr(personContainer1);

        assertNotNull(res);


        personContainer1.setThePersonNameDtoCollection(nameCol);
    }


}
