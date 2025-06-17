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

import java.util.*;

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

    @Test
    void retrieveTreatmentSummaryVOForInv_ReturnsEmptyMap() {
        Long publicHealthUid = 12345L;

        Map<Object, Object> result = retrieveSummaryService.retrieveTreatmentSummaryVOForInv(publicHealthUid);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }



    @Test
    void retrieveNotificationSummaryListForInvestigation_DataAccessClauseNull() throws DataProcessingException {
        Long publicHealthUid = 123L;

        when(queryHelper.getDataAccessWhereClause(any(), any(), any())).thenReturn(null);
        when(customRepository.retrieveNotificationSummaryListForInvestigation(eq(publicHealthUid), any()))
                .thenReturn(List.of());

        NotificationSummaryContainer mockContainer = new NotificationSummaryContainer();
        mockContainer.setCdNotif(NEDSSConstant.CLASS_CD_NOTF);
        mockContainer.setCd("cd");
        mockContainer.setCaseClassCd("class");
        mockContainer.setRecipient("recipient");

        when(catchingValueService.getCodedValuesCallRepos(any())).thenReturn("value");
        when(catchingValueService.getCodeDescTxtForCd(any(), any())).thenReturn("desc");

        Collection<Object> result = retrieveSummaryService.retrieveNotificationSummaryListForInvestigation1(publicHealthUid);

        assertTrue(result.isEmpty());
    }

    @Test
    void retrieveNotificationSummaryListForInvestigation_SetsRecipientToAdminFlagCDC_WhenNndIndIsYes() throws DataProcessingException {
        Long publicHealthUid = 789L;

        NotificationSummaryContainer container = new NotificationSummaryContainer();
        container.setRecipient(null); // trigger the else block
        container.setNndInd(NEDSSConstant.YES);
        container.setCdNotif(NEDSSConstant.CLASS_CD_NOTF); // skip setting caseReport
        container.setCd("someCd");
        container.setCaseClassCd("someClass");

        when(queryHelper.getDataAccessWhereClause(any(), any(), any())).thenReturn("");
        when(customRepository.retrieveNotificationSummaryListForInvestigation(eq(publicHealthUid), any()))
                .thenReturn(List.of(container))  // first SQL returns one row
                .thenReturn(Collections.emptyList()); // second SQL returns nothing

        when(catchingValueService.getCodedValuesCallRepos(any())).thenReturn("label");
        when(catchingValueService.getCodeDescTxtForCd(any(), any())).thenReturn("desc");

        Collection<Object> result = retrieveSummaryService.retrieveNotificationSummaryListForInvestigation1(publicHealthUid);

        assertEquals(1, result.size());
        NotificationSummaryContainer resultContainer = (NotificationSummaryContainer) result.iterator().next();
        assertEquals(NEDSSConstant.ADMINFLAGCDC, resultContainer.getRecipient());
    }

    @Test
    void retrieveNotificationSummaryListForInvestigation_SetsRecipientToLocalDesc_WhenNndIndIsNotYesAndRecipientIsNull() throws DataProcessingException {
        Long publicHealthUid = 20250617L;

        NotificationSummaryContainer container = new NotificationSummaryContainer();
        container.setRecipient(null); // <-- trigger null check
        container.setNndInd("N");     // <-- not equal to YES
        container.setCdNotif(NEDSSConstant.CLASS_CD_NOTF); // skip setting caseReport
        container.setCd("cd");
        container.setCaseClassCd("caseClass");

        when(queryHelper.getDataAccessWhereClause(any(), any(), any())).thenReturn("");
        when(customRepository.retrieveNotificationSummaryListForInvestigation(eq(publicHealthUid), any()))
                .thenReturn(List.of(container)) // First call
                .thenReturn(Collections.emptyList()); // Second call to break loop

        when(catchingValueService.getCodedValuesCallRepos(any())).thenReturn("label");
        when(catchingValueService.getCodeDescTxtForCd(any(), any())).thenReturn("desc");

        Collection<Object> result = retrieveSummaryService.retrieveNotificationSummaryListForInvestigation1(publicHealthUid);

        assertEquals(1, result.size());
        NotificationSummaryContainer resultContainer = (NotificationSummaryContainer) result.iterator().next();
        assertEquals(NEDSSConstant.LOCAl_DESC, resultContainer.getRecipient());
    }







    @Test
    void retrieveNotificationSummaryListForInvestigation_DataAccessClauseNullOri() throws DataProcessingException {
        Long publicHealthUid = 123L;

        when(queryHelper.getDataAccessWhereClause(any(), any(), any())).thenReturn(null);
        when(customRepository.retrieveNotificationSummaryListForInvestigation(eq(publicHealthUid), any()))
                .thenReturn(List.of());

        NotificationSummaryContainer mockContainer = new NotificationSummaryContainer();
        mockContainer.setCdNotif(NEDSSConstant.CLASS_CD_NOTF);
        mockContainer.setCd("cd");
        mockContainer.setCaseClassCd("class");
        mockContainer.setRecipient("recipient");

        when(catchingValueService.getCodedValuesCallRepos(any())).thenReturn("value");
        when(catchingValueService.getCodeDescTxtForCd(any(), any())).thenReturn("desc");

        Collection<Object> result = retrieveSummaryService.retrieveNotificationSummaryListForInvestigation(publicHealthUid);

        assertTrue(result.isEmpty());
    }

    @Test
    void retrieveNotificationSummaryListForInvestigation_SetsRecipientToAdminFlagCDC_WhenNndIndIsYesOri() throws DataProcessingException {
        Long publicHealthUid = 789L;

        NotificationSummaryContainer container = new NotificationSummaryContainer();
        container.setRecipient(null); // trigger the else block
        container.setNndInd(NEDSSConstant.YES);
        container.setCdNotif(NEDSSConstant.CLASS_CD_NOTF); // skip setting caseReport
        container.setCd("someCd");
        container.setCaseClassCd("someClass");

        when(queryHelper.getDataAccessWhereClause(any(), any(), any())).thenReturn("");
        when(customRepository.retrieveNotificationSummaryListForInvestigation(eq(publicHealthUid), any()))
                .thenReturn(List.of(container))  // first SQL returns one row
                .thenReturn(Collections.emptyList()); // second SQL returns nothing

        when(catchingValueService.getCodedValuesCallRepos(any())).thenReturn("label");
        when(catchingValueService.getCodeDescTxtForCd(any(), any())).thenReturn("desc");

        Collection<Object> result = retrieveSummaryService.retrieveNotificationSummaryListForInvestigation(publicHealthUid);

        assertEquals(1, result.size());
        NotificationSummaryContainer resultContainer = (NotificationSummaryContainer) result.iterator().next();
        assertEquals(NEDSSConstant.ADMINFLAGCDC, resultContainer.getRecipient());
    }

    @Test
    void retrieveNotificationSummaryListForInvestigation_SetsRecipientToLocalDesc_WhenNndIndIsNotYesAndRecipientIsNullOri() throws DataProcessingException {
        Long publicHealthUid = 20250617L;

        NotificationSummaryContainer container = new NotificationSummaryContainer();
        container.setRecipient(null); // <-- trigger null check
        container.setNndInd("N");     // <-- not equal to YES
        container.setCdNotif(NEDSSConstant.CLASS_CD_NOTF); // skip setting caseReport
        container.setCd("cd");
        container.setCaseClassCd("caseClass");

        when(queryHelper.getDataAccessWhereClause(any(), any(), any())).thenReturn("");
        when(customRepository.retrieveNotificationSummaryListForInvestigation(eq(publicHealthUid), any()))
                .thenReturn(List.of(container)) // First call
                .thenReturn(Collections.emptyList()); // Second call to break loop

        when(catchingValueService.getCodedValuesCallRepos(any())).thenReturn("label");
        when(catchingValueService.getCodeDescTxtForCd(any(), any())).thenReturn("desc");

        Collection<Object> result = retrieveSummaryService.retrieveNotificationSummaryListForInvestigation(publicHealthUid);

        assertEquals(1, result.size());
        NotificationSummaryContainer resultContainer = (NotificationSummaryContainer) result.iterator().next();
        assertEquals(NEDSSConstant.LOCAl_DESC, resultContainer.getRecipient());
    }

    @Test
    void notificationSummaryOnInvestigation_UsesRetrieveNotificationSummaryListForInvestigation1_WhenCaseClassCdIsNull() throws DataProcessingException {
        Long publicHealthUid = 999L;

        // Setup DTO with null caseClassCd
        PublicHealthCaseDto phcDto = new PublicHealthCaseDto();
        phcDto.setPublicHealthCaseUid(publicHealthUid);
        phcDto.setCaseClassCd(null); // <-- forces else block
        phcDto.setCd("123");
        phcDto.setCdDescTxt("Some Description");

        PublicHealthCaseContainer phcContainer = new PublicHealthCaseContainer();
        phcContainer.setThePublicHealthCaseDto(phcDto);

        NotificationSummaryContainer notification = new NotificationSummaryContainer();
        notification.setRecordStatusCd(NEDSSConstant.NOTIFICATION_APPROVED_CODE); // trigger assoc flag
        notification.setCdNotif(NEDSSConstant.CLASS_CD_NOTF);
        notification.setCd("cd");
        notification.setCaseClassCd("caseClass");

        when(customRepository.retrieveNotificationSummaryListForInvestigation(eq(publicHealthUid), any()))
                .thenReturn(List.of(notification)) // First loop
                .thenReturn(Collections.emptyList()); // Second loop to exit

        when(queryHelper.getDataAccessWhereClause(any(), any(), any())).thenReturn("");
        when(catchingValueService.getCodedValuesCallRepos(any())).thenReturn("label");
        when(catchingValueService.getCodeDescTxtForCd(any(), any())).thenReturn("desc");

        InvestigationContainer proxy = new InvestigationContainer();
        Collection<Object> result = retrieveSummaryService.notificationSummaryOnInvestigation(phcContainer, proxy);

        assertEquals(1, result.size());
        assertTrue(proxy.isAssociatedNotificationsInd());
        verify(queryHelper).getDataAccessWhereClause(any(), any(), any()); // ensure retrieveNotificationSummaryListForInvestigation1 was triggered
    }

    @Test
    void notificationSummaryOnInvestigation_AssociatedIndTrue_WhenAutoResendIndTrue_AndPamProxy() throws DataProcessingException {
        // Setup PublicHealthCaseContainer with no caseClassCd (to trigger retrieveNotificationSummaryListForInvestigation1)
        PublicHealthCaseDto phcDto = new PublicHealthCaseDto();
        phcDto.setPublicHealthCaseUid(3L);
        phcDto.setCd("cd");
        phcDto.setCdDescTxt("desc");

        PublicHealthCaseContainer phcContainer = new PublicHealthCaseContainer();
        phcContainer.setThePublicHealthCaseDto(phcDto);

        // Setup NotificationSummaryContainer with autoResendInd = "T"
        NotificationSummaryContainer summary = new NotificationSummaryContainer();
        summary.setRecordStatusCd("ANY"); // not APPROVED or PENDING
        summary.setAutoResendInd("T");    // trigger the third condition
        summary.setCdNotif(NEDSSConstant.CLASS_CD_NOTF); // skip caseReport logic

        // Mocks
        when(queryHelper.getDataAccessWhereClause(any(), any(), any())).thenReturn("");
        when(customRepository.retrieveNotificationSummaryListForInvestigation(anyLong(), any()))
                .thenReturn(List.of(summary)) // First call returns summary
                .thenReturn(Collections.emptyList()); // Second call exits loop
        when(catchingValueService.getCodedValuesCallRepos(any())).thenReturn("label");
        when(catchingValueService.getCodeDescTxtForCd(any(), any())).thenReturn("desc");

        // PamProxyContainer used instead of InvestigationContainer
        PamProxyContainer pamProxy = new PamProxyContainer();

        // Act
        Collection<Object> result = retrieveSummaryService.notificationSummaryOnInvestigation(phcContainer, pamProxy);

        // Assert
        assertTrue(pamProxy.isAssociatedNotificationsInd(), "PamProxyContainer.associatedNotificationsInd should be true");
    }




}
