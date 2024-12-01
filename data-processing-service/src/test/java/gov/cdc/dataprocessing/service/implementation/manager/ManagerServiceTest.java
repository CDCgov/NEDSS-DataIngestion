package gov.cdc.dataprocessing.service.implementation.manager;

import gov.cdc.dataprocessing.cache.SrteCache;
import gov.cdc.dataprocessing.constant.DecisionSupportConstants;
import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.exception.DataProcessingException;
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
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.service.interfaces.action.ILabReportProcessing;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static gov.cdc.dataprocessing.constant.ManagerEvent.EVENT_ELR;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

    @InjectMocks
    private ManagerService managerService;
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
        SrteCache.programAreaCodesMap.clear();
        SrteCache.jurisdictionCodeMap.clear();

    }

    @AfterEach
    void tearDown() {
        Mockito.reset(observationService, edxLogService, dataExtractionService, nbsInterfaceRepository, managerCacheService,
                decisionSupportService, managerUtil, kafkaManagerProducer, managerAggregationService,
                labReportProcessing, pageService,pamService, investigationNotificationService, authUtil);
    }


    @Test
    void processDistribution_Test() throws DataProcessingConsumerException, JAXBException, DataProcessingException {
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
        when(managerAggregationService.processingObservationMatching(any(), any(), any())).thenReturn(edxLabInformationDto);


        when(nbsInterfaceRepository.findByNbsInterfaceUid(any())).thenReturn(Optional.ofNullable(labData));


        ObservationDto observationDto = new ObservationDto();
        observationDto.setLocalId("LOCAL");
        observationDto.setObservationUid(10L);
        observationDto.setProgAreaCd("A");
        SrteCache.programAreaCodesMap.put("A", "A");
        observationDto.setJurisdictionCd("A");
        SrteCache.jurisdictionCodeMap.put("A", "A");

        when(observationService.processingLabResultContainer(any())).thenReturn(observationDto);


        managerService.processDistribution(123);

        verify(kafkaManagerProducer, times(1)).sendDataPhc(any());
    }

    @Test
    void initiatingInvestigationAndPublicHealthCase_Test_PageAct_ToStep3() {
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
        when(nbsInterfaceRepository.findByNbsInterfaceUid(any())).thenReturn(Optional.of(nbs));


        managerService.initiatingInvestigationAndPublicHealthCase(publicHealthCaseFlowContainer);

        verify(kafkaManagerProducer, times(1)).sendDataLabHandling(any());

    }

    @Test
    void initiatingInvestigationAndPublicHealthCase_Test_Pam_ToStep3() {
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
        when(nbsInterfaceRepository.findByNbsInterfaceUid(any())).thenReturn(Optional.of(nbs));


        managerService.initiatingInvestigationAndPublicHealthCase(publicHealthCaseFlowContainer);

        verify(kafkaManagerProducer, times(1)).sendDataLabHandling(any());

    }

    @Test
    void initiatingInvestigationAndPublicHealthCase_NoFurther() {
        PublicHealthCaseFlowContainer publicHealthCaseFlowContainer = new PublicHealthCaseFlowContainer();
        var edxLabInfoDto = new EdxLabInformationDto();
        var obsDto = new ObservationDto();
        var labResult = new LabResultProxyContainer();

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
        when(nbsInterfaceRepository.findByNbsInterfaceUid(any())).thenReturn(Optional.of(nbs));


        managerService.initiatingInvestigationAndPublicHealthCase(publicHealthCaseFlowContainer);

        verify(kafkaManagerProducer, times(0)).sendDataLabHandling(any());

    }


    @Test
    void initiatingInvestigationAndPublicHealthCase_Exception() {
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


        managerService.initiatingInvestigationAndPublicHealthCase(publicHealthCaseFlowContainer);


        verify(nbsInterfaceRepository, times(1)).findByNbsInterfaceUid(any());

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
        when(nbsInterfaceRepository.findByNbsInterfaceUid(any())).thenReturn(Optional.of(nbs));

        managerService.initiatingLabProcessing(publicHealthCaseFlowContainer);
        verify(nbsInterfaceRepository, times(1)).save(any());
        verify(labReportProcessing, times(1)).markAsReviewedHandler(any(), any());


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
        when(nbsInterfaceRepository.findByNbsInterfaceUid(any())).thenReturn(Optional.of(nbs));

        managerService.initiatingLabProcessing(publicHealthCaseFlowContainer);
        verify(nbsInterfaceRepository, times(1)).save(any());
        verify(labReportProcessing, times(1)).markAsReviewedHandler(any(), any());


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
        when(pageService.setPageProxyWithAutoAssoc(any(), any(),
                any(), any(), any())).thenReturn(12L);


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
        when(pamService.setPamProxyWithAutoAssoc(any(), any(),
                any())).thenReturn(12L);


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
        when(pageService.setPageProxyWithAutoAssoc(any(), any(),
                any(), any(), any())).thenReturn(12L);

        EDXActivityDetailLogDto detail = new EDXActivityDetailLogDto();
        detail.setLogType(null);
        when(investigationNotificationService.sendNotification(any(), any()))
                .thenReturn(detail);



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
        phcConn.setErrorText("ERROR");
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



        managerService.initiatingLabProcessing(publicHealthCaseFlowContainer);
        verify(nbsInterfaceRepository, times(1)).save(any());
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
        phcConn.setErrorText("ERROR");
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



        managerService.initiatingLabProcessing(publicHealthCaseFlowContainer);
        verify(nbsInterfaceRepository, times(1)).save(any());
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



        managerService.initiatingLabProcessing(publicHealthCaseFlowContainer);
        verify(nbsInterfaceRepository, times(1)).save(any());
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



        managerService.initiatingLabProcessing(publicHealthCaseFlowContainer);
        verify(nbsInterfaceRepository, times(1)).save(any());
    }

    @Test
    void processDistribution_Error_1() throws DataProcessingConsumerException, JAXBException, DataProcessingException {
        var test = new TestDataReader();
        String eventType = EVENT_ELR;

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
        SrteCache.programAreaCodesMap.put("A", "A");
        observationDto.setJurisdictionCd("A");
        SrteCache.jurisdictionCodeMap.put("A", "A");

        when(observationService.processingLabResultContainer(any()))  .thenThrow(new DataProcessingException("Invalid XML"));
        when(nbsInterfaceRepository.findByNbsInterfaceUid(any())).thenReturn(Optional.ofNullable(labData));

        managerService.processDistribution(123);

        verify(kafkaManagerProducer, times(1)).sendDataEdxActivityLog(any());
    }

    @Test
    void processDistribution_Error_2() throws DataProcessingConsumerException, JAXBException, DataProcessingException {
        var test = new TestDataReader();
        String eventType = EVENT_ELR;

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
        SrteCache.programAreaCodesMap.put("A", "A");
        observationDto.setJurisdictionCd("A");
        SrteCache.jurisdictionCodeMap.put("A", "A");

        when(observationService.processingLabResultContainer(any()))  .thenThrow(new DataProcessingException(EdxELRConstant.SQL_FIELD_TRUNCATION_ERROR_MSG));
        when(nbsInterfaceRepository.findByNbsInterfaceUid(any())).thenReturn(Optional.ofNullable(labData));

        managerService.processDistribution(123);

        verify(kafkaManagerProducer, times(1)).sendDataEdxActivityLog(any());
    }

    @Test
    void processDistribution_Error_3() throws DataProcessingConsumerException, JAXBException, DataProcessingException {
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
        SrteCache.programAreaCodesMap.put("A", "A");
        observationDto.setJurisdictionCd("A");
        SrteCache.jurisdictionCodeMap.put("A", "A");

        when(observationService.processingLabResultContainer(any()))  .thenThrow(new DataProcessingException(EdxELRConstant.SQL_FIELD_TRUNCATION_ERROR_MSG));
        when(nbsInterfaceRepository.findByNbsInterfaceUid(any())).thenReturn(Optional.ofNullable(labData));

        managerService.processDistribution(123);

        verify(kafkaManagerProducer, times(1)).sendDataEdxActivityLog(any());
    }

    @Test
    void processDistribution_Error_4() throws DataProcessingConsumerException, JAXBException, DataProcessingException {
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
        SrteCache.programAreaCodesMap.put("A", "A");
        observationDto.setJurisdictionCd("A");
        SrteCache.jurisdictionCodeMap.put("A", "A");

        when(observationService.processingLabResultContainer(any()))  .thenThrow(new DataProcessingException(EdxELRConstant.SQL_FIELD_TRUNCATION_ERROR_MSG));

        when(nbsInterfaceRepository.findByNbsInterfaceUid(any())).thenReturn(Optional.ofNullable(labData));
        managerService.processDistribution(123);

        verify(kafkaManagerProducer, times(1)).sendDataEdxActivityLog(any());
    }

    @Test
    void processDistribution_Error_5() throws DataProcessingConsumerException, JAXBException, DataProcessingException {
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
        SrteCache.programAreaCodesMap.put("A", "A");
        observationDto.setJurisdictionCd("A");
        SrteCache.jurisdictionCodeMap.put("A", "A");

        when(observationService.processingLabResultContainer(any()))  .thenThrow(new DataProcessingException(EdxELRConstant.SQL_FIELD_TRUNCATION_ERROR_MSG));
        when(nbsInterfaceRepository.findByNbsInterfaceUid(any())).thenReturn(Optional.ofNullable(labData));

        managerService.processDistribution(123);

        verify(kafkaManagerProducer, times(1)).sendDataEdxActivityLog(any());
    }

    @Test
    void processDistribution_Error_6() throws DataProcessingConsumerException, JAXBException, DataProcessingException {
        var test = new TestDataReader();
        String eventType = EVENT_ELR;

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
        SrteCache.programAreaCodesMap.put("A", "A");
        observationDto.setJurisdictionCd("A");
        SrteCache.jurisdictionCodeMap.put("A", "A");

        when(observationService.processingLabResultContainer(any()))  .thenThrow(new DataProcessingException(EdxELRConstant.DATE_VALIDATION));
        when(nbsInterfaceRepository.findByNbsInterfaceUid(any())).thenReturn(Optional.ofNullable(labData));

        managerService.processDistribution(123);

        verify(kafkaManagerProducer, times(1)).sendDataEdxActivityLog(any());
    }

    @Test
    void processDistribution_Error_7() throws DataProcessingConsumerException, JAXBException, DataProcessingException {
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
        SrteCache.programAreaCodesMap.put("A", "A");
        observationDto.setJurisdictionCd("A");
        SrteCache.jurisdictionCodeMap.put("A", "A");

        when(observationService.processingLabResultContainer(any()))  .thenThrow(new DataProcessingException("BLAH"));

        when(nbsInterfaceRepository.findByNbsInterfaceUid(any())).thenReturn(Optional.ofNullable(labData));
        managerService.processDistribution(123);

        verify(kafkaManagerProducer, times(1)).sendDataEdxActivityLog(any());
    }

}
