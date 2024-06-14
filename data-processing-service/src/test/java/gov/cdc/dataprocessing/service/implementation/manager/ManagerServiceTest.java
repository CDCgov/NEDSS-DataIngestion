package gov.cdc.dataprocessing.service.implementation.manager;

import com.google.gson.Gson;
import gov.cdc.dataprocessing.cache.SrteCache;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.kafka.producer.KafkaManagerProducer;
import gov.cdc.dataprocessing.repository.nbs.msgoute.model.NbsInterfaceModel;
import gov.cdc.dataprocessing.repository.nbs.msgoute.repos.NbsInterfaceRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.dsm.DsmAlgorithmRepository;
import gov.cdc.dataprocessing.repository.nbs.srte.model.ConditionCode;
import gov.cdc.dataprocessing.repository.nbs.srte.model.ElrXref;
import gov.cdc.dataprocessing.service.implementation.investigation.DsmAlgorithmService;
import gov.cdc.dataprocessing.service.interfaces.action.ILabReportProcessing;
import gov.cdc.dataprocessing.service.interfaces.cache.ICatchingValueService;
import gov.cdc.dataprocessing.service.interfaces.data_extraction.IDataExtractionService;
import gov.cdc.dataprocessing.service.interfaces.log.IEdxLogService;
import gov.cdc.dataprocessing.service.interfaces.manager.IManagerAggregationService;
import gov.cdc.dataprocessing.service.interfaces.observation.IObservationService;
import gov.cdc.dataprocessing.service.interfaces.page_and_pam.IPageService;
import gov.cdc.dataprocessing.service.interfaces.page_and_pam.IPamService;
import gov.cdc.dataprocessing.service.interfaces.public_health_case.IDecisionSupportService;
import gov.cdc.dataprocessing.service.interfaces.public_health_case.IInvestigationNotificationService;
import gov.cdc.dataprocessing.service.model.auth_user.AuthUserProfileInfo;
import gov.cdc.dataprocessing.test_data.TestDataReader;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.generic_helper.ManagerUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.cache.CacheManager;

import static gov.cdc.dataprocessing.constant.ManagerEvent.EVENT_ELR;

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
    private  ICatchingValueService cachingValueService;
    @Mock
    private  CacheManager cacheManager;
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
        createCachedValue();
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(observationService, edxLogService, dataExtractionService, nbsInterfaceRepository, cachingValueService, cacheManager,
                decisionSupportService, managerUtil, kafkaManagerProducer, managerAggregationService,
                labReportProcessing, pageService,pamService, investigationNotificationService, authUtil);
    }

    void createCachedValue() {
        SrteCache.loincCodesMap.put("TEST", "TEST");
        SrteCache.raceCodesMap.put("TEST", "TEST");
        SrteCache.programAreaCodesMap.put("TEST", "TEST");
        SrteCache.jurisdictionCodeMap.put("TEST", "TEST");
        SrteCache.jurisdictionCodeMapWithNbsUid.put("TEST",1);
        SrteCache.programAreaCodesMapWithNbsUid.put("TEST", 1);
        SrteCache.elrXrefsList.add(new ElrXref());
        SrteCache.coInfectionConditionCode.put("TEST" , "TEST");
        SrteCache.conditionCodes.add(new ConditionCode());
        SrteCache.labResultByDescMap.put("TEST", "TEST");
        SrteCache.snomedCodeByDescMap.put("TEST", "TEST");
        SrteCache.labResultWithOrganismNameIndMap.put("TEST", "TEST");
        SrteCache.loinCodeWithComponentNameMap.put("TEST", "TEST");
    }

    @Test
    void processDistribution_Test() throws DataProcessingConsumerException {
        var test = new TestDataReader();
        String eventType = EVENT_ELR;

        NbsInterfaceModel labData = test.readDataFromJsonPath("manager/manager_first_process.json", NbsInterfaceModel.class);
        Gson gson = new Gson();
        String data = gson.toJson(labData);


     //   managerService.processDistribution(eventType, data);

    }

}
