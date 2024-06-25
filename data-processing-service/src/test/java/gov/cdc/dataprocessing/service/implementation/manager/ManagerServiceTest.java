package gov.cdc.dataprocessing.service.implementation.manager;

import com.google.gson.Gson;
import gov.cdc.dataprocessing.cache.SrteCache;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.kafka.producer.KafkaManagerProducer;
import gov.cdc.dataprocessing.model.container.model.*;
import gov.cdc.dataprocessing.model.dto.lab_result.EdxLabInformationDto;
import gov.cdc.dataprocessing.model.dto.log.EDXActivityLogDto;
import gov.cdc.dataprocessing.model.dto.observation.ObservationDto;
import gov.cdc.dataprocessing.model.dto.person.PersonDto;
import gov.cdc.dataprocessing.model.dto.phc.PublicHealthCaseDto;
import gov.cdc.dataprocessing.repository.nbs.msgoute.model.NbsInterfaceModel;
import gov.cdc.dataprocessing.repository.nbs.msgoute.repos.NbsInterfaceRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.repository.nbs.odse.model.person.Person;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.dsm.DsmAlgorithmRepository;
import gov.cdc.dataprocessing.repository.nbs.srte.model.ConditionCode;
import gov.cdc.dataprocessing.repository.nbs.srte.model.ElrXref;
import gov.cdc.dataprocessing.service.implementation.investigation.DsmAlgorithmService;
import gov.cdc.dataprocessing.service.interfaces.action.ILabReportProcessing;
import gov.cdc.dataprocessing.service.interfaces.cache.ICatchingValueService;
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
import org.springframework.cache.CacheManager;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static gov.cdc.dataprocessing.constant.ManagerEvent.EVENT_ELR;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

public class ManagerServiceTest {
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
        String eventType = EVENT_ELR;

        NbsInterfaceModel labData = test.readDataFromJsonPath("manager/manager_first_process.json", NbsInterfaceModel.class);
        Gson gson = new Gson();
        String data = gson.toJson(labData);


     //   managerService.processDistribution(eventType, data);
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



        ObservationDto observationDto = new ObservationDto();
        observationDto.setLocalId("LOCAL");
        observationDto.setObservationUid(10L);
        observationDto.setProgAreaCd("A");
        SrteCache.programAreaCodesMap.put("A", "A");
        observationDto.setJurisdictionCd("A");
        SrteCache.jurisdictionCodeMap.put("A", "A");

        when(observationService.processingLabResultContainer(any())).thenReturn(observationDto);


        managerService.processDistribution(eventType, data);

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


}
