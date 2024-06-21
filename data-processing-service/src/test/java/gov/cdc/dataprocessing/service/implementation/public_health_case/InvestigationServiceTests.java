package gov.cdc.dataprocessing.service.implementation.public_health_case;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import gov.cdc.dataprocessing.constant.elr.NBSBOLookup;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.*;
import gov.cdc.dataprocessing.model.dto.act.ActRelationshipDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.model.dto.phc.*;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.repository.nbs.odse.model.observation.Observation;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.edx.EdxDocumentRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.observation.ObservationRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.observation.Observation_SummaryRepository;
import gov.cdc.dataprocessing.repository.nbs.srte.repository.LabTestRepository;
import gov.cdc.dataprocessing.service.implementation.act.ActRelationshipService;
import gov.cdc.dataprocessing.service.implementation.cache.CachingValueService;
import gov.cdc.dataprocessing.service.implementation.observation.EdxDocumentService;
import gov.cdc.dataprocessing.service.implementation.observation.ObservationSummaryService;
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
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class InvestigationServiceTests {
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
    private CachingValueService cachingValueService;
    @Mock
    private ILdfService ldfService;
    @Mock
    private LabTestRepository labTestRepository;
    @InjectMocks
    private InvestigationService investigationService;
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


        Gson gson = new Gson();
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
        var treemap = new TreeMap<String, String>();
        treemap.put("26", "BLAH");
        when(cachingValueService.getCodedValues("SPECMN_SRC", "26")).thenReturn(treemap);

        when(observationSummaryService.getProviderInformation(any(), any())).thenReturn(27L);


        investigationService.setAssociations(investigationUid, reportSumVOCollection,
                null,
                null,
                null,
                true);

        verify(publicHealthCaseRepositoryUtil, times(1)).findPublicHealthCase(eq(investigationUid));
        verify(observationRepositoryUtil, times(1)).loadObject(eq(10006210L));
        verify(retrieveSummaryService, times(1)).checkBeforeCreateAndStoreMessageLogDTCollection(any(), any());

    }

    @Test
    void setObservationAssociationsImpl_Test_Success() throws DataProcessingException {
        String reportSumVOCollectionStr = "[{\"isTouched\":true,\"isAssociated\":true,\"observationUid\":10006210,\"providerFirstName\":\"\",\"providerLastName\":\"\",\"providerSuffix\":\"\",\"providerPrefix\":\"\",\"providerDegree\":\"\",\"providerUid\":\"\",\"isLabFromMorb\":false,\"isReactor\":false,\"disabled\":\"\",\"isLabFromDoc\":false,\"itNew\":false,\"itOld\":false,\"itDirty\":false,\"itDelete\":false}]";
        String phcDTStr = "{\"caseStatusDirty\":false,\"isPamCase\":false,\"isPageCase\":false,\"isStdHivProgramAreaCode\":false,\"caseTypeCd\":\"I\",\"publicHealthCaseUid\":10006070,\"activityFromTime\":\"Jun 20, 2024, 12:00:00 AM\",\"addTime\":\"Jun 20, 2024, 12:36:18 PM\",\"addUserId\":36,\"cd\":\"11120\",\"cdDescTxt\":\"Acute flaccid myelitis\",\"groupCaseCnt\":1,\"investigationStatusCd\":\"O\",\"jurisdictionCd\":\"130001\",\"lastChgTime\":\"Jun 20, 2024, 12:36:18 PM\",\"lastChgUserId\":36,\"localId\":\"CAS10006070GA01\",\"mmwrWeek\":\"25\",\"mmwrYear\":\"2024\",\"progAreaCd\":\"GCD\",\"recordStatusCd\":\"OPEN\",\"recordStatusTime\":\"Jun 20, 2024, 12:36:18 PM\",\"rptFormCmpltTime\":\"Jun 20, 2024, 12:36:11 PM\",\"statusCd\":\"A\",\"programJurisdictionOid\":1300100009,\"sharedInd\":\"T\",\"versionCtrlNbr\":1,\"isSummaryCase\":false,\"itNew\":false,\"itOld\":false,\"itDirty\":false,\"itDelete\":false}";


        Gson gson = new Gson();
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

        verify(publicHealthCaseRepositoryUtil, times(1)).findPublicHealthCase(eq(investigationUid));
        verify(observationRepositoryUtil, times(1)).loadObject(eq(10006210L));
    }

    @Test
    void setObservationAssociationsImpl_Test_Success_NotAssociated() throws DataProcessingException {
        String reportSumVOCollectionStr = "[{\"isTouched\":true,\"isAssociated\":true,\"observationUid\":10006210,\"providerFirstName\":\"\",\"providerLastName\":\"\",\"providerSuffix\":\"\",\"providerPrefix\":\"\",\"providerDegree\":\"\",\"providerUid\":\"\",\"isLabFromMorb\":false,\"isReactor\":false,\"disabled\":\"\",\"isLabFromDoc\":false,\"itNew\":false,\"itOld\":false,\"itDirty\":false,\"itDelete\":false}]";
        String phcDTStr = "{\"caseStatusDirty\":false,\"isPamCase\":false,\"isPageCase\":false,\"isStdHivProgramAreaCode\":false,\"caseTypeCd\":\"I\",\"publicHealthCaseUid\":10006070,\"activityFromTime\":\"Jun 20, 2024, 12:00:00 AM\",\"addTime\":\"Jun 20, 2024, 12:36:18 PM\",\"addUserId\":36,\"cd\":\"11120\",\"cdDescTxt\":\"Acute flaccid myelitis\",\"groupCaseCnt\":1,\"investigationStatusCd\":\"O\",\"jurisdictionCd\":\"130001\",\"lastChgTime\":\"Jun 20, 2024, 12:36:18 PM\",\"lastChgUserId\":36,\"localId\":\"CAS10006070GA01\",\"mmwrWeek\":\"25\",\"mmwrYear\":\"2024\",\"progAreaCd\":\"GCD\",\"recordStatusCd\":\"OPEN\",\"recordStatusTime\":\"Jun 20, 2024, 12:36:18 PM\",\"rptFormCmpltTime\":\"Jun 20, 2024, 12:36:11 PM\",\"statusCd\":\"A\",\"programJurisdictionOid\":1300100009,\"sharedInd\":\"T\",\"versionCtrlNbr\":1,\"isSummaryCase\":false,\"itNew\":false,\"itOld\":false,\"itDirty\":false,\"itDelete\":false}";


        Gson gson = new Gson();

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

        verify(publicHealthCaseRepositoryUtil, times(1)).findPublicHealthCase(eq(investigationUid));
        verify(observationRepositoryUtil, times(0)).loadObject(eq(10006210L));
    }

}
