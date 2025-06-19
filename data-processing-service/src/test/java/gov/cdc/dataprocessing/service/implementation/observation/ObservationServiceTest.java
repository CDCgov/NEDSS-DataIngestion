package gov.cdc.dataprocessing.service.implementation.observation;

import gov.cdc.dataprocessing.constant.elr.NBSBOLookup;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.constant.elr.ProgramAreaJurisdiction;
import gov.cdc.dataprocessing.constant.enums.DataProcessingMapKey;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.model.container.model.*;
import gov.cdc.dataprocessing.model.dto.act.ActRelationshipDto;
import gov.cdc.dataprocessing.model.dto.edx.EDXDocumentDto;
import gov.cdc.dataprocessing.model.dto.entity.RoleDto;
import gov.cdc.dataprocessing.model.dto.log.MessageLogDto;
import gov.cdc.dataprocessing.model.dto.log.NNDActivityLogDto;
import gov.cdc.dataprocessing.model.dto.material.MaterialDto;
import gov.cdc.dataprocessing.model.dto.observation.ObservationDto;
import gov.cdc.dataprocessing.model.dto.organization.OrganizationDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.model.dto.person.PersonDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.repository.nbs.odse.model.observation.Observation;
import gov.cdc.dataprocessing.repository.nbs.odse.model.person.Person;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.observation.ObservationRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.person.PersonRepository;
import gov.cdc.dataprocessing.service.interfaces.act.IActRelationshipService;
import gov.cdc.dataprocessing.service.interfaces.answer.IAnswerService;
import gov.cdc.dataprocessing.service.interfaces.jurisdiction.IJurisdictionService;
import gov.cdc.dataprocessing.service.interfaces.jurisdiction.IProgramAreaService;
import gov.cdc.dataprocessing.service.interfaces.log.IMessageLogService;
import gov.cdc.dataprocessing.service.interfaces.log.INNDActivityLogService;
import gov.cdc.dataprocessing.service.interfaces.material.IMaterialService;
import gov.cdc.dataprocessing.service.interfaces.notification.INotificationService;
import gov.cdc.dataprocessing.service.interfaces.observation.IEdxDocumentService;
import gov.cdc.dataprocessing.service.interfaces.observation.IObservationCodeService;
import gov.cdc.dataprocessing.service.interfaces.paticipation.IParticipationService;
import gov.cdc.dataprocessing.service.interfaces.public_health_case.IInvestigationService;
import gov.cdc.dataprocessing.service.interfaces.role.IRoleService;
import gov.cdc.dataprocessing.service.interfaces.uid_generator.IUidService;
import gov.cdc.dataprocessing.service.model.auth_user.AuthUserProfileInfo;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.generic_helper.PrepareAssocModelHelper;
import gov.cdc.dataprocessing.utilities.component.observation.ObservationRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.observation.ObservationUtil;
import gov.cdc.dataprocessing.utilities.component.organization.OrganizationRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.patient.PatientRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.patient.PersonUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("java:S1640")
class ObservationServiceTest {
    @Mock
    private INNDActivityLogService nndActivityLogService;
    @Mock
    private IMessageLogService messageLogService;
    @Mock
    private ObservationRepositoryUtil observationRepositoryUtil;
    @Mock
    private INotificationService notificationService;
    @Mock
    private IMaterialService materialService;
    @Mock
    private PatientRepositoryUtil patientRepositoryUtil;
    @Mock
    private IRoleService roleService;
    @Mock
    private IActRelationshipService actRelationshipService;
    @Mock
    private IEdxDocumentService edxDocumentService;
    @Mock
    private IAnswerService answerService;
    @Mock
    private IParticipationService participationService;
    @Mock
    private ObservationRepository observationRepository;
    @Mock
    private PersonRepository personRepository;
    @Mock
    private IJurisdictionService jurisdictionService;
    @Mock
    private OrganizationRepositoryUtil organizationRepositoryUtil;
    @Mock
    private IObservationCodeService observationCodeService;
    @Mock
    private ObservationUtil observationUtil;
    @Mock
    private PersonUtil personUtil;
    @Mock
    private IProgramAreaService programAreaService;
    @Mock
    private PrepareAssocModelHelper prepareAssocModelHelper;
    @Mock
    private IUidService uidService;

    @Mock
    private IInvestigationService investigationService;

    @InjectMocks
    private ObservationService observationService;


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
    @AfterEach
    void tearDown() {
        Mockito.reset(nndActivityLogService, messageLogService, observationRepositoryUtil, notificationService,
                materialService, patientRepositoryUtil, roleService, actRelationshipService, edxDocumentService,
                answerService, participationService, observationRepository, personRepository, jurisdictionService,
                organizationRepositoryUtil, observationCodeService, observationUtil, personUtil, programAreaService,
                prepareAssocModelHelper, uidService, investigationService, authUtil);
    }

