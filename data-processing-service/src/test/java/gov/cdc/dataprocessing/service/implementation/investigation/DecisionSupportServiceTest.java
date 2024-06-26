package gov.cdc.dataprocessing.service.implementation.investigation;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.container.model.ObservationContainer;
import gov.cdc.dataprocessing.model.container.model.PageActProxyContainer;
import gov.cdc.dataprocessing.model.container.model.PersonContainer;
import gov.cdc.dataprocessing.model.dsma_algorithm.*;
import gov.cdc.dataprocessing.model.dto.edx.EdxRuleManageDto;
import gov.cdc.dataprocessing.model.dto.lab_result.EdxLabInformationDto;
import gov.cdc.dataprocessing.model.dto.nbs.NbsQuestionMetadata;
import gov.cdc.dataprocessing.model.dto.observation.ObservationDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.repository.nbs.odse.model.dsm.DsmAlgorithm;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.CustomAuthUserRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.auth.AuthUserRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.stored_proc.PublicHealthCaseStoredProcRepository;
import gov.cdc.dataprocessing.service.implementation.auth_user.AuthUserService;
import gov.cdc.dataprocessing.service.interfaces.public_health_case.IAutoInvestigationService;
import gov.cdc.dataprocessing.service.model.auth_user.AuthUserProfileInfo;
import gov.cdc.dataprocessing.service.model.decision_support.DsmLabMatchHelper;
import gov.cdc.dataprocessing.test_data.TestData;
import gov.cdc.dataprocessing.test_data.TestDataReader;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.edx.EdxPhcrDocumentUtil;
import gov.cdc.dataprocessing.utilities.component.wds.ValidateDecisionSupport;
import gov.cdc.dataprocessing.utilities.time.TimeStampUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Type;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class DecisionSupportServiceTest {
    @Mock
    private EdxPhcrDocumentUtil edxPhcrDocumentUtil;
    @Mock
    private IAutoInvestigationService autoInvestigationService;
    @Mock
    private ValidateDecisionSupport validateDecisionSupport;
    @Mock
    private PublicHealthCaseStoredProcRepository publicHealthCaseStoredProcRepository;
    @Mock
    private DsmAlgorithmService dsmAlgorithmService;
    @InjectMocks
    private DecisionSupportService decisionSupportService;
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
        Mockito.reset(edxPhcrDocumentUtil, autoInvestigationService, validateDecisionSupport, publicHealthCaseStoredProcRepository,
                dsmAlgorithmService , authUtil);
    }


    @Test
    void validateProxyContainer_Success_MarkedAsReview() throws DataProcessingException {

        var alsoCol = "[{\"dsmAlgorithmUid\":9,\"algorithmNm\":\"He\",\"eventType\":\"11648804\",\"conditionList\":\"11065\",\"frequency\":\"1\",\"applyTo\":\"1\",\"sendingSystemList\":\"\",\"eventAction\":\"3\",\"algorithmPayload\":\"\\u003cAlgorithm xmlns\\u003d\\\"http://www.cdc.gov/NEDSS\\\"\\u003e\\n  \\u003cAlgorithmName\\u003eHe\\u003c/AlgorithmName\\u003e\\n  \\u003cEvent\\u003e\\n    \\u003cCode\\u003e11648804\\u003c/Code\\u003e\\n    \\u003cCodeDescTxt\\u003eLaboratory Report\\u003c/CodeDescTxt\\u003e\\n    \\u003cCodeSystemCode\\u003e2.16.840.1.113883.6.96\\u003c/CodeSystemCode\\u003e\\n  \\u003c/Event\\u003e\\n  \\u003cFrequency\\u003e\\n    \\u003cCode\\u003e1\\u003c/Code\\u003e\\n    \\u003cCodeDescTxt\\u003eReal-Time\\u003c/CodeDescTxt\\u003e\\n    \\u003cCodeSystemCode\\u003eL\\u003c/CodeSystemCode\\u003e\\n  \\u003c/Frequency\\u003e\\n  \\u003cAppliesToEntryMethods\\u003e\\n    \\u003cEntryMethod\\u003e\\n      \\u003cCode\\u003e1\\u003c/Code\\u003e\\n      \\u003cCodeDescTxt\\u003eElectronic Document\\u003c/CodeDescTxt\\u003e\\n      \\u003cCodeSystemCode\\u003eL\\u003c/CodeSystemCode\\u003e\\n    \\u003c/EntryMethod\\u003e\\n  \\u003c/AppliesToEntryMethods\\u003e\\n  \\u003cInvestigationType/\\u003e\\n  \\u003cApplyToConditions\\u003e\\n    \\u003cCondition\\u003e\\n      \\u003cCode\\u003e11065\\u003c/Code\\u003e\\n      \\u003cCodeDescTxt\\u003e2019 Novel Coronavirus\\u003c/CodeDescTxt\\u003e\\n      \\u003cCodeSystemCode\\u003e2.16.840.1.114222.4.5.277\\u003c/CodeSystemCode\\u003e\\n    \\u003c/Condition\\u003e\\n  \\u003c/ApplyToConditions\\u003e\\n  \\u003cComment/\\u003e\\n  \\u003cElrAdvancedCriteria\\u003e\\n    \\u003cEventDateLogic\\u003e\\n      \\u003cElrTimeLogic\\u003e\\n        \\u003cElrTimeLogicInd\\u003e\\n          \\u003cCode\\u003eN\\u003c/Code\\u003e\\n        \\u003c/ElrTimeLogicInd\\u003e\\n      \\u003c/ElrTimeLogic\\u003e\\n    \\u003c/EventDateLogic\\u003e\\n    \\u003cAndOrLogic\\u003eOR\\u003c/AndOrLogic\\u003e\\n    \\u003cElrCriteria\\u003e\\n      \\u003cResultedTest\\u003e\\n        \\u003cCode\\u003e77190-7\\u003c/Code\\u003e\\n        \\u003cCodeDescTxt\\u003eHepatitis B virus core and surface Ab and surface Ag panel - Serum (77190-7)\\u003c/CodeDescTxt\\u003e\\n      \\u003c/ResultedTest\\u003e\\n      \\u003cElrNumericResultValue\\u003e\\n        \\u003cComparatorCode\\u003e\\n          \\u003cCode\\u003e\\u003e\\u003d\\u003c/Code\\u003e\\n          \\u003cCodeDescTxt\\u003e\\u003e\\u003d\\u003c/CodeDescTxt\\u003e\\n        \\u003c/ComparatorCode\\u003e\\n        \\u003cValue1\\u003e1\\u003c/Value1\\u003e\\n        \\u003cUnit\\u003e\\n          \\u003cCode\\u003emL\\u003c/Code\\u003e\\n          \\u003cCodeDescTxt\\u003emL\\u003c/CodeDescTxt\\u003e\\n        \\u003c/Unit\\u003e\\n      \\u003c/ElrNumericResultValue\\u003e\\n    \\u003c/ElrCriteria\\u003e\\n    \\u003cInvLogic\\u003e\\n      \\u003cInvLogicInd\\u003e\\n        \\u003cCode\\u003eN\\u003c/Code\\u003e\\n      \\u003c/InvLogicInd\\u003e\\n    \\u003c/InvLogic\\u003e\\n  \\u003c/ElrAdvancedCriteria\\u003e\\n  \\u003cAction\\u003e\\n    \\u003cMarkAsReviewed\\u003e\\n      \\u003cOnFailureToMarkAsReviewed\\u003e\\n        \\u003cCode\\u003e2\\u003c/Code\\u003e\\n        \\u003cCodeDescTxt\\u003eRetain Event Record\\u003c/CodeDescTxt\\u003e\\n        \\u003cCodeSystemCode\\u003eL\\u003c/CodeSystemCode\\u003e\\n      \\u003c/OnFailureToMarkAsReviewed\\u003e\\n      \\u003cAdditionalComment/\\u003e\\n    \\u003c/MarkAsReviewed\\u003e\\n  \\u003c/Action\\u003e\\n\\u003c/Algorithm\\u003e\",\"adminComment\":\"\",\"statusCd\":\"A\",\"statusTime\":\"Jun 13, 2024, 7:18:51 PM\",\"lastChgUserId\":10055282,\"lastChgTime\":\"Jun 13, 2024, 7:18:48 PM\",\"resultedTestList\":\"77190-7\"}]";
        Gson gson = new Gson();
        TestDataReader test = new TestDataReader();
        LabResultProxyContainer labProxyContainer = test.readDataFromJsonPath("wds/wds_reviewed_lab.json", LabResultProxyContainer.class);
        EdxLabInformationDto edxLab = test.readDataFromJsonPath("wds/wds_reviewed_edx.json", EdxLabInformationDto.class);
        Type collectionType = new TypeToken<Collection<DsmAlgorithm>>(){}.getType();
        Collection<DsmAlgorithm> algoCol = gson.fromJson(alsoCol, collectionType);


        when(dsmAlgorithmService.findActiveDsmAlgorithm()).thenReturn(algoCol);


        var res = decisionSupportService.validateProxyContainer(labProxyContainer, edxLab);

        assertNotNull(res);
        assertEquals("MARK_AS_REVIEWED", res.getWdsReports().get(0).getAction());

    }

    @Test
    void validateProxyContainer_Success_Investigation() throws DataProcessingException {

        var alsoCol = "[{\"dsmAlgorithmUid\":9,\"algorithmNm\":\"He\",\"eventType\":\"11648804\",\"conditionList\":\"11065\",\"frequency\":\"1\",\"applyTo\":\"1\",\"sendingSystemList\":\"\",\"eventAction\":\"1\",\"algorithmPayload\":\"\\u003cAlgorithm xmlns\\u003d\\\"http://www.cdc.gov/NEDSS\\\"\\u003e\\n  \\u003cAlgorithmName\\u003eHe\\u003c/AlgorithmName\\u003e\\n  \\u003cEvent\\u003e\\n    \\u003cCode\\u003e11648804\\u003c/Code\\u003e\\n    \\u003cCodeDescTxt\\u003eLaboratory Report\\u003c/CodeDescTxt\\u003e\\n    \\u003cCodeSystemCode\\u003e2.16.840.1.113883.6.96\\u003c/CodeSystemCode\\u003e\\n  \\u003c/Event\\u003e\\n  \\u003cFrequency\\u003e\\n    \\u003cCode\\u003e1\\u003c/Code\\u003e\\n    \\u003cCodeDescTxt\\u003eReal-Time\\u003c/CodeDescTxt\\u003e\\n    \\u003cCodeSystemCode\\u003eL\\u003c/CodeSystemCode\\u003e\\n  \\u003c/Frequency\\u003e\\n  \\u003cAppliesToEntryMethods\\u003e\\n    \\u003cEntryMethod\\u003e\\n      \\u003cCode\\u003e1\\u003c/Code\\u003e\\n      \\u003cCodeDescTxt\\u003eElectronic Document\\u003c/CodeDescTxt\\u003e\\n      \\u003cCodeSystemCode\\u003eL\\u003c/CodeSystemCode\\u003e\\n    \\u003c/EntryMethod\\u003e\\n  \\u003c/AppliesToEntryMethods\\u003e\\n  \\u003cInvestigationType/\\u003e\\n  \\u003cApplyToConditions\\u003e\\n    \\u003cCondition\\u003e\\n      \\u003cCode\\u003e11065\\u003c/Code\\u003e\\n      \\u003cCodeDescTxt\\u003e2019 Novel Coronavirus\\u003c/CodeDescTxt\\u003e\\n      \\u003cCodeSystemCode\\u003e2.16.840.1.114222.4.5.277\\u003c/CodeSystemCode\\u003e\\n    \\u003c/Condition\\u003e\\n  \\u003c/ApplyToConditions\\u003e\\n  \\u003cComment/\\u003e\\n  \\u003cElrAdvancedCriteria\\u003e\\n    \\u003cEventDateLogic\\u003e\\n      \\u003cElrTimeLogic\\u003e\\n        \\u003cElrTimeLogicInd\\u003e\\n          \\u003cCode\\u003eN\\u003c/Code\\u003e\\n        \\u003c/ElrTimeLogicInd\\u003e\\n      \\u003c/ElrTimeLogic\\u003e\\n    \\u003c/EventDateLogic\\u003e\\n    \\u003cAndOrLogic\\u003eOR\\u003c/AndOrLogic\\u003e\\n    \\u003cElrCriteria\\u003e\\n      \\u003cResultedTest\\u003e\\n        \\u003cCode\\u003e77190-7\\u003c/Code\\u003e\\n        \\u003cCodeDescTxt\\u003eHepatitis B virus core and surface Ab and surface Ag panel - Serum (77190-7)\\u003c/CodeDescTxt\\u003e\\n      \\u003c/ResultedTest\\u003e\\n      \\u003cElrNumericResultValue\\u003e\\n        \\u003cComparatorCode\\u003e\\n          \\u003cCode\\u003e\\u003e\\u003d\\u003c/Code\\u003e\\n          \\u003cCodeDescTxt\\u003e\\u003e\\u003d\\u003c/CodeDescTxt\\u003e\\n        \\u003c/ComparatorCode\\u003e\\n        \\u003cValue1\\u003e1\\u003c/Value1\\u003e\\n        \\u003cUnit\\u003e\\n          \\u003cCode\\u003emL\\u003c/Code\\u003e\\n          \\u003cCodeDescTxt\\u003emL\\u003c/CodeDescTxt\\u003e\\n        \\u003c/Unit\\u003e\\n      \\u003c/ElrNumericResultValue\\u003e\\n    \\u003c/ElrCriteria\\u003e\\n    \\u003cInvLogic\\u003e\\n      \\u003cInvLogicInd\\u003e\\n        \\u003cCode\\u003eY\\u003c/Code\\u003e\\n      \\u003c/InvLogicInd\\u003e\\n    \\u003c/InvLogic\\u003e\\n  \\u003c/ElrAdvancedCriteria\\u003e\\n  \\u003cAction\\u003e\\n    \\u003cCreateInvestigation\\u003e\\n      \\u003cOnFailureToCreateInvestigation\\u003e\\n        \\u003cCode\\u003e2\\u003c/Code\\u003e\\n        \\u003cCodeDescTxt\\u003eRetain Event Record\\u003c/CodeDescTxt\\u003e\\n        \\u003cCodeSystemCode\\u003eL\\u003c/CodeSystemCode\\u003e\\n      \\u003c/OnFailureToCreateInvestigation\\u003e\\n    \\u003c/CreateInvestigation\\u003e\\n  \\u003c/Action\\u003e\\n\\u003c/Algorithm\\u003e\",\"adminComment\":\"\",\"statusCd\":\"A\",\"statusTime\":\"Jun 13, 2024, 9:01:07 PM\",\"lastChgUserId\":10055282,\"lastChgTime\":\"Jun 13, 2024, 9:01:04 PM\",\"resultedTestList\":\"77190-7\"}]";
        Gson gson = new Gson();
        TestDataReader test = new TestDataReader();
        LabResultProxyContainer labProxyContainer = test.readDataFromJsonPath("wds/wds_reviewed_lab.json", LabResultProxyContainer.class);
        EdxLabInformationDto edxLab = test.readDataFromJsonPath("wds/wds_reviewed_edx.json", EdxLabInformationDto.class);
        Type collectionType = new TypeToken<Collection<DsmAlgorithm>>(){}.getType();
        Collection<DsmAlgorithm> algoCol = gson.fromJson(alsoCol, collectionType);


        when(dsmAlgorithmService.findActiveDsmAlgorithm()).thenReturn(algoCol);


        var objString = "{\"pageProxyTypeCd\":\"\",\"publicHealthCaseContainer\":{\"isPamCase\":false,\"theCaseManagementDto\":{\"isCaseManagementDTPopulated\":false,\"itNew\":false,\"itOld\":false,\"itDirty\":false,\"itDelete\":false},\"thePublicHealthCaseDto\":{\"caseStatusDirty\":false,\"isPamCase\":false,\"isPageCase\":false,\"isStdHivProgramAreaCode\":false,\"caseTypeCd\":\"I\",\"publicHealthCaseUid\":-30,\"activityFromTime\":\"Jun 13, 2024, 12:00:00 AM\",\"addTime\":\"Jun 13, 2024, 5:21:36 PM\",\"addUserId\":36,\"cd\":\"11120\",\"cdDescTxt\":\"Acute flaccid myelitis\",\"groupCaseCnt\":1,\"investigationStatusCd\":\"O\",\"jurisdictionCd\":\"130001\",\"lastChgTime\":\"Jun 13, 2024, 5:21:36 PM\",\"mmwrWeek\":\"24\",\"mmwrYear\":\"2024\",\"progAreaCd\":\"GCD\",\"rptFormCmpltTime\":\"Jun 13, 2024, 5:18:20 PM\",\"statusCd\":\"A\",\"sharedInd\":\"T\",\"isSummaryCase\":false,\"itNew\":true,\"itOld\":false,\"itDirty\":false,\"itDelete\":false},\"theActIdDTCollection\":[{\"actIdSeq\":1,\"typeCd\":\"STATE\",\"itNew\":true,\"itOld\":false,\"itDirty\":false,\"itDelete\":false}],\"isCoinfectionCondition\":false,\"itNew\":true,\"itOld\":false,\"itDirty\":false,\"itDelete\":false},\"isSTDProgramArea\":false,\"pageVO\":{},\"isOOSystemInd\":false,\"isOOSystemPendInd\":false,\"associatedNotificationsInd\":false,\"isUnsavedNote\":false,\"isMergeCase\":false,\"isRenterant\":false,\"isConversionHasModified\":false,\"messageLogDTMap\":{},\"itNew\":true,\"itOld\":false,\"itDirty\":false,\"itDelete\":false}";
        PageActProxyContainer obj = gson.fromJson(objString, PageActProxyContainer.class);

        when(autoInvestigationService.autoCreateInvestigation(any(), any())).thenReturn(obj);


        var res = decisionSupportService.validateProxyContainer(labProxyContainer, edxLab);

        assertNotNull(res);
        assertEquals("CREATE_INVESTIGATION", res.getWdsReports().get(0).getAction());

    }

    @Test
    void validateProxyContainer_Success_Notification() throws DataProcessingException {

        var alsoCol = "[{\"dsmAlgorithmUid\":9,\"algorithmNm\":\"He\",\"eventType\":\"11648804\",\"conditionList\":\"11120\",\"frequency\":\"1\",\"applyTo\":\"1\",\"sendingSystemList\":\"\",\"eventAction\":\"2\",\"algorithmPayload\":\"\\u003cAlgorithm xmlns\\u003d\\\"http://www.cdc.gov/NEDSS\\\"\\u003e\\n  \\u003cAlgorithmName\\u003eHe\\u003c/AlgorithmName\\u003e\\n  \\u003cEvent\\u003e\\n    \\u003cCode\\u003e11648804\\u003c/Code\\u003e\\n    \\u003cCodeDescTxt\\u003eLaboratory Report\\u003c/CodeDescTxt\\u003e\\n    \\u003cCodeSystemCode\\u003e2.16.840.1.113883.6.96\\u003c/CodeSystemCode\\u003e\\n  \\u003c/Event\\u003e\\n  \\u003cFrequency\\u003e\\n    \\u003cCode\\u003e1\\u003c/Code\\u003e\\n    \\u003cCodeDescTxt\\u003eReal-Time\\u003c/CodeDescTxt\\u003e\\n    \\u003cCodeSystemCode\\u003eL\\u003c/CodeSystemCode\\u003e\\n  \\u003c/Frequency\\u003e\\n  \\u003cAppliesToEntryMethods\\u003e\\n    \\u003cEntryMethod\\u003e\\n      \\u003cCode\\u003e1\\u003c/Code\\u003e\\n      \\u003cCodeDescTxt\\u003eElectronic Document\\u003c/CodeDescTxt\\u003e\\n      \\u003cCodeSystemCode\\u003eL\\u003c/CodeSystemCode\\u003e\\n    \\u003c/EntryMethod\\u003e\\n  \\u003c/AppliesToEntryMethods\\u003e\\n  \\u003cInvestigationType\\u003ePG_Generic_V2_Investigation\\u003c/InvestigationType\\u003e\\n  \\u003cApplyToConditions\\u003e\\n    \\u003cCondition\\u003e\\n      \\u003cCode\\u003e11120\\u003c/Code\\u003e\\n      \\u003cCodeDescTxt\\u003eAcute flaccid myelitis\\u003c/CodeDescTxt\\u003e\\n      \\u003cCodeSystemCode\\u003e2.16.840.1.114222.4.5.277\\u003c/CodeSystemCode\\u003e\\n    \\u003c/Condition\\u003e\\n  \\u003c/ApplyToConditions\\u003e\\n  \\u003cComment/\\u003e\\n  \\u003cElrAdvancedCriteria\\u003e\\n    \\u003cEventDateLogic\\u003e\\n      \\u003cElrTimeLogic\\u003e\\n        \\u003cElrTimeLogicInd\\u003e\\n          \\u003cCode\\u003eN\\u003c/Code\\u003e\\n        \\u003c/ElrTimeLogicInd\\u003e\\n      \\u003c/ElrTimeLogic\\u003e\\n    \\u003c/EventDateLogic\\u003e\\n    \\u003cAndOrLogic\\u003eOR\\u003c/AndOrLogic\\u003e\\n    \\u003cElrCriteria\\u003e\\n      \\u003cResultedTest\\u003e\\n        \\u003cCode\\u003e77190-7\\u003c/Code\\u003e\\n        \\u003cCodeDescTxt\\u003eHepatitis B virus core and surface Ab and surface Ag panel - Serum (77190-7)\\u003c/CodeDescTxt\\u003e\\n      \\u003c/ResultedTest\\u003e\\n      \\u003cElrNumericResultValue\\u003e\\n        \\u003cComparatorCode\\u003e\\n          \\u003cCode\\u003e\\u003e\\u003d\\u003c/Code\\u003e\\n          \\u003cCodeDescTxt\\u003e\\u003e\\u003d\\u003c/CodeDescTxt\\u003e\\n        \\u003c/ComparatorCode\\u003e\\n        \\u003cValue1\\u003e1\\u003c/Value1\\u003e\\n        \\u003cUnit\\u003e\\n          \\u003cCode\\u003emL\\u003c/Code\\u003e\\n          \\u003cCodeDescTxt\\u003emL\\u003c/CodeDescTxt\\u003e\\n        \\u003c/Unit\\u003e\\n      \\u003c/ElrNumericResultValue\\u003e\\n    \\u003c/ElrCriteria\\u003e\\n    \\u003cInvLogic\\u003e\\n      \\u003cInvLogicInd\\u003e\\n        \\u003cCode\\u003eY\\u003c/Code\\u003e\\n      \\u003c/InvLogicInd\\u003e\\n    \\u003c/InvLogic\\u003e\\n  \\u003c/ElrAdvancedCriteria\\u003e\\n  \\u003cAction\\u003e\\n    \\u003cCreateInvestigationWithNND\\u003e\\n      \\u003cOnFailureToCreateInvestigation\\u003e\\n        \\u003cCode\\u003e2\\u003c/Code\\u003e\\n        \\u003cCodeDescTxt\\u003eRetain Event Record\\u003c/CodeDescTxt\\u003e\\n        \\u003cCodeSystemCode\\u003eL\\u003c/CodeSystemCode\\u003e\\n      \\u003c/OnFailureToCreateInvestigation\\u003e\\n      \\u003cInvestigationDefaultValues\\u003e\\n        \\u003cDefaultValue\\u003e\\n          \\u003cDefaultQuestion\\u003e\\n            \\u003cCode\\u003eINV163\\u003c/Code\\u003e\\n            \\u003cCodeDescTxt\\u003eCase Status\\u003c/CodeDescTxt\\u003e\\n            \\u003cCodeSystemCode\\u003e2.16.840.1.113883.6.1\\u003c/CodeSystemCode\\u003e\\n          \\u003c/DefaultQuestion\\u003e\\n          \\u003cDefaultBehavior\\u003e\\n            \\u003cCode\\u003e1\\u003c/Code\\u003e\\n            \\u003cCodeDescTxt\\u003eOverwrite Existing Values\\u003c/CodeDescTxt\\u003e\\n            \\u003cCodeSystemCode\\u003eL\\u003c/CodeSystemCode\\u003e\\n          \\u003c/DefaultBehavior\\u003e\\n          \\u003cDefaultStringValue\\u003eC\\u003c/DefaultStringValue\\u003e\\n        \\u003c/DefaultValue\\u003e\\n      \\u003c/InvestigationDefaultValues\\u003e\\n      \\u003cQueueForApproval\\u003efalse\\u003c/QueueForApproval\\u003e\\n      \\u003cOnFailureToCreateNND\\u003e\\n        \\u003cCode\\u003e3\\u003c/Code\\u003e\\n        \\u003cCodeDescTxt\\u003eRetain Investigation and Event Record\\u003c/CodeDescTxt\\u003e\\n        \\u003cCodeSystemCode\\u003eL\\u003c/CodeSystemCode\\u003e\\n      \\u003c/OnFailureToCreateNND\\u003e\\n      \\u003cNNDComment\\u003eTEST\\u003c/NNDComment\\u003e\\n    \\u003c/CreateInvestigationWithNND\\u003e\\n  \\u003c/Action\\u003e\\n\\u003c/Algorithm\\u003e\",\"adminComment\":\"\",\"statusCd\":\"A\",\"statusTime\":\"Jun 13, 2024, 9:39:03 PM\",\"lastChgUserId\":10055282,\"lastChgTime\":\"Jun 13, 2024, 9:39:00 PM\",\"resultedTestList\":\"77190-7\"}]";
        Gson gson = new Gson();
        TestDataReader test = new TestDataReader();
        LabResultProxyContainer labProxyContainer = test.readDataFromJsonPath("wds/wds_reviewed_lab.json", LabResultProxyContainer.class);
        EdxLabInformationDto edxLab = test.readDataFromJsonPath("wds/wds_reviewed_edx.json", EdxLabInformationDto.class);
        Type collectionType = new TypeToken<Collection<DsmAlgorithm>>(){}.getType();
        Collection<DsmAlgorithm> algoCol = gson.fromJson(alsoCol, collectionType);


        when(dsmAlgorithmService.findActiveDsmAlgorithm()).thenReturn(algoCol);


        var objString = "{\"pageProxyTypeCd\":\"\",\"publicHealthCaseContainer\":{\"isPamCase\":false,\"theCaseManagementDto\":{\"isCaseManagementDTPopulated\":false,\"itNew\":false,\"itOld\":false,\"itDirty\":false,\"itDelete\":false},\"thePublicHealthCaseDto\":{\"caseStatusDirty\":false,\"isPamCase\":false,\"isPageCase\":false,\"isStdHivProgramAreaCode\":false,\"caseTypeCd\":\"I\",\"publicHealthCaseUid\":-30,\"activityFromTime\":\"Jun 13, 2024, 12:00:00 AM\",\"addTime\":\"Jun 13, 2024, 5:21:36 PM\",\"addUserId\":36,\"cd\":\"11120\",\"cdDescTxt\":\"Acute flaccid myelitis\",\"groupCaseCnt\":1,\"investigationStatusCd\":\"O\",\"jurisdictionCd\":\"130001\",\"lastChgTime\":\"Jun 13, 2024, 5:21:36 PM\",\"mmwrWeek\":\"24\",\"mmwrYear\":\"2024\",\"progAreaCd\":\"GCD\",\"rptFormCmpltTime\":\"Jun 13, 2024, 5:18:20 PM\",\"statusCd\":\"A\",\"sharedInd\":\"T\",\"isSummaryCase\":false,\"itNew\":true,\"itOld\":false,\"itDirty\":false,\"itDelete\":false},\"theActIdDTCollection\":[{\"actIdSeq\":1,\"typeCd\":\"STATE\",\"itNew\":true,\"itOld\":false,\"itDirty\":false,\"itDelete\":false}],\"isCoinfectionCondition\":false,\"itNew\":true,\"itOld\":false,\"itDirty\":false,\"itDelete\":false},\"isSTDProgramArea\":false,\"pageVO\":{},\"isOOSystemInd\":false,\"isOOSystemPendInd\":false,\"associatedNotificationsInd\":false,\"isUnsavedNote\":false,\"isMergeCase\":false,\"isRenterant\":false,\"isConversionHasModified\":false,\"messageLogDTMap\":{},\"itNew\":true,\"itOld\":false,\"itDirty\":false,\"itDelete\":false}";
        PageActProxyContainer obj = gson.fromJson(objString, PageActProxyContainer.class);

        when(autoInvestigationService.autoCreateInvestigation(any(), any())).thenReturn(obj);

        var res = decisionSupportService.validateProxyContainer(labProxyContainer, edxLab);

        assertNotNull(res);
        assertEquals("CREATE_INVESTIGATION_WITH_NOTIFICATION", res.getWdsReports().get(0).getAction());

    }

    @Test
    void checkActionInvalid_review_code_1() {
        Algorithm algo = new Algorithm();
        ActionType actionType = new ActionType();
        var action = new MarkAsReviewedType();
        var codeType = new CodedType();
        codeType.setCode("1");
        action.setOnFailureToMarkAsReviewed(codeType);
        actionType.setMarkAsReviewed(action);
        algo.setAction(actionType);
        boolean matching = true;
        var res = decisionSupportService.checkActionInvalid(algo, matching);

        assertTrue(res);
    }

    @Test
    void checkActionInvalid_invest_code_2() {
        Algorithm algo = new Algorithm();
        ActionType actionType = new ActionType();
        var action = new CreateInvestigationType();
        var codeType = new CodedType();
        codeType.setCode("2");
        action.setOnFailureToCreateInvestigation(codeType);
        actionType.setCreateInvestigation(action);
        algo.setAction(actionType);
        boolean matching = true;
        var res = decisionSupportService.checkActionInvalid(algo, matching);

        assertTrue(res);
    }

    @Test
    void checkActionInvalid_return_false() {
        Algorithm algo = new Algorithm();
        ActionType actionType = new ActionType();
        var action = new CreateInvestigationType();
        var codeType = new CodedType();
        codeType.setCode("2");
        action.setOnFailureToCreateInvestigation(codeType);
        actionType.setCreateInvestigation(action);
        algo.setAction(actionType);
        boolean matching = false;
        var res = decisionSupportService.checkActionInvalid(algo, matching);

        assertFalse(res);
    }

    @Test
    void checkActionInvalid_return_false_2() {
        Algorithm algo = new Algorithm();
        algo.setAction(null);
        boolean matching = true;
        var res = decisionSupportService.checkActionInvalid(algo, matching);

        assertFalse(res);
    }

    @Test
    void checkActionInvalid_return_false_3() {
        Algorithm algo = null;
        boolean matching = true;
        var res = decisionSupportService.checkActionInvalid(algo, matching);

        assertFalse(res);
    }

    @Test
    void checkActiveWdsAlgorithm_Test() throws DataProcessingException {
        EdxLabInformationDto edxLabInformationDT = new EdxLabInformationDto();
        List<DsmLabMatchHelper> activeElrAlgorithmList = new ArrayList<>();

        var dsmLst = new ArrayList<DsmAlgorithm>();
        var dsm = new DsmAlgorithm();
        dsmLst.add(dsm);
        when(dsmAlgorithmService.findActiveDsmAlgorithm()).thenReturn(dsmLst);

        var res = decisionSupportService.checkActiveWdsAlgorithm(edxLabInformationDT, activeElrAlgorithmList);

        assertFalse(res);
    }


    @Test
    void checkActiveWdsAlgorithm_Test_2() throws DataProcessingException {
        EdxLabInformationDto edxLabInformationDT = new EdxLabInformationDto();
        List<DsmLabMatchHelper> activeElrAlgorithmList = new ArrayList<>();


        var res = decisionSupportService.checkActiveWdsAlgorithm(edxLabInformationDT, activeElrAlgorithmList);

        assertFalse(res);
    }


    @Test
    void checkActiveWdsAlgorithm_Test_3() throws DataProcessingException {
        EdxLabInformationDto edxLabInformationDT = new EdxLabInformationDto();
        List<DsmLabMatchHelper> activeElrAlgorithmList = new ArrayList<>();

        var dsmLst = new ArrayList<DsmAlgorithm>();
        var dsm = new DsmAlgorithm();
        dsm.setStatusCd(NEDSSConstant.INACTIVE);
        dsmLst.add(dsm);
        when(dsmAlgorithmService.findActiveDsmAlgorithm()).thenReturn(dsmLst);

        var res = decisionSupportService.checkActiveWdsAlgorithm(edxLabInformationDT, activeElrAlgorithmList);

        assertFalse(res);
    }

    @Test
    void checkActiveWdsAlgorithm_Test_4() throws DataProcessingException {
        EdxLabInformationDto edxLabInformationDT = new EdxLabInformationDto();
        List<DsmLabMatchHelper> activeElrAlgorithmList = new ArrayList<>();

        var dsmLst = new ArrayList<DsmAlgorithm>();
        var dsm = new DsmAlgorithm();
        dsm.setEventType(NEDSSConstant.PHC_236);
        dsmLst.add(dsm);
        when(dsmAlgorithmService.findActiveDsmAlgorithm()).thenReturn(dsmLst);

        var res = decisionSupportService.checkActiveWdsAlgorithm(edxLabInformationDT, activeElrAlgorithmList);

        assertFalse(res);
    }

}
