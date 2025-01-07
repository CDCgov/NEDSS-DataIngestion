package gov.cdc.dataprocessing.service.implementation.manager;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.container.model.ObservationContainer;
import gov.cdc.dataprocessing.model.container.model.OrganizationContainer;
import gov.cdc.dataprocessing.model.container.model.PersonContainer;
import gov.cdc.dataprocessing.model.dto.act.ActIdDto;
import gov.cdc.dataprocessing.model.dto.entity.RoleDto;
import gov.cdc.dataprocessing.model.dto.lab_result.EdxLabInformationDto;
import gov.cdc.dataprocessing.model.dto.observation.ObservationDto;
import gov.cdc.dataprocessing.model.dto.person.PersonDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.service.interfaces.jurisdiction.IJurisdictionService;
import gov.cdc.dataprocessing.service.interfaces.jurisdiction.IProgramAreaService;
import gov.cdc.dataprocessing.service.interfaces.observation.IObservationMatchingService;
import gov.cdc.dataprocessing.service.interfaces.observation.IObservationService;
import gov.cdc.dataprocessing.service.interfaces.organization.IOrganizationService;
import gov.cdc.dataprocessing.service.interfaces.person.IPersonService;
import gov.cdc.dataprocessing.service.interfaces.role.IRoleService;
import gov.cdc.dataprocessing.service.interfaces.uid_generator.IUidService;
import gov.cdc.dataprocessing.service.model.auth_user.AuthUserProfileInfo;
import gov.cdc.dataprocessing.service.model.person.PersonAggContainer;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ManagerAggregationServiceTest {
    @Mock
    private IOrganizationService organizationService;
    @Mock
    private IPersonService patientService;
    @Mock
    private IUidService uidService;
    @Mock
    private IObservationService observationService;
    @Mock
    private IObservationMatchingService observationMatchingService;
    @Mock
    private IProgramAreaService programAreaService;
    @Mock
    private IJurisdictionService jurisdictionService;
    @Mock
    private IRoleService roleService;
    @InjectMocks
    private ManagerAggregationService managerAggregationService;
    @Mock
    AuthUtil authUtil;

    @Mock
    private LabResultProxyContainer labResult;

    @Mock
    private EdxLabInformationDto edxLabInformationDto;

    @Mock
    private PersonAggContainer personAggContainer;

    @Mock
    private OrganizationContainer organizationContainer;

    @Mock
    private Collection<ObservationContainer> observationContainerCollection;

    @Mock
    private Collection<PersonContainer> personContainerCollection;

    @Mock
    private ObservationContainer observationContainer;

    @Mock
    private ObservationDto observationDto;

    @Mock
    private ActIdDto actIdDto;

    @Mock
    private PersonContainer personContainer;

    @Mock
    private PersonDto personDto;
    @Mock
    private LabResultProxyContainer labResultProxyContainer;

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

    @AfterEach
    void tearDown() {
        Mockito.reset(organizationService, patientService, uidService, observationService,
                observationMatchingService, programAreaService,jurisdictionService,roleService, authUtil,
                labResult, edxLabInformationDto, personAggContainer, organizationContainer, observationContainerCollection,
                personContainerCollection, observationContainer, observationDto, actIdDto,
                personContainer, labResultProxyContainer, personDto);
    }

    @SuppressWarnings("java:S1117")
    @Test
    void processingObservationMatching_Test_1() throws DataProcessingException {
        EdxLabInformationDto edxLabInformationDto = new EdxLabInformationDto();
        LabResultProxyContainer labResultProxyContainer = new LabResultProxyContainer();
        Long aPersonUid = 10L;

        var obsDto = new ObservationDto();
        obsDto.setObservationUid(11L);
        obsDto.setProgAreaCd("PRO");
        obsDto.setJurisdictionCd("JUR");

        when(observationMatchingService.checkingMatchingObservation(edxLabInformationDto)).thenReturn(obsDto);
        when(observationService.getObservationToLabResultContainer(11L)).thenReturn(labResultProxyContainer);

        var test = managerAggregationService.processingObservationMatching(edxLabInformationDto, labResultProxyContainer, aPersonUid);

        assertNotNull(test);
        assertTrue(test.isLabIsUpdateDRRQ());
    }

    @SuppressWarnings("java:S1117")
    @Test
    void processingObservationMatching_Test_2() throws DataProcessingException {
        EdxLabInformationDto edxLabInformationDto = new EdxLabInformationDto();
        LabResultProxyContainer labResultProxyContainer = new LabResultProxyContainer();
        Long aPersonUid = 10L;

        var obsDto = new ObservationDto();
        obsDto.setObservationUid(11L);
        obsDto.setProgAreaCd(null);
        obsDto.setJurisdictionCd(null);

        when(observationMatchingService.checkingMatchingObservation(edxLabInformationDto)).thenReturn(obsDto);
        when(observationService.getObservationToLabResultContainer(11L)).thenReturn(labResultProxyContainer);

        var test = managerAggregationService.processingObservationMatching(edxLabInformationDto, labResultProxyContainer, aPersonUid);

        assertNotNull(test);
        assertFalse(test.isLabIsUpdateDRRQ());
        assertTrue(test.isLabIsUpdateDRSA());

    }

    @SuppressWarnings("java:S1117")
    @Test
    void processingObservationMatching_Test_3() throws DataProcessingException {
        EdxLabInformationDto edxLabInformationDto = new EdxLabInformationDto();
        LabResultProxyContainer labResultProxyContainer = new LabResultProxyContainer();
        Long aPersonUid = 10L;


        when(observationMatchingService.checkingMatchingObservation(edxLabInformationDto)).thenReturn(null);
        when(observationService.getObservationToLabResultContainer(11L)).thenReturn(labResultProxyContainer);

        var test = managerAggregationService.processingObservationMatching(edxLabInformationDto, labResultProxyContainer, aPersonUid);

        assertNotNull(test);
        assertFalse(test.isLabIsUpdateDRRQ());
        assertFalse(test.isLabIsUpdateDRSA());
        assertTrue(test.isLabIsCreate());
    }

    @SuppressWarnings("java:S1117")
    @Test
    void testProgAndJurisdictionAggregationAsync_HappyPath() throws DataProcessingException, ExecutionException, InterruptedException {
        // Prepare mock data
        Collection<ObservationContainer> observationContainerCollection = new ArrayList<>();
        observationContainerCollection.add(observationContainer);
        when(labResult.getTheObservationContainerCollection()).thenReturn(observationContainerCollection);


        when(observationContainer.getTheObservationDto()).thenReturn(observationDto);
        when(observationDto.getObsDomainCdSt1()).thenReturn(EdxELRConstant.ELR_ORDER_CD);

        // Mock behavior of services
        doNothing().when(programAreaService).getProgramArea(any(), any(), any());
        doNothing().when(jurisdictionService).assignJurisdiction(any(), any(), any(), any());
//
//        // Call the method under test
//        CompletableFuture<Void> future =
//        managerAggregationService.progAndJurisdictionAggregationAsync(labResult, edxLabInformationDto, personAggContainer, organizationContainer);
//
//        // Wait for the CompletableFuture to complete
//        future.get();

        managerAggregationService.progAndJurisdictionAggregation(labResult, edxLabInformationDto, personAggContainer, organizationContainer);
        // Verify that the services were called
        verify(programAreaService).getProgramArea(any(), any(), any());
        verify(jurisdictionService).assignJurisdiction(any(), any(), any(), any());
    }

    @SuppressWarnings("java:S1117")
    @Test
    void testProgAndJurisdictionAggregationAsync_Exception() throws DataProcessingException {
        Collection<ObservationContainer> observationContainerCollection = new ArrayList<>();
        observationContainerCollection.add(observationContainer);
        when(labResult.getTheObservationContainerCollection()).thenReturn(observationContainerCollection);
        when(observationContainer.getTheObservationDto()).thenReturn(observationDto);
        when(observationDto.getObsDomainCdSt1()).thenReturn(EdxELRConstant.ELR_ORDER_CD);
        when(observationDto.getProgAreaCd()).thenReturn(null);
        when(observationDto.getJurisdictionCd()).thenReturn(null);

        // Mock behavior of services to throw exception
        doThrow(new DataProcessingException("Error")).when(programAreaService).getProgramArea(any(), any(), any());

        assertThrows(RuntimeException.class, () -> managerAggregationService.progAndJurisdictionAggregation(labResult, edxLabInformationDto, personAggContainer, organizationContainer));

    }

    @Test
    void testRoleAggregation_HappyPath() {
        RoleDto role1 = new RoleDto();
        role1.setSubjectEntityUid(1L);
        role1.setCd("CD1");
        role1.setScopingEntityUid(10L);
        role1.setItDelete(false);
        role1.setRoleSeq(1L);
        role1.setSubjectClassCd(EdxELRConstant.ELR_CON);

        RoleDto role2 = new RoleDto();
        role2.setSubjectEntityUid(2L);
        role2.setCd("CD2");
        role2.setScopingEntityUid(20L);
        role2.setItDelete(true);

        Collection<RoleDto> roleCollection = Arrays.asList(role1, role2);

        when(labResult.getTheRoleDtoCollection()).thenReturn(roleCollection);

        when(roleService.loadCountBySubjectCdComb(any())).thenReturn(0);
        when(roleService.loadCountBySubjectScpingCdComb(any())).thenReturn(0);

        managerAggregationService.roleAggregation(labResult);

        Collection<RoleDto> expectedFinalRoles = new ArrayList<>(roleCollection);
        assertEquals(expectedFinalRoles.size(), labResult.getTheRoleDtoCollection().size());
        assertEquals(expectedFinalRoles, labResult.getTheRoleDtoCollection());
    }

    @Test
    void testRoleAggregation_WithNewAndUpdatedRoles() {
        RoleDto role1 = new RoleDto();
        role1.setSubjectEntityUid(1L);
        role1.setCd("CD1");
        role1.setScopingEntityUid(10L);
        role1.setItDelete(false);

        RoleDto role2 = new RoleDto();
        role2.setSubjectEntityUid(1L);
        role2.setCd("CD1");
        role2.setScopingEntityUid(10L);
        role2.setItDelete(true);

        RoleDto role3 = new RoleDto();
        role3.setSubjectEntityUid(2L);
        role3.setCd("CD2");
        role3.setScopingEntityUid(20L);
        role3.setItDelete(false);

        Collection<RoleDto> roleCollection = Arrays.asList(role1, role2, role3);

        when(labResult.getTheRoleDtoCollection()).thenReturn(roleCollection);

        when(roleService.loadCountBySubjectCdComb(any())).thenReturn(0);
        when(roleService.loadCountBySubjectScpingCdComb(any())).thenReturn(0);

        managerAggregationService.roleAggregation(labResult);

        assertEquals(3, labResult.getTheRoleDtoCollection().size());
    }

    @Test
    void testRoleAggregation_WithExistingRoles() {
        RoleDto role1 = new RoleDto();
        role1.setSubjectEntityUid(1L);
        role1.setCd("CD1");
        role1.setScopingEntityUid(10L);
        role1.setItDelete(false);

        RoleDto role2 = new RoleDto();
        role2.setSubjectEntityUid(1L);
        role2.setCd("CD1");
        role2.setScopingEntityUid(10L);
        role2.setItDelete(true);

        RoleDto role3 = new RoleDto();
        role3.setSubjectEntityUid(2L);
        role3.setCd("CD2");
        role3.setScopingEntityUid(20L);
        role3.setItDelete(false);

        Collection<RoleDto> roleCollection = Arrays.asList(role1, role2, role3);

        when(labResult.getTheRoleDtoCollection()).thenReturn(roleCollection);

        when(roleService.loadCountBySubjectCdComb(any())).thenReturn(1);
        when(roleService.loadCountBySubjectScpingCdComb(any())).thenReturn(1);

        managerAggregationService.roleAggregation(labResult);

        assertEquals(3, labResult.getTheRoleDtoCollection().size());
    }

    @Test
    void testRoleAggregation_EmptyRoles() {
        when(labResult.getTheRoleDtoCollection()).thenReturn(Collections.emptyList());
        managerAggregationService.roleAggregation(labResult);
        assertEquals(0, labResult.getTheRoleDtoCollection().size());
    }

    @SuppressWarnings("java:S1117")
    @Test
    void testObservationAggregation_HappyPath() {
        // Prepare mock data
        when(observationContainer.getTheObservationDto()).thenReturn(observationDto);
        when(observationDto.getObservationUid()).thenReturn(123L);
        when(edxLabInformationDto.getRootObserbationUid()).thenReturn(123L);

        Collection<ActIdDto> actIdDtoCollection = Arrays.asList(actIdDto);
        when(observationContainer.getTheActIdDtoCollection()).thenReturn(actIdDtoCollection);

        Collection<ObservationContainer> observationContainerCollection = Arrays.asList(observationContainer);

        // Call the method under test
        managerAggregationService.observationAggregation(labResult, edxLabInformationDto, observationContainerCollection);

        // Verify interactions with the mocked dependencies
        verify(uidService, times(1)).setFalseToNewForObservation(labResult, -1L, 123L);
        verify(actIdDto, times(1)).setItNew(false);
        verify(actIdDto, times(1)).setItDirty(true);
        verify(actIdDto, times(1)).setActUid(123L);
    }

    @SuppressWarnings("java:S1117")
    @Test
    void testObservationAggregation_NoMatchingObservationUid() {
        // Prepare mock data
        when(observationContainer.getTheObservationDto()).thenReturn(observationDto);
        when(observationDto.getObservationUid()).thenReturn(123L);
        when(edxLabInformationDto.getRootObserbationUid()).thenReturn(456L);

        Collection<ObservationContainer> observationContainerCollection = Arrays.asList(observationContainer);

        // Call the method under test
        managerAggregationService.observationAggregation(labResult, edxLabInformationDto, observationContainerCollection);

        // Verify that no interactions with uidService occurred
        verify(uidService, never()).setFalseToNewForObservation(any(), anyLong(), anyLong());
    }

    @SuppressWarnings("java:S1117")
    @Test
    void patientAggregation_Test() throws DataProcessingConsumerException, DataProcessingException {
        LabResultProxyContainer labResultProxyContainer = new LabResultProxyContainer();
        EdxLabInformationDto edxLabInformationDto = new EdxLabInformationDto();
        var personContainerCollection = new ArrayList<PersonContainer>();
        var perCon = new PersonContainer();
        perCon.setRole(EdxELRConstant.ELR_NEXT_OF_KIN);
        personContainerCollection.add(perCon);

        perCon = new PersonContainer();
        var perDt = new PersonDto();
        perDt.setCd(EdxELRConstant.ELR_PATIENT_CD);
        perCon.setThePersonDto(perDt);
        personContainerCollection.add(perCon);

        perCon = new PersonContainer();
        perDt = new PersonDto();
        perDt.setCd(EdxELRConstant.ELR_PROVIDER_CD);
        perCon.setThePersonDto(perDt);
        personContainerCollection.add(perCon);

        var res = managerAggregationService.patientAggregation(labResultProxyContainer, edxLabInformationDto, personContainerCollection);

        assertNotNull(res);
    }
}
