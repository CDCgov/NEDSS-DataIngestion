package gov.cdc.dataprocessing.service.implementation.observation;

import gov.cdc.dataprocessing.constant.elr.NBSBOLookup;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.LabReportSummaryContainer;
import gov.cdc.dataprocessing.model.container.model.ProviderDataForPrintContainer;
import gov.cdc.dataprocessing.model.container.model.ResultedTestSummaryContainer;
import gov.cdc.dataprocessing.model.container.model.UidSummaryContainer;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.repository.nbs.odse.model.observation.Observation_Summary;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.CustomRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.observation.Observation_SummaryRepository;
import gov.cdc.dataprocessing.service.model.auth_user.AuthUserProfileInfo;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.sql.QueryHelper;
import gov.cdc.dataprocessing.utilities.time.TimeStampUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ObservationSummaryServiceTest {
    @Mock
    private Observation_SummaryRepository observationSummaryRepository;
    @Mock
    private CustomRepository customRepository;
    @Mock
    private QueryHelper queryHelper;
    @InjectMocks
    private ObservationSummaryService observationSummaryService;
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
        Mockito.reset(observationSummaryRepository, customRepository, queryHelper, authUtil);
    }

    @Test
    void findAllActiveLabReportUidListForManage_Success() throws DataProcessingException {
        long investUid = 10L;
        String where = "";

        var colSum = new ArrayList<Observation_Summary>();
        var sum = new Observation_Summary();
        sum.setUid(10L);
        sum.setAddTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        sum.setAddReasonCd("TEST");
        colSum.add(sum);
        when(observationSummaryRepository.findAllActiveLabReportUidListForManage(10L, where))
                .thenReturn(colSum);

        var test = observationSummaryService.findAllActiveLabReportUidListForManage(investUid, where);

        assertEquals(1, test.size());

    }

    @Test
    void findAllActiveLabReportUidListForManage_Exception()  {
        long investUid = 10L;
        String where = "";

        var colSum = new ArrayList<Observation_Summary>();
        var sum = new Observation_Summary();
        sum.setUid(10L);
        sum.setAddTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        sum.setAddReasonCd("TEST");
        colSum.add(sum);
        when(observationSummaryRepository.findAllActiveLabReportUidListForManage(10L, where))
                .thenThrow(new RuntimeException("TEST"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            observationSummaryService.findAllActiveLabReportUidListForManage(investUid, where);
        });
        assertEquals("TEST", thrown.getMessage());

    }

    @Test
    void getLabParticipations_Success() throws DataProcessingException {
        long uid = 10L;
        when(customRepository.getLabParticipations(10L))
                .thenReturn(new HashMap<>());

        var test = observationSummaryService.getLabParticipations(uid);

        assertNotNull(test);
    }

    @Test
    void getLabParticipations_Exception() {
        long uid = 10L;
        when(customRepository.getLabParticipations(10L))
                .thenThrow(new RuntimeException("TEST"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            observationSummaryService.getLabParticipations(uid);
        });
        assertEquals("TEST", thrown.getMessage());
    }

    @Test
    void getPatientPersonInfo_Success() throws DataProcessingException {
        long uid = 10L;
        when(customRepository.getPatientPersonInfo(10L))
                .thenReturn(new ArrayList<>());

        var test = observationSummaryService.getPatientPersonInfo(uid);

        assertNotNull(test);
    }

    @Test
    void getPatientPersonInfo_Exception() {
        long uid = 10L;
        when(customRepository.getPatientPersonInfo(10L))
                .thenThrow(new RuntimeException("TEST"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            observationSummaryService.getPatientPersonInfo(uid);
        });
        assertEquals("TEST", thrown.getMessage());
    }

    @Test
    void getProviderInfo_Success() throws DataProcessingException {
        long uid = 10L;
        when(customRepository.getProviderInfo(10L, "Type"))
                .thenReturn(new ArrayList<>());

        var test = observationSummaryService.getProviderInfo(uid, "Type");

        assertNotNull(test);
    }

    @Test
    void getProviderInfo_Exception() {
        long uid = 10L;
        when(customRepository.getProviderInfo(10L, "Type"))
                .thenThrow(new RuntimeException("TEST"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            observationSummaryService.getProviderInfo(uid, "Type");
        });
        assertEquals("TEST", thrown.getMessage());
    }

    @Test
    void getActIdDetails_Success() throws DataProcessingException {
        long uid = 10L;
        when(customRepository.getActIdDetails(10L))
                .thenReturn(new ArrayList<>());

        var test = observationSummaryService.getProviderInfo(uid, "Type");

        assertNotNull(test);
    }

    @Test
    void getActIdDetails_Exception() {
        long uid = 10L;
        when(customRepository.getActIdDetails(10L))
                .thenThrow(new RuntimeException("TEST"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            observationSummaryService.getActIdDetails(uid);
        });
        assertEquals("TEST", thrown.getMessage());
    }

    @Test
    void getReportingFacilityName_Success() throws DataProcessingException {
        long uid = 10L;
        when(customRepository.getReportingFacilityName(10L))
                .thenReturn("");

        var test = observationSummaryService.getReportingFacilityName(uid);

        assertNotNull(test);
    }

    @Test
    void getReportingFacilityName_Exception() {
        long uid = 10L;
        when(customRepository.getReportingFacilityName(10L))
                .thenThrow(new RuntimeException("TEST"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            observationSummaryService.getReportingFacilityName(uid);
        });
        assertEquals("TEST", thrown.getMessage());
    }

    @Test
    void getSpecimanSource_Success() throws DataProcessingException {
        long uid = 10L;
        when(customRepository.getSpecimanSource(10L))
                .thenReturn("");

        var test = observationSummaryService.getSpecimanSource(uid);

        assertNotNull(test);
    }

    @Test
    void getSpecimanSource_Exception() {
        long uid = 10L;
        when(customRepository.getSpecimanSource(10L))
                .thenThrow(new RuntimeException("TEST"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            observationSummaryService.getSpecimanSource(uid);
        });
        assertEquals("TEST", thrown.getMessage());
    }

    @Test
    void getOrderingFacilityAddress_Success() throws DataProcessingException {
        long uid = 10L;
        var prodConn = new ProviderDataForPrintContainer();
        when(customRepository.getOrderingFacilityAddress(prodConn, 10L))
                .thenReturn(new ProviderDataForPrintContainer());

        var test = observationSummaryService.getOrderingFacilityAddress(prodConn, uid);

        assertNotNull(test);
    }

    @Test
    void getOrderingFacilityAddress_Exception() {
        long uid = 10L;
        var prodConn = new ProviderDataForPrintContainer();
        when(customRepository.getOrderingFacilityAddress(prodConn, 10L))
                .thenThrow(new RuntimeException("TEST"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            observationSummaryService.getOrderingFacilityAddress(prodConn, uid);
        });
        assertEquals("TEST", thrown.getMessage());
    }


    @Test
    void getOrderingFacilityPhone_Success() throws DataProcessingException {
        long uid = 10L;
        var prodConn = new ProviderDataForPrintContainer();
        when(customRepository.getOrderingFacilityPhone(prodConn, 10L))
                .thenReturn(new ProviderDataForPrintContainer());

        var test = observationSummaryService.getOrderingFacilityPhone(prodConn, uid);

        assertNotNull(test);
    }

    @Test
    void getOrderingFacilityPhone_Exception() {
        long uid = 10L;
        var prodConn = new ProviderDataForPrintContainer();
        when(customRepository.getOrderingFacilityPhone(prodConn, 10L))
                .thenThrow(new RuntimeException("TEST"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            observationSummaryService.getOrderingFacilityPhone(prodConn, uid);
        });
        assertEquals("TEST", thrown.getMessage());
    }

    @Test
    void getOrderingPersonAddress_Success() throws DataProcessingException {
        long uid = 10L;
        var prodConn = new ProviderDataForPrintContainer();
        when(customRepository.getOrderingPersonAddress(prodConn, 10L))
                .thenReturn(new ProviderDataForPrintContainer());

        var test = observationSummaryService.getOrderingPersonAddress(prodConn, uid);

        assertNotNull(test);
    }

    @Test
    void getOrderingPersonAddress_Exception() {
        long uid = 10L;
        var prodConn = new ProviderDataForPrintContainer();
        when(customRepository.getOrderingPersonAddress(prodConn, 10L))
                .thenThrow(new RuntimeException("TEST"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            observationSummaryService.getOrderingPersonAddress(prodConn, uid);
        });
        assertEquals("TEST", thrown.getMessage());
    }

    @Test
    void getOrderingPersonPhone_Success() throws DataProcessingException {
        long uid = 10L;
        var prodConn = new ProviderDataForPrintContainer();
        when(customRepository.getOrderingPersonPhone(prodConn, 10L))
                .thenReturn(new ProviderDataForPrintContainer());

        var test = observationSummaryService.getOrderingPersonPhone(prodConn, uid);

        assertNotNull(test);
    }

    @Test
    void getOrderingPersonPhone_Exception() {
        long uid = 10L;
        var prodConn = new ProviderDataForPrintContainer();
        when(customRepository.getOrderingPersonPhone(prodConn, 10L))
                .thenThrow(new RuntimeException("TEST"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            observationSummaryService.getOrderingPersonPhone(prodConn, uid);
        });
        assertEquals("TEST", thrown.getMessage());
    }

    @Test
    void getProviderInformation_Success() {
        var prod = new ArrayList<>();
        prod.add("Test");
        prod.add("Test");
        prod.add("Test");
        prod.add("Test");
        prod.add("Test");
        prod.add(1L);

        var labSum = new LabReportSummaryContainer();

        var test = observationSummaryService.getProviderInformation(prod, labSum);

        assertEquals(1L, test);


    }

    @Test
    void getTestAndSusceptibilities_Sucess() {
        String typeCode = "typeCode";
        Long observationUid = 10L;
        LabReportSummaryContainer labRepEvent = new LabReportSummaryContainer();
        LabReportSummaryContainer labRepSumm = new LabReportSummaryContainer();

        var testListCol = new ArrayList<ResultedTestSummaryContainer>(); // RVO
        var rvo = new ResultedTestSummaryContainer();
        rvo.setSourceActUid(11L);
        testListCol.add(rvo);
        when(customRepository.getTestAndSusceptibilities(typeCode, 10L, labRepEvent, labRepSumm))
                .thenReturn(testListCol);

        // setSusceptibility 246
        var uidSumCol = new ArrayList<UidSummaryContainer>();
        var uidSum = new UidSummaryContainer();
        uidSum.setUid(12L);
        uidSumCol.add(uidSum);
        when(customRepository.getSusceptibilityUidSummary(eq(testListCol.get(0)),
                any(LabReportSummaryContainer.class),
                any(LabReportSummaryContainer.class), eq("REFR"), eq(11L)))
                .thenReturn(uidSumCol);

        var resTestSumCol =  new ArrayList<ResultedTestSummaryContainer>();
        var resTestSum = new ResultedTestSummaryContainer();
        resTestSumCol.add(resTestSum);
        when(customRepository.getSusceptibilityResultedTestSummary("COMP", 12L))
                .thenReturn(resTestSumCol);

        observationSummaryService.getTestAndSusceptibilities(typeCode, observationUid, labRepEvent, labRepSumm);

        verify(customRepository, times(1)).getTestAndSusceptibilities(typeCode, 10L, labRepEvent, labRepSumm);

    }

    @Test
    void getAssociatedInvList_Success() throws DataProcessingException {
        long uid = 10L;
        String sourceClassCode = "TEST";

        when(queryHelper.getDataAccessWhereClause(NBSBOLookup.INVESTIGATION, "VIEW", ""))
                .thenReturn("TEST");

        when(customRepository.getAssociatedInvList(uid, sourceClassCode, "TEST"))
                .thenReturn(new HashMap<>());

        observationSummaryService.getAssociatedInvList(uid, sourceClassCode);

        verify(customRepository, times(1)).getAssociatedInvList(eq(uid), eq(sourceClassCode), any());

    }

    @Test
    void getAssociatedInvList_Success_2() throws DataProcessingException {
        long uid = 10L;
        String sourceClassCode = "TEST";

        when(queryHelper.getDataAccessWhereClause(NBSBOLookup.INVESTIGATION, "VIEW", ""))
                .thenReturn(null);

        when(customRepository.getAssociatedInvList(uid, sourceClassCode, "TEST"))
                .thenReturn(new HashMap<>());

        observationSummaryService.getAssociatedInvList(uid, sourceClassCode);

        verify(customRepository, times(1)).getAssociatedInvList(eq(uid), eq(sourceClassCode), any());

    }

    @Test
    void getAssociatedInvList_Exception()  {
        long uid = 10L;
        String sourceClassCode = "TEST";

        when(queryHelper.getDataAccessWhereClause(NBSBOLookup.INVESTIGATION, "VIEW", ""))
                .thenThrow(new RuntimeException("TEST"));



        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            observationSummaryService.getAssociatedInvList(uid, sourceClassCode);
        });
        assertEquals("TEST", thrown.getMessage());
    }
}
