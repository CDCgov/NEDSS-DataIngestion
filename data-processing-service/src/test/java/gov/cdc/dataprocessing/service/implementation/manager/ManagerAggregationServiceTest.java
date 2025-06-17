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

import java.util.*;
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

        assertThrows(DataProcessingException.class, () -> managerAggregationService.progAndJurisdictionAggregation(labResult, edxLabInformationDto, personAggContainer, organizationContainer));

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

    @Test
    void testPatientAggregation_WhenPersonCollectionIsNull_ReturnsEmptyContainer() throws Exception {
        PersonAggContainer result = managerAggregationService.patientAggregation(
                labResultProxyContainer,
                edxLabInformationDto,
                null
        );

        assertNotNull(result);
        assertNull(result.getPersonContainer());
        assertNull(result.getProviderContainer());
    }

    @Test
    void testPatientAggregation_WhenCdIsProvider_CallsProcessingProvider() throws Exception {
        // Use real objects for critical internal structure
        PersonDto personDto = new PersonDto();
        personDto.setCd(EdxELRConstant.ELR_PROVIDER_CD);

        PersonContainer container = new PersonContainer();
        container.setRole("IGNORED");
        container.setThePersonDto(personDto);

        Collection<PersonContainer> personList = List.of(container);

        when(patientService.processingProvider(
                eq(labResultProxyContainer),
                eq(edxLabInformationDto),
                eq(container),
                eq(false))
        ).thenReturn(container);

        PersonAggContainer result = managerAggregationService.patientAggregation(
                labResultProxyContainer,
                edxLabInformationDto,
                personList
        );

        assertNotNull(result);
        assertEquals(container, result.getProviderContainer());
        verify(patientService).processingProvider(eq(labResultProxyContainer), eq(edxLabInformationDto), eq(container), eq(false));
    }

    @Test
    void testRoleAggregation_ScopingEntityUidNull_CountZero() {
        RoleDto roleDto = new RoleDto();
        roleDto.setItDelete(false);
        roleDto.setSubjectEntityUid(1L);
        roleDto.setCd("PROVIDER");
        roleDto.setScopingEntityUid(null);
        roleDto.setSubjectClassCd("SUB");

        List<RoleDto> roleDtos = List.of(roleDto);
        when(labResult.getTheRoleDtoCollection()).thenReturn(roleDtos);
        when(roleService.loadCountBySubjectCdComb(any())).thenReturn(0);

        new ManagerAggregationService(null, null, null, null, null, null, null, roleService)
                .roleAggregation(labResult);

        verify(roleService).loadCountBySubjectCdComb(roleDto);
        verify(labResult).setTheRoleDtoCollection(argThat(list -> list.stream()
                .anyMatch(r -> ((RoleDto) r).getRoleSeq() == 1)));
    }

    @Test
    void testRoleAggregation_SpecialCase_ProviderAndConRole() {
        RoleDto roleDto = new RoleDto();
        roleDto.setItDelete(false);
        roleDto.setSubjectEntityUid(2L);
        roleDto.setCd(EdxELRConstant.ELR_COPY_TO_CD);
        roleDto.setScopingEntityUid(3L);
        roleDto.setSubjectClassCd(EdxELRConstant.ELR_CON);
        roleDto.setScopingRoleCd("ANY");

        List<RoleDto> roleDtos = List.of(roleDto);
        when(labResult.getTheRoleDtoCollection()).thenReturn(roleDtos);
        when(roleService.loadCountBySubjectScpingCdComb(any())).thenReturn(0);
        when(roleService.loadCountBySubjectCdComb(any())).thenReturn(0);

        new ManagerAggregationService(null, null, null, null, null, null, null, roleService)
                .roleAggregation(labResult);

        verify(roleService).loadCountBySubjectScpingCdComb(roleDto);
        verify(roleService).loadCountBySubjectCdComb(roleDto);
        verify(labResult).setTheRoleDtoCollection(argThat(list -> list.stream()
                .anyMatch(r -> ((RoleDto) r).getRoleSeq() == 1)));
    }


    @Test
    void testRoleAggregation_SpecialMaterialCase_SkipsSetRoleSeq() {
        // Arrange
        RoleDto roleDto = new RoleDto();
        roleDto.setItDelete(false);
        roleDto.setSubjectEntityUid(3L);
        roleDto.setCd(EdxELRConstant.ELR_SPECIMEN_PROCURER_CD);
        roleDto.setScopingEntityUid(4L);
        roleDto.setSubjectClassCd(EdxELRConstant.ELR_MAT_CD);
        roleDto.setScopingRoleCd(EdxELRConstant.ELR_SPECIMEN_PROCURER_CD);
        roleDto.setRoleSeq(2L); // triggers the inner "Material is a special" block

        List<RoleDto> roleDtos = List.of(roleDto);
        when(labResult.getTheRoleDtoCollection()).thenReturn(roleDtos);
        when(roleService.loadCountBySubjectScpingCdComb(any())).thenReturn(0);
        when(roleService.loadCountBySubjectCdComb(any())).thenReturn(1); // countForPKValues != 0

        // Act
        new ManagerAggregationService(null, null, null, null, null, null, null, roleService)
                .roleAggregation(labResult);

        // Assert
        verify(roleService).loadCountBySubjectScpingCdComb(roleDto);
        verify(roleService).loadCountBySubjectCdComb(roleDto);
        verify(labResult).setTheRoleDtoCollection(argThat(list ->
                list.stream().anyMatch(r ->
                        ((RoleDto) r).getRoleSeq() == 2 &&
                                ((RoleDto) r).getSubjectClassCd().equals(EdxELRConstant.ELR_MAT_CD)
                )
        ));
    }

    @Test
    void testProgAndJurisdictionAggregationHelper_AssignsProgramAndJurisdiction() throws DataProcessingException {
        // Arrange
        ObservationDto requestDto = new ObservationDto();
        requestDto.setObsDomainCdSt1(EdxELRConstant.ELR_ORDER_CD); // observationRequest
        requestDto.setProgAreaCd(null);
        requestDto.setJurisdictionCd(null);

        ObservationDto resultDto = new ObservationDto();
        resultDto.setObsDomainCdSt1(EdxELRConstant.ELR_RESULT_CD);

        ObservationContainer requestContainer = new ObservationContainer();
        requestContainer.setTheObservationDto(requestDto);

        ObservationContainer resultContainer = new ObservationContainer();
        resultContainer.setTheObservationDto(resultDto);

        Collection<ObservationContainer> obsList = Arrays.asList(resultContainer, requestContainer);

        LabResultProxyContainer labResult = mock(LabResultProxyContainer.class);
        when(labResult.getTheObservationContainerCollection()).thenReturn(obsList);

        EdxLabInformationDto edxInfo = new EdxLabInformationDto();
        edxInfo.setSendingFacilityClia("CLIA123");

        PersonAggContainer personAgg = new PersonAggContainer();
        personAgg.setPersonContainer(new PersonContainer());
        personAgg.setProviderContainer(new PersonContainer());

        OrganizationContainer orgContainer = new OrganizationContainer();

        // Act
        managerAggregationService.progAndJurisdictionAggregationHelper(labResult, edxInfo, personAgg, orgContainer);

        // Assert
        verify(programAreaService).getProgramArea(
                argThat(col -> col.contains(resultContainer)), eq(requestContainer),
                eq("CLIA123")
        );

        verify(jurisdictionService).assignJurisdiction(
                eq(personAgg.getPersonContainer()),
                eq(personAgg.getProviderContainer()),
                eq(orgContainer),
                eq(requestContainer)
        );
    }

    @Test
    void testServiceAggregation_ExecutesAllAggregationSteps() throws Exception {
        // Arrange
        ManagerAggregationService spyService = Mockito.spy(new ManagerAggregationService(
                organizationService,
                patientService,
                uidService,
                observationService,
                observationMatchingService,
                programAreaService,
                jurisdictionService,
                roleService
        ));

        when(labResult.getTheObservationContainerCollection()).thenReturn(observationContainerCollection);
        when(labResult.getThePersonContainerCollection()).thenReturn(personContainerCollection);
        doNothing().when(spyService).observationAggregation(eq(labResult), eq(edxLabInformationDto), eq(observationContainerCollection));
        doReturn(personAggContainer).when(spyService).patientAggregation(eq(labResult), eq(edxLabInformationDto), eq(personContainerCollection));
        when(organizationService.processingOrganization(eq(labResult))).thenReturn(organizationContainer);
        doNothing().when(spyService).roleAggregation(eq(labResult));
        doNothing().when(spyService).progAndJurisdictionAggregationHelper(eq(labResult), eq(edxLabInformationDto), eq(personAggContainer), eq(organizationContainer));

        // Act
        spyService.serviceAggregation(labResult, edxLabInformationDto);

        // Assert
        verify(spyService).observationAggregation(labResult, edxLabInformationDto, observationContainerCollection);
        verify(spyService).patientAggregation(labResult, edxLabInformationDto, personContainerCollection);
        verify(organizationService).processingOrganization(labResult);
        verify(spyService).roleAggregation(labResult);
        verify(spyService).progAndJurisdictionAggregationHelper(labResult, edxLabInformationDto, personAggContainer, organizationContainer);
    }

}