    @Test
    void getObservationToLabResultContainer_ObservationUidIsNull_ShouldThrowException() {
        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            observationService.getObservationToLabResultContainer(null);
        });

        assertEquals("HL7CommonLabUtil.getLabResultToProxy observationUid is null", thrown.getMessage());
    }

    // getObservationToLabResultContainer
    @Test
    void getObservationToLabResultContainer_ReturnData_ELRisTrue() throws DataProcessingException {
        long obsUid = 1L;

        // loadingObservationToLabResultContainer
        ObservationContainer observationContainer = new ObservationContainer();
        var partCol = new ArrayList<ParticipationDto>();
        var pat = new ParticipationDto();
        pat.setSubjectEntityUid(16L);
        pat.setSubjectClassCd(NEDSSConstant.PAR110_SUB_CD);
        pat.setRecordStatusCd(NEDSSConstant.ACTIVE);
        pat.setTypeCd(NEDSSConstant.PAR110_TYP_CD);
        partCol.add(pat);


        pat = new ParticipationDto();
        pat.setSubjectEntityUid(13L);
        pat.setActUid(13L);
        pat.setSubjectClassCd(NEDSSConstant.PAR102_SUB_CD);
        pat.setRecordStatusCd(NEDSSConstant.ACTIVE);
        partCol.add(pat);

        pat = new ParticipationDto();
        pat.setSubjectEntityUid(14L);
        pat.setSubjectClassCd(NEDSSConstant.PAR104_SUB_CD);
        pat.setRecordStatusCd(NEDSSConstant.ACTIVE);
        partCol.add(pat);


        observationContainer.setTheParticipationDtoCollection(partCol);

        var actCol = new ArrayList<ActRelationshipDto>();
        var actReDto = new ActRelationshipDto();
        actReDto.setSourceClassCd(NEDSSConstant.INTERVENTION_CLASS_CODE);
        actReDto.setTargetClassCd(NEDSSConstant.OBSERVATION_CLASS_CODE);
        actReDto.setRecordStatusCd(NEDSSConstant.ACTIVE);
        actCol.add(actReDto);

        actReDto = new ActRelationshipDto();
        actReDto.setSourceClassCd(NEDSSConstant.OBSERVATION_CLASS_CODE);
        actReDto.setTargetClassCd(NEDSSConstant.OBSERVATION_CLASS_CODE);
        actReDto.setRecordStatusCd(NEDSSConstant.ACTIVE);
        actReDto.setSourceActUid(2L);
        actReDto.setTypeCd((NEDSSConstant.ACT_TYPE_PROCESSING_DECISION));
        actCol.add(actReDto);

        actReDto = new ActRelationshipDto();
        actReDto.setSourceClassCd(NEDSSConstant.OBSERVATION_CLASS_CODE);
        actReDto.setTargetClassCd(NEDSSConstant.OBSERVATION_CLASS_CODE);
        actReDto.setRecordStatusCd(NEDSSConstant.ACTIVE);
        actReDto.setSourceActUid(3L);
        actReDto.setTypeCd(("APND"));
        actCol.add(actReDto);

        actReDto = new ActRelationshipDto();
        actReDto.setSourceClassCd(NEDSSConstant.OBSERVATION_CLASS_CODE);
        actReDto.setTargetClassCd(NEDSSConstant.OBSERVATION_CLASS_CODE);
        actReDto.setRecordStatusCd(NEDSSConstant.ACTIVE);
        actReDto.setSourceActUid(4L);
        actReDto.setTypeCd(NEDSSConstant.ACT108_TYP_CD);
        actCol.add(actReDto);

        observationContainer.setTheActRelationshipDtoCollection(actCol);

        when(observationRepositoryUtil.loadObject(obsUid)).thenReturn(observationContainer);

        // retrieveEntityFromParticipationForContainer
         // - retrievePersonAndRoleFromParticipation
          // - retrieveScopedPersons
        var patContainer = new PersonContainer();
        var rolCol = new ArrayList<RoleDto>();
        var role = new RoleDto();
        rolCol.add(role);
        patContainer.setTheRoleDtoCollection(rolCol);

        var patDto = new PersonDto();
        patDto.setPersonUid(1L);
        patContainer.setThePersonDto(patDto);
        when(patientRepositoryUtil.loadPerson(partCol.get(0).getSubjectEntityUid())).thenReturn(patContainer);

        var roleDtColl = new ArrayList<RoleDto>();
        var role2 = new RoleDto();
        role2.setSubjectEntityUid(1L);
        roleDtColl.add(role2);
        var scopedPerson = new PersonContainer();
        scopedPerson.setTheRoleDtoCollection(roleDtColl);
        when(roleService.findRoleScopedToPatient(1L)).thenReturn(roleDtColl);
        when(patientRepositoryUtil.loadPerson(roleDtColl.get(0).getSubjectEntityUid())).thenReturn(scopedPerson);

        //Retrieve associated organizations
        when(organizationRepositoryUtil.loadObject(13L, 13L)).thenReturn(new OrganizationContainer());

        //Retrieve associated materials
        when(materialService.loadMaterialObject(14L)).thenReturn(new MaterialContainer());



        // retrieveActForLabResultContainer
        // - retrieveInterventionFromActRelationship
           // NOTE: This will return intervention which is not relevant at the time this test is created
        // - retrieveObservationFromActRelationship

        // NEDSSConstant.ACT_TYPE_PROCESSING_DECISION
        when(observationRepositoryUtil.loadObject(2L)).thenReturn(new ObservationContainer());
        // APND
        var apndObsContainer = new ObservationContainer();
        var apndObsActReCol = new ArrayList<ActRelationshipDto>();
        var apndActRe = new ActRelationshipDto();
        apndActRe.setTypeCd("COMP");
        apndActRe.setSourceActUid(21L);
        apndObsActReCol.add(apndActRe);
        apndObsContainer.setTheActRelationshipDtoCollection(apndObsActReCol);
        when(observationRepositoryUtil.loadObject(3L)).thenReturn(apndObsContainer);
        when(observationRepositoryUtil.loadObject(21L)).thenReturn(new ObservationContainer());

        // NEDSSConstant.ACT108_TYP_CD)
        var act108ObsContainer = new ObservationContainer();
        var act108patCol = new ArrayList<ParticipationDto>();
        var act108pat = new ParticipationDto();
        act108pat.setTypeCd(NEDSSConstant.PAR122_TYP_CD);
        act108pat.setSubjectClassCd(NEDSSConstant.PAR122_SUB_CD);
        act108pat.setActClassCd(NEDSSConstant.OBSERVATION_CLASS_CODE);
        act108pat.setRecordStatusCd(NEDSSConstant.ACTIVE);
        act108pat.setSubjectEntityUid(41L);
        act108pat.setActUid(41L);
        act108patCol.add(act108pat);
        act108ObsContainer.setTheParticipationDtoCollection(act108patCol);

        var act108ActCol = new ArrayList<ActRelationshipDto>();
        var act108Act = new ActRelationshipDto();
        act108Act.setTypeCd(NEDSSConstant.ACT109_TYP_CD);
        act108Act.setSourceClassCd(NEDSSConstant.OBSERVATION_CLASS_CODE);
        act108Act.setTargetClassCd(NEDSSConstant.OBSERVATION_CLASS_CODE);
        act108Act.setRecordStatusCd(NEDSSConstant.ACTIVE);
        act108Act.setSourceActUid(42L);
        act108ActCol.add(act108Act);
        act108ObsContainer.setTheActRelationshipDtoCollection(act108ActCol);

        when(observationRepositoryUtil.loadObject(4L)).thenReturn(act108ObsContainer);

        // retrievePerformingLabAkaOrganizationFromParticipation
        var orgContainer = new OrganizationContainer();
        when(organizationRepositoryUtil.loadObject(act108pat.getSubjectEntityUid(), act108pat.getActUid())).thenReturn(orgContainer);

        // retrieveReflexObservationsFromActRelationship
        var act108ActObsContainer = new ObservationContainer();
        var act108ActObsContainerActCol = new ArrayList<ActRelationshipDto>();
        var act108ActObsContainerAct = new ActRelationshipDto();
        act108ActObsContainerAct.setTypeCd(NEDSSConstant.ACT110_TYP_CD);
        act108ActObsContainerAct.setSourceClassCd(NEDSSConstant.OBSERVATION_CLASS_CODE);
        act108ActObsContainerAct.setTargetClassCd(NEDSSConstant.OBSERVATION_CLASS_CODE);
        act108ActObsContainerAct.setRecordStatusCd(NEDSSConstant.ACTIVE);
        act108ActObsContainerAct.setSourceActUid(43L);
        act108ActObsContainerActCol.add(act108ActObsContainerAct);
        act108ActObsContainer.setTheActRelationshipDtoCollection(act108ActObsContainerActCol);
        when(observationRepositoryUtil.loadObject(act108Act.getSourceActUid())).thenReturn(act108ActObsContainer);

        // retrieveReflexRTsAkaObservationFromActRelationship
        when(observationRepositoryUtil.loadObject(act108ActObsContainerAct.getSourceActUid())).thenReturn(new ObservationContainer());


        when(observationRepositoryUtil.loadObject(act108ActObsContainerAct.getSourceActUid())).thenReturn(new ObservationContainer());

        var test = observationService.getObservationToLabResultContainer(obsUid);

        assertNotNull(test);

        assertEquals(2, test.getTheOrganizationContainerCollection().size());
        assertEquals(7, test.getTheObservationContainerCollection().size());
        assertEquals(2, test.getThePersonContainerCollection().size());

    }

    @Test
    void getAbstractObjectForObservationOrIntervention_Test() throws DataProcessingException {
        var res = observationService.getAbstractObjectForObservationOrIntervention("NA", 1L);
        assertNull(res);
    }

    @Test
    void processingLabResultContainer_ThrowException() {
        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            observationService.processingLabResultContainer(null);
        });

        assertEquals("Lab Result Container Is Null", thrown.getMessage());
    }

    @Test
    void processingLabResultContainer_Success() throws DataProcessingException {
        LabResultProxyContainer labResult = new LabResultProxyContainer();
        var patCol = new ArrayList<PersonContainer>();
        var patCon = new PersonContainer();
        patCol.add(patCon);
        labResult.setThePersonContainerCollection(patCol);

        var obsCol = new ArrayList<ObservationContainer>();
        var obsCon = new ObservationContainer();
        var obsDto = new ObservationDto();
        obsDto.setObsDomainCdSt1(NEDSSConstant.ORDERED_TEST_OBS_DOMAIN_CD);
        obsDto.setCd("LAB112");
        obsCon.setTheObservationDto(obsDto);
        obsCol.add(obsCon);

        labResult.setTheObservationContainerCollection(obsCol);

        // setLabResultProxy
        // - setLabResultProxyWithoutNotificationAutoResend
        when(personUtil.processLabPersonContainerCollection(labResult.getThePersonContainerCollection(), false, labResult))
                .thenReturn(1L);
        //  - processObservationContainerCollection 836
        //   - processLabReportObsContainerCollection 1091
        var rootObs = new ObservationDto();
        rootObs.setElectronicInd("Y");
        when(observationUtil.getRootObservationDto(labResult)).thenReturn(rootObs);
        //    - processLabReportOrderTest 1159
        var orderTest = new ObservationContainer();
        var orderTestObsDto = new ObservationDto();
        orderTestObsDto.setProgAreaCd("ANY");
        orderTestObsDto.setJurisdictionCd("ANY");
        orderTestObsDto.setProcessingDecisionCd("test");
        orderTestObsDto.setObservationUid(11L);
        orderTest.setTheObservationDto(orderTestObsDto);
        when(observationUtil.getRootObservationContainer(labResult)).thenReturn(orderTest);
        when(programAreaService.deriveProgramAreaCd(labResult, orderTest)).thenReturn(null);
        when(jurisdictionService.deriveJurisdictionCd(labResult, orderTest.getTheObservationDto())).thenReturn(null);
        //     - performOrderTestStateTransition 1223
        labResult.setItNew(true);
        var existObs = new ObservationContainer();
        var existObsObsDto = new ObservationDto();
        existObsObsDto.setVersionCtrlNbr(1);
        existObs.setTheObservationDto(existObsObsDto);
        when(observationRepositoryUtil.loadObject(11L)).thenReturn(existObs);
        var newObservationDto = new ObservationDto();
        when(prepareAssocModelHelper.prepareVO(orderTest.getTheObservationDto(), NBSBOLookup.OBSERVATIONLABREPORT,
                NEDSSConstant.OBS_LAB_CR_MR, "OBSERVATION", NEDSSConstant.BASE, 1))
                .thenReturn(newObservationDto);

        var orgCol = new ArrayList<OrganizationContainer>();
        var orgConn = new OrganizationContainer();
        var orgDto = new OrganizationDto();
        orgDto.setOrganizationUid(30L);
        orgDto.setVersionCtrlNbr(1);
        orgConn.setTheOrganizationDto(orgDto);
        orgConn.setItNew(true);
        orgCol.add(orgConn);

        orgConn = new OrganizationContainer();
        orgDto = new OrganizationDto();
        orgDto.setOrganizationUid(31L);
        orgDto.setVersionCtrlNbr(1);
        orgConn.setTheOrganizationDto(orgDto);
        orgConn.setItNew(false);
        orgConn.setItDirty(true);
        orgCol.add(orgConn);


        labResult.setTheOrganizationContainerCollection(orgCol);
        when(organizationRepositoryUtil.loadObject(30L, null)).thenReturn(orgCol.get(0));
        when(organizationRepositoryUtil.loadObject(31L, null)).thenReturn(orgCol.get(1));

        when(prepareAssocModelHelper.prepareVO(
                orgCol.get(0).getTheOrganizationDto(),
                NBSBOLookup.ORGANIZATION,
                NEDSSConstant.ORG_CR, "ORGANIZATION",
                NEDSSConstant.BASE,
                1))
                .thenReturn(orgCol.get(0).getTheOrganizationDto());

        when(prepareAssocModelHelper.prepareVO(
                orgCol.get(1).getTheOrganizationDto(),
                NBSBOLookup.ORGANIZATION,
                NEDSSConstant.ORG_CR, "ORGANIZATION",
                NEDSSConstant.BASE,
                1))
                .thenReturn(orgCol.get(1).getTheOrganizationDto());


        var matCol = new ArrayList<MaterialContainer>();
        var matCon = new MaterialContainer();
        var matDto = new MaterialDto();
        matDto.setMaterialUid(32L);
        matDto.setVersionCtrlNbr(1);
        matCon.setTheMaterialDto(matDto);
        matCon.setItNew(true);
        matCol.add(matCon);

        matCon = new MaterialContainer();
        matDto = new MaterialDto();
        matDto.setMaterialUid(33L);
        matDto.setVersionCtrlNbr(1);
        matCon.setTheMaterialDto(matDto);
        matCon.setItNew(false);
        matCon.setItDirty(true);
        matCol.add(matCon);

        labResult.setTheMaterialContainerCollection(matCol);

        when(materialService.loadMaterialObject(32L)).thenReturn(matCol.get(0));
        when(materialService.loadMaterialObject(33L)).thenReturn(matCol.get(1));

        when(prepareAssocModelHelper.prepareVO(
                matCol.get(0).getTheMaterialDto(),
                NBSBOLookup.MATERIAL,
                NEDSSConstant.MAT_MFG_CR, "MATERIAL",
                NEDSSConstant.BASE,
                1))
                .thenReturn(matCol.get(0).getTheMaterialDto());

        when(prepareAssocModelHelper.prepareVO(
                matCol.get(1).getTheMaterialDto(),
                NBSBOLookup.MATERIAL,
                NEDSSConstant.MAT_MFG_EDIT, "MATERIAL",
                NEDSSConstant.BASE,
                1))
                .thenReturn(matCol.get(1).getTheMaterialDto());


        var patCol1 = new ArrayList<ParticipationDto>();
        var patDto1 = new ParticipationDto();
        patDto1.setActUid(34L);
        patDto1.setSubjectEntityUid(34L);
        patCol1.add(patDto1);

        patCol1 = new ArrayList<ParticipationDto>();
        patDto1 = new ParticipationDto();
        patDto1.setActUid(35L);
        patDto1.setSubjectEntityUid(35L);
        patDto1.setItDelete(true);
        patCol1.add(patDto1);


        labResult.setTheParticipationDtoCollection(patCol1);


        var actCol1 = new ArrayList<ActRelationshipDto>();
        var actDto1 = new ActRelationshipDto();
        actCol1.add(actDto1);
        labResult.setTheActRelationshipDtoCollection(actCol1);

        var rolCol1 = new ArrayList<RoleDto>();
        var rolDto1 = new RoleDto();
        rolCol1.add(rolDto1);
        labResult.setTheRoleDtoCollection(rolCol1);

        var edxCol = new ArrayList<EDXDocumentDto>();
        var edxDto = new EDXDocumentDto();
        edxDto.setPayload("<Container>Test</Container>");
        edxDto.setItNew(true);
        edxCol.add(edxDto);
        labResult.setEDXDocumentCollection(edxCol);




        //    - storeObservationVOCollection 1162
        when(observationRepositoryUtil.saveObservation(obsCon)).thenReturn(12L);


        // - updateLabResultWithAutoResendNotification
        labResult.setAssociatedInvInd(true);

        var logCol = new ArrayList<MessageLogDto>();
        var logDto = new MessageLogDto();
        logCol.add(logDto);
        labResult.setMessageLogDCollection(logCol);

        var test = observationService.processingLabResultContainer(labResult);

        assertNotNull(test);
    }

    @Test
    void setLabInvAssociation_Success() throws DataProcessingException {
        long labUid = 1L;
        long investigationUid = 1L;

        doNothing().when(investigationService).setAssociations(eq(investigationUid), anyCollection(),
                eq(null), eq(null), eq(null), eq(true));
        observationService.setLabInvAssociation(labUid, investigationUid);
        verify(investigationService, times(1)).setAssociations(eq(investigationUid),
                anyCollection(), eq(null), eq(null), eq(null), eq(true));
    }

    @Test
    void processObservation_Success() throws DataProcessingException {
        long obsUid = 1L;
        var obsCon = new ObservationContainer();
        var obsDto = new ObservationDto();
        obsDto.setCtrlCdDisplayForm(NEDSSConstant.LABRESULT_CODE);
        obsDto.setRecordStatusCd(NEDSSConstant.OBS_UNPROCESSED);
        obsDto.setVersionCtrlNbr(1);
        obsCon.setTheObservationDto(obsDto);

        when(observationRepositoryUtil.loadObject(obsUid)).thenReturn(obsCon);

        when(prepareAssocModelHelper.prepareVO(obsDto,
                NBSBOLookup.OBSERVATIONLABREPORT,
                NEDSSConstant.OBS_LAB_PROCESS,
                "OBSERVATION",
                NEDSSConstant.BASE,
                obsDto.getVersionCtrlNbr())).thenReturn(obsDto);

        var test = observationService.processObservation(1L);

        assertTrue(test);
    }

    @Test
    void testProcessObservationWithProcessingDecision_NotLabResultCode() throws DataProcessingException {
        Long observationUid = 1L;
        String processingDecisionCd = "DECISION_CD";
        String processingDecisionTxt = "DECISION_TXT";

        ObservationContainer observationVO = mock(ObservationContainer.class);
        ObservationDto observationDT = new ObservationDto();
        observationDT.setCtrlCdDisplayForm("NOT_LABRESULT_CODE");

        when(observationRepositoryUtil.loadObject(observationUid)).thenReturn(observationVO);
        when(observationVO.getTheObservationDto()).thenReturn(observationDT);

        DataProcessingException exception = assertThrows(DataProcessingException.class, () ->
                observationService.processObservationWithProcessingDecision(observationUid, processingDecisionCd, processingDecisionTxt)
        );

        assertEquals("This is not a Lab Report OR a Morbidity Report! MarkAsReviewed only applies to Lab Report or Morbidity Report ", exception.getMessage());
    }


    @Test
    void testProcessObservationWithProcessingDecision_NotUnprocessed() throws DataProcessingException {
        Long observationUid = 1L;
        String processingDecisionCd = "DECISION_CD";
        String processingDecisionTxt = "DECISION_TXT";

        ObservationContainer observationVO = mock(ObservationContainer.class);
        ObservationDto observationDT = new ObservationDto();
        observationDT.setCtrlCdDisplayForm(NEDSSConstant.LABRESULT_CODE);
        observationDT.setRecordStatusCd("PROCESSED");

        when(observationRepositoryUtil.loadObject(observationUid)).thenReturn(observationVO);
        when(observationVO.getTheObservationDto()).thenReturn(observationDT);

        boolean result = observationService.processObservationWithProcessingDecision(observationUid, processingDecisionCd, processingDecisionTxt);

        assertFalse(result);
        verify(observationRepositoryUtil).loadObject(observationUid);
        verify(observationVO).getTheObservationDto();
    }

    @Test
    void testProcessObservationWithProcessingDecision_Valid() throws DataProcessingException {
        Long observationUid = 1L;
        String processingDecisionCd = "DECISION_CD";
        String processingDecisionTxt = "DECISION_TXT";

        ObservationContainer observationVO = mock(ObservationContainer.class);
        ObservationDto observationDT = new ObservationDto();
        observationDT.setCtrlCdDisplayForm(NEDSSConstant.LABRESULT_CODE);
        observationDT.setRecordStatusCd(NEDSSConstant.OBS_UNPROCESSED);
        observationDT.setVersionCtrlNbr(1);

        when(observationRepositoryUtil.loadObject(observationUid)).thenReturn(observationVO);
        when(observationVO.getTheObservationDto()).thenReturn(observationDT);
        when(prepareAssocModelHelper.prepareVO(any(), anyString(), anyString(), anyString(), anyString(), anyInt())).thenReturn(observationDT);

        boolean result = observationService.processObservationWithProcessingDecision(observationUid, processingDecisionCd, processingDecisionTxt);

        assertTrue(result);
        assertEquals(processingDecisionCd, observationDT.getProcessingDecisionCd());
        assertEquals(processingDecisionTxt, observationDT.getProcessingDecisionTxt());
        verify(observationRepositoryUtil).saveObservation(observationVO);
    }


    @Test
    void testProcessObservationWithProcessingDecision_Exception() throws DataProcessingException {
        Long observationUid = 1L;
        String processingDecisionCd = "DECISION_CD";
        String processingDecisionTxt = "DECISION_TXT";

        when(observationRepositoryUtil.loadObject(observationUid)).thenThrow(new RuntimeException("Test Exception"));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                observationService.processObservationWithProcessingDecision(observationUid, processingDecisionCd, processingDecisionTxt)
        );

        assertEquals("Test Exception", exception.getMessage());
    }
    @Test
    void testPerformOrderTestStateTransition_NewWithProcessingDecision() throws DataProcessingException {
        LabResultProxyContainer labResultProxyVO = mock(LabResultProxyContainer.class);
        ObservationContainer orderTest = mock(ObservationContainer.class);
        ObservationDto observationDto = new ObservationDto();
        observationDto.setProcessingDecisionCd("DECISION");
        observationDto.setObservationUid(-1L);
        when(labResultProxyVO.isItNew()).thenReturn(true);
        when(orderTest.getTheObservationDto()).thenReturn(observationDto);


        observationService.performOrderTestStateTransition(labResultProxyVO, orderTest, false);

        verify(prepareAssocModelHelper).prepareVO(any(),any(),
                any(), any(), any(), any());
    }


    @Test
    void testProcessingOrderTestStateTransition_NewWithProcessingDecision() {
        LabResultProxyContainer labResultProxyVO = mock(LabResultProxyContainer.class);
        ObservationContainer orderTest = mock(ObservationContainer.class);
        ObservationDto observationDto = new ObservationDto();
        observationDto.setProcessingDecisionCd("DECISION");
        when(labResultProxyVO.isItNew()).thenReturn(true);
        when(orderTest.getTheObservationDto()).thenReturn(observationDto);

        String result = observationService.processingOrderTestStateTransition(labResultProxyVO, orderTest, null, false);

        assertEquals(NEDSSConstant.OBS_LAB_CR_MR, result);
    }

    @Test
    void testProcessingOrderTestStateTransition_NewWithoutProcessingDecision() {
        LabResultProxyContainer labResultProxyVO = mock(LabResultProxyContainer.class);
        ObservationContainer orderTest = mock(ObservationContainer.class);
        ObservationDto observationDto = new ObservationDto();
        when(labResultProxyVO.isItNew()).thenReturn(true);
        when(orderTest.getTheObservationDto()).thenReturn(observationDto);

        String result = observationService.processingOrderTestStateTransition(labResultProxyVO, orderTest, null, false);

        assertEquals(NEDSSConstant.OBS_LAB_CR, result);
    }

    @Test
    void testProcessingOrderTestStateTransition_DirtyAndIsELR() {
        LabResultProxyContainer labResultProxyVO = mock(LabResultProxyContainer.class);
        ObservationContainer orderTest = mock(ObservationContainer.class);
        when(labResultProxyVO.isItDirty()).thenReturn(true);

        String result = observationService.processingOrderTestStateTransition(labResultProxyVO, orderTest, null, true);

        assertEquals(NEDSSConstant.OBS_LAB_CORRECT, result);
    }

    @Test
    void testProcessingOrderTestStateTransition_DirtyAndNotIsELR() {
        LabResultProxyContainer labResultProxyVO = mock(LabResultProxyContainer.class);
        ObservationContainer orderTest = mock(ObservationContainer.class);
        when(labResultProxyVO.isItDirty()).thenReturn(true);

        String result = observationService.processingOrderTestStateTransition(labResultProxyVO, orderTest, null, false);

        assertEquals(NEDSSConstant.OBS_LAB_EDIT, result);
    }

    @Test
    void testProcessingOrderTestStateTransition_NoConditionsMet() {
        LabResultProxyContainer labResultProxyVO = mock(LabResultProxyContainer.class);
        ObservationContainer orderTest = mock(ObservationContainer.class);
        ObservationDto observationDto = new ObservationDto();
        when(labResultProxyVO.isItNew()).thenReturn(false);
        when(labResultProxyVO.isItDirty()).thenReturn(false);
        when(orderTest.getTheObservationDto()).thenReturn(observationDto);

        String businessTriggerCd = "INITIAL_TRIGGER";
        String result = observationService.processingOrderTestStateTransition(labResultProxyVO, orderTest, businessTriggerCd, false);

        assertEquals("INITIAL_TRIGGER", result);
    }


    @Test
    void testRetrieveOrganizationFromParticipation_Continue()  {
        Collection<ParticipationDto> partColl = new ArrayList<>();
        partColl.add(null);  // This should trigger the continue statement

        ParticipationDto validParticipationDto = new ParticipationDto();
        validParticipationDto.setSubjectClassCd(NEDSSConstant.PAR102_SUB_CD);
        validParticipationDto.setRecordStatusCd(NEDSSConstant.ACTIVE);
        validParticipationDto.setSubjectEntityUid(1L);
        validParticipationDto.setActUid(2L);
        partColl.add(validParticipationDto);

        when(organizationRepositoryUtil.loadObject(1L, 2L)).thenReturn(new OrganizationContainer());

        Collection<Object> result = observationService.retrieveOrganizationFromParticipation(partColl);

        // Check that the continue statement worked and the valid ParticipationDto was processed
        assertEquals(1, result.size());
        verify(organizationRepositoryUtil, times(1)).loadObject(1L, 2L);
    }

    @Test
    void testRetrieveMaterialFromParticipation_Continue() {
        Collection<ParticipationDto> partColl = new ArrayList<>();
        partColl.add(null);  // This should trigger the continue statement

        ParticipationDto validParticipationDto = new ParticipationDto();
        validParticipationDto.setSubjectClassCd(NEDSSConstant.PAR104_SUB_CD);
        validParticipationDto.setRecordStatusCd(NEDSSConstant.ACTIVE);
        validParticipationDto.setSubjectEntityUid(1L);
        partColl.add(validParticipationDto);

        MaterialContainer mockMaterialContainer = new MaterialContainer();
        when(materialService.loadMaterialObject(1L)).thenReturn(mockMaterialContainer);

        Collection<Object> result = observationService.retrieveMaterialFromParticipation(partColl);

        // Check that the continue statement worked and the valid ParticipationDto was processed
        assertEquals(1, result.size());
        verify(materialService, times(1)).loadMaterialObject(1L);
    }


    @Test
    void testRetrievePersonAndRoleFromParticipation_Continue() {
        Collection<ParticipationDto> partColl = new ArrayList<>();
        partColl.add(null);  // This should trigger the continue statement

        ParticipationDto validParticipationDto = new ParticipationDto();
        validParticipationDto.setSubjectClassCd(NEDSSConstant.PAR110_SUB_CD);
        validParticipationDto.setRecordStatusCd(NEDSSConstant.ACTIVE);
        validParticipationDto.setTypeCd(NEDSSConstant.PAR110_TYP_CD);
        validParticipationDto.setSubjectEntityUid(1L);
        partColl.add(validParticipationDto);

        PersonContainer mockPersonContainer = new PersonContainer();
        mockPersonContainer.setTheRoleDtoCollection(Collections.emptyList());
        when(patientRepositoryUtil.loadPerson(1L)).thenReturn(mockPersonContainer);

        Map<DataProcessingMapKey, Object> result = observationService.retrievePersonAndRoleFromParticipation(partColl);

        // Check that the continue statement worked and the valid ParticipationDto was processed
        assertEquals(1, ((Collection<?>) result.get(DataProcessingMapKey.PERSON)).size());
        verify(patientRepositoryUtil, times(1)).loadPerson(1L);
    }


    @Test
    void testRetrieveScopedPersons_Continue() {
        Long scopingUid = 1L;

        // Mock RoleService to return a collection with a null element and a valid element
        RoleDto nullRoleDto = null;
        RoleDto validRoleDto = new RoleDto();
        validRoleDto.setSubjectEntityUid(2L);

        Collection<RoleDto> roleDTColl = new ArrayList<>();
        roleDTColl.add(nullRoleDto);  // This should trigger the continue statement
        roleDTColl.add(validRoleDto);

        when(roleService.findRoleScopedToPatient(scopingUid)).thenReturn(roleDTColl);

        PersonContainer mockPersonContainer = new PersonContainer();
        when(patientRepositoryUtil.loadPerson(2L)).thenReturn(mockPersonContainer);

        Collection<PersonContainer> result = observationService.retrieveScopedPersons(scopingUid);

        // Check that the continue statement worked and the valid RoleDto was processed
        assertEquals(1, result.size());
        verify(patientRepositoryUtil, times(1)).loadPerson(2L);
    }

    @Test
    void testProcessingNotELRLab_isELRTrue() throws DataProcessingException {
        boolean isELR = true;
        LabResultProxyContainer lrProxyVO = new LabResultProxyContainer();
        ObservationContainer orderedTest = new ObservationContainer();
        long observationId = 1L;

        // When isELR is true, no further processing should happen
        observationService.processingNotELRLab(isELR, lrProxyVO, orderedTest, observationId);

        assertFalse(lrProxyVO.isAssociatedNotificationInd());
        assertFalse(lrProxyVO.isAssociatedInvInd());
        assertNull(lrProxyVO.getEDXDocumentCollection());
        assertNull(lrProxyVO.getTheConditionsList());

        verifyNoInteractions(notificationService, actRelationshipService, edxDocumentService, observationCodeService);
    }

    @Test
    void testProcessingNotELRLab_isELRFalse_NoExistingNotification() throws DataProcessingException {
        boolean isELR = false;
        LabResultProxyContainer lrProxyVO = new LabResultProxyContainer();
        ObservationContainer orderedTest = new ObservationContainer();
        long observationId = 1L;

        when(notificationService.checkForExistingNotification(lrProxyVO)).thenReturn(false);

        Collection<ActRelationshipDto> actRelationshipDtos = new ArrayList<>();
        when(actRelationshipService.loadActRelationshipBySrcIdAndTypeCode(observationId, NEDSSConstant.LAB_REPORT)).thenReturn(actRelationshipDtos);

        Collection<EDXDocumentDto> edxDocumentDtos = new ArrayList<>();
        when(edxDocumentService.selectEdxDocumentCollectionByActUid(observationId)).thenReturn(edxDocumentDtos);

        ArrayList<String> conditionList = new ArrayList<>();
        when(observationCodeService.deriveTheConditionCodeList(lrProxyVO, orderedTest)).thenReturn(conditionList);

        observationService.processingNotELRLab(isELR, lrProxyVO, orderedTest, observationId);

        verify(notificationService).checkForExistingNotification(lrProxyVO);
        verify(actRelationshipService).loadActRelationshipBySrcIdAndTypeCode(observationId, NEDSSConstant.LAB_REPORT);
        verify(edxDocumentService).selectEdxDocumentCollectionByActUid(observationId);
        verify(observationCodeService).deriveTheConditionCodeList(lrProxyVO, orderedTest);
    }

    @Test
    void testProcessingNotELRLab_isELRFalse_ExistingNotification() throws DataProcessingException {
        boolean isELR = false;
        LabResultProxyContainer lrProxyVO = new LabResultProxyContainer();
        ObservationContainer orderedTest = new ObservationContainer();
        long observationId = 1L;

        when(notificationService.checkForExistingNotification(lrProxyVO)).thenReturn(true);

        Collection<ActRelationshipDto> actRelationshipDtos = new ArrayList<>();
        actRelationshipDtos.add(new ActRelationshipDto());
        when(actRelationshipService.loadActRelationshipBySrcIdAndTypeCode(observationId, NEDSSConstant.LAB_REPORT)).thenReturn(actRelationshipDtos);

        Collection<EDXDocumentDto> edxDocumentDtos = new ArrayList<>();
        edxDocumentDtos.add(new EDXDocumentDto());
        when(edxDocumentService.selectEdxDocumentCollectionByActUid(observationId)).thenReturn(edxDocumentDtos);

        ArrayList<String> conditionList = new ArrayList<>();
        conditionList.add("condition");
        when(observationCodeService.deriveTheConditionCodeList(lrProxyVO, orderedTest)).thenReturn(conditionList);

        observationService.processingNotELRLab(isELR, lrProxyVO, orderedTest, observationId);

        assertTrue(lrProxyVO.isAssociatedNotificationInd());
        assertTrue(lrProxyVO.isAssociatedInvInd());
        assertEquals(edxDocumentDtos, lrProxyVO.getEDXDocumentCollection());
        assertEquals(conditionList, lrProxyVO.getTheConditionsList());

        verify(notificationService).checkForExistingNotification(lrProxyVO);
        verify(actRelationshipService).loadActRelationshipBySrcIdAndTypeCode(observationId, NEDSSConstant.LAB_REPORT);
        verify(edxDocumentService).selectEdxDocumentCollectionByActUid(observationId);
        verify(observationCodeService).deriveTheConditionCodeList(lrProxyVO, orderedTest);
    }


    @Test
    void testLoadingObservationToLabResultContainerActHelper_AllCases() {
        LabResultProxyContainer lrProxyVO = new LabResultProxyContainer();
        boolean isELR = false;
        ObservationContainer orderedTest = new ObservationContainer();
        ObservationDto observationDto = new ObservationDto();
        orderedTest.setTheObservationDto(observationDto);

        Map<DataProcessingMapKey, Object> allAct = new HashMap<>();
        allAct.put(DataProcessingMapKey.INTERVENTION, new ArrayList<>());
        allAct.put(DataProcessingMapKey.OBSERVATION, new ArrayList<ObservationContainer>());
        allAct.put(DataProcessingMapKey.ORGANIZATION, new ArrayList<OrganizationContainer>());


        observationService.loadingObservationToLabResultContainerActHelper(lrProxyVO, isELR, allAct, orderedTest);

        assertEquals(1, ((Collection<ObservationContainer>) allAct.get(DataProcessingMapKey.OBSERVATION)).size());
        assertEquals(((Collection<ObservationContainer>) allAct.get(DataProcessingMapKey.OBSERVATION)).size(), lrProxyVO.getTheObservationContainerCollection().size());
    }

    @Test
    void testLoadingObservationToLabResultContainerActHelper_ELRTrue() {
        LabResultProxyContainer lrProxyVO = new LabResultProxyContainer();
        boolean isELR = true;
        ObservationContainer orderedTest = new ObservationContainer();
        ObservationDto observationDto = new ObservationDto();
        orderedTest.setTheObservationDto(observationDto);

        Map<DataProcessingMapKey, Object> allAct = new HashMap<>();
        allAct.put(DataProcessingMapKey.INTERVENTION, new ArrayList<>());
        allAct.put(DataProcessingMapKey.OBSERVATION, new ArrayList<ObservationContainer>());
        allAct.put(DataProcessingMapKey.ORGANIZATION, new ArrayList<OrganizationContainer>());

        observationService.loadingObservationToLabResultContainerActHelper(lrProxyVO, isELR, allAct, orderedTest);

        assertNull(orderedTest.getTheObservationDto().getAddUserName());
        assertNull(orderedTest.getTheObservationDto().getLastChgUserName());
        assertEquals(1, ((Collection<ObservationContainer>) allAct.get(DataProcessingMapKey.OBSERVATION)).size());
        assertEquals(((Collection<ObservationContainer>) allAct.get(DataProcessingMapKey.OBSERVATION)).size(), lrProxyVO.getTheObservationContainerCollection().size());
    }

    @Test
    void testLoadingObservationToLabResultContainerActHelper_LabCollectionNotEmpty() {
        LabResultProxyContainer lrProxyVO = new LabResultProxyContainer();
        boolean isELR = false;
        ObservationContainer orderedTest = new ObservationContainer();
        ObservationDto observationDto = new ObservationDto();
        orderedTest.setTheObservationDto(observationDto);

        Map<DataProcessingMapKey, Object> allAct = new HashMap<>();
        allAct.put(DataProcessingMapKey.INTERVENTION, new ArrayList<>());
        allAct.put(DataProcessingMapKey.OBSERVATION, new ArrayList<ObservationContainer>());
        allAct.put(DataProcessingMapKey.ORGANIZATION, new ArrayList<OrganizationContainer>());

        Collection<OrganizationContainer> labColl = new ArrayList<>();
        labColl.add(new OrganizationContainer());
        allAct.put(DataProcessingMapKey.ORGANIZATION, labColl);


        observationService.loadingObservationToLabResultContainerActHelper(lrProxyVO, isELR, allAct, orderedTest);

        assertEquals(1, ((Collection<ObservationContainer>) allAct.get(DataProcessingMapKey.OBSERVATION)).size());
        assertEquals(((Collection<ObservationContainer>) allAct.get(DataProcessingMapKey.OBSERVATION)).size(), lrProxyVO.getTheObservationContainerCollection().size());
        assertEquals(1, lrProxyVO.getTheOrganizationContainerCollection().size());
    }

    @Test
    void testLoadingObservationToLabResultContainerActHelper_AllActEmpty() {
        LabResultProxyContainer lrProxyVO = new LabResultProxyContainer();
        boolean isELR = false;
        ObservationContainer orderedTest = new ObservationContainer();

        Map<DataProcessingMapKey, Object> allAct = new HashMap<>();

        observationService.loadingObservationToLabResultContainerActHelper(lrProxyVO, isELR, allAct, orderedTest);

        assertNull(lrProxyVO.getTheInterventionVOCollection());
    }


    @Test
    void testUpdateLabResultWithAutoResendNotification_Success() throws DataProcessingException {
        LabResultProxyContainer labResultProxyVO = new LabResultProxyContainer();
        labResultProxyVO.associatedNotificationInd = true;

        observationService.updateLabResultWithAutoResendNotification(labResultProxyVO);

        verify(investigationService, times(1)).updateAutoResendNotificationsAsync(labResultProxyVO);
        verifyNoInteractions(nndActivityLogService);
    }

    @Test
    void testUpdateLabResultWithAutoResendNotification_Exception() throws DataProcessingException {
        LabResultProxyContainer labResultProxyVO = new LabResultProxyContainer();
        labResultProxyVO.associatedNotificationInd = true;

        doThrow(new RuntimeException("Test Exception")).when(investigationService).updateAutoResendNotificationsAsync(labResultProxyVO);

        ObservationDto observationDto = new ObservationDto();
        observationDto.setLocalId("testLocalId");
        observationDto.setCd("LabReport");
        ObservationContainer observationContainer = new ObservationContainer();
        observationContainer.setTheObservationDto(observationDto);
        Collection<ObservationContainer> observationCollection = new ArrayList<>();
        observationCollection.add(observationContainer);
        labResultProxyVO.setTheObservationContainerCollection(observationCollection);

        NNDActivityLogDto nndActivityLogDto = observationService.updateLabResultWithAutoResendNotification(labResultProxyVO);

        verify(investigationService, times(1)).updateAutoResendNotificationsAsync(labResultProxyVO);
        verify(nndActivityLogService, times(1)).saveNddActivityLog(any(NNDActivityLogDto.class));
        assertEquals("testLocalId", nndActivityLogDto.getLocalId());
        assertEquals("java.lang.RuntimeException: Test Exception", nndActivityLogDto.getErrorMessageTxt());
    }

    @Test
    void testUpdateLabResultWithAutoResendNotification_NoNotification() throws DataProcessingException {
        LabResultProxyContainer labResultProxyVO = new LabResultProxyContainer();
        labResultProxyVO.associatedNotificationInd = false;

        NNDActivityLogDto result = observationService.updateLabResultWithAutoResendNotification(labResultProxyVO);

        verifyNoInteractions(investigationService);
        verifyNoInteractions(nndActivityLogService);
        assertNull(result);
    }

    @Test
    void testUpdateLabResultWithAutoResendNotification_ExceptionWithNullLocalId() throws DataProcessingException {
        LabResultProxyContainer labResultProxyVO = new LabResultProxyContainer();
        labResultProxyVO.associatedNotificationInd = true;

        doThrow(new RuntimeException("Test Exception")).when(investigationService).updateAutoResendNotificationsAsync(labResultProxyVO);

        ObservationDto observationDto = new ObservationDto();
        observationDto.setCd("LabReport");

        ObservationContainer observationContainer = new ObservationContainer();
        observationContainer.setTheObservationDto(observationDto);
        Collection<ObservationContainer> observationCollection = new ArrayList<>();
        observationCollection.add(observationContainer);
        labResultProxyVO.setTheObservationContainerCollection(observationCollection);

        NNDActivityLogDto nndActivityLogDto = observationService.updateLabResultWithAutoResendNotification(labResultProxyVO);

        verify(investigationService, times(1)).updateAutoResendNotificationsAsync(labResultProxyVO);
        verify(nndActivityLogService, times(1)).saveNddActivityLog(any(NNDActivityLogDto.class));
        assertEquals("N/A", nndActivityLogDto.getLocalId());
        assertEquals("java.lang.RuntimeException: Test Exception", nndActivityLogDto.getErrorMessageTxt());
    }


    @Test
    void testFindObservationByCode_CollectionIsNull() {
        ObservationContainer result = observationService.findObservationByCode(null, "someCode");
        assertNull(result);
    }

    @Test
    void testFindObservationByCode_NoMatchingCode() {
        Collection<ObservationContainer> coll = new ArrayList<>();
        coll.add(createObservationContainer("code1"));
        coll.add(createObservationContainer("code2"));

        ObservationContainer result = observationService.findObservationByCode(coll, "someCode");
        assertNull(result);
    }

    @Test
    void testFindObservationByCode_MatchingCode() {
        Collection<ObservationContainer> coll = new ArrayList<>();
        coll.add(createObservationContainer("code1"));
        coll.add(createObservationContainer("someCode"));

        ObservationContainer result = observationService.findObservationByCode(coll, "someCode");
        assertEquals("someCode", result.getTheObservationDto().getCd());
    }

    @Test
    void testFindObservationByCode_ObservationDtoIsNull() {
        Collection<ObservationContainer> coll = new ArrayList<>();
        coll.add(new ObservationContainer());
        coll.add(createObservationContainer("someCode"));

        ObservationContainer result = observationService.findObservationByCode(coll, "someCode");
        assertEquals("someCode", result.getTheObservationDto().getCd());
    }

    @Test
    void testFindObservationByCode_CodeIsNull() {
        Collection<ObservationContainer> coll = new ArrayList<>();
        coll.add(createObservationContainer(null));
        coll.add(createObservationContainer("someCode"));

        ObservationContainer result = observationService.findObservationByCode(coll, "someCode");
        assertEquals("someCode", result.getTheObservationDto().getCd());
    }

    @Test
    void testFindObservationByCode_TrimmedCodeMatching() {
        Collection<ObservationContainer> coll = new ArrayList<>();
        coll.add(createObservationContainer("  someCode  "));

        ObservationContainer result = observationService.findObservationByCode(coll, "someCode");
        assertEquals("  someCode  ", result.getTheObservationDto().getCd());
    }

    private ObservationContainer createObservationContainer(String code) {
        ObservationDto observationDto = new ObservationDto();
        observationDto.setCd(code);

        ObservationContainer observationContainer = new ObservationContainer();
        observationContainer.setTheObservationDto(observationDto);

        return observationContainer;
    }

    @Test
    void testHandleFalseUidReplacement_whenItIsNewAndFalseUidIsNegative_callsUidService() {
        // Arrange
        BaseContainer proxyVO = new LabResultProxyContainer(); // or any BaseContainer subclass
        ObservationDto dto = new ObservationDto();
        dto.setObservationUid(-99L); // false UID

        ObservationContainer container = new ObservationContainer();
        container.setItNew(true);
        container.setTheObservationDto(dto);

        Long realUid = 123L;

        // Act
        observationService.handleFalseUidReplacement(proxyVO, container, realUid);

        // Assert
        verify(uidService).setFalseToNewForObservation(proxyVO, -99L, realUid);
    }

    @Test
    void testHandleFalseUidReplacement_whenItIsNewButFalseUidIsPositive_doesNotCallUidService() {
        // Arrange
        BaseContainer proxyVO = new LabResultProxyContainer();
        ObservationDto dto = new ObservationDto();
        dto.setObservationUid(99L); // valid UID

        ObservationContainer container = new ObservationContainer();
        container.setItNew(true);
        container.setTheObservationDto(dto);

        Long realUid = 123L;

        // Act
        observationService.handleFalseUidReplacement(proxyVO, container, realUid);

        // Assert
        verify(uidService, never()).setFalseToNewForObservation(any(), anyLong(), anyLong());
    }

    @Test
    void testHandleFalseUidReplacement_whenItIsNewButUidIsNull_doesNotCallUidService() {
        // Arrange
        BaseContainer proxyVO = new LabResultProxyContainer();
        ObservationDto dto = new ObservationDto(); // UID is null

        ObservationContainer container = new ObservationContainer();
        container.setItNew(true);
        container.setTheObservationDto(dto);

        Long realUid = 123L;

        // Act
        observationService.handleFalseUidReplacement(proxyVO, container, realUid);

        // Assert
        verify(uidService, never()).setFalseToNewForObservation(any(), any(), anyLong());
    }

    @Test
    void testHandleFalseUidReplacement_whenItIsNotNew_doesNotCallUidService() {
        // Arrange
        BaseContainer proxyVO = new LabResultProxyContainer();
        ObservationDto dto = new ObservationDto();
        dto.setObservationUid(-88L);

        ObservationContainer container = new ObservationContainer();
        container.setItNew(false); // not new
        container.setTheObservationDto(dto);

        Long realUid = 123L;

        // Act
        observationService.handleFalseUidReplacement(proxyVO, container, realUid);

        // Assert
        verify(uidService, never()).setFalseToNewForObservation(any(), anyLong(), anyLong());
    }

    @Test
    void testIsRootObservation_whenCtrlCdMatchesMobCtrlCdDisplay_returnsTrue() {
        // Arrange
        ObservationDto dto = new ObservationDto();
        dto.setObsDomainCdSt1("not-matching"); // ensure this does not trigger first if
        dto.setCtrlCdDisplayForm(NEDSSConstant.MOB_CTRLCD_DISPLAY); // triggers return true

        ObservationContainer container = new ObservationContainer();
        container.setTheObservationDto(dto);

        // Act
        boolean result = observationService.isRootObservation(container, false);

        // Assert
        assertTrue(result, "Should return true when ctrlCdDisplayForm matches MOB_CTRLCD_DISPLAY");
    }

    @Test
    void testExtractObservationCollection_whenNotLabResultProxy_returnsNull() {
        // Arrange
        BaseContainer proxyVO = new BaseContainer() {}; // anonymous subclass, not LabResultProxyContainer

        // Act
        Collection<ObservationContainer> result = observationService.extractObservationCollection(proxyVO);

        // Assert
        assertNull(result, "Expected null when proxyVO is not a LabResultProxyContainer");
    }

    @Test
    void testStoreObservationVOCollection_whenExtractedCollectionIsNull_returnsNull() throws DataProcessingException {
        // Arrange
        BaseContainer proxyVO = new BaseContainer() {}; // not a LabResultProxyContainer
        // No need to mock extractObservationCollection since we want it to return null

        // Act
        Long result = observationService.storeObservationVOCollection(proxyVO);

        // Assert
        assertNull(result, "Expected null when extracted ObservationContainer collection is null");
    }

    @Test
    void testStoreObservationVOCollection_whenExtractedCollectionIsEmpty_returnsNull() throws DataProcessingException {
        // Arrange
        LabResultProxyContainer proxyVO = new LabResultProxyContainer();
        proxyVO.setTheObservationContainerCollection(Collections.emptyList()); // empty collection

        // Act
        Long result = observationService.storeObservationVOCollection(proxyVO);

        // Assert
        assertNull(result, "Expected null when extracted ObservationContainer collection is empty");
    }

    @Test
    void testFindLocalUidsFor_observationAndPersonFound_setsExpectedLocalIds() {
        // Arrange
        Long obsUid = 100L;
        Long personUid = 200L;

        // Mock observation repository result
        Observation obs = new Observation();
        obs.setLocalId("OBS_LOCAL_ID");
        when(observationRepository.findById(obsUid)).thenReturn(Optional.of(obs));

        // Mock person repository result
        Person person = new Person();
        person.setLocalId("PERSON_LOCAL_ID");
        when(personRepository.findById(personUid)).thenReturn(Optional.of(person));

        // Act
        Map<Object, Object> result = observationService.findLocalUidsFor(personUid, obsUid);

        // Assert
        assertEquals("OBS_LOCAL_ID", result.get(NEDSSConstant.SETLAB_RETURN_OBS_LOCAL));
        assertTrue(result.get(NEDSSConstant.SETLAB_RETURN_OBSDT) instanceof ObservationDto);
        assertEquals("PERSON_LOCAL_ID", result.get(NEDSSConstant.SETLAB_RETURN_MPR_LOCAL));
    }

    @Test
    void testHandleJurisdictionCode_codeIsNull_doesNothing() throws DataProcessingException {
        // Arrange
        ObservationDto dto = new ObservationDto();
        dto.setJurisdictionCd(null);
        Map<Object, Object> errors = new HashMap<>();

        // Act
        observationService.handleJurisdictionCode(new LabResultProxyContainer(), dto, errors);

        // Assert
        assertTrue(errors.isEmpty());
    }

    @Test
    void testHandleJurisdictionCode_codeIsAnyJurisdiction_addsError() throws DataProcessingException {
        // Arrange
        ObservationDto dto = new ObservationDto();
        dto.setJurisdictionCd(ProgramAreaJurisdiction.ANY_JURISDICTION);
        Map<Object, Object> errors = new HashMap<>();
        LabResultProxyContainer proxy = new LabResultProxyContainer();

        when(jurisdictionService.deriveJurisdictionCd(proxy, dto)).thenReturn("SomeError");

        // Act
        observationService.handleJurisdictionCode(proxy, dto, errors);

        // Assert
        assertTrue(errors.containsKey(NEDSSConstant.SETLAB_RETURN_JURISDICTION_ERRORS));
        assertEquals(ProgramAreaJurisdiction.ANY_JURISDICTION, errors.get(NEDSSConstant.SETLAB_RETURN_JURISDICTION_ERRORS));
    }

    @Test
    void testHandleJurisdictionCode_codeIsJurisdictionNone_addsError() throws DataProcessingException {
        // Arrange
        ObservationDto dto = new ObservationDto();
        dto.setJurisdictionCd(ProgramAreaJurisdiction.JURISDICTION_NONE);
        Map<Object, Object> errors = new HashMap<>();
        LabResultProxyContainer proxy = new LabResultProxyContainer();

        when(jurisdictionService.deriveJurisdictionCd(proxy, dto)).thenReturn("SomeError");

        // Act
        observationService.handleJurisdictionCode(proxy, dto, errors);

        // Assert
        assertTrue(errors.containsKey(NEDSSConstant.SETLAB_RETURN_JURISDICTION_ERRORS));
        assertEquals(ProgramAreaJurisdiction.JURISDICTION_NONE, errors.get(NEDSSConstant.SETLAB_RETURN_JURISDICTION_ERRORS));
    }

    @Test
    void testHandleJurisdictionCode_codeIsAnyJurisdiction_butNoErrorReturned_doesNotAddToMap() throws DataProcessingException {
        // Arrange
        ObservationDto dto = new ObservationDto();
        dto.setJurisdictionCd(ProgramAreaJurisdiction.ANY_JURISDICTION);
        Map<Object, Object> errors = new HashMap<>();
        LabResultProxyContainer proxy = new LabResultProxyContainer();

        when(jurisdictionService.deriveJurisdictionCd(proxy, dto)).thenReturn(null);

        // Act
        observationService.handleJurisdictionCode(proxy, dto, errors);

        // Assert
        assertFalse(errors.containsKey(NEDSSConstant.SETLAB_RETURN_JURISDICTION_ERRORS));
    }



    @Test
    void testHandleProgramAreaCode_whenPaCdIsNull_doesNothing() throws DataProcessingException {
        // Arrange
        ObservationDto dto = new ObservationDto();
        dto.setProgAreaCd(null);
        ObservationContainer container = new ObservationContainer();
        container.setTheObservationDto(dto);
        Map<Object, Object> errors = new HashMap<>();

        // Act
        observationService.handleProgramAreaCode(new LabResultProxyContainer(), container, errors);

        // Assert
        assertTrue(errors.isEmpty());
    }

    @Test
    void testHandleProgramAreaCode_whenPaCdIsNotAnyProgramArea_doesNothing() throws DataProcessingException {
        // Arrange
        ObservationDto dto = new ObservationDto();
        dto.setProgAreaCd("STD"); // Not "ANY"
        ObservationContainer container = new ObservationContainer();
        container.setTheObservationDto(dto);
        Map<Object, Object> errors = new HashMap<>();

        // Act
        observationService.handleProgramAreaCode(new LabResultProxyContainer(), container, errors);

        // Assert
        assertTrue(errors.isEmpty());
    }

    @Test
    void testHandleProgramAreaCode_whenDerivedErrorIsNull_doesNothing() throws DataProcessingException {
        // Arrange
        ObservationDto dto = new ObservationDto();
        dto.setProgAreaCd(ProgramAreaJurisdiction.ANY_PROGRAM_AREA);
        ObservationContainer container = new ObservationContainer();
        container.setTheObservationDto(dto);
        LabResultProxyContainer proxy = new LabResultProxyContainer();
        Map<Object, Object> errors = new HashMap<>();

        when(programAreaService.deriveProgramAreaCd(proxy, container)).thenReturn(null);

        // Act
        observationService.handleProgramAreaCode(proxy, container, errors);

        // Assert
        assertTrue(errors.isEmpty());
    }

    @Test
    void testHandleProgramAreaCode_whenDerivedErrorIsPresent_addsToErrors() throws DataProcessingException {
        // Arrange
        ObservationDto dto = new ObservationDto();
        dto.setProgAreaCd(ProgramAreaJurisdiction.ANY_PROGRAM_AREA);
        ObservationContainer container = new ObservationContainer();
        container.setTheObservationDto(dto);
        LabResultProxyContainer proxy = new LabResultProxyContainer();
        Map<Object, Object> errors = new HashMap<>();

        when(programAreaService.deriveProgramAreaCd(proxy, container)).thenReturn("SomeError");

        // Act
        observationService.handleProgramAreaCode(proxy, container, errors);

        // Assert
        assertEquals("SomeError", errors.get(NEDSSConstant.SETLAB_RETURN_PROGRAM_AREA_ERRORS));
    }


    @Test
    void testRetrieveEntityFromParticipationForContainer_WhenNoPersonOrRoleKeys() {
        // Arrange
        ParticipationDto participationDto = new ParticipationDto();
        Collection<ParticipationDto> partColl = Collections.singletonList(participationDto);

        // Stub retrievePersonAndRoleFromParticipation to return empty map
        ObservationService spyService = Mockito.spy(observationService);
        doReturn(Collections.emptyMap()).when(spyService).retrievePersonAndRoleFromParticipation(partColl);

        // Stub retrieveOrganizationFromParticipation
        Collection<Object> mockOrgCollection = Collections.singletonList(new Object());
        doReturn(mockOrgCollection).when(spyService).retrieveOrganizationFromParticipation(partColl);

        // Stub retrieveMaterialFromParticipation
        Collection<Object> mockMaterialCollection = Collections.singletonList(new Object());
        doReturn(mockMaterialCollection).when(spyService).retrieveMaterialFromParticipation(partColl);

        // Act
        Map<DataProcessingMapKey, Object> result = spyService.retrieveEntityFromParticipationForContainer(partColl);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.containsKey(DataProcessingMapKey.ORGANIZATION));
        assertTrue(result.containsKey(DataProcessingMapKey.MATERIAL));
        assertFalse(result.containsKey(DataProcessingMapKey.PERSON));
        assertFalse(result.containsKey(DataProcessingMapKey.ROLE));
        assertSame(mockOrgCollection, result.get(DataProcessingMapKey.ORGANIZATION));
        assertSame(mockMaterialCollection, result.get(DataProcessingMapKey.MATERIAL));

    }

    @Test
    void testRetrieveOrganizationFromParticipation_NoMatch_SubjectClassCdNull() {
        ParticipationDto dto = new ParticipationDto();
        dto.setSubjectClassCd(null);
        dto.setRecordStatusCd(NEDSSConstant.ACTIVE);
        var result = observationService.retrieveOrganizationFromParticipation(List.of(dto));
        assertNull(result);
    }

    @Test
    void testRetrieveOrganizationFromParticipation_NoMatch_SubjectClassCdWrong() {
        ParticipationDto dto = new ParticipationDto();
        dto.setSubjectClassCd("WRONG_CODE");
        dto.setRecordStatusCd(NEDSSConstant.ACTIVE);
        var result = observationService.retrieveOrganizationFromParticipation(List.of(dto));
        assertNull(result);
    }

    @Test
    void testRetrieveOrganizationFromParticipation_NoMatch_RecordStatusCdNull() {
        ParticipationDto dto = new ParticipationDto();
        dto.setSubjectClassCd(NEDSSConstant.PAR102_SUB_CD);
        dto.setRecordStatusCd(null);
        var result = observationService.retrieveOrganizationFromParticipation(List.of(dto));
        assertNull(result);
    }

    @Test
    void testRetrieveOrganizationFromParticipation_NoMatch_RecordStatusCdWrong() {
        ParticipationDto dto = new ParticipationDto();
        dto.setSubjectClassCd(NEDSSConstant.PAR102_SUB_CD);
        dto.setRecordStatusCd("INACTIVE");
        var result = observationService.retrieveOrganizationFromParticipation(List.of(dto));
        assertNull(result);
    }

    @Test
    void testRetrieveOrganizationFromParticipation_Match_Success() {
        Long expectedOrgUid = 123L;
        Long actUid = 456L;
        OrganizationContainer mockOrg = new OrganizationContainer();

        ParticipationDto dto = new ParticipationDto();
        dto.setSubjectClassCd(NEDSSConstant.PAR102_SUB_CD);
        dto.setRecordStatusCd(NEDSSConstant.ACTIVE);
        dto.setSubjectEntityUid(expectedOrgUid);
        dto.setActUid(actUid);

        when(organizationRepositoryUtil.loadObject(expectedOrgUid, actUid)).thenReturn(mockOrg);

        var result = observationService.retrieveOrganizationFromParticipation(List.of(dto));

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(mockOrg, result.iterator().next());

        verify(organizationRepositoryUtil).loadObject(expectedOrgUid, actUid);
    }

    @Test
    void testRetrieveOrganizationFromParticipation_MultipleMatches_TriggersElsePath() {
        Long uid1 = 1L, uid2 = 2L;
        Long act1 = 10L, act2 = 20L;

        OrganizationContainer org1 = new OrganizationContainer();
        OrganizationContainer org2 = new OrganizationContainer();

        ParticipationDto dto1 = new ParticipationDto();
        dto1.setSubjectClassCd(NEDSSConstant.PAR102_SUB_CD);
        dto1.setRecordStatusCd(NEDSSConstant.ACTIVE);
        dto1.setSubjectEntityUid(uid1);
        dto1.setActUid(act1);

        ParticipationDto dto2 = new ParticipationDto();
        dto2.setSubjectClassCd(NEDSSConstant.PAR102_SUB_CD);
        dto2.setRecordStatusCd(NEDSSConstant.ACTIVE);
        dto2.setSubjectEntityUid(uid2);
        dto2.setActUid(act2);

        when(organizationRepositoryUtil.loadObject(uid1, act1)).thenReturn(org1);
        when(organizationRepositoryUtil.loadObject(uid2, act2)).thenReturn(org2);

        Collection<ParticipationDto> input = List.of(dto1, dto2);

        var result = observationService.retrieveOrganizationFromParticipation(input);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(org1));
        assertTrue(result.contains(org2));

        verify(organizationRepositoryUtil).loadObject(uid1, act1);
        verify(organizationRepositoryUtil).loadObject(uid2, act2);
    }

    @Test
    void testRetrieveMaterialFromParticipation_SubjectClassCdNull() {
        ParticipationDto dto = new ParticipationDto();
        dto.setSubjectClassCd(null);
        dto.setRecordStatusCd(NEDSSConstant.ACTIVE);

        var result = observationService.retrieveMaterialFromParticipation(List.of(dto));

        assertNull(result);
    }

    @Test
    void testRetrieveMaterialFromParticipation_SubjectClassCdWrong() {
        ParticipationDto dto = new ParticipationDto();
        dto.setSubjectClassCd("WRONG_CODE");
        dto.setRecordStatusCd(NEDSSConstant.ACTIVE);

        var result = observationService.retrieveMaterialFromParticipation(List.of(dto));

        assertNull(result);
    }

    @Test
    void testRetrieveMaterialFromParticipation_RecordStatusCdNull() {
        ParticipationDto dto = new ParticipationDto();
        dto.setSubjectClassCd(NEDSSConstant.PAR104_SUB_CD);
        dto.setRecordStatusCd(null);

        var result = observationService.retrieveMaterialFromParticipation(List.of(dto));

        assertNull(result);
    }

    @Test
    void testRetrieveMaterialFromParticipation_RecordStatusCdWrong() {
        ParticipationDto dto = new ParticipationDto();
        dto.setSubjectClassCd(NEDSSConstant.PAR104_SUB_CD);
        dto.setRecordStatusCd("INACTIVE");

        var result = observationService.retrieveMaterialFromParticipation(List.of(dto));

        assertNull(result);
    }

    @Test
    void testRetrieveMaterialFromParticipation_ValidMatch() {
        Long materialUid = 123L;
        ParticipationDto dto = new ParticipationDto();
        dto.setSubjectClassCd(NEDSSConstant.PAR104_SUB_CD);
        dto.setRecordStatusCd(NEDSSConstant.ACTIVE);
        dto.setSubjectEntityUid(materialUid);

        MaterialContainer material = new MaterialContainer();
        when(materialService.loadMaterialObject(materialUid)).thenReturn(material);

        var result = observationService.retrieveMaterialFromParticipation(List.of(dto));

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(material));
        verify(materialService).loadMaterialObject(materialUid);
    }

    @Test
    void testRetrieveMaterialFromParticipation_TwoValidMatches() {
        ParticipationDto dto1 = new ParticipationDto();
        dto1.setSubjectClassCd(NEDSSConstant.PAR104_SUB_CD);
        dto1.setRecordStatusCd(NEDSSConstant.ACTIVE);
        dto1.setSubjectEntityUid(100L);

        ParticipationDto dto2 = new ParticipationDto();
        dto2.setSubjectClassCd(NEDSSConstant.PAR104_SUB_CD);
        dto2.setRecordStatusCd(NEDSSConstant.ACTIVE);
        dto2.setSubjectEntityUid(200L);

        MaterialContainer mat1 = new MaterialContainer();
        MaterialContainer mat2 = new MaterialContainer();

        when(materialService.loadMaterialObject(100L)).thenReturn(mat1);
        when(materialService.loadMaterialObject(200L)).thenReturn(mat2);

        var result = observationService.retrieveMaterialFromParticipation(List.of(dto1, dto2));

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(mat1));
        assertTrue(result.contains(mat2));

        verify(materialService).loadMaterialObject(100L);
        verify(materialService).loadMaterialObject(200L);
    }

    @Test
    void testRetrieveMaterialFromParticipation_TwoValidEntries_HitsElsePath() {
        Long uid1 = 101L;
        Long uid2 = 202L;

        ParticipationDto dto1 = new ParticipationDto();
        dto1.setSubjectClassCd(NEDSSConstant.PAR104_SUB_CD);
        dto1.setRecordStatusCd(NEDSSConstant.ACTIVE);
        dto1.setSubjectEntityUid(uid1);

        ParticipationDto dto2 = new ParticipationDto();
        dto2.setSubjectClassCd(NEDSSConstant.PAR104_SUB_CD);
        dto2.setRecordStatusCd(NEDSSConstant.ACTIVE);
        dto2.setSubjectEntityUid(uid2);

        MaterialContainer mat1 = new MaterialContainer();
        MaterialContainer mat2 = new MaterialContainer();

        when(materialService.loadMaterialObject(uid1)).thenReturn(mat1);
        when(materialService.loadMaterialObject(uid2)).thenReturn(mat2);

        var result = observationService.retrieveMaterialFromParticipation(List.of(dto1, dto2));

        // Validate the collection contains both materials
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(mat1));
        assertTrue(result.contains(mat2));

        verify(materialService).loadMaterialObject(uid1);
        verify(materialService).loadMaterialObject(uid2);
    }

    @Test
    void testRetrievePersonAndRole_SubjectClassCdNull() {
        ParticipationDto dto = new ParticipationDto();
        dto.setSubjectClassCd(null);
        dto.setRecordStatusCd(NEDSSConstant.ACTIVE);

        var result = observationService.retrievePersonAndRoleFromParticipation(List.of(dto));

        assertTrue(((Collection<?>) result.get(DataProcessingMapKey.PERSON)).isEmpty());
        assertTrue(((Collection<?>) result.get(DataProcessingMapKey.ROLE)).isEmpty());
    }

    @Test
    void testRetrievePersonAndRole_SubjectClassCdWrong() {
        ParticipationDto dto = new ParticipationDto();
        dto.setSubjectClassCd("WRONG_CODE");
        dto.setRecordStatusCd(NEDSSConstant.ACTIVE);

        var result = observationService.retrievePersonAndRoleFromParticipation(List.of(dto));

        assertTrue(((Collection<?>) result.get(DataProcessingMapKey.PERSON)).isEmpty());
        assertTrue(((Collection<?>) result.get(DataProcessingMapKey.ROLE)).isEmpty());
    }

    @Test
    void testRetrievePersonAndRole_RecordStatusCdNull() {
        ParticipationDto dto = new ParticipationDto();
        dto.setSubjectClassCd(NEDSSConstant.PAR110_SUB_CD);
        dto.setRecordStatusCd(null);

        var result = observationService.retrievePersonAndRoleFromParticipation(List.of(dto));

        assertTrue(((Collection<?>) result.get(DataProcessingMapKey.PERSON)).isEmpty());
        assertTrue(((Collection<?>) result.get(DataProcessingMapKey.ROLE)).isEmpty());
    }

    @Test
    void testRetrievePersonAndRole_RecordStatusCdWrong() {
        ParticipationDto dto = new ParticipationDto();
        dto.setSubjectClassCd(NEDSSConstant.PAR110_SUB_CD);
        dto.setRecordStatusCd("INACTIVE");

        var result = observationService.retrievePersonAndRoleFromParticipation(List.of(dto));

        assertTrue(((Collection<?>) result.get(DataProcessingMapKey.PERSON)).isEmpty());
        assertTrue(((Collection<?>) result.get(DataProcessingMapKey.ROLE)).isEmpty());
    }

    @Test
    void testRetrievePersonAndRole_ValidMatch_PersonLoaded() {
        Long personUid = 111L;
        Long subjectEntityUid = 222L;

        RoleDto role = new RoleDto();
        PersonDto personDto = new PersonDto();
        personDto.setPersonUid(personUid);

        PersonContainer vo = new PersonContainer();
        vo.setThePersonDto(personDto);
        vo.setTheRoleDtoCollection(List.of(role));

        ParticipationDto dto = new ParticipationDto();
        dto.setSubjectClassCd(NEDSSConstant.PAR110_SUB_CD);
        dto.setRecordStatusCd(NEDSSConstant.ACTIVE);
        dto.setTypeCd(NEDSSConstant.PAR110_TYP_CD);
        dto.setSubjectEntityUid(subjectEntityUid);

        when(patientRepositoryUtil.loadPerson(subjectEntityUid)).thenReturn(vo);
        when(observationService.retrieveScopedPersons(personUid)).thenReturn(Collections.emptyList());

        var result = observationService.retrievePersonAndRoleFromParticipation(List.of(dto));

        var persons = (Collection<?>) result.get(DataProcessingMapKey.PERSON);
        var roles = (Collection<?>) result.get(DataProcessingMapKey.ROLE);

        assertEquals(1, persons.size());
        assertEquals(1, roles.size());
        assertTrue(persons.contains(vo));
        assertTrue(roles.contains(role));

        verify(patientRepositoryUtil).loadPerson(subjectEntityUid);
    }



    @Test
    void testRetrieveScopedPersons_Else_scopedPersonsAlreadyInitialized() {
        Long scopingUid = 123L;
        RoleDto role1 = new RoleDto();
        role1.setSubjectEntityUid(111L); // triggers init and add

        RoleDto role2 = new RoleDto();
        role2.setSubjectEntityUid(222L); // should hit else for scopedPersons

        PersonContainer person1 = new PersonContainer();
        PersonContainer person2 = new PersonContainer();

        when(roleService.findRoleScopedToPatient(scopingUid)).thenReturn(List.of(role1, role2));
        when(patientRepositoryUtil.loadPerson(111L)).thenReturn(person1);
        when(patientRepositoryUtil.loadPerson(222L)).thenReturn(person2);

        var result = observationService.retrieveScopedPersons(scopingUid);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.containsAll(List.of(person1, person2)));
    }


    @Test
    void testRetrieveScopedPersons_Else_SkipsWhenSubjectEntityUidIsNull() {
        Long scopingUid = 123L;
        RoleDto role = new RoleDto();
        role.setSubjectEntityUid(null); // triggers else: skip add

        when(roleService.findRoleScopedToPatient(scopingUid)).thenReturn(List.of(role));

        var result = observationService.retrieveScopedPersons(scopingUid);

        assertNotNull(result); // because scopedPersons never initialized
        verify(patientRepositoryUtil, never()).loadPerson(any());
    }


    @Test
    void testIfCondition_SourceClassCdNull_SkipsBlock() throws Exception {
        ActRelationshipDto dto = actRel(null, "OBS", "ACTIVE", "PROCESS");

        var result = observationService.retrieveObservationFromActRelationship(List.of(dto));

        assertTrue(((Collection<?>) result.get(DataProcessingMapKey.OBSERVATION)).isEmpty());
    }

    @Test
    void testIfCondition_SourceClassCdWrong_SkipsBlock() throws Exception {
        ActRelationshipDto dto = actRel("WRONG", "OBS", "ACTIVE", "PROCESS");

        var result = observationService.retrieveObservationFromActRelationship(List.of(dto));

        assertTrue(((Collection<?>) result.get(DataProcessingMapKey.OBSERVATION)).isEmpty());
    }

    @Test
    void testIfCondition_TargetClassCdNull_SkipsBlock() throws Exception {
        ActRelationshipDto dto = actRel("OBS", null, "ACTIVE", "PROCESS");

        var result = observationService.retrieveObservationFromActRelationship(List.of(dto));

        assertTrue(((Collection<?>) result.get(DataProcessingMapKey.OBSERVATION)).isEmpty());
    }

    @Test
    void testIfCondition_TargetClassCdWrong_SkipsBlock() throws Exception {
        ActRelationshipDto dto = actRel("OBS", "WRONG", "ACTIVE", "PROCESS");

        var result = observationService.retrieveObservationFromActRelationship(List.of(dto));

        assertTrue(((Collection<?>) result.get(DataProcessingMapKey.OBSERVATION)).isEmpty());
    }

    @Test
    void testIfCondition_RecordStatusCdNull_SkipsBlock() throws Exception {
        ActRelationshipDto dto = actRel("OBS", "OBS", null, "PROCESS");

        var result = observationService.retrieveObservationFromActRelationship(List.of(dto));

        assertTrue(((Collection<?>) result.get(DataProcessingMapKey.OBSERVATION)).isEmpty());
    }

    @Test
    void testIfCondition_RecordStatusCdWrong_SkipsBlock() throws Exception {
        ActRelationshipDto dto = actRel("OBS", "OBS", "INACTIVE", "PROCESS");

        var result = observationService.retrieveObservationFromActRelationship(List.of(dto));

        assertTrue(((Collection<?>) result.get(DataProcessingMapKey.OBSERVATION)).isEmpty());
    }

    private ActRelationshipDto actRel(String srcCls, String tgtCls, String recStatus, String typeCd) {
        ActRelationshipDto dto = new ActRelationshipDto();
        dto.setSourceClassCd(srcCls);
        dto.setTargetClassCd(tgtCls);
        dto.setRecordStatusCd(recStatus);
        dto.setTypeCd(typeCd);
        dto.setSourceActUid(111L);
        return dto;
    }


    @Test
    void testResultedTestObservation_ContinueWhenRtObservationIsNull() throws Exception {
        ActRelationshipDto dto = actRel("OBS", "OBS", "ACTIVE", NEDSSConstant.ACT108_TYP_CD);

        ObservationService spyService = Mockito.spy(observationService);
        doReturn(null).when(spyService)
                .getAbstractObjectForObservationOrIntervention(eq("OBS"), anyLong());

        var result = spyService.retrieveObservationFromActRelationship(List.of(dto));

        assertTrue(((Collection<?>) result.get(DataProcessingMapKey.OBSERVATION)).isEmpty());
        assertTrue(((Collection<?>) result.get(DataProcessingMapKey.ORGANIZATION)).isEmpty());
    }

    @Test
    void testResultedTestObservation_ContinueWhenReflexObsEmpty() throws Exception {
        ActRelationshipDto dto = actRel("OBS", "OBS", "ACTIVE", NEDSSConstant.ACT108_TYP_CD);

        ObservationDto obsDto = new ObservationDto();
        obsDto.setAddUserName("testUser"); // if needed

        ObservationContainer rtObs = mock(ObservationContainer.class);
        when(rtObs.getTheObservationDto()).thenReturn(obsDto);
        when(rtObs.getTheParticipationDtoCollection()).thenReturn(Collections.emptyList());
        when(rtObs.getTheActRelationshipDtoCollection()).thenReturn(Collections.emptyList());

        ObservationService spyService = Mockito.spy(observationService);
        doReturn(rtObs).when(spyService)
                .getAbstractObjectForObservationOrIntervention(eq("OBS"), anyLong());

        doReturn(null).when(spyService)
                .retrieveReflexObservationsFromActRelationship(any());

        var result = spyService.retrieveObservationFromActRelationship(List.of(dto));

        assertEquals(1, ((Collection<?>) result.get(DataProcessingMapKey.OBSERVATION)).size()); // only RT
        assertTrue(((Collection<?>) result.get(DataProcessingMapKey.ORGANIZATION)).isEmpty());
    }


    @Test
    void testResultedTestObservation_ContinueWhenReflexObsIsEmpty() throws Exception {
        ActRelationshipDto dto = actRel("OBS", "OBS", "ACTIVE", NEDSSConstant.ACT108_TYP_CD);

        ObservationDto obsDto = new ObservationDto();

        ObservationContainer rtObs = mock(ObservationContainer.class);
        when(rtObs.getTheObservationDto()).thenReturn(obsDto);
        when(rtObs.getTheParticipationDtoCollection()).thenReturn(Collections.emptyList());
        when(rtObs.getTheActRelationshipDtoCollection()).thenReturn(Collections.emptyList());

        ObservationService spyService = Mockito.spy(observationService);
        doReturn(rtObs).when(spyService).getAbstractObjectForObservationOrIntervention(eq("OBS"), anyLong());
        doReturn(Collections.emptyList()).when(spyService).retrieveReflexObservationsFromActRelationship(any());

        var result = spyService.retrieveObservationFromActRelationship(List.of(dto));

        assertEquals(1, ((Collection<?>) result.get(DataProcessingMapKey.OBSERVATION)).size()); // only RT added
    }

    @Test
    void testContinue_WhenActRelDtoIsNull() throws Exception {
        ObservationService spyService = Mockito.spy(observationService);

        Collection<ActRelationshipDto> input = Collections.singletonList(null); // Correct way

        Collection<ObservationContainer> result = spyService.retrieveReflexObservationsFromActRelationship(input);

        assertNull(result); // reflexObsVOCollection is never initialized
    }


    @Test
    void testContinue_WhenReflexObservationIsNull() throws Exception {
        ActRelationshipDto dto = createReflexDto();

        ObservationService spyService = Mockito.spy(observationService);
        doReturn(null).when(spyService)
                .getAbstractObjectForObservationOrIntervention(eq("OBS"), anyLong());

        Collection<ObservationContainer> result = spyService.retrieveReflexObservationsFromActRelationship(List.of(dto));

        assertNull(result); // reflexObsVOCollection never initialized
    }

    @Test
    void testContinue_WhenReflexRTsIsEmpty() throws Exception {
        ActRelationshipDto dto = createReflexDto();

        ObservationDto obsDto = new ObservationDto();
        ObservationContainer reflexObs = mock(ObservationContainer.class);

        when(reflexObs.getTheObservationDto()).thenReturn(obsDto);
        when(reflexObs.getTheActRelationshipDtoCollection()).thenReturn(Collections.emptyList());

        ObservationService spyService = Mockito.spy(observationService);
        doReturn(reflexObs).when(spyService)
                .getAbstractObjectForObservationOrIntervention(eq("OBS"), anyLong());

        // reflex RTs returned as empty
        doReturn(Collections.emptyList()).when(spyService)
                .retrieveReflexRTsAkaObservationFromActRelationship(any());

        Collection<ObservationContainer> result = spyService.retrieveReflexObservationsFromActRelationship(List.of(dto));

        assertNotNull(result); // reflexObsVOCollection was initialized
        assertEquals(1, result.size()); // only reflexObs was added
        assertTrue(result.contains(reflexObs));
    }

    private ActRelationshipDto createReflexDto() {
        ActRelationshipDto dto = new ActRelationshipDto();
        dto.setTypeCd(NEDSSConstant.ACT109_TYP_CD);
        dto.setSourceClassCd(NEDSSConstant.OBSERVATION_CLASS_CODE);
        dto.setTargetClassCd(NEDSSConstant.OBSERVATION_CLASS_CODE);
        dto.setRecordStatusCd(NEDSSConstant.ACTIVE);
        dto.setSourceActUid(123L);
        return dto;
    }


    @ParameterizedTest
    @MethodSource("provideAct109TestCases")
    void testAct109Condition(String typeCd, String srcCls, String tgtCls, String recStatus, boolean shouldMatch) throws Exception {
        ActRelationshipDto dto = new ActRelationshipDto();
        dto.setTypeCd(typeCd);
        dto.setSourceClassCd(srcCls);
        dto.setTargetClassCd(tgtCls);
        dto.setRecordStatusCd(recStatus);
        dto.setSourceActUid(123L);

        ObservationDto obsDto = new ObservationDto();
        ObservationContainer reflexObs = mock(ObservationContainer.class);
        when(reflexObs.getTheObservationDto()).thenReturn(obsDto);
        when(reflexObs.getTheActRelationshipDtoCollection()).thenReturn(Collections.emptyList());

        ObservationService spyService = Mockito.spy(observationService);

        // Only mock getAbstractObjectForObservationOrIntervention if type matches
        if ("ACT109".equalsIgnoreCase(typeCd)) {
            doReturn(reflexObs).when(spyService)
                    .getAbstractObjectForObservationOrIntervention(eq("OBS"), eq(123L));
        }

        // Reflex RTs must return empty to avoid adding more
        doReturn(Collections.emptyList()).when(spyService)
                .retrieveReflexRTsAkaObservationFromActRelationship(any());

        var result = spyService.retrieveReflexObservationsFromActRelationship(List.of(dto));

        if (shouldMatch) {
            assertNotNull(result);
            assertEquals(1, result.size());
        } else {
            assertTrue(result == null || result.isEmpty());
        }
    }

    private static Stream<Arguments> provideAct109TestCases() {
        return Stream.of(
                Arguments.of(null,     "OBS", "OBS", "ACTIVE", false),
                Arguments.of("WRONG",  "OBS", "OBS", "ACTIVE", false),
                Arguments.of("REFR", null,  "OBS", "ACTIVE", false),
                Arguments.of("REFR", "WRONG", "OBS", "ACTIVE", false),
                Arguments.of("REFR", "OBS", null,  "ACTIVE", false),
                Arguments.of("REFR", "OBS", "WRONG", "ACTIVE", false),
                Arguments.of("REFR", "OBS", "OBS", null, false),
                Arguments.of("REFR", "OBS", "OBS", "INACTIVE", false)
        );
    }
    @Test
    void testContinue_WhenActRelDtoIsNull2() throws Exception {
        ObservationService spyService = Mockito.spy(observationService);

        Collection<ActRelationshipDto> input = Collections.singletonList(null);

        var result = spyService.retrieveReflexRTsAkaObservationFromActRelationship(input);

        assertNull(result); // never initialized
    }


    @Test
    void testContinue_WhenReflexObsIsNull() throws Exception {
        ActRelationshipDto dto = new ActRelationshipDto();
        dto.setTypeCd(NEDSSConstant.ACT110_TYP_CD);
        dto.setSourceClassCd(NEDSSConstant.OBSERVATION_CLASS_CODE);
        dto.setTargetClassCd(NEDSSConstant.OBSERVATION_CLASS_CODE);
        dto.setRecordStatusCd(NEDSSConstant.ACTIVE);
        dto.setSourceActUid(123L);

        ObservationService spyService = Mockito.spy(observationService);
        doReturn(null).when(spyService).getAbstractObjectForObservationOrIntervention("OBS", 123L);

        var result = spyService.retrieveReflexRTsAkaObservationFromActRelationship(List.of(dto));

        assertNull(result); // never initialized
    }

    @ParameterizedTest
    @MethodSource("provideElseCaseInputs")
    void testRetrieveReflexRTsAkaObservationFromActRelationship_ElseCases(
            String typeCd,
            String sourceClassCd,
            String targetClassCd,
            String recordStatusCd
    ) throws Exception {
        ActRelationshipDto dto = new ActRelationshipDto();
        dto.setTypeCd(typeCd);
        dto.setSourceClassCd(sourceClassCd);
        dto.setTargetClassCd(targetClassCd);
        dto.setRecordStatusCd(recordStatusCd);
        dto.setSourceActUid(123L);

        ObservationService spyService = Mockito.spy(observationService);
        doReturn(mock(ObservationContainer.class)).when(spyService)
                .getAbstractObjectForObservationOrIntervention(anyString(), anyLong());

        var result = spyService.retrieveReflexRTsAkaObservationFromActRelationship(List.of(dto));

        assertTrue(result == null || result.isEmpty(), "Should skip block (else case)");
    }
    private static Stream<Arguments> provideElseCaseInputs() {
        return Stream.of(
                Arguments.of(null,     "OBS",  "OBS",  "ACTIVE"),   // E1
                Arguments.of("WRONG",  "OBS",  "OBS",  "ACTIVE"),   // E2
                Arguments.of("COMP", null,   "OBS",  "ACTIVE"),   // E3
                Arguments.of("COMP", "WRONG","OBS",  "ACTIVE"),   // E4
                Arguments.of("COMP", "OBS",  null,   "ACTIVE"),   // E5
                Arguments.of("COMP", "OBS",  "WRONG","ACTIVE"),   // E6
                Arguments.of("COMP", "OBS",  "OBS",  null),       // E7
                Arguments.of("COMP", "OBS",  "OBS",  "INACTIVE")  // E8
        );
    }

    @Test
    void testContinueWhenPartDtoIsNull() {
        ObservationService spyService = Mockito.spy(observationService);
        Collection<ParticipationDto> input = Collections.singletonList(null);

        OrganizationContainer result = spyService.retrievePerformingLabAkaOrganizationFromParticipation(input);

        assertNull(result);
    }


    @ParameterizedTest
    @MethodSource("provideElseCasesForLab")
    void testElseCasesForPerformingLab(
            String typeCd,
            String subjectClassCd,
            String actClassCd,
            String recordStatusCd
    ) {
        ParticipationDto dto = new ParticipationDto();
        dto.setTypeCd(typeCd);
        dto.setSubjectClassCd(subjectClassCd);
        dto.setActClassCd(actClassCd);
        dto.setRecordStatusCd(recordStatusCd);
        dto.setSubjectEntityUid(1L);
        dto.setActUid(2L);

        ObservationService spyService = Mockito.spy(observationService);

        OrganizationContainer result = spyService.retrievePerformingLabAkaOrganizationFromParticipation(List.of(dto));

        assertNull(result); // None should trigger loading lab
    }

    private static Stream<Arguments> provideElseCasesForLab() {
        return Stream.of(
                Arguments.of(null,     "ORG",  "OBS", "ACTIVE"),    // typeCd null
                Arguments.of("WRONG",  "ORG",  "OBS", "ACTIVE"),    // typeCd wrong
                Arguments.of("PRF",    null,   "OBS", "ACTIVE"),    // subjectClassCd null
                Arguments.of("PRF",    "WRONG","OBS", "ACTIVE"),    // subjectClassCd wrong
                Arguments.of("PRF",    "ORG",  null,  "ACTIVE"),    // actClassCd null
                Arguments.of("PRF",    "ORG",  "WRONG","ACTIVE"),   // actClassCd wrong
                Arguments.of("PRF",    "ORG",  "OBS", null),        // recordStatusCd null
                Arguments.of("PRF",    "ORG",  "OBS", "INACTIVE")   // recordStatusCd wrong
        );
    }

    @Test
    void testContinueWhenActRelDtoIsNull() throws Exception {
        ObservationService spyService = Mockito.spy(observationService);
        Collection<ActRelationshipDto> input = Collections.singletonList(null);

        Collection<Object> result = spyService.retrieveInterventionFromActRelationship(input);

        assertNull(result); // collection should never initialize
    }

    @ParameterizedTest
    @MethodSource("provideElseCasesForIntervention")
    void testElseCasesForRetrieveIntervention(
            String sourceClassCd,
            String targetClassCd,
            String recordStatusCd
    ) throws Exception {
        ActRelationshipDto dto = new ActRelationshipDto();
        dto.setSourceClassCd(sourceClassCd);
        dto.setTargetClassCd(targetClassCd);
        dto.setRecordStatusCd(recordStatusCd);
        dto.setSourceActUid(999L);

        ObservationService spyService = Mockito.spy(observationService);

        var result = spyService.retrieveInterventionFromActRelationship(List.of(dto));

        assertNull(result); // should not trigger intervention retrieval
    }

    private static Stream<Arguments> provideElseCasesForIntervention() {
        return Stream.of(
                Arguments.of(null,     "OBS", "ACTIVE"),     // sourceClassCd null
                Arguments.of("WRONG",  "OBS", "ACTIVE"),     // sourceClassCd wrong
                Arguments.of("INTV",   null,  "ACTIVE"),     // targetClassCd null
                Arguments.of("INTV",   "WRONG", "ACTIVE"),   // targetClassCd wrong
                Arguments.of("INTV",   "OBS", null),         // recordStatusCd null
                Arguments.of("INTV",   "OBS", "INACTIVE")    // recordStatusCd wrong
        );
    }

    @Test
    void testProcessingNotELRLab_ElseCasesForAllConditions() throws Exception {
        // Arrange
        LabResultProxyContainer lrProxyVO = new LabResultProxyContainer();
        ObservationContainer orderedTest = new ObservationContainer();
        long obsId = 123L;

        // ensure we enter the main block
        boolean isELR = false;

        // Mock checkForExistingNotification to return false (doesn't matter here)
        when(notificationService.checkForExistingNotification(lrProxyVO)).thenReturn(false);

        // Force else cases:
        when(actRelationshipService.loadActRelationshipBySrcIdAndTypeCode(obsId, NEDSSConstant.LAB_REPORT))
                .thenReturn(Collections.emptyList()); // triggers first else

        when(edxDocumentService.selectEdxDocumentCollectionByActUid(obsId))
                .thenReturn(null); // triggers second else

        when(observationCodeService.deriveTheConditionCodeList(lrProxyVO, orderedTest))
                .thenReturn(new ArrayList<>()); // triggers third else

        // Act
        observationService.processingNotELRLab(isELR, lrProxyVO, orderedTest, obsId);

        // Assert - all should remain at default/null/false
        assertFalse(lrProxyVO.isAssociatedInvInd());               // not set to true
        assertNull(lrProxyVO.getEDXDocumentCollection());          // not set
        assertNull(lrProxyVO.getTheConditionsList());              // not set
    }


}
