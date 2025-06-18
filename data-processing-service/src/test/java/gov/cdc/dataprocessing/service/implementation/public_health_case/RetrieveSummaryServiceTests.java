package gov.cdc.dataprocessing.service.implementation.public_health_case;

import gov.cdc.dataprocessing.constant.elr.NBSBOLookup;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.*;
import gov.cdc.dataprocessing.model.dto.notification.NotificationDto;
import gov.cdc.dataprocessing.model.dto.phc.PublicHealthCaseDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.CustomRepository;
import gov.cdc.dataprocessing.service.interfaces.cache.ICatchingValueDpService;
import gov.cdc.dataprocessing.service.model.auth_user.AuthUserProfileInfo;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.generic_helper.PrepareAssocModelHelper;
import gov.cdc.dataprocessing.utilities.component.notification.NotificationRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.public_health_case.PublicHealthCaseRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.sql.QueryHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import static gov.cdc.dataprocessing.constant.elr.NEDSSConstant.CASE_CLASS_CODE_SET_NM;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class RetrieveSummaryServiceTests {
    @Mock
    private PublicHealthCaseRepositoryUtil publicHealthCaseRepositoryUtil;
    @Mock
    private QueryHelper queryHelper;
    @Mock
    private CustomRepository customRepository;
    @Mock
    private ICatchingValueDpService catchingValueService;
    @Mock
    private PrepareAssocModelHelper prepareAssocModelHelper;
    @Mock
    private NotificationRepositoryUtil notificationRepositoryUtil;
    @InjectMocks
    private RetrieveSummaryService retrieveSummaryService;
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
        Mockito.reset(publicHealthCaseRepositoryUtil, queryHelper, customRepository,
                catchingValueService, prepareAssocModelHelper, notificationRepositoryUtil, authUtil);
    }


    @Test
    void retrieveDocumentSummaryVOForInv_Success() {
        long uid = 10L;
        when(customRepository.retrieveDocumentSummaryVOForInv(10L)).thenReturn(
                new HashMap<>()
        );
        retrieveSummaryService.retrieveDocumentSummaryVOForInv(uid);
        verify(customRepository, times(1)).retrieveDocumentSummaryVOForInv(
                any()
        );

    }

    @Test
    void retrieveDocumentSummaryVOForInv_Exception() {
        long uid = 10L;
        when(customRepository.retrieveDocumentSummaryVOForInv(10L)).thenThrow(
              new RuntimeException("TEST")
        );
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            retrieveSummaryService.retrieveDocumentSummaryVOForInv(uid);
        });

        assertNotNull(thrown);
        assertEquals("TEST", thrown.getMessage());

    }

    @Test
    void notificationSummaryOnInvestigation_Success() throws DataProcessingException {
        PublicHealthCaseContainer phcConn = new PublicHealthCaseContainer();
        InvestigationContainer object = new InvestigationContainer();

        var phcDt = new PublicHealthCaseDto();
        phcDt.setPublicHealthCaseUid(10L);
        phcDt.setCaseClassCd("CASE");
        phcDt.setCd("CODE");
        phcDt.setCdDescTxt("CODE");
        phcConn.setThePublicHealthCaseDto(phcDt);

        var notSumCol = new ArrayList< NotificationSummaryContainer >();
        var notSum = new NotificationSummaryContainer();
        notSum.setCaseClassCd("Y");
        notSum.setCd("Y");
        notSum.setCdNotif(NEDSSConstant.CLASS_CD_NOTF);
        notSum.setRecipient("Y");
        notSum.setRecordStatusCd(NEDSSConstant.NOTIFICATION_APPROVED_CODE);
        notSum.setCaseReport(true);
        notSum.setIsHistory("T");
        notSumCol.add(notSum);
        notSum = new NotificationSummaryContainer();
        notSum.setCaseClassCd("Y");
        notSum.setCd("Y");
        notSum.setCdNotif(NEDSSConstant.CLASS_CD_NOTF);
        notSum.setRecipient("Y");
        notSum.setRecordStatusCd(NEDSSConstant.NOTIFICATION_PENDING_CODE);
        notSum.setCaseReport(true);
        notSum.setIsHistory("T");
        notSumCol.add(notSum);
        notSum = new NotificationSummaryContainer();
        notSum.setCaseClassCd("Y");
        notSum.setCd("Y");
        notSum.setCdNotif(NEDSSConstant.CLASS_CD_NOTF);
        notSum.setRecipient("Y");
        notSum.setRecordStatusCd(NEDSSConstant.PENDING_APPROVAL_STATUS);
        notSum.setCaseReport(true);
        notSum.setIsHistory("T");
        notSumCol.add(notSum);
        notSum = new NotificationSummaryContainer();
        notSum.setCaseClassCd("Y");
        notSum.setCd("Y");
        notSum.setCdNotif(NEDSSConstant.CLASS_CD_EXP_NOTF);
        notSum.setRecipient("Y");
        notSum.setRecordStatusCd(NEDSSConstant.NOTIFICATION_MESSAGE_FAILED);
        notSum.setCaseReport(true);
        notSum.setIsHistory("T");
        notSumCol.add(notSum);

        when(customRepository.retrieveNotificationSummaryListForInvestigation(eq(10L), any())).thenReturn(notSumCol);

        var map = new HashMap<String, String>();
        map.put("Y", "TXT");
        when(catchingValueService.getCodedValuesCallRepos(CASE_CLASS_CODE_SET_NM)).thenReturn("TXT");

        when(catchingValueService.getCodeDescTxtForCd(NEDSSConstant.CLASS_CD_NOTF,"NBS_DOC_PURPOSE" ))
                .thenReturn("TEST");


        var test = retrieveSummaryService.notificationSummaryOnInvestigation(phcConn, object);

        assertNotNull(test);
        assertEquals(8, test.size());
    }

    @Test
    void getAssociatedDocumentList_Success()  {
        long uid = 10L;
        String targetClassCd= "CODE";
        String sourceClassCd = "CODE";

        when(queryHelper.getDataAccessWhereClause(NBSBOLookup.DOCUMENT, "VIEW", "")).thenReturn("BLAH");
        when(customRepository.getAssociatedDocumentList(eq(uid), eq(targetClassCd), eq(sourceClassCd), any()))
                .thenReturn(new HashMap<>());

        var test = retrieveSummaryService.getAssociatedDocumentList(uid, targetClassCd, sourceClassCd);
        assertNotNull(test);
    }

    @Test
    void getAssociatedDocumentList_Success_2()  {
        long uid = 10L;
        String targetClassCd= "CODE";
        String sourceClassCd = "CODE";

        when(queryHelper.getDataAccessWhereClause(NBSBOLookup.DOCUMENT, "VIEW", ""))
                .thenReturn(null);
        when(customRepository.getAssociatedDocumentList(eq(uid), eq(targetClassCd), eq(sourceClassCd), any()))
                .thenReturn(new HashMap<>());

        var test = retrieveSummaryService.getAssociatedDocumentList(uid, targetClassCd, sourceClassCd);
        assertNotNull(test);
    }

    @Test
    void getAssociatedDocumentList_Exception() {
        long uid = 10L;
        String targetClassCd= "CODE";
        String sourceClassCd = "CODE";

        when(queryHelper.getDataAccessWhereClause(NBSBOLookup.DOCUMENT, "VIEW", ""))
                .thenThrow(new RuntimeException("TEST"));
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            retrieveSummaryService.getAssociatedDocumentList(uid, targetClassCd, sourceClassCd);
        });

        assertNotNull(thrown);
        assertEquals("TEST", thrown.getMessage());

    }


    @Test
    void updateNotification_Success() throws DataProcessingException {
        Long notificationUid = 10L;
        String businessTriggerCd = "TRIGGER";
        String phcCd = "CODE";
        String phcClassCd = "CODE";
        String progAreaCd = "CODE";
        String jurisdictionCd = "CODE";
        String sharedInd = "Y";

        var notConn = new NotificationContainer();
        var notDt = new NotificationDto();
        notDt.setVersionCtrlNbr(1);
        notConn.setTheNotificationDT(notDt);
        when(notificationRepositoryUtil.getNotificationContainer(10L))
                .thenReturn(notConn);

        when(prepareAssocModelHelper.prepareVO(
                any(), eq(NBSBOLookup.NOTIFICATION), eq(businessTriggerCd),
                eq("Notification"), eq(NEDSSConstant.BASE), eq(1)))
                .thenReturn(notDt);

        retrieveSummaryService.updateNotification(notificationUid,
                businessTriggerCd, phcCd, phcClassCd, progAreaCd, jurisdictionCd, sharedInd);

        verify(notificationRepositoryUtil, times(1)).setNotification(any());

    }

    @Test
    void updateNotification_Exception() throws DataProcessingException {
        Long notificationUid = 10L;
        String businessTriggerCd = "TRIGGER";
        String phcCd = "CODE";
        String phcClassCd = "CODE";
        String progAreaCd = "CODE";
        String jurisdictionCd = "CODE";
        String sharedInd = "Y";

        var notConn = new NotificationContainer();
        var notDt = new NotificationDto();
        notDt.setVersionCtrlNbr(1);
        notConn.setTheNotificationDT(notDt);
        when(notificationRepositoryUtil.getNotificationContainer(10L))
                .thenReturn(notConn);

        when(prepareAssocModelHelper.prepareVO(
                any(), eq(NBSBOLookup.NOTIFICATION), eq(businessTriggerCd),
                eq("Notification"), eq(NEDSSConstant.BASE), eq(1)))
                .thenThrow(
                        new RuntimeException("TEST")
                );



        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            retrieveSummaryService.updateNotification(notificationUid,
                    businessTriggerCd, phcCd, phcClassCd, progAreaCd, jurisdictionCd, sharedInd);
        });

        assertNotNull(thrown);
        assertEquals("TEST", thrown.getMessage());
    }

    @Test
    void retrieveNotificationSummaryListForInvestigation1_Success() throws DataProcessingException {
        long publicHealthUID = 10L;

        when(queryHelper.getDataAccessWhereClause(
                NBSBOLookup.INVESTIGATION,
                "VIEW",
                "Notification"))
                .thenReturn("BLAH");

        var map = new HashMap<String, String>();
        map.put("1", "1");
        when(catchingValueService.getCodedValuesCallRepos(
                "PHC_CLASS"))
                .thenReturn("1");

        when(catchingValueService.getCodedValuesCallRepos(
                "PHC_TYPE"))
                .thenReturn("1");

        var notSumCol = new ArrayList<NotificationSummaryContainer>();
        var notSum = new NotificationSummaryContainer();
        notSum.setCaseClassCd("1");
        notSum.setCaseClassCdTxt("1");
        notSum.setCd("1");
        notSum.setCdTxt("1");
        notSum.setCdNotif("1");
        notSum.setRecipient("1");
        notSumCol.add(notSum);

        when(customRepository.retrieveNotificationSummaryListForInvestigation(
                eq(10L), any()))
                .thenReturn(notSumCol);

        when(catchingValueService.getCodeDescTxtForCd(
                "1", "NBS_DOC_PURPOSE"))
                .thenReturn("1");

        var test = retrieveSummaryService.retrieveNotificationSummaryListForInvestigation1(publicHealthUID);

        assertNotNull(test);
        assertEquals(2, test.size());
    }

    @Test
    void notificationSummaryOnInvestigationProcessingNotificationCol_Inves() {
        Collection<Object> theNotificationSummaryVOCollection = new ArrayList<>();
        var noConn = new NotificationSummaryContainer();
        noConn.setCaseReport(true);
        noConn.setRecordStatusCd(NEDSSConstant.NOTIFICATION_APPROVED_CODE);
        noConn.setIsHistory("F");
        noConn.setCdNotif(NEDSSConstant.CLASS_CD_EXP_NOTF);
        theNotificationSummaryVOCollection.add(noConn);


        NotificationSummaryContainer notificationSummaryVO = new NotificationSummaryContainer();
        notificationSummaryVO.setCaseReport(true);
        notificationSummaryVO.setRecordStatusCd(NEDSSConstant.NOTIFICATION_APPROVED_CODE);
        notificationSummaryVO.setIsHistory("F");
        notificationSummaryVO.setCdNotif(NEDSSConstant.CLASS_CD_EXP_NOTF);
        Object object = new InvestigationContainer();

        var test = retrieveSummaryService.notificationSummaryOnInvestigationProcessingNotificationCol(theNotificationSummaryVOCollection,
                notificationSummaryVO, object);

        assertNotNull(test);
        assertEquals(1, test.size());
    }

    @Test
    void notificationSummaryOnInvestigationProcessingNotificationCol_PAM() {
        Collection<Object> theNotificationSummaryVOCollection = new ArrayList<>();
        var noConn = new NotificationSummaryContainer();
        noConn.setCaseReport(true);
        noConn.setRecordStatusCd(NEDSSConstant.NOTIFICATION_APPROVED_CODE);
        noConn.setIsHistory("F");
        noConn.setCdNotif(NEDSSConstant.CLASS_CD_EXP_NOTF);
        theNotificationSummaryVOCollection.add(noConn);


        NotificationSummaryContainer notificationSummaryVO = new NotificationSummaryContainer();
        notificationSummaryVO.setCaseReport(true);
        notificationSummaryVO.setRecordStatusCd(NEDSSConstant.NOTIFICATION_APPROVED_CODE);
        notificationSummaryVO.setIsHistory("F");
        notificationSummaryVO.setCdNotif(NEDSSConstant.CLASS_CD_EXP_NOTF);
        Object object = new PamProxyContainer();

        var test = retrieveSummaryService.notificationSummaryOnInvestigationProcessingNotificationCol(theNotificationSummaryVOCollection,
                notificationSummaryVO, object);

        assertNotNull(test);
        assertEquals(1, test.size());
    }

    @Test
    void notificationSummaryOnInvestigationProcessingNotificationCol_Page() {
        Collection<Object> theNotificationSummaryVOCollection = new ArrayList<>();
        var noConn = new NotificationSummaryContainer();
        noConn.setCaseReport(true);
        noConn.setRecordStatusCd(NEDSSConstant.NOTIFICATION_APPROVED_CODE);
        noConn.setIsHistory("F");
        noConn.setCdNotif(NEDSSConstant.CLASS_CD_EXP_NOTF);
        theNotificationSummaryVOCollection.add(noConn);


        NotificationSummaryContainer notificationSummaryVO = new NotificationSummaryContainer();
        notificationSummaryVO.setCaseReport(true);
        notificationSummaryVO.setRecordStatusCd(NEDSSConstant.NOTIFICATION_APPROVED_CODE);
        notificationSummaryVO.setIsHistory("F");
        notificationSummaryVO.setCdNotif(NEDSSConstant.CLASS_CD_EXP_NOTF);
        Object object = new PageActProxyContainer();

        var test = retrieveSummaryService.notificationSummaryOnInvestigationProcessingNotificationCol(theNotificationSummaryVOCollection,
                notificationSummaryVO, object);

        assertNotNull(test);
        assertEquals(1, test.size());
    }
}
