package gov.cdc.dataprocessing.service.implementation.observation;

import gov.cdc.dataprocessing.constant.elr.NBSBOLookup;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.constant.enums.DataProcessingMapKey;
import gov.cdc.dataprocessing.exception.DataProcessingException;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    void testRetrieveOrganizationFromParticipation_Continue() throws DataProcessingException {
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
}
