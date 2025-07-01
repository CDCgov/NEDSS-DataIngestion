package gov.cdc.dataprocessing.service.implementation.manager;


import gov.cdc.dataprocessing.cache.PropertyUtilCache;
import gov.cdc.dataprocessing.config.ServicePropertiesProvider;
import gov.cdc.dataprocessing.constant.DecisionSupportConstants;
import gov.cdc.dataprocessing.constant.DpConstant;
import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.constant.enums.ObjectName;
import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.exception.DataProcessingDBException;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.exception.EdxLogException;
import gov.cdc.dataprocessing.kafka.producer.KafkaManagerProducer;
import gov.cdc.dataprocessing.model.container.model.*;
import gov.cdc.dataprocessing.model.dto.edx.EdxRuleAlgorothmManagerDto;
import gov.cdc.dataprocessing.model.dto.lab_result.EdxLabInformationDto;
import gov.cdc.dataprocessing.model.dto.log.EDXActivityDetailLogDto;
import gov.cdc.dataprocessing.model.dto.log.EDXActivityLogDto;
import gov.cdc.dataprocessing.model.dto.observation.ObservationDto;
import gov.cdc.dataprocessing.model.dto.person.PersonDto;
import gov.cdc.dataprocessing.model.dto.phc.PublicHealthCaseDto;
import gov.cdc.dataprocessing.repository.nbs.msgoute.model.NbsInterfaceModel;
import gov.cdc.dataprocessing.repository.nbs.msgoute.repos.NbsInterfaceRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template.NbsInterfaceJdbcRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template.RtiDltJdbcRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.service.interfaces.action.ILabReportProcessing;
import gov.cdc.dataprocessing.service.interfaces.cache.ICacheApiService;
import gov.cdc.dataprocessing.service.interfaces.data_extraction.IDataExtractionService;
import gov.cdc.dataprocessing.service.interfaces.log.IEdxLogService;
import gov.cdc.dataprocessing.service.interfaces.manager.IManagerAggregationService;
import gov.cdc.dataprocessing.service.interfaces.manager.IManagerCacheService;
import gov.cdc.dataprocessing.service.interfaces.observation.IObservationService;
import gov.cdc.dataprocessing.service.interfaces.page_and_pam.IPageService;
import gov.cdc.dataprocessing.service.interfaces.page_and_pam.IPamService;
import gov.cdc.dataprocessing.service.interfaces.public_health_case.IDecisionSupportService;
import gov.cdc.dataprocessing.service.interfaces.public_health_case.IInvestigationNotificationService;
import gov.cdc.dataprocessing.service.model.auth_user.AuthUserProfileInfo;
import gov.cdc.dataprocessing.service.model.phc.PublicHealthCaseFlowContainer;
import gov.cdc.dataprocessing.test_data.TestDataReader;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.generic_helper.ManagerUtil;
import jakarta.xml.bind.JAXBException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.dao.TransientDataAccessException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static gov.cdc.dataprocessing.constant.DpConstant.DP_FAILURE_STEP_2;
import static gov.cdc.dataprocessing.constant.elr.NEDSSConstant.ERROR;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@SuppressWarnings("java:S6068")
class ManagerServiceTest {
    @Mock
    private  IObservationService observationService;
    @Mock
    private  IEdxLogService edxLogService;
    @Mock
    private  IDataExtractionService dataExtractionService;
    @Mock
    private  NbsInterfaceRepository nbsInterfaceRepository;
    @Mock
    private  IDecisionSupportService decisionSupportService;
    @Mock
    private  ManagerUtil managerUtil;
    @Mock
    private  KafkaManagerProducer kafkaManagerProducer;
    @Mock
    private  IManagerAggregationService managerAggregationService;
    @Mock
    private  ILabReportProcessing labReportProcessing;
    @Mock
    private  IPageService pageService;
    @Mock
    private  IPamService pamService;
    @Mock
    private  IInvestigationNotificationService investigationNotificationService;
    @Mock
    private IManagerCacheService managerCacheService;

    @Mock
    private RtiDltJdbcRepository rtiDltJdbcRepository;
    @Mock
    private ServicePropertiesProvider servicePropertiesProvider;


    @Mock
    private NbsInterfaceJdbcRepository nbsInterfaceJdbcRepository;

    @Spy
    @InjectMocks
    private ManagerService managerService;
    @Mock
    AuthUtil authUtil;

    @Mock
    LabService labService;

    @Mock
    ICacheApiService cacheApiService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        AuthUserProfileInfo userInfo = new AuthUserProfileInfo();
        AuthUser user = new AuthUser();
        user.setAuthUserUid(1L);
        user.setUserType(NEDSSConstant.SEC_USERTYPE_EXTERNAL);
        userInfo.setAuthUser(user);

