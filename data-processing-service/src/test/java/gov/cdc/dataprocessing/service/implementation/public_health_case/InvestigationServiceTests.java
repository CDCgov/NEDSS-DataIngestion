package gov.cdc.dataprocessing.service.implementation.public_health_case;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import gov.cdc.dataprocessing.constant.elr.NBSBOLookup;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.base.BasePamContainer;
import gov.cdc.dataprocessing.model.container.model.*;
import gov.cdc.dataprocessing.model.dto.act.ActRelationshipDto;
import gov.cdc.dataprocessing.model.dto.notification.NotificationDto;
import gov.cdc.dataprocessing.model.dto.observation.ObservationDto;
import gov.cdc.dataprocessing.model.dto.organization.OrganizationNameDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.model.dto.phc.*;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.repository.nbs.odse.model.observation.Observation;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.edx.EdxDocumentRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.observation.ObservationRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.observation.Observation_SummaryRepository;
import gov.cdc.dataprocessing.repository.nbs.srte.model.LabTest;
import gov.cdc.dataprocessing.repository.nbs.srte.repository.LabTestRepository;
import gov.cdc.dataprocessing.service.implementation.act.ActRelationshipService;
import gov.cdc.dataprocessing.service.implementation.cache.CachingValueDpDpService;
import gov.cdc.dataprocessing.service.interfaces.cache.ICacheApiService;
import gov.cdc.dataprocessing.service.interfaces.material.IMaterialService;
import gov.cdc.dataprocessing.service.interfaces.notification.INotificationService;
import gov.cdc.dataprocessing.service.interfaces.observation.IObservationSummaryService;
import gov.cdc.dataprocessing.service.interfaces.public_health_case.IContactSummaryService;
import gov.cdc.dataprocessing.service.interfaces.public_health_case.ILdfService;
import gov.cdc.dataprocessing.service.interfaces.public_health_case.IRetrieveSummaryService;
import gov.cdc.dataprocessing.service.model.auth_user.AuthUserProfileInfo;
import gov.cdc.dataprocessing.test_data.TestDataReader;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.generic_helper.PrepareAssocModelHelper;
import gov.cdc.dataprocessing.utilities.component.observation.ObservationRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.organization.OrganizationRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.patient.PatientRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.public_health_case.PublicHealthCaseRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.sql.QueryHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static gov.cdc.dataprocessing.test_data.TestDataReader.gsonForTest;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class InvestigationServiceTests {
    @Mock
    private EdxDocumentRepository edxDocumentRepository;
    @Mock
    private PublicHealthCaseRepositoryUtil publicHealthCaseRepositoryUtil;
    @Mock
    private OrganizationRepositoryUtil organizationRepositoryUtil;
    @Mock
    private PatientRepositoryUtil patientRepositoryUtil;
    @Mock
    private IMaterialService materialService;
    @Mock
    private PrepareAssocModelHelper prepareAssocModelHelper;
    @Mock
    private ObservationRepositoryUtil observationRepositoryUtil;
    @Mock
    private ObservationRepository observationRepository;
    @Mock
    private IRetrieveSummaryService retrieveSummaryService;
    @Mock
    private ActRelationshipService actRelationshipService;
    @Mock
    private INotificationService notificationService;
    @Mock
    private IObservationSummaryService observationSummaryService;
    @Mock
    private QueryHelper queryHelper;
    @Mock
    private Observation_SummaryRepository observationSummaryRepository;
    @Mock
    private IContactSummaryService contactSummaryService;
    @Mock
    private CachingValueDpDpService cachingValueDpService;
    @Mock
    private ILdfService ldfService;
    @Mock
    private LabTestRepository labTestRepository;
    @InjectMocks
    private InvestigationService investigationService;
    @Mock
    private ICacheApiService cacheApiService;
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
        Mockito.reset(edxDocumentRepository, authUtil);
    }

    @Test
    void setAssociations_Success() throws DataProcessingException {
        String reportSumVOCollectionStr = "[{\"isTouched\":true,\"isAssociated\":true,\"observationUid\":10006210,\"providerFirstName\":\"\",\"providerLastName\":\"\",\"providerSuffix\":\"\",\"providerPrefix\":\"\",\"providerDegree\":\"\",\"providerUid\":\"\",\"isLabFromMorb\":false,\"isReactor\":false,\"disabled\":\"\",\"isLabFromDoc\":false,\"itNew\":false,\"itOld\":false,\"itDirty\":false,\"itDelete\":false}]";
        String phcDTStr = "{\"caseStatusDirty\":false,\"isPamCase\":false,\"isPageCase\":false,\"isStdHivProgramAreaCode\":false,\"caseTypeCd\":\"I\",\"publicHealthCaseUid\":10006070,\"activityFromTime\":\"Jun 20, 2024, 12:00:00 AM\",\"addTime\":\"Jun 20, 2024, 12:36:18 PM\",\"addUserId\":36,\"cd\":\"11120\",\"cdDescTxt\":\"Acute flaccid myelitis\",\"groupCaseCnt\":1,\"investigationStatusCd\":\"O\",\"jurisdictionCd\":\"130001\",\"lastChgTime\":\"Jun 20, 2024, 12:36:18 PM\",\"lastChgUserId\":36,\"localId\":\"CAS10006070GA01\",\"mmwrWeek\":\"25\",\"mmwrYear\":\"2024\",\"progAreaCd\":\"GCD\",\"recordStatusCd\":\"OPEN\",\"recordStatusTime\":\"Jun 20, 2024, 12:36:18 PM\",\"rptFormCmpltTime\":\"Jun 20, 2024, 12:36:11 PM\",\"statusCd\":\"A\",\"programJurisdictionOid\":1300100009,\"sharedInd\":\"T\",\"versionCtrlNbr\":1,\"isSummaryCase\":false,\"itNew\":false,\"itOld\":false,\"itDirty\":false,\"itDelete\":false}";


        Gson gson = gsonForTest();
        TestDataReader test = new TestDataReader();
        ObservationContainer labProxyContainer = test.readDataFromJsonPath("phc/phc_investigation_obs.json", ObservationContainer.class);

        var phcDt = gson.fromJson(phcDTStr, PublicHealthCaseDto.class);
        Type listOfPersonsType = new TypeToken<Collection<LabReportSummaryContainer>>(){}.getType();
        Collection<LabReportSummaryContainer> reportSumVOCollection = gson.fromJson(reportSumVOCollectionStr, listOfPersonsType);

        Long investigationUid = 10L;

        when(publicHealthCaseRepositoryUtil.findPublicHealthCase(investigationUid)).thenReturn(phcDt);

        var actRelationshipDT = new ActRelationshipDto();
        actRelationshipDT.setTargetActUid(investigationUid);
        actRelationshipDT.setSourceActUid(10006210L);
        actRelationshipDT.setLastChgUserId(AuthUtil.authUser.getAuthUserUid());
        actRelationshipDT.setSourceClassCd(NEDSSConstant.OBSERVATION_CLASS_CODE);
        actRelationshipDT.setTargetClassCd(NEDSSConstant.PUBLIC_HEALTH_CASE_CLASS_CODE);
        actRelationshipDT.setTypeCd(NEDSSConstant.LAB_DISPALY_FORM);
        when(prepareAssocModelHelper.prepareAssocDTForActRelationship(any())).thenReturn(actRelationshipDT);

        when(observationRepositoryUtil.loadObject(10006210L)).thenReturn(labProxyContainer);

        when(prepareAssocModelHelper.prepareVO(any(), eq("OBSERVATIONLABREPORT"),
                eq("OBS_LAB_ASC"), eq("OBSERVATION"),
                eq("BASE"), eq(1))).thenReturn(labProxyContainer.getTheObservationDto());

        var phcConn = new PublicHealthCaseContainer();
        phcDt.setPatientGroupId(11L);
        phcConn.setThePublicHealthCaseDto(phcDt);
        var patCol = new ArrayList<ParticipationDto>();
        var patDt = new ParticipationDto();
        patDt.setSubjectEntityUid(12L);
        patDt.setSubjectClassCd(NEDSSConstant.PLACE);
        patDt.setTypeCd("TYPE");
        patDt.setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);
        patCol.add(patDt);
        patDt = new ParticipationDto();
        patDt.setSubjectEntityUid(13L);
        patDt.setSubjectClassCd(NEDSSConstant.NONPERSONLIVINGSUBJECT);
        patDt.setTypeCd("TYPE");
        patDt.setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);
        patCol.add(patDt);
        patDt = new ParticipationDto();
        patDt.setSubjectEntityUid(14L);
        patDt.setSubjectClassCd(NEDSSConstant.ORGANIZATION);
        patDt.setTypeCd("TYPE");
        patDt.setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);
        patCol.add(patDt);
        patDt = new ParticipationDto();
        patDt.setSubjectEntityUid(15L);
        patDt.setSubjectClassCd(NEDSSConstant.PERSON);
        patDt.setTypeCd("TYPE");
        patDt.setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);
        patCol.add(patDt);
        patDt = new ParticipationDto();
        patDt.setSubjectEntityUid(16L);
        patDt.setSubjectClassCd(NEDSSConstant.MATERIAL);
        patDt.setTypeCd("TYPE");
        patDt.setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);
        patCol.add(patDt);
        patDt = new ParticipationDto();
        patDt.setSubjectEntityUid(17L);
        patDt.setSubjectClassCd(NEDSSConstant.ENTITYGROUP);
        patDt.setTypeCd("TYPE");
        patDt.setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);
        patCol.add(patDt);
        phcConn.setTheParticipationDTCollection(patCol);

        var actReCol = new ArrayList<ActRelationshipDto>();
        var act = new ActRelationshipDto();
        act.setSourceActUid(18L);
        act.setSourceClassCd(NEDSSConstant.CLINICAL_DOCUMENT_CLASS_CODE);
        act.setTypeCd("TYPE");
        act.setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);
        actReCol.add(act);
        act = new ActRelationshipDto();
        act.setSourceActUid(19L);
        act.setSourceClassCd(NEDSSConstant.REFERRAL_CLASS_CODE);
        act.setTypeCd("TYPE");
        act.setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);
        actReCol.add(act);
        act = new ActRelationshipDto();
        act.setSourceActUid(20L);
        act.setSourceClassCd(NEDSSConstant.PATIENT_ENCOUNTER_CLASS_CODE);
        act.setTypeCd("TYPE");
        act.setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);
        actReCol.add(act);
        act = new ActRelationshipDto();
        act.setSourceActUid(21L);
        act.setSourceClassCd(NEDSSConstant.OBSERVATION_CLASS_CODE);
        act.setTypeCd(NEDSSConstant.PHC_INV_FORM);
        act.setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);
        actReCol.add(act);
        phcConn.setTheActRelationshipDTCollection(actReCol);

        when(publicHealthCaseRepositoryUtil.loadObject(10L)).thenReturn(phcConn);
        var entityGroupDt = new EntityGroupDto();
        when(publicHealthCaseRepositoryUtil.getEntityGroup(11L)).thenReturn(entityGroupDt);
        when(publicHealthCaseRepositoryUtil.getPlace(12L)).thenReturn(new PlaceDto());
        when(publicHealthCaseRepositoryUtil.getNonPersonLivingSubject(13L)).thenReturn(new NonPersonLivingSubjectDto());
        when(organizationRepositoryUtil.loadObject(14L, null)).thenReturn(new OrganizationContainer());
        when(patientRepositoryUtil.loadPerson(15L)).thenReturn(new PersonContainer());
        when(materialService.loadMaterialObject(16L)).thenReturn(new MaterialContainer());
        when(publicHealthCaseRepositoryUtil.getEntityGroup(17L)).thenReturn(new EntityGroupDto());
        when(publicHealthCaseRepositoryUtil.getClinicalDocument(18L)).thenReturn(new ClinicalDocumentDto());
        when(publicHealthCaseRepositoryUtil.getReferral(19L)).thenReturn(new ReferralDto());
        when(publicHealthCaseRepositoryUtil.getPatientEncounter(20L)).thenReturn(new PatientEncounterDto());
        when(observationRepositoryUtil.loadObject(21L)).thenReturn(new ObservationContainer());
        when(observationRepositoryUtil.retrieveObservationQuestion(21L)).thenReturn(new ArrayList<>());
        when(ldfService.getLDFCollection(10L, null)).thenReturn(new ArrayList<>());
        when(queryHelper.getDataAccessWhereClause(NBSBOLookup.OBSERVATIONLABREPORT, "VIEW", "obs")).thenReturn("BLAH");
        when(queryHelper.getDataAccessWhereClause(NBSBOLookup.OBSERVATIONLABREPORT, "VIEW", "")).thenReturn("BLAH");

        // LABORATORY_UID
        var labReportUidSummarVOs = new ArrayList<UidSummaryContainer>();
        var labReportUid = new UidSummaryContainer();
        labReportUid.setUid(22L);
        labReportUidSummarVOs.add(labReportUid);
        when(observationSummaryService.findAllActiveLabReportUidListForManage(10L, " AND BLAH")).thenReturn(labReportUidSummarVOs);

        var observation = new Observation();
        observation.setObservationUid(23L);
        observation.setRecordStatusCd("UNPROCESSED");
        when(observationRepository.findById(22L)).thenReturn(Optional.of(observation));
        var uidMap = new HashMap<Object, Object>();
        uidMap.put(NEDSSConstant.PAR110_TYP_CD, 24L);

        when(observationSummaryService.getLabParticipations(23L)).thenReturn(uidMap);
        when(observationSummaryService.getProviderInfo(23L, "ORD")).thenReturn(new ArrayList<>());
        when(observationSummaryService.getActIdDetails(23L)).thenReturn(new ArrayList<>());
        when(observationSummaryService.getAssociatedInvList(23L, "OBS")).thenReturn(new HashMap<>());
        uidMap.put(NEDSSConstant.PAR111_TYP_CD, 25L);
        uidMap.put(NEDSSConstant.PAR101_TYP_CD, 25L);
        uidMap.put(NEDSSConstant.PAR104_TYP_CD, 26L);

        when(observationSummaryService.getReportingFacilityName(25L)).thenReturn("25");
        when(observationSummaryService.getSpecimanSource(26L)).thenReturn("26");
        var treemap = new HashMap<String, String>();
        treemap.put("26", "BLAH");
   //     when(cachingValueService.getCodedValues("SPECMN_SRC", "26")).thenReturn(treemap);

        when(observationSummaryService.getProviderInformation(any(), any())).thenReturn(27L);


        investigationService.setAssociations(investigationUid, reportSumVOCollection,
                null,
                null,
                null,
                true);

        verify(publicHealthCaseRepositoryUtil, times(1)).findPublicHealthCase(investigationUid);
        verify(observationRepositoryUtil, times(1)).loadObject(10006210L);
        verify(retrieveSummaryService, times(1)).checkBeforeCreateAndStoreMessageLogDTCollection(any(), any());

    }

    @Test
    void setObservationAssociationsImpl_Test_Success() throws DataProcessingException {
        String reportSumVOCollectionStr = "[{\"isTouched\":true,\"isAssociated\":true,\"observationUid\":10006210,\"providerFirstName\":\"\",\"providerLastName\":\"\",\"providerSuffix\":\"\",\"providerPrefix\":\"\",\"providerDegree\":\"\",\"providerUid\":\"\",\"isLabFromMorb\":false,\"isReactor\":false,\"disabled\":\"\",\"isLabFromDoc\":false,\"itNew\":false,\"itOld\":false,\"itDirty\":false,\"itDelete\":false}]";
        String phcDTStr = "{\"caseStatusDirty\":false,\"isPamCase\":false,\"isPageCase\":false,\"isStdHivProgramAreaCode\":false,\"caseTypeCd\":\"I\",\"publicHealthCaseUid\":10006070,\"activityFromTime\":\"Jun 20, 2024, 12:00:00 AM\",\"addTime\":\"Jun 20, 2024, 12:36:18 PM\",\"addUserId\":36,\"cd\":\"11120\",\"cdDescTxt\":\"Acute flaccid myelitis\",\"groupCaseCnt\":1,\"investigationStatusCd\":\"O\",\"jurisdictionCd\":\"130001\",\"lastChgTime\":\"Jun 20, 2024, 12:36:18 PM\",\"lastChgUserId\":36,\"localId\":\"CAS10006070GA01\",\"mmwrWeek\":\"25\",\"mmwrYear\":\"2024\",\"progAreaCd\":\"GCD\",\"recordStatusCd\":\"OPEN\",\"recordStatusTime\":\"Jun 20, 2024, 12:36:18 PM\",\"rptFormCmpltTime\":\"Jun 20, 2024, 12:36:11 PM\",\"statusCd\":\"A\",\"programJurisdictionOid\":1300100009,\"sharedInd\":\"T\",\"versionCtrlNbr\":1,\"isSummaryCase\":false,\"itNew\":false,\"itOld\":false,\"itDirty\":false,\"itDelete\":false}";


        Gson gson = gsonForTest();
        TestDataReader test = new TestDataReader();
        ObservationContainer labProxyContainer = test.readDataFromJsonPath("phc/phc_investigation_obs.json", ObservationContainer.class);

        var phcDt = gson.fromJson(phcDTStr, PublicHealthCaseDto.class);
        Type listOfPersonsType = new TypeToken<Collection<LabReportSummaryContainer>>(){}.getType();
        Collection<LabReportSummaryContainer> reportSumVOCollection = gson.fromJson(reportSumVOCollectionStr, listOfPersonsType);

        Long investigationUid = 10L;

        when(publicHealthCaseRepositoryUtil.findPublicHealthCase(investigationUid)).thenReturn(phcDt);

        var actRelationshipDT = new ActRelationshipDto();
        actRelationshipDT.setTargetActUid(investigationUid);
        actRelationshipDT.setSourceActUid(10006210L);
        actRelationshipDT.setLastChgUserId(AuthUtil.authUser.getAuthUserUid());
        actRelationshipDT.setSourceClassCd(NEDSSConstant.OBSERVATION_CLASS_CODE);
        actRelationshipDT.setTargetClassCd(NEDSSConstant.PUBLIC_HEALTH_CASE_CLASS_CODE);
        actRelationshipDT.setTypeCd(NEDSSConstant.LAB_DISPALY_FORM);
        when(prepareAssocModelHelper.prepareAssocDTForActRelationship(any())).thenReturn(actRelationshipDT);

        when(observationRepositoryUtil.loadObject(10006210L)).thenReturn(labProxyContainer);

        when(prepareAssocModelHelper.prepareVO(any(), eq("OBSERVATIONLABREPORT"),
                eq("OBS_LAB_ASC"), eq("OBSERVATION"),
                eq("BASE"), eq(1))).thenReturn(labProxyContainer.getTheObservationDto());


        investigationService.setObservationAssociationsImpl(investigationUid, reportSumVOCollection, true);

        verify(publicHealthCaseRepositoryUtil, times(1)).findPublicHealthCase(investigationUid);
        verify(observationRepositoryUtil, times(1)).loadObject(10006210L);
    }

    @Test
    void setObservationAssociationsImpl_Test_Success_NotAssociated() throws DataProcessingException {
        String reportSumVOCollectionStr = "[{\"isTouched\":true,\"isAssociated\":true,\"observationUid\":10006210,\"providerFirstName\":\"\",\"providerLastName\":\"\",\"providerSuffix\":\"\",\"providerPrefix\":\"\",\"providerDegree\":\"\",\"providerUid\":\"\",\"isLabFromMorb\":false,\"isReactor\":false,\"disabled\":\"\",\"isLabFromDoc\":false,\"itNew\":false,\"itOld\":false,\"itDirty\":false,\"itDelete\":false}]";
        String phcDTStr = "{\"caseStatusDirty\":false,\"isPamCase\":false,\"isPageCase\":false,\"isStdHivProgramAreaCode\":false,\"caseTypeCd\":\"I\",\"publicHealthCaseUid\":10006070,\"activityFromTime\":\"Jun 20, 2024, 12:00:00 AM\",\"addTime\":\"Jun 20, 2024, 12:36:18 PM\",\"addUserId\":36,\"cd\":\"11120\",\"cdDescTxt\":\"Acute flaccid myelitis\",\"groupCaseCnt\":1,\"investigationStatusCd\":\"O\",\"jurisdictionCd\":\"130001\",\"lastChgTime\":\"Jun 20, 2024, 12:36:18 PM\",\"lastChgUserId\":36,\"localId\":\"CAS10006070GA01\",\"mmwrWeek\":\"25\",\"mmwrYear\":\"2024\",\"progAreaCd\":\"GCD\",\"recordStatusCd\":\"OPEN\",\"recordStatusTime\":\"Jun 20, 2024, 12:36:18 PM\",\"rptFormCmpltTime\":\"Jun 20, 2024, 12:36:11 PM\",\"statusCd\":\"A\",\"programJurisdictionOid\":1300100009,\"sharedInd\":\"T\",\"versionCtrlNbr\":1,\"isSummaryCase\":false,\"itNew\":false,\"itOld\":false,\"itDirty\":false,\"itDelete\":false}";


        Gson gson = gsonForTest();

        var phcDt = gson.fromJson(phcDTStr, PublicHealthCaseDto.class);
        Type listOfPersonsType = new TypeToken<List<LabReportSummaryContainer>>(){}.getType();
        List<LabReportSummaryContainer> reportSumVOCollection = gson.fromJson(reportSumVOCollectionStr, listOfPersonsType);
        reportSumVOCollection.get(0).setLabFromDoc(true);
        reportSumVOCollection.get(0).setAssociated(false);
        Long investigationUid = 10L;

        when(publicHealthCaseRepositoryUtil.findPublicHealthCase(investigationUid)).thenReturn(phcDt);

        var actRelationshipDT = new ActRelationshipDto();
        actRelationshipDT.setTargetActUid(investigationUid);
        actRelationshipDT.setSourceActUid(10006210L);
        actRelationshipDT.setLastChgUserId(AuthUtil.authUser.getAuthUserUid());
        actRelationshipDT.setSourceClassCd(NEDSSConstant.OBSERVATION_CLASS_CODE);
        actRelationshipDT.setTargetClassCd(NEDSSConstant.PUBLIC_HEALTH_CASE_CLASS_CODE);
        actRelationshipDT.setTypeCd(NEDSSConstant.LAB_DISPALY_FORM);
        when(prepareAssocModelHelper.prepareAssocDTForActRelationship(any())).thenReturn(actRelationshipDT);


        investigationService.setObservationAssociationsImpl(investigationUid, reportSumVOCollection, true);

        verify(publicHealthCaseRepositoryUtil, times(1)).findPublicHealthCase(investigationUid);
        verify(observationRepositoryUtil, times(0)).loadObject(10006210L);
    }


    @Test
    void getPageProxyVO_Success() throws DataProcessingException, ParseException {
        String typeCd= "PRINT_CDC_CASE";
        long publicHealthCaseUid = 10006070L;
        String phcDTStr = "{\"caseStatusDirty\":false,\"isPamCase\":false,\"isPageCase\":false,\"isStdHivProgramAreaCode\":false,\"caseTypeCd\":\"I\",\"publicHealthCaseUid\":10006070,\"activityFromTime\":\"Jun 20, 2024, 12:00:00 AM\",\"addTime\":\"Jun 20, 2024, 12:36:18 PM\",\"addUserId\":36,\"cd\":\"11120\",\"cdDescTxt\":\"Acute flaccid myelitis\",\"groupCaseCnt\":1,\"investigationStatusCd\":\"O\",\"jurisdictionCd\":\"130001\",\"lastChgTime\":\"Jun 20, 2024, 12:36:18 PM\",\"lastChgUserId\":36,\"localId\":\"CAS10006070GA01\",\"mmwrWeek\":\"25\",\"mmwrYear\":\"2024\",\"progAreaCd\":\"GCD\",\"recordStatusCd\":\"OPEN\",\"recordStatusTime\":\"Jun 20, 2024, 12:36:18 PM\",\"rptFormCmpltTime\":\"Jun 20, 2024, 12:36:11 PM\",\"statusCd\":\"A\",\"programJurisdictionOid\":1300100009,\"sharedInd\":\"T\",\"versionCtrlNbr\":1,\"isSummaryCase\":false,\"itNew\":false,\"itOld\":false,\"itDirty\":false,\"itDelete\":false}";
        Gson gson = gsonForTest();
        var phcDt = gson.fromJson(phcDTStr, PublicHealthCaseDto.class);

        PublicHealthCaseContainer phcConn = new PublicHealthCaseContainer();
        phcConn.setThePublicHealthCaseDto(phcDt);
        var patCol = new ArrayList<ParticipationDto>();
        var pat = new ParticipationDto();
        pat.setSubjectEntityUid(11L);
        pat.setSubjectClassCd(NEDSSConstant.ORGANIZATION);
        pat.setTypeCd("TYPE");
        pat.setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);
        patCol.add(pat);
        pat = new ParticipationDto();
        pat.setSubjectEntityUid(12L);
        pat.setSubjectClassCd(NEDSSConstant.PERSON);
        pat.setTypeCd("TYPE");
        pat.setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);
        patCol.add(pat);
        pat = new ParticipationDto();
        pat.setSubjectEntityUid(13L);
        pat.setSubjectClassCd(NEDSSConstant.MATERIAL);
        pat.setTypeCd("TYPE");
        pat.setRecordStatusCd(NEDSSConstant.RECORD_STATUS_ACTIVE);
        patCol.add(pat);
        phcConn.setTheParticipationDTCollection(patCol);
        when(publicHealthCaseRepositoryUtil.loadObject(10006070L)).thenReturn(phcConn);

        var pageVO = new BasePamContainer();
        when(publicHealthCaseRepositoryUtil.getPamVO(10006070L)).thenReturn(pageVO);

        when(organizationRepositoryUtil.loadObject(11L, null)).thenReturn(new OrganizationContainer());
        when(patientRepositoryUtil.loadPerson(12L)).thenReturn(new PersonContainer());
        when(materialService.loadMaterialObject(13L)).thenReturn(new MaterialContainer());


        var notiSumCol = new ArrayList<>();
        var noti = new NotificationSummaryContainer();
        noti.setCdNotif(NEDSSConstant.CLASS_CD_SHARE_NOTF);
        noti.setNotificationUid(14L);
        notiSumCol.add(noti);
        noti = new NotificationSummaryContainer();
        noti.setCdNotif(NEDSSConstant.CLASS_CD_EXP_NOTF);
        noti.setNotificationUid(15L);
        notiSumCol.add(noti);
        noti = new NotificationSummaryContainer();
        noti.setCdNotif(NEDSSConstant.CLASS_CD_NOTF);
        noti.setNotificationUid(16L);
        notiSumCol.add(noti);

        var actCol = new ArrayList<ActRelationshipDto>();
        var act = new ActRelationshipDto();
        act.setSourceActUid(14L);
        act.setSourceClassCd("SRC");
        actCol.add(act);
        act = new ActRelationshipDto();
        act.setSourceActUid(15L);
        act.setSourceClassCd("SRC");
        actCol.add(act);
        act = new ActRelationshipDto();
        act.setSourceActUid(16L);
        act.setSourceClassCd("SRC");
        actCol.add(act);
        phcConn.setTheActRelationshipDTCollection(actCol);



        when(retrieveSummaryService.notificationSummaryOnInvestigation(any(), any())).thenReturn(notiSumCol);

        when(queryHelper
                .getDataAccessWhereClause( NBSBOLookup.OBSERVATIONLABREPORT, "VIEW", "obs"))
                .thenReturn("BLAH");

        var uidSumCol = new ArrayList<UidSummaryContainer>();
        var uidSum = new UidSummaryContainer();
        uidSum.setUid(17L);
        String dateString = "2024-06-20 12:36:18.0";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        Date parsedDate = dateFormat.parse(dateString);

        uidSum.setAddTime(new Timestamp(parsedDate.getTime()));
        uidSumCol.add(uidSum);
        when(observationSummaryService
                .findAllActiveLabReportUidListForManage(10006070L, " AND BLAH"))
                .thenReturn(uidSumCol);

        // getObservationSummaryListForWorkupRevisited
        // isCDCFormPrintCase = true, uidType = LABORATORY_UID
        var obs = new Observation();
        obs.setObservationUid(18L);
        obs.setRecordStatusCd("UNPROCESSED");
        when(observationRepository.findById(17L)).thenReturn(Optional.of(obs));
        var map = new HashMap<>();
        map.put(NEDSSConstant.PAR110_TYP_CD, 19L);
        map.put(NEDSSConstant.PAR101_TYP_CD, 20L);

        when(observationSummaryService.getLabParticipations(18L)).thenReturn(map);
        var orgConn = new OrganizationContainer();
        var orgNameCol = new ArrayList<OrganizationNameDto>();
        var orgName = new OrganizationNameDto("UTC");
        orgName.setNmTxt("TEST");
        orgNameCol.add(orgName);
        orgConn.setTheOrganizationNameDtoCollection(orgNameCol);
        when(organizationRepositoryUtil.loadObject(20L, null)).thenReturn(orgConn);

        var actIdCol = new ArrayList<>();
        actIdCol.add("19");
        when(observationSummaryService.getActIdDetails(18L)).thenReturn(actIdCol);

        var result = investigationService.getPageProxyVO(typeCd, publicHealthCaseUid);


        assertNotNull(result);
        verify(observationSummaryService, times(1)).getActIdDetails(18L);
        verify(organizationRepositoryUtil, times(1)).loadObject(20L, null);
        verify(observationSummaryService, times(1)).getLabParticipations(18L);


    }

    @Test
    void processingNonAssociatedReportSummaryContainer_Success() throws DataProcessingException {
        var reportSumVO = new LabReportSummaryContainer();
        reportSumVO.setAssociated(false);
        var odsDT = new ObservationDto();
        odsDT.setVersionCtrlNbr(1);
        var rootDT = new ObservationDto();
        rootDT.setObservationUid(10L);

        var actCol = new ArrayList<ActRelationshipDto>();
        var act = new ActRelationshipDto();
        actCol.add(act);
        when(actRelationshipService.loadActRelationshipBySrcIdAndTypeCode(10L, "LabReport"))
                .thenReturn(actCol);
        when(prepareAssocModelHelper.prepareVO(any(),
                eq(NEDSSConstant.OBSERVATIONLABREPORT),
                eq(NEDSSConstant.OBS_LAB_UNPROCESS),
                eq(NEDSSConstant.OBSERVATION),
                eq(NEDSSConstant.BASE),
                eq(1) ))
                .thenReturn(rootDT);

        var test = investigationService.processingNonAssociatedReportSummaryContainer(reportSumVO, odsDT, rootDT);

        assertNotNull(test);
    }

    @Test
    void updateAutoResendNotifications_Success_InvestigationConn() {
        var pageProx = new InvestigationContainer();
        var phcConn = new PublicHealthCaseContainer();
        var phcDt = new PublicHealthCaseDto();
        phcDt.setCaseClassCd("CASE");
        phcDt.setProgAreaCd("PRG");
        phcDt.setJurisdictionCd("JUST");
        phcDt.setSharedInd("Y");
        phcDt.setCaseStatusDirty(false);
        phcDt.setCd("CODE");
        phcConn.setThePublicHealthCaseDto(phcDt);
        pageProx.setThePublicHealthCaseContainer(phcConn);
        var notSummaryCol = new ArrayList<>();
        var notSum = new NotificationSummaryContainer();
        notSum.setIsHistory("F");
        notSum.setAutoResendInd("T");
        notSum.setNotificationUid(10L);

        notSummaryCol.add(notSum);
        pageProx.setTheNotificationSummaryVOCollection(notSummaryCol);

        var notification = new NotificationDto();
        notification.setVersionCtrlNbr(1);
        notification.setAutoResendInd("T");
        notification.setNotificationUid(11L);
        when(notificationService.getNotificationById(10L))
                .thenReturn(notification);

        investigationService.updateAutoResendNotifications(pageProx);

        verify(notificationService, times(1)).saveNotification(any());

    }


    @Mock
    InvestigationContainer invesCon;
    @Test
    void testSetAssociations_ExceptionCase() {
        Long investigationUID = 1L;
        Collection<LabReportSummaryContainer> reportSumVOCollection = new ArrayList<>();
        Collection<Object> vaccinationSummaryVOCollection = new ArrayList<>();
        Collection<Object> summaryDTColl = new ArrayList<>();
        Collection<Object> treatmentSumColl = new ArrayList<>();
        Boolean isNNDResendCheckRequired = true;

        var phcCon = new PublicHealthCaseContainer();
        when(invesCon.getThePublicHealthCaseContainer()).thenReturn(phcCon);
        DataProcessingException exception = assertThrows(DataProcessingException.class, () -> {
            investigationService.setAssociations(investigationUID, reportSumVOCollection, vaccinationSummaryVOCollection, summaryDTColl, treatmentSumColl, isNNDResendCheckRequired);
        });

        assertNotNull(exception);

    }


    @Test
    void testProcessingInvestigationSummary_LiteCase() throws DataProcessingException {
        InvestigationContainer investigationProxyVO = new InvestigationContainer();
        PublicHealthCaseContainer thePublicHealthCaseContainer = new PublicHealthCaseContainer();
        boolean lite = true;

        assertDoesNotThrow(() -> {
            investigationService.processingInvestigationSummary(investigationProxyVO, thePublicHealthCaseContainer, lite);
        });

        verify(retrieveSummaryService, never()).notificationSummaryOnInvestigation(any(), any());
    }

    @Test
    void testProcessingInvestigationSummary_NonLiteCase() throws DataProcessingException {
        InvestigationContainer investigationProxyVO = new InvestigationContainer();
        investigationProxyVO.setThePublicHealthCaseContainer(new PublicHealthCaseContainer());
        PublicHealthCaseContainer thePublicHealthCaseContainer = new PublicHealthCaseContainer();
        boolean lite = false;

        NotificationSummaryContainer notifVO1 = new NotificationSummaryContainer();
        notifVO1.setCdNotif(NEDSSConstant.CLASS_CD_SHARE_NOTF);
        notifVO1.setNotificationUid(1L);

        NotificationSummaryContainer notifVO2 = new NotificationSummaryContainer();
        notifVO2.setCdNotif(NEDSSConstant.CLASS_CD_EXP_NOTF);
        notifVO2.setNotificationUid(2L);

        NotificationSummaryContainer notifVO3 = new NotificationSummaryContainer();
        notifVO3.setCdNotif(NEDSSConstant.CLASS_CD_NOTF);
        notifVO3.setNotificationUid(3L);

        Collection<Object> notificationSummaryVOCollection = new ArrayList<>();
        notificationSummaryVOCollection.add(notifVO1);
        notificationSummaryVOCollection.add(notifVO2);
        notificationSummaryVOCollection.add(notifVO3);

        ActRelationshipDto actRelationDT1 = new ActRelationshipDto();
        actRelationDT1.setSourceActUid(1L);

        ActRelationshipDto actRelationDT2 = new ActRelationshipDto();
        actRelationDT2.setSourceActUid(2L);

        ActRelationshipDto actRelationDT3 = new ActRelationshipDto();
        actRelationDT3.setSourceActUid(3L);

        Collection<ActRelationshipDto> actRelationshipDTCollection = new ArrayList<>();
        actRelationshipDTCollection.add(actRelationDT1);
        actRelationshipDTCollection.add(actRelationDT2);
        actRelationshipDTCollection.add(actRelationDT3);

        investigationProxyVO.setTheNotificationSummaryVOCollection(notificationSummaryVOCollection);
        investigationProxyVO.getThePublicHealthCaseContainer().setTheActRelationshipDTCollection(actRelationshipDTCollection);

        when(retrieveSummaryService.notificationSummaryOnInvestigation(any(), any())).thenReturn(notificationSummaryVOCollection);

        investigationService.processingInvestigationSummary(investigationProxyVO, thePublicHealthCaseContainer, lite);


        verify(retrieveSummaryService, times(1)).notificationSummaryOnInvestigation(any(), any());
    }


    @Test
    void populateDescTxtFromCachedValues_Test() throws DataProcessingException {
        var reportCol = new ArrayList<>();
        var report = new LabReportSummaryContainer();
        report.setProgramArea("TEST");
        report.setJurisdiction("TEST");
        report.setStatus("TEST");

        var labCol = new ArrayList<ResultedTestSummaryContainer>();
        var lab = new ResultedTestSummaryContainer();
        lab.setCtrlCdUserDefined1("N");
        lab.setCodedResultValue("TEST");
        lab.setCdSystemCd("LN");
        lab.setResultedTestCd("TEST");
        lab.setResultedTestStatus("TEST");
        labCol.add(lab);

        lab = new ResultedTestSummaryContainer();
        lab.setCtrlCdUserDefined1("Y");
        lab.setOrganismName("TEST");
        lab.setOrganismCodeSystemCd("SNM");
        lab.setCodedResultValue("TEST");
        lab.setCdSystemCd("LNA");
        lab.setResultedTestCd("TEST");
        labCol.add(lab);

        lab = new ResultedTestSummaryContainer();
        lab.setCtrlCdUserDefined1("Y");
        lab.setOrganismName("TEST");
        lab.setOrganismCodeSystemCd("BLAH");
        lab.setCodedResultValue("TEST");
        lab.setCdSystemCd("LNA");
        lab.setResultedTestCd("TEST-2");
        labCol.add(lab);

        lab = new ResultedTestSummaryContainer();
        lab.setCtrlCdUserDefined1(null);
        lab.setOrganismName("TEST");
        lab.setOrganismCodeSystemCd("SNM");
        lab.setCodedResultValue("TEST");
        labCol.add(lab);

        lab = new ResultedTestSummaryContainer();
        lab.setCtrlCdUserDefined1(null);
        lab.setOrganismName("TEST");
        lab.setOrganismCodeSystemCd("SNM");
        lab.setCodedResultValue("TEST-A");
        labCol.add(lab);

        //
        lab = new ResultedTestSummaryContainer();
        lab.setCtrlCdUserDefined1(null);
        lab.setOrganismName("TEST");
        lab.setOrganismCodeSystemCd("BLAH");
        lab.setCodedResultValue("TEST");
        labCol.add(lab);

        lab = new ResultedTestSummaryContainer();
        lab.setCtrlCdUserDefined1(null);
        lab.setOrganismName("TEST");
        lab.setOrganismCodeSystemCd("BLAH");
        lab.setCodedResultValue("TEST-A");
        labCol.add(lab);


        lab = new ResultedTestSummaryContainer();
        lab.setCtrlCdUserDefined1(null);
        lab.setOrganismName("TEST");
        lab.setOrganismCodeSystemCd(null);
        lab.setCodedResultValue("TEST-A");
        labCol.add(lab);

        lab = new ResultedTestSummaryContainer();
        lab.setCtrlCdUserDefined1(null);
        lab.setOrganismName(null);
        lab.setOrganismCodeSystemCd(null);
        lab.setCodedResultValue("TEST-A");
        labCol.add(lab);

        lab = new ResultedTestSummaryContainer();
        lab.setCtrlCdUserDefined1(null);
        lab.setOrganismName(null);
        lab.setOrganismCodeSystemCd(null);
        lab.setCodedResultValue("TEST");
        lab.setResultedTestStatusCd("TEST");
        lab.setResultedTestCd("TEST");

        var susCol = new ArrayList<>();
        var sus = new ResultedTestSummaryContainer();
        sus.setCodedResultValue("TEST");
        sus.setCdSystemCd("LN");
        sus.setResultedTestCd("TEST");
        susCol.add(sus);

        sus = new ResultedTestSummaryContainer();
        sus.setCodedResultValue("TEST-A");
        sus.setCdSystemCd(null);
        susCol.add(sus);

        sus = new ResultedTestSummaryContainer();
        sus.setCodedResultValue("TEST");
        sus.setCdSystemCd("LN");
        sus.setResultedTestCd("TEST-A");
        susCol.add(sus);


        sus = new ResultedTestSummaryContainer();
        sus.setCodedResultValue("TEST");
        sus.setCdSystemCd("LNA");
        sus.setResultedTestCd("TEST-A");
        susCol.add(sus);


        sus = new ResultedTestSummaryContainer();
        sus.setCodedResultValue("TEST");
        sus.setCdSystemCd("LNA");
        sus.setResultedTestCd("TEST");
        susCol.add(sus);

        lab.setTheSusTestSummaryVOColl(susCol);
        labCol.add(lab);




        report.setTheResultedTestSummaryVOCollection(labCol);
        reportCol.add(report);

//        SrteCache.programAreaCodesMap.put("TEST", "TEST");
//        SrteCache.jurisdictionCodeMap.put("TEST", "TEST");
//        SrteCache.labResultByDescMap.put("TEST", "TEST");
//        SrteCache.snomedCodeByDescMap.put("TEST", "TEST");
//        SrteCache.labResultWithOrganismNameIndMap.put("TEST", "TEST");
//        SrteCache.loinCodeWithComponentNameMap.put("TEST", "TEST");


        when(cachingValueDpService.getCodeDescTxtForCd(any(), any())).thenReturn("TEST");
        var labLst = new ArrayList<LabTest>();
        var labTs = new LabTest();
        labLst.add(labTs);
        when(labTestRepository.findLabTestByLabIdAndLabTestCode(any(), eq("TEST"))).thenReturn(Optional.of(labLst));
        when(cachingValueDpService.getCodeDescTxtForCd(any(), eq("TEST"))).thenReturn("TEST");

        when(cacheApiService.getSrteCacheString(anyString(), any())).thenReturn("TEST");


        investigationService.populateDescTxtFromCachedValues(reportCol);

//        SrteCache.programAreaCodesMap.clear();
//        SrteCache.jurisdictionCodeMap.clear();
//        SrteCache.labResultByDescMap.clear();
//        SrteCache.snomedCodeByDescMap.clear();
//        SrteCache.labResultWithOrganismNameIndMap.clear();
//        SrteCache.loinCodeWithComponentNameMap.clear();


        verify(cachingValueDpService, times(2)).getCodeDescTxtForCd(any(), any());
    }
}