        when(servicePropertiesProvider.getTz()).thenReturn("UTC");
        authUtil.setGlobalAuthUser(userInfo);

    }

    @AfterEach
    void tearDown() {
        Mockito.reset(observationService, edxLogService, dataExtractionService, nbsInterfaceRepository, managerCacheService,
                decisionSupportService, managerUtil, kafkaManagerProducer, managerAggregationService,
                labReportProcessing, pageService,pamService, investigationNotificationService, authUtil);
    }


    @Test
    void processDistribution_Test() throws DataProcessingConsumerException, JAXBException, DataProcessingException, EdxLogException, DataProcessingDBException {
        var test = new TestDataReader();

        NbsInterfaceModel labData = test.readDataFromJsonPath("manager/manager_first_process.json", NbsInterfaceModel.class);


        CompletableFuture<Void> mockedFuture = mock(CompletableFuture.class);
        when(managerCacheService.loadAndInitCachedValueAsync()).thenReturn(mockedFuture);
        when(mockedFuture.join()).thenReturn(null); // You can adjust this to simulate different behaviors

        LabResultProxyContainer labResultProxyContainer = new LabResultProxyContainer();
        when(dataExtractionService.parsingDataToObject(any(), any())).thenReturn(labResultProxyContainer);

        var personCol = new ArrayList<PersonContainer>();

        labResultProxyContainer.setThePersonContainerCollection(personCol);

        EdxLabInformationDto edxLabInformationDto = new EdxLabInformationDto();
        edxLabInformationDto.setLabIsUpdateDRRQ(true);
        edxLabInformationDto.setLabIsCreate(true);
        edxLabInformationDto.setLabIsCreateSuccess(true);
        edxLabInformationDto.setProgramAreaName(null);
        var edxAcLog = new EDXActivityLogDto();
        edxLabInformationDto.setEdxActivityLogDto(edxAcLog);
        when(managerAggregationService.processingObservationMatching(any(), any(), any())).thenReturn(edxLabInformationDto);


        when(nbsInterfaceJdbcRepository.getNbsInterfaceByUid(any())).thenReturn(labData);


        ObservationDto observationDto = new ObservationDto();
        observationDto.setLocalId("LOCAL");
        observationDto.setObservationUid(10L);
        observationDto.setProgAreaCd("A");
        observationDto.setJurisdictionCd("A");

        when(observationService.processingLabResultContainer(any())).thenReturn(observationDto);


        when(cacheApiService.getSrteCacheBool(any(), any())).thenReturn(true);
        managerService.processingELR(123, false);

        verify(kafkaManagerProducer, times(0)).sendDataEdxActivityLog(any());
    }

    @Test
    void initiatingInvestigationAndPublicHealthCase_Test_PageAct_ToStep3() throws DataProcessingException {
        PublicHealthCaseFlowContainer publicHealthCaseFlowContainer = new PublicHealthCaseFlowContainer();
        var edxLabInfoDto = new EdxLabInformationDto();
        var obsDto = new ObservationDto();
        var labResult = new LabResultProxyContainer();

        edxLabInfoDto.setLabIsUpdateDRRQ(true);
        edxLabInfoDto.setLabIsCreate(true);
        edxLabInfoDto.setWdsReports(new ArrayList<>());
        var pageAct = new PageActProxyContainer();
        var phcConn = new PublicHealthCaseContainer();
        var phcDt = new PublicHealthCaseDto();
        phcConn.setThePublicHealthCaseDto(phcDt);
        pageAct.setPublicHealthCaseContainer(phcConn);
        edxLabInfoDto.setPageActContainer(pageAct);
        obsDto.setJurisdictionCd("A");
        obsDto.setProgAreaCd("A");
        var perCol = new ArrayList<PersonContainer>();
        var perCon = new PersonContainer();
        var perDto = new PersonDto();
        perDto.setCd("PAT");
        perDto.setPersonUid(11L);
        perDto.setPersonParentUid(12L);
        perDto.setFirstNm("TEST");
        perDto.setLastNm("TEST");
        perCon.setThePersonDto(perDto);
        perCol.add(perCon);
        labResult.setThePersonContainerCollection(perCol);
        publicHealthCaseFlowContainer.setEdxLabInformationDto(edxLabInfoDto);
        publicHealthCaseFlowContainer.setObservationDto(obsDto);
        publicHealthCaseFlowContainer.setLabResultProxyContainer(labResult);


        var nbs = new NbsInterfaceModel();
        nbs.setNbsInterfaceUid(13);
        nbs.setRecordStatusCd("RTI_SUCCESS_STEP_1");
        publicHealthCaseFlowContainer.setNbsInterfaceModel(nbs);

        managerService.initiatingInvestigationAndPublicHealthCase(publicHealthCaseFlowContainer);

        verify(kafkaManagerProducer, times(0)).sendDataLabHandling(any());

    }

    @Test
    void initiatingInvestigationAndPublicHealthCase_Test_Pam_ToStep3() throws DataProcessingException {
        PublicHealthCaseFlowContainer publicHealthCaseFlowContainer = new PublicHealthCaseFlowContainer();
        var edxLabInfoDto = new EdxLabInformationDto();
        var obsDto = new ObservationDto();
        var labResult = new LabResultProxyContainer();

        edxLabInfoDto.setLabIsUpdateDRSA(false);
        edxLabInfoDto.setLabIsCreate(true);
        edxLabInfoDto.setWdsReports(new ArrayList<>());
        var pageAct = new PamProxyContainer();
        var phcConn = new PublicHealthCaseContainer();
        var phcDt = new PublicHealthCaseDto();
        phcConn.setThePublicHealthCaseDto(phcDt);
        pageAct.setPublicHealthCaseContainer(phcConn);
        edxLabInfoDto.setPamContainer(pageAct);
        obsDto.setJurisdictionCd("A");
        obsDto.setProgAreaCd("A");
        var perCol = new ArrayList<PersonContainer>();
        var perCon = new PersonContainer();
        var perDto = new PersonDto();
        perDto.setCd("PAT");
        perDto.setPersonUid(11L);
        perDto.setPersonParentUid(12L);
        perDto.setFirstNm("TEST");
        perDto.setLastNm("TEST");
        perCon.setThePersonDto(perDto);
        perCol.add(perCon);
        labResult.setThePersonContainerCollection(perCol);
        publicHealthCaseFlowContainer.setEdxLabInformationDto(edxLabInfoDto);
        publicHealthCaseFlowContainer.setObservationDto(obsDto);
        publicHealthCaseFlowContainer.setLabResultProxyContainer(labResult);


        var nbs = new NbsInterfaceModel();
        nbs.setNbsInterfaceUid(13);
        nbs.setRecordStatusCd("RTI_SUCCESS_STEP_1");
        publicHealthCaseFlowContainer.setNbsInterfaceModel(nbs);
        managerService.initiatingInvestigationAndPublicHealthCase(publicHealthCaseFlowContainer);

        verify(kafkaManagerProducer, times(0)).sendDataLabHandling(any());

    }

    @Test
    void initiatingInvestigationAndPublicHealthCase_NoFurther() throws DataProcessingException {
        PublicHealthCaseFlowContainer publicHealthCaseFlowContainer = new PublicHealthCaseFlowContainer();
        var edxLabInfoDto = new EdxLabInformationDto();
        var obsDto = new ObservationDto();
        var labResult = new LabResultProxyContainer();
        var perConList = new ArrayList<PersonContainer>();
        labResult.setThePersonContainerCollection(perConList);
        var pageAct = new PamProxyContainer();
        var phcConn = new PublicHealthCaseContainer();
        var phcDt = new PublicHealthCaseDto();
        phcConn.setThePublicHealthCaseDto(phcDt);
        pageAct.setPublicHealthCaseContainer(phcConn);
        edxLabInfoDto.setPamContainer(pageAct);
        obsDto.setJurisdictionCd(null);
        obsDto.setProgAreaCd(null);
        publicHealthCaseFlowContainer.setEdxLabInformationDto(edxLabInfoDto);
        publicHealthCaseFlowContainer.setObservationDto(obsDto);
        publicHealthCaseFlowContainer.setLabResultProxyContainer(labResult);


        var nbs = new NbsInterfaceModel();
        nbs.setNbsInterfaceUid(13);
        publicHealthCaseFlowContainer.setNbsInterfaceModel(nbs);
        managerService.initiatingInvestigationAndPublicHealthCase(publicHealthCaseFlowContainer);

        verify(kafkaManagerProducer, times(0)).sendDataLabHandling(any());

    }


    @Test
    void initiatingInvestigationAndPublicHealthCase_Exception()  {
        PublicHealthCaseFlowContainer publicHealthCaseFlowContainer = new PublicHealthCaseFlowContainer();
        var edxLabInfoDto = new EdxLabInformationDto();
        var obsDto = new ObservationDto();
        var labResult = new LabResultProxyContainer();

        edxLabInfoDto.setLabIsUpdateDRSA(false);
        edxLabInfoDto.setLabIsCreate(true);
        edxLabInfoDto.setWdsReports(new ArrayList<>());
        var pageAct = new PamProxyContainer();
        var phcConn = new PublicHealthCaseContainer();
        var phcDt = new PublicHealthCaseDto();
        phcConn.setThePublicHealthCaseDto(phcDt);
        pageAct.setPublicHealthCaseContainer(phcConn);
        edxLabInfoDto.setPamContainer(pageAct);
        obsDto.setJurisdictionCd("A");
        obsDto.setProgAreaCd("A");
        publicHealthCaseFlowContainer.setEdxLabInformationDto(edxLabInfoDto);
        publicHealthCaseFlowContainer.setObservationDto(obsDto);
        publicHealthCaseFlowContainer.setLabResultProxyContainer(labResult);


        var nbs = new NbsInterfaceModel();
        nbs.setNbsInterfaceUid(13);
        when(nbsInterfaceRepository.findByNbsInterfaceUid(any())).thenReturn(Optional.empty());



        assertThrows(NullPointerException.class, () -> managerService.initiatingInvestigationAndPublicHealthCase(publicHealthCaseFlowContainer));

        verify(nbsInterfaceRepository, times(0)).findByNbsInterfaceUid(any());

    }

    @Test
    void initiatingLabProcessing_Test_MarkReviewed_Assoc() throws DataProcessingException {
        PublicHealthCaseFlowContainer publicHealthCaseFlowContainer = new PublicHealthCaseFlowContainer();
        var edxLabInfoDto = new EdxLabInformationDto();
        var obsDto = new ObservationDto();
        var labResult = new LabResultProxyContainer();

        edxLabInfoDto.setLabIsUpdateDRSA(false);
        edxLabInfoDto.setLabIsCreate(true);
        edxLabInfoDto.setWdsReports(new ArrayList<>());
        var pageAct = new PageActProxyContainer();
        var phcConn = new PublicHealthCaseContainer();
        var phcDt = new PublicHealthCaseDto();
        phcConn.setThePublicHealthCaseDto(phcDt);
        pageAct.setPublicHealthCaseContainer(phcConn);
        edxLabInfoDto.setPageActContainer(pageAct);
        edxLabInfoDto.setAction(DecisionSupportConstants.MARK_AS_REVIEWED);
        edxLabInfoDto.setAssociatedPublicHealthCaseUid(12L);

        obsDto.setJurisdictionCd("A");
        obsDto.setProgAreaCd("A");
        obsDto.setObservationUid(11L);

        publicHealthCaseFlowContainer.setEdxLabInformationDto(edxLabInfoDto);
        publicHealthCaseFlowContainer.setObservationDto(obsDto);
        publicHealthCaseFlowContainer.setLabResultProxyContainer(labResult);
        publicHealthCaseFlowContainer.setNbsInterfaceId(10);

        var nbs = new NbsInterfaceModel();
        nbs.setNbsInterfaceUid(10);
        nbs.setRecordStatusCd("RTI_SUCCESS_STEP_2");

        when(nbsInterfaceRepository.findByNbsInterfaceUid(any())).thenReturn(Optional.of(nbs));
        publicHealthCaseFlowContainer.setNbsInterfaceModel(nbs);

        managerService.initiatingLabProcessing(publicHealthCaseFlowContainer);
        verify(nbsInterfaceRepository, times(1)).save(any());
        verify(labService, times(1)).handleMarkAsReviewed(any(), any());


    }

    @Test
    void initiatingLabProcessing_Test_MarkReviewed_NotAssoc() throws DataProcessingException {
        PublicHealthCaseFlowContainer publicHealthCaseFlowContainer = new PublicHealthCaseFlowContainer();
        var edxLabInfoDto = new EdxLabInformationDto();
        var obsDto = new ObservationDto();
        var labResult = new LabResultProxyContainer();

        edxLabInfoDto.setLabIsUpdateDRSA(false);
        edxLabInfoDto.setLabIsCreate(true);
        edxLabInfoDto.setWdsReports(new ArrayList<>());
        var pageAct = new PageActProxyContainer();
        var phcConn = new PublicHealthCaseContainer();
        var phcDt = new PublicHealthCaseDto();
        phcConn.setThePublicHealthCaseDto(phcDt);
        pageAct.setPublicHealthCaseContainer(phcConn);
        edxLabInfoDto.setPageActContainer(pageAct);
        edxLabInfoDto.setAction(DecisionSupportConstants.MARK_AS_REVIEWED);

        obsDto.setJurisdictionCd("A");
        obsDto.setProgAreaCd("A");
        obsDto.setObservationUid(11L);

        publicHealthCaseFlowContainer.setEdxLabInformationDto(edxLabInfoDto);
        publicHealthCaseFlowContainer.setObservationDto(obsDto);
        publicHealthCaseFlowContainer.setLabResultProxyContainer(labResult);
        publicHealthCaseFlowContainer.setNbsInterfaceId(10);

        var nbs = new NbsInterfaceModel();
        nbs.setNbsInterfaceUid(10);
        nbs.setRecordStatusCd("RTI_SUCCESS_STEP_2");

        when(nbsInterfaceRepository.findByNbsInterfaceUid(any())).thenReturn(Optional.of(nbs));
        publicHealthCaseFlowContainer.setNbsInterfaceModel(nbs);

        managerService.initiatingLabProcessing(publicHealthCaseFlowContainer);
        verify(nbsInterfaceRepository, times(1)).save(any());
        verify(labService, times(1)).handleMarkAsReviewed(any(), any());


    }

    @Test
    void initiatingLabProcessing_Test_Investigation_Page() throws DataProcessingException {
        PublicHealthCaseFlowContainer publicHealthCaseFlowContainer = new PublicHealthCaseFlowContainer();
        var edxLabInfoDto = new EdxLabInformationDto();
        var obsDto = new ObservationDto();
        var labResult = new LabResultProxyContainer();

        edxLabInfoDto.setLabIsUpdateDRSA(false);
        edxLabInfoDto.setLabIsCreate(true);
        edxLabInfoDto.setWdsReports(new ArrayList<>());
        var pageAct = new PageActProxyContainer();
        var phcConn = new PublicHealthCaseContainer();
        var phcDt = new PublicHealthCaseDto();
        phcConn.setThePublicHealthCaseDto(phcDt);
        pageAct.setPublicHealthCaseContainer(phcConn);
        edxLabInfoDto.setPageActContainer(pageAct);
        edxLabInfoDto.setAction(DecisionSupportConstants.CREATE_INVESTIGATION_VALUE);
        edxLabInfoDto.setAssociatedPublicHealthCaseUid(12L);

        obsDto.setJurisdictionCd("A");
        obsDto.setProgAreaCd("A");
        obsDto.setObservationUid(11L);

        publicHealthCaseFlowContainer.setEdxLabInformationDto(edxLabInfoDto);
        publicHealthCaseFlowContainer.setObservationDto(obsDto);
        publicHealthCaseFlowContainer.setLabResultProxyContainer(labResult);
        publicHealthCaseFlowContainer.setNbsInterfaceId(10);

        var nbs = new NbsInterfaceModel();
        nbs.setNbsInterfaceUid(10);
        when(nbsInterfaceRepository.findByNbsInterfaceUid(any())).thenReturn(Optional.of(nbs));
        when(labService.handlePageContainer(any(), any())).thenReturn(12L);

        publicHealthCaseFlowContainer.setNbsInterfaceModel(nbs);

        managerService.initiatingLabProcessing(publicHealthCaseFlowContainer);
        verify(nbsInterfaceRepository, times(1)).save(any());
    }

    @Test
    void initiatingLabProcessing_Test_Investigation_Pam() throws DataProcessingException {
        PublicHealthCaseFlowContainer publicHealthCaseFlowContainer = new PublicHealthCaseFlowContainer();
        var edxLabInfoDto = new EdxLabInformationDto();
        var obsDto = new ObservationDto();
        var labResult = new LabResultProxyContainer();

        edxLabInfoDto.setLabIsUpdateDRSA(false);
        edxLabInfoDto.setLabIsCreate(true);
        edxLabInfoDto.setWdsReports(new ArrayList<>());
        var pageAct = new PamProxyContainer();
        var phcConn = new PublicHealthCaseContainer();
        var phcDt = new PublicHealthCaseDto();
        phcConn.setThePublicHealthCaseDto(phcDt);
        pageAct.setPublicHealthCaseContainer(phcConn);
        edxLabInfoDto.setPamContainer(pageAct);
        edxLabInfoDto.setAction(DecisionSupportConstants.CREATE_INVESTIGATION_VALUE);
        edxLabInfoDto.setAssociatedPublicHealthCaseUid(12L);

        obsDto.setJurisdictionCd("A");
        obsDto.setProgAreaCd("A");
        obsDto.setObservationUid(11L);

        publicHealthCaseFlowContainer.setEdxLabInformationDto(edxLabInfoDto);
        publicHealthCaseFlowContainer.setObservationDto(obsDto);
        publicHealthCaseFlowContainer.setLabResultProxyContainer(labResult);
        publicHealthCaseFlowContainer.setNbsInterfaceId(10);

        var nbs = new NbsInterfaceModel();
        nbs.setNbsInterfaceUid(10);
        when(nbsInterfaceRepository.findByNbsInterfaceUid(any())).thenReturn(Optional.of(nbs));

        when(labService.handlePamContainer(any(), any())).thenReturn(12L);
        publicHealthCaseFlowContainer.setNbsInterfaceModel(nbs);
        managerService.initiatingLabProcessing(publicHealthCaseFlowContainer);
        verify(nbsInterfaceRepository, times(1)).save(any());
    }

    @Test
    void initiatingLabProcessing_Test_Investigation_Notification_Page() throws DataProcessingException {
        PublicHealthCaseFlowContainer publicHealthCaseFlowContainer = new PublicHealthCaseFlowContainer();
        var edxLabInfoDto = new EdxLabInformationDto();
        var obsDto = new ObservationDto();
        var labResult = new LabResultProxyContainer();

        edxLabInfoDto.setLabIsUpdateDRSA(false);
        edxLabInfoDto.setLabIsCreate(true);
        edxLabInfoDto.setWdsReports(new ArrayList<>());
        var pageAct = new PageActProxyContainer();
        var phcConn = new PublicHealthCaseContainer();
        var phcDt = new PublicHealthCaseDto();
        phcConn.setThePublicHealthCaseDto(phcDt);
        pageAct.setPublicHealthCaseContainer(phcConn);
        edxLabInfoDto.setPageActContainer(pageAct);
        edxLabInfoDto.setAction(DecisionSupportConstants.CREATE_INVESTIGATION_WITH_NND_VALUE);
        edxLabInfoDto.setAssociatedPublicHealthCaseUid(12L);

        EDXActivityLogDto edx = new EDXActivityLogDto();
        edx.setEDXActivityLogDTWithVocabDetails(new ArrayList<>());
        edxLabInfoDto.setEdxActivityLogDto(edx);

        obsDto.setJurisdictionCd("A");
        obsDto.setProgAreaCd("A");
        obsDto.setObservationUid(11L);

        publicHealthCaseFlowContainer.setEdxLabInformationDto(edxLabInfoDto);
        publicHealthCaseFlowContainer.setObservationDto(obsDto);
        publicHealthCaseFlowContainer.setLabResultProxyContainer(labResult);
        publicHealthCaseFlowContainer.setNbsInterfaceId(10);

        var nbs = new NbsInterfaceModel();
        nbs.setNbsInterfaceUid(10);
        when(nbsInterfaceRepository.findByNbsInterfaceUid(any())).thenReturn(Optional.of(nbs));
        when(labService.handlePageContainer(any(), any())).thenReturn(12L);

        EDXActivityDetailLogDto detail = new EDXActivityDetailLogDto();
        detail.setLogType(null);

        publicHealthCaseFlowContainer.setNbsInterfaceModel(nbs);


        managerService.initiatingLabProcessing(publicHealthCaseFlowContainer);
        verify(nbsInterfaceRepository, times(1)).save(any());
    }


    @Test
    void initiatingLabProcessing_Error_1() throws DataProcessingException {
        PublicHealthCaseFlowContainer publicHealthCaseFlowContainer = new PublicHealthCaseFlowContainer();
        var edxLabInfoDto = new EdxLabInformationDto();
        var obsDto = new ObservationDto();
        var labResult = new LabResultProxyContainer();

        edxLabInfoDto.setLabIsUpdateDRSA(false);
        edxLabInfoDto.setLabIsCreate(true);
        edxLabInfoDto.setWdsReports(new ArrayList<>());
        var pageAct = new PageActProxyContainer();
        var phcConn = new PublicHealthCaseContainer();
        var phcDt = new PublicHealthCaseDto();
        phcConn.setThePublicHealthCaseDto(phcDt);
        phcConn.setErrorText(ERROR);
        pageAct.setPublicHealthCaseContainer(phcConn);
        edxLabInfoDto.setPageActContainer(pageAct);
        edxLabInfoDto.setAction(DecisionSupportConstants.CREATE_INVESTIGATION_WITH_NND_VALUE);
        edxLabInfoDto.setAssociatedPublicHealthCaseUid(12L);

        EDXActivityLogDto edx = new EDXActivityLogDto();
        edx.setEDXActivityLogDTWithVocabDetails(null);
        edxLabInfoDto.setEdxActivityLogDto(edx);

        obsDto.setJurisdictionCd("A");
        obsDto.setProgAreaCd("A");
        obsDto.setObservationUid(11L);

        publicHealthCaseFlowContainer.setEdxLabInformationDto(edxLabInfoDto);
        publicHealthCaseFlowContainer.setObservationDto(obsDto);
        publicHealthCaseFlowContainer.setLabResultProxyContainer(labResult);
        publicHealthCaseFlowContainer.setNbsInterfaceId(10);

        var nbs = new NbsInterfaceModel();
        nbs.setNbsInterfaceUid(10);
        when(nbsInterfaceRepository.findByNbsInterfaceUid(any())).thenReturn(Optional.of(nbs));
        when(pageService.setPageProxyWithAutoAssoc(any(), any(),
                any(), any(), any())).thenReturn(12L);

        EDXActivityDetailLogDto detail = new EDXActivityDetailLogDto();
        detail.setLogType(null);
        when(investigationNotificationService.sendNotification(any(), any()))
                .thenReturn(detail);


        assertThrows(DataProcessingException.class, () ->managerService.initiatingLabProcessing(publicHealthCaseFlowContainer));
    }

    @Test
    void initiatingLabProcessing_Error_2() throws DataProcessingException {
        PublicHealthCaseFlowContainer publicHealthCaseFlowContainer = new PublicHealthCaseFlowContainer();
        var edxLabInfoDto = new EdxLabInformationDto();
        var obsDto = new ObservationDto();
        var labResult = new LabResultProxyContainer();

        edxLabInfoDto.setLabIsUpdateDRSA(false);
        edxLabInfoDto.setLabIsCreate(true);
        edxLabInfoDto.setWdsReports(new ArrayList<>());
        var pageAct = new PamProxyContainer();
        var phcConn = new PublicHealthCaseContainer();
        var phcDt = new PublicHealthCaseDto();
        phcConn.setThePublicHealthCaseDto(phcDt);
        phcConn.setErrorText(ERROR);
        pageAct.setPublicHealthCaseContainer(phcConn);
        edxLabInfoDto.setPamContainer(pageAct);
        edxLabInfoDto.setInvestigationSuccessfullyCreated(true);
        edxLabInfoDto.setAction(DecisionSupportConstants.CREATE_INVESTIGATION_WITH_NND_VALUE);
        edxLabInfoDto.setAssociatedPublicHealthCaseUid(12L);

        EDXActivityLogDto edx = new EDXActivityLogDto();
        edx.setEDXActivityLogDTWithVocabDetails(null);
        edxLabInfoDto.setEdxActivityLogDto(edx);

        obsDto.setJurisdictionCd("A");
        obsDto.setProgAreaCd("A");
        obsDto.setObservationUid(11L);

        publicHealthCaseFlowContainer.setEdxLabInformationDto(edxLabInfoDto);
        publicHealthCaseFlowContainer.setObservationDto(obsDto);
        publicHealthCaseFlowContainer.setLabResultProxyContainer(labResult);
        publicHealthCaseFlowContainer.setNbsInterfaceId(10);

        var nbs = new NbsInterfaceModel();
        nbs.setNbsInterfaceUid(10);
        when(nbsInterfaceRepository.findByNbsInterfaceUid(any())).thenReturn(Optional.of(nbs));
        when(pageService.setPageProxyWithAutoAssoc(any(), any(),
                any(), any(), any())).thenReturn(12L);

        EDXActivityDetailLogDto detail = new EDXActivityDetailLogDto();
        detail.setLogType(null);
        when(investigationNotificationService.sendNotification(any(), any()))
                .thenReturn(detail);

        assertThrows(DataProcessingException.class, () ->managerService.initiatingLabProcessing(publicHealthCaseFlowContainer));
    }


    @Test
    void initiatingLabProcessing_Error_3() throws DataProcessingException {
        PublicHealthCaseFlowContainer publicHealthCaseFlowContainer = new PublicHealthCaseFlowContainer();
        var edxLabInfoDto = new EdxLabInformationDto();
        var obsDto = new ObservationDto();
        var labResult = new LabResultProxyContainer();

        edxLabInfoDto.setLabIsUpdateDRSA(false);
        edxLabInfoDto.setLabIsCreate(true);
        edxLabInfoDto.setWdsReports(new ArrayList<>());
        var pageAct = new PageActProxyContainer();
        var phcConn = new PublicHealthCaseContainer();
        var phcDt = new PublicHealthCaseDto();
        phcConn.setThePublicHealthCaseDto(phcDt);
        pageAct.setPublicHealthCaseContainer(phcConn);
        edxLabInfoDto.setPageActContainer(pageAct);
        edxLabInfoDto.setAction(DecisionSupportConstants.CREATE_INVESTIGATION_WITH_NND_VALUE);
        edxLabInfoDto.setAssociatedPublicHealthCaseUid(12L);

        EDXActivityLogDto edx = new EDXActivityLogDto();
        var detailLogCol = new ArrayList<EDXActivityDetailLogDto>();
        var detailLog = new EDXActivityDetailLogDto();
        detailLog.setLogType(EdxRuleAlgorothmManagerDto.STATUS_VAL.Failure.name());
        detailLog.setComment(EdxELRConstant.MISSING_NOTF_REQ_FIELDS);
        detailLogCol.add(detailLog);
        edx.setEDXActivityLogDTWithVocabDetails(detailLogCol);


        edxLabInfoDto.setEdxActivityLogDto(edx);

        obsDto.setJurisdictionCd("A");
        obsDto.setProgAreaCd("A");
        obsDto.setObservationUid(11L);

        publicHealthCaseFlowContainer.setEdxLabInformationDto(edxLabInfoDto);
        publicHealthCaseFlowContainer.setObservationDto(obsDto);
        publicHealthCaseFlowContainer.setLabResultProxyContainer(labResult);
        publicHealthCaseFlowContainer.setNbsInterfaceId(10);

        var nbs = new NbsInterfaceModel();
        nbs.setNbsInterfaceUid(10);
        when(nbsInterfaceRepository.findByNbsInterfaceUid(any())).thenReturn(Optional.of(nbs));
        when(pageService.setPageProxyWithAutoAssoc(any(), any(),
                any(), any(), any())).thenReturn(12L);


        when(investigationNotificationService.sendNotification(any(), any()))
                .thenReturn(detailLog);

        assertThrows(NullPointerException.class, () ->managerService.initiatingLabProcessing(publicHealthCaseFlowContainer));

    }

    @Test
    void initiatingLabProcessing_Error_4() throws DataProcessingException {
        PublicHealthCaseFlowContainer publicHealthCaseFlowContainer = new PublicHealthCaseFlowContainer();
        var edxLabInfoDto = new EdxLabInformationDto();
        var obsDto = new ObservationDto();
        var labResult = new LabResultProxyContainer();

        edxLabInfoDto.setLabIsUpdateDRSA(false);
        edxLabInfoDto.setLabIsCreate(true);
        edxLabInfoDto.setWdsReports(new ArrayList<>());
        var pageAct = new PageActProxyContainer();
        var phcConn = new PublicHealthCaseContainer();
        var phcDt = new PublicHealthCaseDto();
        phcConn.setThePublicHealthCaseDto(phcDt);
        pageAct.setPublicHealthCaseContainer(phcConn);
        edxLabInfoDto.setPageActContainer(pageAct);
        edxLabInfoDto.setAction(DecisionSupportConstants.CREATE_INVESTIGATION_WITH_NND_VALUE);
        edxLabInfoDto.setAssociatedPublicHealthCaseUid(12L);

        EDXActivityLogDto edx = new EDXActivityLogDto();
        var detailLogCol = new ArrayList<EDXActivityDetailLogDto>();
        var detailLog = new EDXActivityDetailLogDto();
        detailLog.setLogType(EdxRuleAlgorothmManagerDto.STATUS_VAL.Failure.name());
        detailLog.setComment(EdxELRConstant.MISSING_NOTF_REQ_FIELDS);
        detailLogCol.add(detailLog);
        edx.setEDXActivityLogDTWithVocabDetails(detailLogCol);


        edxLabInfoDto.setEdxActivityLogDto(edx);

        obsDto.setJurisdictionCd("A");
        obsDto.setProgAreaCd("A");
        obsDto.setObservationUid(11L);

        publicHealthCaseFlowContainer.setEdxLabInformationDto(edxLabInfoDto);
        publicHealthCaseFlowContainer.setObservationDto(obsDto);
        publicHealthCaseFlowContainer.setLabResultProxyContainer(labResult);
        publicHealthCaseFlowContainer.setNbsInterfaceId(10);

        var nbs = new NbsInterfaceModel();
        nbs.setNbsInterfaceUid(10);
        when(nbsInterfaceRepository.findByNbsInterfaceUid(any())).thenReturn(Optional.of(nbs));
        when(pageService.setPageProxyWithAutoAssoc(any(), any(),
                any(), any(), any())).thenReturn(12L);


        detailLog.setComment(null);
        when(investigationNotificationService.sendNotification(any(), any()))
                .thenReturn(detailLog);

        assertThrows(NullPointerException.class, () ->managerService.initiatingLabProcessing(publicHealthCaseFlowContainer));

    }

    @Test
    void processDistribution_Error_1() throws DataProcessingConsumerException, JAXBException, DataProcessingException, EdxLogException, DataProcessingDBException {
        var test = new TestDataReader();

        NbsInterfaceModel labData = test.readDataFromJsonPath("manager/manager_first_process.json", NbsInterfaceModel.class);



        CompletableFuture<Void> mockedFuture = mock(CompletableFuture.class);
        when(managerCacheService.loadAndInitCachedValueAsync()).thenReturn(mockedFuture);
        when(mockedFuture.join()).thenReturn(null); // You can adjust this to simulate different behaviors

        LabResultProxyContainer labResultProxyContainer = new LabResultProxyContainer();
        when(dataExtractionService.parsingDataToObject(any(), any())).thenReturn(labResultProxyContainer);

        EdxLabInformationDto edxLabInformationDto = new EdxLabInformationDto();
        edxLabInformationDto.setLabIsUpdateDRRQ(true);
        edxLabInformationDto.setLabIsCreate(true);
        edxLabInformationDto.setLabIsCreateSuccess(true);
        edxLabInformationDto.setProgramAreaName(null);
        var edxAcLog = new EDXActivityLogDto();
        edxLabInformationDto.setEdxActivityLogDto(edxAcLog);
        var conn = new PageActProxyContainer();
        edxLabInformationDto.setPageActContainer(conn);
        edxLabInformationDto.setInvestigationMissingFields(true);
        edxLabInformationDto.setReflexResultedTestCdMissing(true);
        when(managerAggregationService.processingObservationMatching(any(), any(), any())).thenReturn(edxLabInformationDto);



        ObservationDto observationDto = new ObservationDto();
        observationDto.setLocalId("LOCAL");
        observationDto.setObservationUid(10L);
        observationDto.setProgAreaCd("A");
        observationDto.setJurisdictionCd("A");

        when(observationService.processingLabResultContainer(any()))  .thenThrow(new DataProcessingException("Invalid XML"));
        when(nbsInterfaceJdbcRepository.getNbsInterfaceByUid(any())).thenReturn(labData);

        managerService.processingELR(123, false);

        verify(kafkaManagerProducer, times(0)).sendDataEdxActivityLog(any());
    }

    @Test
    void processDistribution_Error_2() throws DataProcessingConsumerException, JAXBException, DataProcessingException, EdxLogException, DataProcessingDBException {
        var test = new TestDataReader();

        NbsInterfaceModel labData = test.readDataFromJsonPath("manager/manager_first_process.json", NbsInterfaceModel.class);

        CompletableFuture<Void> mockedFuture = mock(CompletableFuture.class);
        when(managerCacheService.loadAndInitCachedValueAsync()).thenReturn(mockedFuture);
        when(mockedFuture.join()).thenReturn(null); // You can adjust this to simulate different behaviors

        LabResultProxyContainer labResultProxyContainer = new LabResultProxyContainer();
        when(dataExtractionService.parsingDataToObject(any(), any())).thenReturn(labResultProxyContainer);

        EdxLabInformationDto edxLabInformationDto = new EdxLabInformationDto();
        edxLabInformationDto.setLabIsUpdateDRRQ(true);
        edxLabInformationDto.setLabIsCreate(true);
        edxLabInformationDto.setLabIsCreateSuccess(true);
        edxLabInformationDto.setProgramAreaName(null);
        var edxAcLog = new EDXActivityLogDto();
        edxLabInformationDto.setEdxActivityLogDto(edxAcLog);
        var conn = new PageActProxyContainer();
        edxLabInformationDto.setPageActContainer(conn);
        edxLabInformationDto.setInvestigationMissingFields(false);
        edxLabInformationDto.setReflexResultedTestCdMissing(true);
        when(managerAggregationService.processingObservationMatching(any(), any(), any())).thenReturn(edxLabInformationDto);



        ObservationDto observationDto = new ObservationDto();
        observationDto.setLocalId("LOCAL");
        observationDto.setObservationUid(10L);
        observationDto.setProgAreaCd("A");
        observationDto.setJurisdictionCd("A");

        when(observationService.processingLabResultContainer(any()))  .thenThrow(new DataProcessingException(EdxELRConstant.SQL_FIELD_TRUNCATION_ERROR_MSG));
        when(nbsInterfaceJdbcRepository.getNbsInterfaceByUid(any())).thenReturn(labData);

        managerService.processingELR(123, false);

        verify(kafkaManagerProducer, times(0)).sendDataEdxActivityLog(any());
    }

    @Test
    void processDistribution_Error_3() throws DataProcessingConsumerException, JAXBException, DataProcessingException, EdxLogException, DataProcessingDBException {
        var test = new TestDataReader();

        NbsInterfaceModel labData = test.readDataFromJsonPath("manager/manager_first_process.json", NbsInterfaceModel.class);

        CompletableFuture<Void> mockedFuture = mock(CompletableFuture.class);
        when(managerCacheService.loadAndInitCachedValueAsync()).thenReturn(mockedFuture);
        when(mockedFuture.join()).thenReturn(null); // You can adjust this to simulate different behaviors

        LabResultProxyContainer labResultProxyContainer = new LabResultProxyContainer();
        when(dataExtractionService.parsingDataToObject(any(), any())).thenReturn(labResultProxyContainer);

        EdxLabInformationDto edxLabInformationDto = new EdxLabInformationDto();
        edxLabInformationDto.setLabIsUpdateDRRQ(true);
        edxLabInformationDto.setLabIsCreate(true);
        edxLabInformationDto.setLabIsCreateSuccess(true);
        edxLabInformationDto.setProgramAreaName(null);
        var edxAcLog = new EDXActivityLogDto();
        edxLabInformationDto.setEdxActivityLogDto(edxAcLog);
        var conn = new PageActProxyContainer();
        edxLabInformationDto.setPageActContainer(conn);
        edxLabInformationDto.setInvestigationMissingFields(false);
        edxLabInformationDto.setReflexResultedTestCdMissing(true);
        edxLabInformationDto.setInvestigationSuccessfullyCreated(true);
        when(managerAggregationService.processingObservationMatching(any(), any(), any())).thenReturn(edxLabInformationDto);



        ObservationDto observationDto = new ObservationDto();
        observationDto.setLocalId("LOCAL");
        observationDto.setObservationUid(10L);
        observationDto.setProgAreaCd("A");
        observationDto.setJurisdictionCd("A");

        when(observationService.processingLabResultContainer(any()))  .thenThrow(new DataProcessingException(EdxELRConstant.SQL_FIELD_TRUNCATION_ERROR_MSG));
        when(nbsInterfaceJdbcRepository.getNbsInterfaceByUid(any())).thenReturn(labData);

        managerService.processingELR(123, false);

        verify(kafkaManagerProducer, times(0)).sendDataEdxActivityLog(any());
    }

    @Test
    void processDistribution_Error_4() throws DataProcessingConsumerException, JAXBException, DataProcessingException, EdxLogException, DataProcessingDBException {
        var test = new TestDataReader();

        NbsInterfaceModel labData = test.readDataFromJsonPath("manager/manager_first_process.json", NbsInterfaceModel.class);


        CompletableFuture<Void> mockedFuture = mock(CompletableFuture.class);
        when(managerCacheService.loadAndInitCachedValueAsync()).thenReturn(mockedFuture);
        when(mockedFuture.join()).thenReturn(null); // You can adjust this to simulate different behaviors

        LabResultProxyContainer labResultProxyContainer = new LabResultProxyContainer();
        when(dataExtractionService.parsingDataToObject(any(), any())).thenReturn(labResultProxyContainer);

        EdxLabInformationDto edxLabInformationDto = new EdxLabInformationDto();
        edxLabInformationDto.setLabIsUpdateDRRQ(true);
        edxLabInformationDto.setLabIsCreate(true);
        edxLabInformationDto.setLabIsCreateSuccess(true);
        edxLabInformationDto.setProgramAreaName(null);
        var edxAcLog = new EDXActivityLogDto();
        edxLabInformationDto.setEdxActivityLogDto(edxAcLog);
        var conn = new PageActProxyContainer();
        edxLabInformationDto.setPageActContainer(conn);
        edxLabInformationDto.setInvestigationMissingFields(false);
        edxLabInformationDto.setReflexResultedTestCdMissing(true);
        edxLabInformationDto.setInvestigationSuccessfullyCreated(true);
        edxLabInformationDto.setNotificationMissingFields(true);
        when(managerAggregationService.processingObservationMatching(any(), any(), any())).thenReturn(edxLabInformationDto);



        ObservationDto observationDto = new ObservationDto();
        observationDto.setLocalId("LOCAL");
        observationDto.setObservationUid(10L);
        observationDto.setProgAreaCd("A");
        observationDto.setJurisdictionCd("A");

        when(observationService.processingLabResultContainer(any()))  .thenThrow(new DataProcessingException(EdxELRConstant.SQL_FIELD_TRUNCATION_ERROR_MSG));

        when(nbsInterfaceJdbcRepository.getNbsInterfaceByUid(any())).thenReturn(labData);
        managerService.processingELR(123, false);

        verify(kafkaManagerProducer, times(0)).sendDataEdxActivityLog(any());
    }

    @Test
    void processDistribution_Error_5() throws DataProcessingConsumerException, JAXBException, DataProcessingException, EdxLogException, DataProcessingDBException {
        var test = new TestDataReader();

        NbsInterfaceModel labData = test.readDataFromJsonPath("manager/manager_first_process.json", NbsInterfaceModel.class);

        CompletableFuture<Void> mockedFuture = mock(CompletableFuture.class);
        when(managerCacheService.loadAndInitCachedValueAsync()).thenReturn(mockedFuture);
        when(mockedFuture.join()).thenReturn(null); // You can adjust this to simulate different behaviors

        LabResultProxyContainer labResultProxyContainer = new LabResultProxyContainer();
        when(dataExtractionService.parsingDataToObject(any(), any())).thenReturn(labResultProxyContainer);

        EdxLabInformationDto edxLabInformationDto = new EdxLabInformationDto();
        edxLabInformationDto.setLabIsUpdateDRRQ(true);
        edxLabInformationDto.setLabIsCreate(true);
        edxLabInformationDto.setLabIsCreateSuccess(true);
        edxLabInformationDto.setProgramAreaName(null);
        var edxAcLog = new EDXActivityLogDto();
        edxLabInformationDto.setEdxActivityLogDto(edxAcLog);
        edxLabInformationDto.setPageActContainer(null);
        edxLabInformationDto.setInvestigationMissingFields(false);
        edxLabInformationDto.setReflexResultedTestCdMissing(true);
        edxLabInformationDto.setInvestigationSuccessfullyCreated(true);
        edxLabInformationDto.setNotificationMissingFields(true);
        edxLabInformationDto.setErrorText(null);
        when(managerAggregationService.processingObservationMatching(any(), any(), any())).thenReturn(edxLabInformationDto);



        ObservationDto observationDto = new ObservationDto();
        observationDto.setLocalId("LOCAL");
        observationDto.setObservationUid(10L);
        observationDto.setProgAreaCd("A");
        observationDto.setJurisdictionCd("A");

        when(observationService.processingLabResultContainer(any()))  .thenThrow(new DataProcessingException(EdxELRConstant.SQL_FIELD_TRUNCATION_ERROR_MSG));
        when(nbsInterfaceJdbcRepository.getNbsInterfaceByUid(any())).thenReturn(labData);

        managerService.processingELR(123, false);

        verify(kafkaManagerProducer, times(0)).sendDataEdxActivityLog(any());
    }

    @Test
    void processDistribution_Error_6() throws DataProcessingConsumerException, JAXBException, DataProcessingException, EdxLogException, DataProcessingDBException {
        var test = new TestDataReader();

        NbsInterfaceModel labData = test.readDataFromJsonPath("manager/manager_first_process.json", NbsInterfaceModel.class);

        CompletableFuture<Void> mockedFuture = mock(CompletableFuture.class);
        when(managerCacheService.loadAndInitCachedValueAsync()).thenReturn(mockedFuture);
        when(mockedFuture.join()).thenReturn(null); // You can adjust this to simulate different behaviors

        LabResultProxyContainer labResultProxyContainer = new LabResultProxyContainer();
        when(dataExtractionService.parsingDataToObject(any(), any())).thenReturn(labResultProxyContainer);

        EdxLabInformationDto edxLabInformationDto = new EdxLabInformationDto();
        edxLabInformationDto.setLabIsUpdateDRRQ(true);
        edxLabInformationDto.setLabIsCreate(true);
        edxLabInformationDto.setLabIsCreateSuccess(true);
        edxLabInformationDto.setProgramAreaName(null);
        var edxAcLog = new EDXActivityLogDto();
        edxLabInformationDto.setEdxActivityLogDto(edxAcLog);
        edxLabInformationDto.setPageActContainer(null);
        edxLabInformationDto.setInvestigationMissingFields(false);
        edxLabInformationDto.setReflexResultedTestCdMissing(true);
        edxLabInformationDto.setInvestigationSuccessfullyCreated(true);
        edxLabInformationDto.setNotificationMissingFields(true);
        edxLabInformationDto.setErrorText(null);
        when(managerAggregationService.processingObservationMatching(any(), any(), any())).thenReturn(edxLabInformationDto);



        ObservationDto observationDto = new ObservationDto();
        observationDto.setLocalId("LOCAL");
        observationDto.setObservationUid(10L);
        observationDto.setProgAreaCd("A");
        observationDto.setJurisdictionCd("A");

        when(observationService.processingLabResultContainer(any()))  .thenThrow(new DataProcessingException(EdxELRConstant.DATE_VALIDATION));
        when(nbsInterfaceJdbcRepository.getNbsInterfaceByUid(any())).thenReturn(labData);

        managerService.processingELR(123, false);

        verify(kafkaManagerProducer, times(0)).sendDataEdxActivityLog(any());
    }

    @Test
    void processDistribution_Error_7() throws DataProcessingConsumerException, JAXBException, DataProcessingException, EdxLogException, DataProcessingDBException {
        var test = new TestDataReader();

        NbsInterfaceModel labData = test.readDataFromJsonPath("manager/manager_first_process.json", NbsInterfaceModel.class);

        CompletableFuture<Void> mockedFuture = mock(CompletableFuture.class);
        when(managerCacheService.loadAndInitCachedValueAsync()).thenReturn(mockedFuture);
        when(mockedFuture.join()).thenReturn(null); // You can adjust this to simulate different behaviors

        LabResultProxyContainer labResultProxyContainer = new LabResultProxyContainer();
        when(dataExtractionService.parsingDataToObject(any(), any())).thenReturn(labResultProxyContainer);

        EdxLabInformationDto edxLabInformationDto = new EdxLabInformationDto();
        edxLabInformationDto.setLabIsUpdateDRRQ(true);
        edxLabInformationDto.setLabIsCreate(true);
        edxLabInformationDto.setLabIsCreateSuccess(true);
        edxLabInformationDto.setProgramAreaName(null);
        var edxAcLog = new EDXActivityLogDto();
        edxLabInformationDto.setEdxActivityLogDto(edxAcLog);
        edxLabInformationDto.setPageActContainer(null);
        edxLabInformationDto.setInvestigationMissingFields(false);
        edxLabInformationDto.setReflexResultedTestCdMissing(true);
        edxLabInformationDto.setInvestigationSuccessfullyCreated(true);
        edxLabInformationDto.setNotificationMissingFields(true);
        edxLabInformationDto.setErrorText(null);
        when(managerAggregationService.processingObservationMatching(any(), any(), any())).thenReturn(edxLabInformationDto);



        ObservationDto observationDto = new ObservationDto();
        observationDto.setLocalId("LOCAL");
        observationDto.setObservationUid(10L);
        observationDto.setProgAreaCd("A");
        observationDto.setJurisdictionCd("A");

        when(observationService.processingLabResultContainer(any()))  .thenThrow(new DataProcessingException("BLAH"));

        when(nbsInterfaceJdbcRepository.getNbsInterfaceByUid(any())).thenReturn(labData);
        managerService.processingELR(123, false);

        verify(kafkaManagerProducer, times(0)).sendDataEdxActivityLog(any());
    }

    @Test
    void initiateStep1KafkaFailed() throws EdxLogException, DataProcessingDBException {
        Integer nbsId = 1;

        var nbs = new NbsInterfaceModel();
        nbs.setNbsInterfaceUid(nbsId);
        nbs.setRecordStatusCd("SUCCESS");
        when(nbsInterfaceRepository.findByNbsInterfaceUid(nbsId)).thenReturn(Optional.ofNullable(nbs));
        managerService.processingELR(nbsId, false);

        verify(kafkaManagerProducer, times(0)).sendDataPhc(any());

    }

    @Test
    void initiateStep1KafkaFailed_ResetCache() throws EdxLogException, DataProcessingDBException {
        Integer nbsId = 1;
        PropertyUtilCache.kafkaFailedCheckStep1 = 100000;
        var nbs = new NbsInterfaceModel();
        nbs.setNbsInterfaceUid(nbsId);
        nbs.setRecordStatusCd("SUCCESS");
        when(nbsInterfaceRepository.findByNbsInterfaceUid(nbsId)).thenReturn(Optional.ofNullable(nbs));
        managerService.processingELR(nbsId, false);

        verify(kafkaManagerProducer, times(0)).sendDataPhc(any());
        PropertyUtilCache.kafkaFailedCheckStep1 = 0;
    }

    @Test
    void initiateStep2KafkaFailed()  {
        Integer nbsId = 1;

        var nbs = new NbsInterfaceModel();
        var phc = new PublicHealthCaseFlowContainer();
        phc.setNbsInterfaceId(nbsId);
        nbs.setNbsInterfaceUid(nbsId);
        nbs.setRecordStatusCd(DpConstant.DP_SUCCESS_STEP_2);
        when(nbsInterfaceRepository.findByNbsInterfaceUid(nbsId)).thenReturn(Optional.ofNullable(nbs));
        assertThrows(NullPointerException.class, () -> managerService.initiatingInvestigationAndPublicHealthCase(phc));

    }

    @Test
    void initiateStep2KafkaFailed_ResetCache()  {
        Integer nbsId = 1;
        PropertyUtilCache.kafkaFailedCheckStep2 = 100000;
        var nbs = new NbsInterfaceModel();
        var phc = new PublicHealthCaseFlowContainer();
        phc.setNbsInterfaceId(nbsId);
        nbs.setNbsInterfaceUid(nbsId);
        nbs.setRecordStatusCd(DpConstant.DP_SUCCESS_STEP_2);
        when(nbsInterfaceRepository.findByNbsInterfaceUid(nbsId)).thenReturn(Optional.ofNullable(nbs));


        assertThrows(NullPointerException.class, () ->managerService.initiatingInvestigationAndPublicHealthCase(phc));
        PropertyUtilCache.kafkaFailedCheckStep2 = 0;
    }

    @Test
    void initiateStep3KafkaFailed()  {
        Integer nbsId = 1;

        var nbs = new NbsInterfaceModel();
        var phc = new PublicHealthCaseFlowContainer();
        phc.setNbsInterfaceId(nbsId);
        nbs.setNbsInterfaceUid(nbsId);
        nbs.setRecordStatusCd(DpConstant.DP_SUCCESS_STEP_3);
        when(nbsInterfaceRepository.findByNbsInterfaceUid(nbsId)).thenReturn(Optional.ofNullable(nbs));

        assertThrows(NullPointerException.class, () ->  managerService.initiatingLabProcessing(phc));

    }


    @Test
    void initiateStep3KafkaFailed_ResetCache()  {
        Integer nbsId = 1;
        PropertyUtilCache.kafkaFailedCheckStep3 = 100000;
        var nbs = new NbsInterfaceModel();
        var phc = new PublicHealthCaseFlowContainer();
        phc.setNbsInterfaceId(nbsId);
        nbs.setNbsInterfaceUid(nbsId);
        nbs.setRecordStatusCd(DpConstant.DP_SUCCESS_STEP_3);
        when(nbsInterfaceRepository.findByNbsInterfaceUid(nbsId)).thenReturn(Optional.ofNullable(nbs));

        assertThrows(NullPointerException.class, () -> managerService.initiatingLabProcessing(phc));

        PropertyUtilCache.kafkaFailedCheckStep3 = 0;
    }

    @Test
    void testHandlingWdsAndLab_CannotAcquireLockException() throws Exception {
        PublicHealthCaseFlowContainer container = mock(PublicHealthCaseFlowContainer.class);
        EdxLabInformationDto edxDto = mock(EdxLabInformationDto.class);
        NbsInterfaceModel model = mock(NbsInterfaceModel.class);

        when(container.getEdxLabInformationDto()).thenReturn(edxDto);
        when(container.getNbsInterfaceModel()).thenReturn(model);

        // Using spy to override protected method
        doThrow(new CannotAcquireLockException("Lock error"))
                .when(managerService).initiatingInvestigationAndPublicHealthCase(container);

        assertDoesNotThrow(() -> {
            try {
                managerService.handlingWdsAndLab(container, false);
            } catch (DataProcessingException | DataProcessingDBException | EdxLogException ignored) {
                //IGNORE THIS
            }
        });
    }

    @Test
    void testHandlingWdsAndLab_QueryTimeoutException() throws DataProcessingException, DataProcessingDBException, EdxLogException {
        PublicHealthCaseFlowContainer container = mock(PublicHealthCaseFlowContainer.class);
        EdxLabInformationDto edxDto = mock(EdxLabInformationDto.class);
        NbsInterfaceModel model = mock(NbsInterfaceModel.class);

        when(container.getEdxLabInformationDto()).thenReturn(edxDto);
        when(container.getNbsInterfaceModel()).thenReturn(model);

        doThrow(new QueryTimeoutException("Timeout"))
                .when(managerService).initiatingInvestigationAndPublicHealthCase(container);

        managerService.handlingWdsAndLab(container, false);
    }

    @SuppressWarnings("java:S2699")
    @Test
    void testHandlingWdsAndLab_GenericException() throws DataProcessingException {
        PublicHealthCaseFlowContainer container = mock(PublicHealthCaseFlowContainer.class);
        EdxLabInformationDto edxDto = mock(EdxLabInformationDto.class);
        NbsInterfaceModel model = mock(NbsInterfaceModel.class);

        when(container.getEdxLabInformationDto()).thenReturn(edxDto);
        when(container.getNbsInterfaceModel()).thenReturn(model);

        doThrow(new RuntimeException("Unexpected error"))
                .when(managerService).initiatingInvestigationAndPublicHealthCase(container);


    }

    @Test
    void processingELR_shouldThrowDataProcessingDBException_onQueryTimeout() throws DataProcessingConsumerException, JAXBException, DataProcessingException, EdxLogException {
        Integer testId = 456;
        NbsInterfaceModel model = new NbsInterfaceModel();
        model.setNbsInterfaceUid(testId);

        when(nbsInterfaceJdbcRepository.getNbsInterfaceByUid(testId)).thenReturn(model);
        when(dataExtractionService.parsingDataToObject(any(), any())).thenThrow(new QueryTimeoutException("Timeout"));

        var ex = managerService.processingELR(testId, false);
        assertNull( ex);
    }

    @Test
    void processingELR_shouldThrowDataProcessingDBException_onSqlException() throws DataProcessingConsumerException, JAXBException, DataProcessingException, EdxLogException {
        Integer testId = 789;
        NbsInterfaceModel model = new NbsInterfaceModel();
        model.setNbsInterfaceUid(testId);

        SQLException sqlException = new SQLException("SQL Error");
        RuntimeException wrapped = new RuntimeException("Wrapper", sqlException);

        when(nbsInterfaceJdbcRepository.getNbsInterfaceByUid(testId)).thenReturn(model);
        when(dataExtractionService.parsingDataToObject(any(), any())).thenThrow(wrapped);



        var ex = managerService.processingELR(testId, false);
        assertNull(ex);
    }

    @Test
    void handleProcessingElrException_whenCannotAcquireLock_setsDltLockError() throws Exception {
        AtomicBoolean dltLock = new AtomicBoolean(false);
        AtomicBoolean nonDlt = new AtomicBoolean(false);
        AtomicBoolean inteDlt = new AtomicBoolean(false);

        AtomicReference<String> msg = new AtomicReference<>("");

        Exception ex = new CannotAcquireLockException("lock issue");

        managerService.handleProcessingElrException(ex, new EdxLabInformationDto(), new NbsInterfaceModel(), dltLock, nonDlt, inteDlt, msg);

        assertTrue(dltLock.get());
        assertFalse(nonDlt.get());
        assertEquals("", msg.get());
    }

    @Test
    void handleProcessingElrException_whenQueryTimeout_throwsDataProcessingDBException() {
        managerService.handleProcessingElrException(
                new QueryTimeoutException("timeout"),
                new EdxLabInformationDto(),
                new NbsInterfaceModel(),
                new AtomicBoolean(),
                new AtomicBoolean(),
                new AtomicBoolean(),
                new AtomicReference<>()
        );
    }

    @Test
    void handleProcessingElrException_whenTransientDataAccess_throwsDataProcessingDBException() {
        managerService.handleProcessingElrException(
                new TransientDataAccessException("transient") {},
                new EdxLabInformationDto(),
                new NbsInterfaceModel(),
                new AtomicBoolean(),
                new AtomicBoolean(),
                new AtomicBoolean(),
                new AtomicReference<>()
        );
    }

    @Test
    void handleProcessingElrException_whenDataAccess_throwsDataProcessingDBException() {
        managerService.handleProcessingElrException(
                new DataAccessException("data") {},
                new EdxLabInformationDto(),
                new NbsInterfaceModel(),
                new AtomicBoolean(),
                new AtomicBoolean(),
                new AtomicBoolean(),
                new AtomicReference<>()
        );
    }

    @Test
    void handleProcessingElrException_whenSqlRootCause_throwsDataProcessingDBException() {
        SQLException sqlCause = new SQLException("SQL error");
        RuntimeException wrapper = new RuntimeException("wrapper", sqlCause);

        managerService.handleProcessingElrException(
                wrapper,
                new EdxLabInformationDto(),
                new NbsInterfaceModel(),
                new AtomicBoolean(),
                new AtomicBoolean(),
                new AtomicBoolean(),
                new AtomicReference<>()
        );
    }


    @Test
    void finalizeProcessingElr_whenDltLockError_shouldPersistAndComposeKafkaEvent() throws Exception {
        int interfaceId = 111;
        Exception ex = new Exception("lock");
        EdxLabInformationDto dto = new EdxLabInformationDto();
        dto.setEdxActivityLogDto(new EDXActivityLogDto());
        NbsInterfaceModel model = new NbsInterfaceModel();

        managerService.finalizeProcessingElr(
                true, false, false,ex, interfaceId, "msg", model, dto, false
        );

        verify(edxLogService).updateActivityLogDT(model, dto);
        verify(edxLogService).addActivityDetailLogs(dto, "msg");
        verify(edxLogService).saveEdxActivityLogs(dto.getEdxActivityLogDto());
    }

    @Test
    void finalizeProcessingElr_whenNonDltError_shouldPersistWithOtherError() throws Exception {
        int interfaceId = 222;
        Exception ex = new Exception("non-dlt");
        EdxLabInformationDto dto = new EdxLabInformationDto();
        dto.setEdxActivityLogDto(new EDXActivityLogDto());
        NbsInterfaceModel model = new NbsInterfaceModel();

        managerService.finalizeProcessingElr(
                false, true, false, ex, interfaceId, "some-details", model, dto, false
        );

        verify(edxLogService).updateActivityLogDT(model, dto);
        verify(edxLogService).addActivityDetailLogs(dto, "some-details");
        verify(edxLogService).saveEdxActivityLogs(dto.getEdxActivityLogDto());
    }

    @Test
    void finalizeProcessingElr_whenNoErrors_shouldOnlyLog() throws Exception {
        int interfaceId = 333;
        Exception ex = new Exception("no issue");
        EdxLabInformationDto dto = new EdxLabInformationDto();
        dto.setEdxActivityLogDto(new EDXActivityLogDto());
        NbsInterfaceModel model = new NbsInterfaceModel();

        managerService.finalizeProcessingElr(
                false, false, false, ex, interfaceId, "just logs", model, dto, false
        );

        verify(edxLogService).updateActivityLogDT(model, dto);
        verify(edxLogService).saveEdxActivityLogs(dto.getEdxActivityLogDto());
    }

    @Test
    void enrichProgramAreaAndJurisdiction_shouldNotSetProgramArea_ifCodeIsNull() throws Exception {
        ObservationDto observation = new ObservationDto();
        observation.setProgAreaCd(null);  // null check
        observation.setJurisdictionCd("JURIS");

        EdxLabInformationDto dto = new EdxLabInformationDto();
        dto.setLabIsCreate(true);

        when(cacheApiService.getSrteCacheBool(eq(ObjectName.JURISDICTION_CODES.name()), eq("JURIS"))).thenReturn(false);

        managerService.enrichProgramAreaAndJurisdiction(observation, dto);

        assertNull(dto.getProgramAreaName());
        assertNull(dto.getJurisdictionName());
        assertTrue(dto.isLabIsCreateSuccess());
        assertEquals(EdxELRConstant.ELR_MASTER_LOG_ID_1, dto.getErrorText());
    }

    @Test
    void enrichProgramAreaAndJurisdiction_shouldNotSetProgramArea_ifCacheBoolIsFalse() throws Exception {
        ObservationDto observation = new ObservationDto();
        observation.setProgAreaCd("PROG");
        observation.setJurisdictionCd("JURIS");

        EdxLabInformationDto dto = new EdxLabInformationDto();
        dto.setLabIsCreate(true);

        when(cacheApiService.getSrteCacheBool(eq(ObjectName.PROGRAM_AREA_CODES.name()), eq("PROG"))).thenReturn(false);
        when(cacheApiService.getSrteCacheBool(eq(ObjectName.JURISDICTION_CODES.name()), eq("JURIS"))).thenReturn(false);

        managerService.enrichProgramAreaAndJurisdiction(observation, dto);

        assertNull(dto.getProgramAreaName());
        assertNull(dto.getJurisdictionName());
        assertTrue(dto.isLabIsCreateSuccess());
        assertEquals(EdxELRConstant.ELR_MASTER_LOG_ID_1, dto.getErrorText());
    }

    @Test
    void enrichProgramAreaAndJurisdiction_shouldNotSetJurisdiction_ifCacheBoolIsFalse() throws Exception {
        ObservationDto observation = new ObservationDto();
        observation.setProgAreaCd("PROG");
        observation.setJurisdictionCd("JURIS");

        EdxLabInformationDto dto = new EdxLabInformationDto();
        dto.setLabIsCreate(true);

        when(cacheApiService.getSrteCacheBool(eq(ObjectName.PROGRAM_AREA_CODES.name()), eq("PROG"))).thenReturn(true);
        when(cacheApiService.getSrteCacheString(eq(ObjectName.PROGRAM_AREA_CODES.name()), eq("PROG"))).thenReturn("Program Area");

        when(cacheApiService.getSrteCacheBool(eq(ObjectName.JURISDICTION_CODES.name()), eq("JURIS"))).thenReturn(false);

        managerService.enrichProgramAreaAndJurisdiction(observation, dto);

        assertEquals("Program Area", dto.getProgramAreaName());
        assertNull(dto.getJurisdictionName());
        assertTrue(dto.isLabIsCreateSuccess());
        assertEquals(EdxELRConstant.ELR_MASTER_LOG_ID_1, dto.getErrorText());
    }

    @Test
    void enrichProgramAreaAndJurisdiction_shouldNotSetLabIsCreateSuccess_ifLabIsCreateIsFalse() throws Exception {
        ObservationDto observation = new ObservationDto();
        observation.setProgAreaCd("PROG");
        observation.setJurisdictionCd("JURIS");

        EdxLabInformationDto dto = new EdxLabInformationDto(); // default isLabIsCreate == false

        when(cacheApiService.getSrteCacheBool(anyString(), anyString())).thenReturn(false);

        managerService.enrichProgramAreaAndJurisdiction(observation, dto);

        assertFalse(dto.isLabIsCreateSuccess());
        assertNull(dto.getErrorText());
    }

    @Test
    void handleWdsAndLabException_whenNonDatabaseError_shouldSetStatusAndSaveModel() throws Exception {
        // Arrange
        AtomicBoolean dltLock = new AtomicBoolean(false);
        AtomicBoolean nonDlt = new AtomicBoolean(false);
        AtomicBoolean inteDlt = new AtomicBoolean(false);


        NbsInterfaceModel model = new NbsInterfaceModel();
        PublicHealthCaseFlowContainer container = new PublicHealthCaseFlowContainer();
        container.setNbsInterfaceModel(model);

        Exception ex = new IllegalArgumentException("non-database error");

        // Act
        managerService.handleWdsAndLabException(ex, container, dltLock, nonDlt, inteDlt);

        // Assert
        assertFalse(dltLock.get());
        assertTrue(nonDlt.get());
        assertEquals(DP_FAILURE_STEP_2, model.getRecordStatusCd());
        assertNotNull(model.getRecordStatusTime());
        verify(nbsInterfaceRepository).save(model);
    }

    @Test
    void finalizeWdsAndLabProcessing_whenNonDltError_shouldPersistAndLog() throws Exception {
        // Arrange
        Exception ex = new Exception("non-dlt-error");
        PublicHealthCaseFlowContainer container = new PublicHealthCaseFlowContainer();

        NbsInterfaceModel model = new NbsInterfaceModel();
        container.setNbsInterfaceModel(model);
        container.setNbsInterfaceId(987);

        EdxLabInformationDto dto = new EdxLabInformationDto();
        dto.setEdxActivityLogDto(new EDXActivityLogDto());
        container.setEdxLabInformationDto(dto);

        // Act
        managerService.finalizeWdsAndLabProcessing(container, ex, false, true, false, false);

        // Assert
        verify(managerService).persistingRtiDlt(
                eq(ex),
                eq(987L),
                anyString(),
                eq("STEP 2"),
                any()
        );
        verify(managerService, never()).composeDltKafkaEvent(any(), any());
        verify(edxLogService).updateActivityLogDT(model, dto);
        verify(edxLogService).addActivityDetailLogs(dto, "");
        verify(edxLogService).saveEdxActivityLogs(dto.getEdxActivityLogDto());
    }
    @Test
    void updateNbsInterfaceStatus_shouldDelegateToJdbcRepository() {
        // Arrange
        List<Integer> ids = List.of(1001, 1002, 1003);

        // Act
        managerService.updateNbsInterfaceStatus(ids);

        // Assert
        verify(nbsInterfaceJdbcRepository).updateRecordStatusToRtiProcess(ids);
    }




}
