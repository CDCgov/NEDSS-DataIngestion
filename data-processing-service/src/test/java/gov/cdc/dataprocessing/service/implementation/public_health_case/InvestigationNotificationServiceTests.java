package gov.cdc.dataprocessing.service.implementation.public_health_case;

import gov.cdc.dataprocessing.constant.EdxPHCRConstants;
import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.base.BasePamContainer;
import gov.cdc.dataprocessing.model.container.model.*;
import gov.cdc.dataprocessing.model.dto.act.ActIdDto;
import gov.cdc.dataprocessing.model.dto.entity.EntityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.locator.PostalLocatorDto;
import gov.cdc.dataprocessing.model.dto.log.EDXActivityDetailLogDto;
import gov.cdc.dataprocessing.model.dto.nbs.NbsQuestionMetadata;
import gov.cdc.dataprocessing.model.dto.notification.NotificationDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.model.dto.person.PersonDto;
import gov.cdc.dataprocessing.model.dto.person.PersonRaceDto;
import gov.cdc.dataprocessing.model.dto.phc.PublicHealthCaseDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.repository.nbs.odse.model.custom_model.QuestionRequiredNnd;
import gov.cdc.dataprocessing.repository.nbs.odse.model.person.Person;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.CustomNbsQuestionRepository;
import gov.cdc.dataprocessing.service.implementation.cache.CacheApiService;
import gov.cdc.dataprocessing.service.interfaces.notification.INotificationService;
import gov.cdc.dataprocessing.service.interfaces.public_health_case.IInvestigationService;
import gov.cdc.dataprocessing.service.model.auth_user.AuthUserProfileInfo;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static gov.cdc.dataprocessing.constant.elr.NEDSSConstant.PHCR_IMPORT_SRT;
import static gov.cdc.dataprocessing.constant.elr.NEDSSConstant.STATE_STR;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class InvestigationNotificationServiceTests {
    @Mock
    private IInvestigationService investigationService;
    @Mock
    private INotificationService notificationService;
    @Mock
    private CustomNbsQuestionRepository customNbsQuestionRepository;
    @Mock
    private CacheApiService cacheApiService;
    @InjectMocks
    private InvestigationNotificationService investigationNotificationService;
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
        Mockito.reset(investigationService, notificationService, customNbsQuestionRepository, authUtil);
    }


    @Test
    void sendNotification_Success_Page() throws DataProcessingException {
        var obj = new PageActProxyContainer();
        var phcDt = new PublicHealthCaseDto();
        phcDt.setCaseClassCd("CASE");
        phcDt.setCd("CODE");
        phcDt.setPublicHealthCaseUid(10L);
        phcDt.setProgAreaCd("PROG");
        phcDt.setJurisdictionCd("JUS");
        phcDt.setSharedInd("Y");
        PublicHealthCaseContainer ohcC = new PublicHealthCaseContainer();
        ohcC.setThePublicHealthCaseDto(phcDt);
        obj.setPublicHealthCaseContainer(ohcC);
        String nndComment = "COM";
        var test = investigationNotificationService.sendNotification(obj, nndComment);

        assertEquals("Failure", test.getLogType());
    }

    @Test
    void sendNotification_Success_Pam() throws DataProcessingException {
        var obj = new PamProxyContainer();
        var phcDt = new PublicHealthCaseDto();
        phcDt.setCaseClassCd("CASE");
        phcDt.setCd("CODE");
        phcDt.setPublicHealthCaseUid(10L);
        phcDt.setProgAreaCd("PROG");
        phcDt.setJurisdictionCd("JUS");
        phcDt.setSharedInd("Y");
        PublicHealthCaseContainer ohcC = new PublicHealthCaseContainer();
        ohcC.setThePublicHealthCaseDto(phcDt);
        obj.setPublicHealthCaseContainer(ohcC);
        String nndComment = "COM";
        var test = investigationNotificationService.sendNotification(obj, nndComment);

        assertEquals("Failure", test.getLogType());
    }

    @Test
    void sendNotification_Exception()  {
        var obj = new Person();
        String nndComment = "COM";
        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            investigationNotificationService.sendNotification(obj, nndComment);

        });

        assertNotNull(thrown);
        assertEquals("Cannot create Notification for unknown page type: gov.cdc.dataprocessing.repository.nbs.odse.model.person.Person", thrown.getMessage());
    }

    @Test
    void sendNotification_Success_PHC() throws DataProcessingException {
        PublicHealthCaseContainer obj = new PublicHealthCaseContainer();
        var phcDt = new PublicHealthCaseDto();
        phcDt.setCaseClassCd("CASE");
        phcDt.setCd("CODE");
        phcDt.setPublicHealthCaseUid(10L);
        phcDt.setProgAreaCd("PROG");
        phcDt.setJurisdictionCd("JUS");
        phcDt.setSharedInd("Y");
        obj.setThePublicHealthCaseDto(phcDt);
        String nndComment = "COM";

   //     SrteCache.investigationFormConditionCode.put("CODE", "investigationFormCd");

        var colRetriQuest = new ArrayList<QuestionRequiredNnd>();
        var colRetri = new QuestionRequiredNnd();
        colRetri.setNbsQuestionUid(11L);
        colRetri.setDataLocation("NBS_Answer.LOC");
        colRetri.setQuestionLabel("LABEL");
        colRetri.setQuestionIdentifier("11");
        colRetriQuest.add(colRetri);
        colRetri = new QuestionRequiredNnd();
        colRetri.setNbsQuestionUid(14L);
        colRetri.setDataLocation("public_health_case.cd");
        colRetri.setQuestionLabel("LABEL");
        colRetri.setQuestionIdentifier("14");
        colRetriQuest.add(colRetri);
        colRetri = new QuestionRequiredNnd();
        colRetri.setNbsQuestionUid(15L);
        colRetri.setDataLocation("person.personUid");
        colRetri.setQuestionLabel("LABEL");
        colRetri.setQuestionIdentifier("15");
        colRetriQuest.add(colRetri);
        colRetri = new QuestionRequiredNnd();
        colRetri.setNbsQuestionUid(16L);
        colRetri.setDataLocation("postal_locator.personUid");
        colRetri.setQuestionLabel("LABEL");
        colRetri.setQuestionIdentifier("16");
        colRetriQuest.add(colRetri);
        colRetri = new QuestionRequiredNnd();
        colRetri.setNbsQuestionUid(17L);
        colRetri.setDataLocation("person_race.personUid");
        colRetri.setQuestionLabel("LABEL");
        colRetri.setQuestionIdentifier("17");
        colRetriQuest.add(colRetri);
        colRetri = new QuestionRequiredNnd();
        colRetri.setNbsQuestionUid(18L);
        colRetri.setDataLocation("act_id.actUid");
        colRetri.setQuestionLabel(STATE_STR);
        colRetri.setQuestionIdentifier("18");
        colRetriQuest.add(colRetri);
        colRetri = new QuestionRequiredNnd();
        colRetri.setNbsQuestionUid(19L);
        colRetri.setDataLocation("nbs_case_answer.actUid");
        colRetri.setQuestionLabel("LABEL");
        colRetri.setQuestionIdentifier("19");
        colRetriQuest.add(colRetri);



        when(customNbsQuestionRepository.retrieveQuestionRequiredNnd("investigationFormCd")).thenReturn(colRetriQuest);

        var pageAct = new PageActProxyContainer();
        var base = new BasePamContainer();
        Map<Object, Object> pamAnswerDTMap = new HashMap<>();
        base.setPamAnswerDTMap(pamAnswerDTMap);
        pageAct.setPageVO(base);

        var patCol = new ArrayList<ParticipationDto>();
        var pat = new ParticipationDto();
        pat.setTypeCd("SubjOfPHC");
        pat.setSubjectEntityUid(12L);
        patCol.add(pat);
        obj.setTheParticipationDTCollection(patCol);

        var actIdCol = new ArrayList<ActIdDto>();
        var actId = new ActIdDto();
        actId.setTypeCd(NEDSSConstant.ACT_ID_STATE_TYPE_CD);
        actId.setRootExtensionTxt("");
        actIdCol.add(actId);
        obj.setTheActIdDTCollection(actIdCol);

        pageAct.setPublicHealthCaseContainer(obj);

        var personCol = new ArrayList<PersonContainer>();
        var personConn = new PersonContainer();
        var personDt = new PersonDto();
        personDt.setPersonUid(12L);
        personConn.setThePersonDto(personDt);
        var entityLocateCol = new ArrayList<EntityLocatorParticipationDto>();
        var entityLocat = new EntityLocatorParticipationDto();
        entityLocat.setUseCd("CODE");
        entityLocat.setClassCd("PST");
        entityLocat.setThePostalLocatorDto(null);
        entityLocateCol.add(entityLocat);
        personConn.setTheEntityLocatorParticipationDtoCollection(entityLocateCol);

        var personRaceCol = new ArrayList<PersonRaceDto>();
        var personRace = new PersonRaceDto();
        personRaceCol.add(personRace);
        personConn.setThePersonRaceDtoCollection(personRaceCol);

        personCol.add(personConn);
        pageAct.setThePersonContainerCollection(personCol);
        when(investigationService.getPageProxyVO(NEDSSConstant.CASE, 10L)).thenReturn(pageAct);

        var test = investigationNotificationService.sendNotification(obj, nndComment);

        assertNotNull(test);
        assertEquals(PHCR_IMPORT_SRT, test.getRecordName());
    }


    @Test
    void validatePAMNotficationRequiredFieldsGivenPageProxy_Success_PAM_1stCond() throws DataProcessingException {
        PamProxyContainer pageObj = new PamProxyContainer();
        pageObj.setTheParticipationDTCollection(new ArrayList<>());
        pageObj.setThePersonVOCollection(new ArrayList<>());
        var basePam = new BasePamContainer();
        basePam.setPamAnswerDTMap(new HashMap<>());
        pageObj.setPamVO(basePam);

        var phc = new PublicHealthCaseContainer();
        phc.setTheActIdDTCollection(new ArrayList<>());
        pageObj.setPublicHealthCaseContainer(phc);
        Long publicHealthCaseUid = 10L;
        Map<Object, Object>  reqFields = new HashMap<>();
        String formCd = NEDSSConstant.INV_FORM_RVCT;


        var test = investigationNotificationService.validatePAMNotficationRequiredFieldsGivenPageProxy(pageObj,
                publicHealthCaseUid, reqFields, formCd);

        assertNull(test);
    }


    @Test
    void testSendProxyToEJB_ExceptionThrown() throws DataProcessingException {
        NotificationProxyContainer proxy = createNotificationProxy("INV_FORM_CD", "AREA_CD", 123L);
        Object dummyPageObj = new Object();

        when(cacheApiService.getSrteCacheString(any(), any())).thenThrow(new RuntimeException("DB error"));

        EDXActivityDetailLogDto result = investigationNotificationService.sendProxyToEJB(proxy, dummyPageObj);

        assertEquals("Failure", result.getLogType());
        assertTrue(result.getComment().contains("java.lang.RuntimeException"));
    }

    @Test
    void sendProxyToEJB_Success_NoValidationErrors() throws DataProcessingException {
        // Setup test data
        NotificationProxyContainer notificationProxyVO = createNotificationProxy("TEST_CODE", "PROG_AREA", 1L);

        // Mock dependencies
        when(cacheApiService.getSrteCacheString(any(), any())).thenReturn("investigationFormCd");
        when(customNbsQuestionRepository.retrieveQuestionRequiredNnd(any())).thenReturn(new ArrayList<>());
        when(notificationService.setNotificationProxy(any())).thenReturn(1L);
        when(investigationService.getPageProxyVO(any(), any())).thenReturn(new PageActProxyContainer());

        // Spy and mock internal validation method to return no errors
        InvestigationNotificationService spyService = Mockito.spy(investigationNotificationService);
        doReturn(Collections.emptyMap()).when(spyService)
                .validatePAMNotficationRequiredFieldsGivenPageProxy(any(), anyLong(), anyMap(), any());

        // Execute
        EDXActivityDetailLogDto result = spyService.sendProxyToEJB(notificationProxyVO, new PageActProxyContainer());

        // Verify
        assertNotNull(result);
        assertEquals("Success", result.getLogType());
        assertEquals("1", result.getRecordId());
        assertEquals("Notification created (UID: 1)", result.getComment());
        assertEquals(EdxPHCRConstants.MSG_TYPE.Notification.name(), result.getRecordType());
        assertEquals(PHCR_IMPORT_SRT, result.getRecordName());

        // Ensure setNotificationProxy was called
        verify(notificationService).setNotificationProxy(any());
    }


    @Test
    void sendProxyToEJB_WithValidationErrors() throws DataProcessingException {
        // Setup test data
        NotificationProxyContainer notificationProxyVO = createNotificationProxy("TEST_CODE", "PROG_AREA", 1L);

        // Create required field that fails validation
        Collection<QuestionRequiredNnd> requiredFields = new ArrayList<>();
        QuestionRequiredNnd requiredField = new QuestionRequiredNnd();
        requiredField.setNbsQuestionUid(1L);
        requiredField.setQuestionLabel("Required Field 1");
        requiredFields.add(requiredField);

        // Mock dependencies
        when(cacheApiService.getSrteCacheString(any(), any())).thenReturn("investigationFormCd");
        when(customNbsQuestionRepository.retrieveQuestionRequiredNnd(any())).thenReturn(requiredFields);
        when(investigationService.getPageProxyVO(any(), any())).thenReturn(new PageActProxyContainer());

        // Spy on service to mock internal validation
        InvestigationNotificationService spyService = Mockito.spy(investigationNotificationService);

        Map<Object, Object> validationErrors = new HashMap<>();
        validationErrors.put("field1", "Required Field 1");

        doReturn(validationErrors).when(spyService)
                .validatePAMNotficationRequiredFieldsGivenPageProxy(any(), anyLong(), anyMap(), any());

        // Execute
        EDXActivityDetailLogDto result = spyService.sendProxyToEJB(notificationProxyVO, new PageActProxyContainer());

        // Verify
        assertNotNull(result);
        assertEquals("Failure", result.getLogType());
        assertEquals("0", result.getRecordId());
        assertTrue(result.getComment().contains(EdxELRConstant.MISSING_NOTF_REQ_FIELDS));
        assertTrue(result.getComment().contains("[Required Field 1]"));

    }

    @Test
    void sendProxyToEJB_WithException() throws DataProcessingException {
        // Setup test data
        NotificationProxyContainer notificationProxyVO = createNotificationProxy("TEST_CODE", "PROG_AREA", 1L);
        
        // Mock dependencies to throw exception
        when(cacheApiService.getSrteCacheString(any(), any())).thenThrow(new RuntimeException("Test Exception"));
        
        // Execute
        EDXActivityDetailLogDto result = investigationNotificationService.sendProxyToEJB(notificationProxyVO, new PageActProxyContainer());
        
        // Verify
        assertNotNull(result);
        assertEquals("Failure", result.getLogType());
        assertTrue(result.getComment().contains("Test Exception"));
    }

    @Test
    void sendProxyToEJB_WithMultipleValidationErrors() throws DataProcessingException {
        // Setup test data
        NotificationProxyContainer notificationProxyVO = createNotificationProxy("TEST_CODE", "PROG_AREA", 1L);

        // Create multiple required fields
        Collection<QuestionRequiredNnd> requiredFields = new ArrayList<>();
        QuestionRequiredNnd requiredField1 = new QuestionRequiredNnd();
        requiredField1.setNbsQuestionUid(1L);
        requiredField1.setQuestionLabel("Required Field 1");
        requiredFields.add(requiredField1);

        QuestionRequiredNnd requiredField2 = new QuestionRequiredNnd();
        requiredField2.setNbsQuestionUid(2L);
        requiredField2.setQuestionLabel("Required Field 2");
        requiredFields.add(requiredField2);

        // Mock dependencies
        when(cacheApiService.getSrteCacheString(any(), any())).thenReturn("investigationFormCd");
        when(customNbsQuestionRepository.retrieveQuestionRequiredNnd(any())).thenReturn(requiredFields);
        when(investigationService.getPageProxyVO(any(), any())).thenReturn(new PageActProxyContainer());

        // Mock internal method via spy
        InvestigationNotificationService spyService = Mockito.spy(investigationNotificationService);
        Map<Object, Object> validationErrors = new HashMap<>();
        validationErrors.put("field1", "Required Field 1");
        validationErrors.put("field2", "Required Field 2");

        doReturn(validationErrors).when(spyService)
                .validatePAMNotficationRequiredFieldsGivenPageProxy(any(), anyLong(), anyMap(), any());

        // Execute
        EDXActivityDetailLogDto result = spyService.sendProxyToEJB(notificationProxyVO, new PageActProxyContainer());

        // Verify
        assertNotNull(result);
        assertEquals("Failure", result.getLogType());
        assertEquals("0", result.getRecordId());
        assertTrue(result.getComment().contains(EdxELRConstant.MISSING_NOTF_REQ_FIELDS));
        assertTrue(result.getComment().contains("[Required Field 1]"));
        assertTrue(result.getComment().contains("[Required Field 2]"));
        assertTrue(result.getComment().contains("; and "));

    }

    @Test
    void sendProxyToEJB_WithEmptyNotificationRequirements() throws DataProcessingException {
        // Setup test data
        NotificationProxyContainer notificationProxyVO = createNotificationProxy("TEST_CODE", "PROG_AREA", 1L);

        // Spy on the service to allow partial mocking
        InvestigationNotificationService spyService = Mockito.spy(investigationNotificationService);

        // Mock dependencies
        when(cacheApiService.getSrteCacheString(any(), any())).thenReturn("investigationFormCd");
        when(customNbsQuestionRepository.retrieveQuestionRequiredNnd(any())).thenReturn(null);
        when(notificationService.setNotificationProxy(any())).thenReturn(1L);

        // Mock the internal method call
        doReturn(null).when(spyService)
                .validatePAMNotficationRequiredFieldsGivenPageProxy(any(), anyLong(), anyMap(), any());

        // Execute
        EDXActivityDetailLogDto result = spyService.sendProxyToEJB(notificationProxyVO, new PageActProxyContainer());

        // Verify
        assertNotNull(result);
        assertEquals("Success", result.getLogType());
        assertEquals("1", result.getRecordId());
        assertEquals("Notification created (UID: 1)", result.getComment());
    }
    private NotificationProxyContainer createNotificationProxy(String conditionCd, String progAreaCd, Long phcUid) {
        NotificationProxyContainer notificationProxyVO = new NotificationProxyContainer();
        
        // Create PublicHealthCaseContainer
        PublicHealthCaseContainer phcContainer = new PublicHealthCaseContainer();
        PublicHealthCaseDto phcDto = new PublicHealthCaseDto();
        phcDto.setPublicHealthCaseUid(phcUid);
        phcDto.setCd(conditionCd);
        phcDto.setProgAreaCd(progAreaCd);
        phcContainer.setThePublicHealthCaseDto(phcDto);
        notificationProxyVO.setThePublicHealthCaseContainer(phcContainer);
        
        // Create NotificationContainer
        NotificationContainer notifVO = new NotificationContainer();
        NotificationDto notifDT = new NotificationDto();
        notifVO.setTheNotificationDT(notifDT);
        notificationProxyVO.setTheNotificationContainer(notifVO);
        
        return notificationProxyVO;
    }

    @Test
    void sendNotification_NullObject_ThrowsException() {
        String nndComment = "COM";
        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            investigationNotificationService.sendNotification(null, nndComment);
        });

        assertNotNull(thrown);
        assertEquals("Cannot create Notification: pageObj is null", thrown.getMessage());
    }

    @Test
    void sendNotification_UnknownType_ThrowsException() {
        // Create an object of an unknown type
        Object unknownType = new Object();
        String nndComment = "COM";
        
        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            investigationNotificationService.sendNotification(unknownType, nndComment);
        });

        assertNotNull(thrown);
        assertEquals("Cannot create Notification for unknown page type: java.lang.Object", thrown.getMessage());
    }

    @Test
    void validatePAMNotficationRequiredFieldsGivenPageProxy_RVCTForm_Success() throws DataProcessingException {
        // Setup test data
        PamProxyContainer pageObj = new PamProxyContainer();
        Long publicHealthCaseUid = 1L;
        Map<Object, Object> reqFields = new HashMap<>();
        
        // Create required field for NBS_Answer
        NbsQuestionMetadata metaData = new NbsQuestionMetadata();
        metaData.setNbsQuestionUid(1L);
        metaData.setDataLocation("NBS_Answer.LOC");
        metaData.setQuestionLabel("Location");
        metaData.setQuestionIdentifier("LOC");
        reqFields.put(1L, metaData);
        
        // Setup PamProxyContainer with required data
        BasePamContainer pamVO = new BasePamContainer();
        Map<Object, Object> answerMap = new HashMap<>();
        answerMap.put(1L, "Test Location"); // Add the required answer
        pamVO.setPamAnswerDTMap(answerMap);
        pageObj.setPamVO(pamVO);
        
        // Setup PublicHealthCaseContainer
        PublicHealthCaseContainer phcContainer = new PublicHealthCaseContainer();
        PublicHealthCaseDto phcDto = new PublicHealthCaseDto();
        phcDto.setPublicHealthCaseUid(publicHealthCaseUid);
        phcContainer.setThePublicHealthCaseDto(phcDto);
        pageObj.setPublicHealthCaseContainer(phcContainer);
        
        // Execute
        Map<Object, Object> result = investigationNotificationService.validatePAMNotficationRequiredFieldsGivenPageProxy(
            pageObj, publicHealthCaseUid, reqFields, NEDSSConstant.INV_FORM_RVCT);
        
        // Verify
        assertNull(result); // No validation errors
    }

    @Test
    void validatePAMNotficationRequiredFieldsGivenPageProxy_RVCTForm_MissingNBSAnswer() throws DataProcessingException {
        // Setup test data
        PamProxyContainer pageObj = new PamProxyContainer();
        Long publicHealthCaseUid = 1L;
        Map<Object, Object> reqFields = new HashMap<>();
        
        // Create required field for NBS_Answer
        NbsQuestionMetadata metaData = new NbsQuestionMetadata();
        metaData.setNbsQuestionUid(1L);
        metaData.setDataLocation("NBS_Answer.LOC");
        metaData.setQuestionLabel("Location");
        metaData.setQuestionIdentifier("LOC");
        reqFields.put(1L, metaData);
        
        // Setup PamProxyContainer with empty answer map
        BasePamContainer pamVO = new BasePamContainer();
        pamVO.setPamAnswerDTMap(new HashMap<>());
        pageObj.setPamVO(pamVO);
        
        // Setup PublicHealthCaseContainer
        PublicHealthCaseContainer phcContainer = new PublicHealthCaseContainer();
        PublicHealthCaseDto phcDto = new PublicHealthCaseDto();
        phcDto.setPublicHealthCaseUid(publicHealthCaseUid);
        phcContainer.setThePublicHealthCaseDto(phcDto);
        pageObj.setPublicHealthCaseContainer(phcContainer);
        
        // Execute
        Map<Object, Object> result = investigationNotificationService.validatePAMNotficationRequiredFieldsGivenPageProxy(
            pageObj, publicHealthCaseUid, reqFields, NEDSSConstant.INV_FORM_RVCT);
        
        // Verify
        assertNotNull(result);
        assertEquals("Location", result.get("LOC"));
    }

    @Test
    void validatePAMNotficationRequiredFieldsGivenPageProxy_WithPublicHealthCaseFields() throws DataProcessingException {
        // Setup test data
        PageActProxyContainer pageObj = new PageActProxyContainer();
        Long publicHealthCaseUid = 1L;
        Map<Object, Object> reqFields = new HashMap<>();
        
        // Create required field for public_health_case
        NbsQuestionMetadata metaData = new NbsQuestionMetadata();
        metaData.setNbsQuestionUid(1L);
        metaData.setDataLocation("public_health_case.cd");
        metaData.setQuestionLabel("Condition Code");
        metaData.setQuestionIdentifier("CD");
        reqFields.put(1L, metaData);
        
        // Setup PublicHealthCaseContainer with empty condition code
        PublicHealthCaseContainer phcContainer = new PublicHealthCaseContainer();
        PublicHealthCaseDto phcDto = new PublicHealthCaseDto();
        phcDto.setPublicHealthCaseUid(publicHealthCaseUid);
        phcDto.setCd(""); // Empty condition code
        phcContainer.setThePublicHealthCaseDto(phcDto);
        pageObj.setPublicHealthCaseContainer(phcContainer);

        var pamContainer = new BasePamContainer();
        pageObj.setPageVO(pamContainer);

        // Execute
        Map<Object, Object> result = investigationNotificationService.validatePAMNotficationRequiredFieldsGivenPageProxy(
            pageObj, publicHealthCaseUid, reqFields, "TEST_FORM");
        
        // Verify
        assertNotNull(result);
        assertEquals("Condition Code", result.get("CD"));
    }

    @Test
    void validatePAMNotficationRequiredFieldsGivenPageProxy_WithPersonFields() throws DataProcessingException {
        // Setup test data
        PageActProxyContainer pageObj = new PageActProxyContainer();
        Long publicHealthCaseUid = 1L;
        Map<Object, Object> reqFields = new HashMap<>();
        
        // Create required field for person
        NbsQuestionMetadata metaData = new NbsQuestionMetadata();
        metaData.setNbsQuestionUid(1L);
        metaData.setDataLocation("person.firstNm");
        metaData.setQuestionLabel("First Name");
        metaData.setQuestionIdentifier("FN");
        reqFields.put(1L, metaData);
        
        // Setup PublicHealthCaseContainer
        PublicHealthCaseContainer phcContainer = new PublicHealthCaseContainer();
        PublicHealthCaseDto phcDto = new PublicHealthCaseDto();
        phcDto.setPublicHealthCaseUid(publicHealthCaseUid);
        phcContainer.setThePublicHealthCaseDto(phcDto);
        
        // Setup participation and person
        Collection<ParticipationDto> participationDTCollection = new ArrayList<>();
        ParticipationDto participation = new ParticipationDto();
        participation.setTypeCd("SubjOfPHC");
        participation.setSubjectEntityUid(1L);
        participationDTCollection.add(participation);
        phcContainer.setTheParticipationDTCollection(participationDTCollection);
        
        // Setup person with empty first name
        Collection<PersonContainer> personContainerCollection = new ArrayList<>();
        PersonContainer personContainer = new PersonContainer();
        PersonDto personDto = new PersonDto();
        personDto.setPersonUid(1L);
        personDto.setFirstNm(""); // Empty first name
        personContainer.setThePersonDto(personDto);
        personContainerCollection.add(personContainer);
        
        pageObj.setPublicHealthCaseContainer(phcContainer);
        pageObj.setThePersonContainerCollection(personContainerCollection);

        var pamContainer = new BasePamContainer();
        pageObj.setPageVO(pamContainer);

        // Execute
        Map<Object, Object> result = investigationNotificationService.validatePAMNotficationRequiredFieldsGivenPageProxy(
            pageObj, publicHealthCaseUid, reqFields, "TEST_FORM");
        
        // Verify
        assertNotNull(result);
        assertEquals("First Name", result.get("FN"));
    }

    @Test
    void validatePAMNotficationRequiredFieldsGivenPageProxy_WithPostalLocatorFields() throws DataProcessingException {
        // Setup test data
        PageActProxyContainer pageObj = new PageActProxyContainer();
        Long publicHealthCaseUid = 1L;
        Map<Object, Object> reqFields = new HashMap<>();
        
        // Create required field for postal_locator
        NbsQuestionMetadata metaData = new NbsQuestionMetadata();
        metaData.setNbsQuestionUid(1L);
        metaData.setDataLocation("postal_locator.streetAddr1");
        metaData.setQuestionLabel("Address Line 1");
        metaData.setQuestionIdentifier("ADDR1");
        metaData.setDataUseCd("HOME");
        reqFields.put(1L, metaData);
        
        // Setup PublicHealthCaseContainer
        PublicHealthCaseContainer phcContainer = new PublicHealthCaseContainer();
        PublicHealthCaseDto phcDto = new PublicHealthCaseDto();
        phcDto.setPublicHealthCaseUid(publicHealthCaseUid);
        phcContainer.setThePublicHealthCaseDto(phcDto);
        
        // Setup participation and person
        Collection<ParticipationDto> participationDTCollection = new ArrayList<>();
        ParticipationDto participation = new ParticipationDto();
        participation.setTypeCd("SubjOfPHC");
        participation.setSubjectEntityUid(1L);
        participationDTCollection.add(participation);
        phcContainer.setTheParticipationDTCollection(participationDTCollection);
        
        // Setup person with empty postal locator
        Collection<PersonContainer> personContainerCollection = new ArrayList<>();
        PersonContainer personContainer = new PersonContainer();
        PersonDto personDto = new PersonDto();
        personDto.setPersonUid(1L);
        personContainer.setThePersonDto(personDto);
        
        // Setup empty postal locator
        Collection<EntityLocatorParticipationDto> entityLocatorCollection = new ArrayList<>();
        EntityLocatorParticipationDto entityLocator = new EntityLocatorParticipationDto();
        entityLocator.setUseCd("HOME");
        entityLocator.setClassCd("PST");
        entityLocator.setThePostalLocatorDto(null);
        entityLocatorCollection.add(entityLocator);
        personContainer.setTheEntityLocatorParticipationDtoCollection(entityLocatorCollection);
        
        personContainerCollection.add(personContainer);
        
        pageObj.setPublicHealthCaseContainer(phcContainer);
        pageObj.setThePersonContainerCollection(personContainerCollection);

        var pamContainer = new BasePamContainer();
        pageObj.setPageVO(pamContainer);
        // Execute
        Map<Object, Object> result = investigationNotificationService.validatePAMNotficationRequiredFieldsGivenPageProxy(
            pageObj, publicHealthCaseUid, reqFields, "TEST_FORM");
        
        // Verify
        assertNotNull(result);
        assertEquals("Address Line 1", result.get("ADDR1"));
    }

    @Test
    void validatePAMNotficationRequiredFieldsGivenPageProxy_WithPersonRaceFields() throws DataProcessingException {
        // Setup test data
        PageActProxyContainer pageObj = new PageActProxyContainer();
        Long publicHealthCaseUid = 1L;
        Map<Object, Object> reqFields = new HashMap<>();
        
        // Create required field for person_race
        NbsQuestionMetadata metaData = new NbsQuestionMetadata();
        metaData.setNbsQuestionUid(1L);
        metaData.setDataLocation("person_race.raceCd");
        metaData.setQuestionLabel("Race");
        metaData.setQuestionIdentifier("RACE");
        reqFields.put(1L, metaData);
        
        // Setup PublicHealthCaseContainer
        PublicHealthCaseContainer phcContainer = new PublicHealthCaseContainer();
        PublicHealthCaseDto phcDto = new PublicHealthCaseDto();
        phcDto.setPublicHealthCaseUid(publicHealthCaseUid);
        phcContainer.setThePublicHealthCaseDto(phcDto);
        
        // Setup participation and person
        Collection<ParticipationDto> participationDTCollection = new ArrayList<>();
        ParticipationDto participation = new ParticipationDto();
        participation.setTypeCd("SubjOfPHC");
        participation.setSubjectEntityUid(1L);
        participationDTCollection.add(participation);
        phcContainer.setTheParticipationDTCollection(participationDTCollection);
        
        // Setup person with empty race
        Collection<PersonContainer> personContainerCollection = new ArrayList<>();
        PersonContainer personContainer = new PersonContainer();
        PersonDto personDto = new PersonDto();
        personDto.setPersonUid(1L);
        personContainer.setThePersonDto(personDto);
        
        // Setup empty race collection
        Collection<PersonRaceDto> personRaceCollection = new ArrayList<>();
        PersonRaceDto personRace = new PersonRaceDto();
        personRace.setRaceCd("");
        personRaceCollection.add(personRace);
        personContainer.setThePersonRaceDtoCollection(personRaceCollection);
        
        personContainerCollection.add(personContainer);
        
        pageObj.setPublicHealthCaseContainer(phcContainer);
        pageObj.setThePersonContainerCollection(personContainerCollection);

        var pamContainer = new BasePamContainer();
        pageObj.setPageVO(pamContainer);

        // Execute
        Map<Object, Object> result = investigationNotificationService.validatePAMNotficationRequiredFieldsGivenPageProxy(
            pageObj, publicHealthCaseUid, reqFields, "TEST_FORM");
        
        // Verify
        assertNotNull(result);
        assertEquals("Race", result.get("RACE"));
    }

    @Test
    void validatePAMNotficationRequiredFieldsGivenPageProxy_WithNbsCaseAnswerFields() throws DataProcessingException {
        // Setup test data
        PageActProxyContainer pageObj = new PageActProxyContainer();
        Long publicHealthCaseUid = 1L;
        Map<Object, Object> reqFields = new HashMap<>();
        
        // Create required field for nbs_case_answer
        NbsQuestionMetadata metaData = new NbsQuestionMetadata();
        metaData.setNbsQuestionUid(1L);
        metaData.setDataLocation("nbs_case_answer.actUid");
        metaData.setQuestionLabel("Case Answer");
        metaData.setQuestionIdentifier("CASE_ANS");
        reqFields.put(1L, metaData);
        
        // Setup PublicHealthCaseContainer
        PublicHealthCaseContainer phcContainer = new PublicHealthCaseContainer();
        PublicHealthCaseDto phcDto = new PublicHealthCaseDto();
        phcDto.setPublicHealthCaseUid(publicHealthCaseUid);
        phcContainer.setThePublicHealthCaseDto(phcDto);
        
        // Setup page with empty answer map
        BasePamContainer basePam = new BasePamContainer();
        basePam.setPamAnswerDTMap(new HashMap<>());
        pageObj.setPageVO(basePam);
        pageObj.setPublicHealthCaseContainer(phcContainer);
        
        // Execute
        Map<Object, Object> result = investigationNotificationService.validatePAMNotficationRequiredFieldsGivenPageProxy(
            pageObj, publicHealthCaseUid, reqFields, "TEST_FORM");
        
        // Verify
        assertNotNull(result);
        assertEquals("Case Answer", result.get("CASE_ANS"));
    }

    @Test
    void validatePAMNotficationRequiredFieldsGivenPageProxy_WithMultipleDataLocations() throws DataProcessingException {
        // Setup test data
        PageActProxyContainer pageObj = new PageActProxyContainer();
        Long publicHealthCaseUid = 1L;
        Map<Object, Object> reqFields = new HashMap<>();
        
        // Create multiple required fields with different data locations
        NbsQuestionMetadata metaData1 = new NbsQuestionMetadata();
        metaData1.setNbsQuestionUid(1L);
        metaData1.setDataLocation("public_health_case.cd");
        metaData1.setQuestionLabel("Condition Code");
        metaData1.setQuestionIdentifier("CD");
        reqFields.put(1L, metaData1);
        
        NbsQuestionMetadata metaData2 = new NbsQuestionMetadata();
        metaData2.setNbsQuestionUid(2L);
        metaData2.setDataLocation("person.firstNm");
        metaData2.setQuestionLabel("First Name");
        metaData2.setQuestionIdentifier("FN");
        reqFields.put(2L, metaData2);
        
        NbsQuestionMetadata metaData3 = new NbsQuestionMetadata();
        metaData3.setNbsQuestionUid(3L);
        metaData3.setDataLocation("postal_locator.streetAddr1");
        metaData3.setQuestionLabel("Address");
        metaData3.setQuestionIdentifier("ADDR");
        metaData3.setDataUseCd("HOME");
        reqFields.put(3L, metaData3);
        
        // Setup PublicHealthCaseContainer with empty fields
        PublicHealthCaseContainer phcContainer = new PublicHealthCaseContainer();
        PublicHealthCaseDto phcDto = new PublicHealthCaseDto();
        phcDto.setPublicHealthCaseUid(publicHealthCaseUid);
        phcDto.setCd("");
        phcContainer.setThePublicHealthCaseDto(phcDto);
        
        // Setup participation and person
        Collection<ParticipationDto> participationDTCollection = new ArrayList<>();
        ParticipationDto participation = new ParticipationDto();
        participation.setTypeCd("SubjOfPHC");
        participation.setSubjectEntityUid(1L);
        participationDTCollection.add(participation);
        phcContainer.setTheParticipationDTCollection(participationDTCollection);
        
        // Setup person with empty fields
        Collection<PersonContainer> personContainerCollection = new ArrayList<>();
        PersonContainer personContainer = new PersonContainer();
        PersonDto personDto = new PersonDto();
        personDto.setPersonUid(1L);
        personDto.setFirstNm("");
        personContainer.setThePersonDto(personDto);
        
        // Setup empty postal locator
        Collection<EntityLocatorParticipationDto> entityLocatorCollection = new ArrayList<>();
        EntityLocatorParticipationDto entityLocator = new EntityLocatorParticipationDto();
        entityLocator.setUseCd("HOME");
        entityLocator.setClassCd("PST");
        entityLocator.setThePostalLocatorDto(null);
        entityLocatorCollection.add(entityLocator);
        personContainer.setTheEntityLocatorParticipationDtoCollection(entityLocatorCollection);
        
        personContainerCollection.add(personContainer);
        
        pageObj.setPublicHealthCaseContainer(phcContainer);
        pageObj.setThePersonContainerCollection(personContainerCollection);

        var pamContainer = new BasePamContainer();
        pageObj.setPageVO(pamContainer);

        // Execute
        Map<Object, Object> result = investigationNotificationService.validatePAMNotficationRequiredFieldsGivenPageProxy(
            pageObj, publicHealthCaseUid, reqFields, "TEST_FORM");
        
        // Verify
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("Condition Code", result.get("CD"));
        assertEquals("First Name", result.get("FN"));
        assertEquals("Address", result.get("ADDR"));
    }

    @Test
    void validatePAMNotficationRequiredFieldsGivenPageProxy_WithInvalidDataLocation() throws DataProcessingException {
        // Setup test data
        PageActProxyContainer pageObj = new PageActProxyContainer();
        Long publicHealthCaseUid = 1L;
        Map<Object, Object> reqFields = new HashMap<>();

        // Create required field with invalid data location
        NbsQuestionMetadata metaData = new NbsQuestionMetadata();
        metaData.setNbsQuestionUid(1L);
        metaData.setDataLocation("invalid.location");
        metaData.setQuestionLabel("Invalid Field");
        metaData.setQuestionIdentifier("INVALID");
        reqFields.put(1L, metaData);

        // Setup PublicHealthCaseContainer
        PublicHealthCaseContainer phcContainer = new PublicHealthCaseContainer();
        PublicHealthCaseDto phcDto = new PublicHealthCaseDto();
        phcDto.setPublicHealthCaseUid(publicHealthCaseUid);
        phcContainer.setThePublicHealthCaseDto(phcDto);
        pageObj.setPublicHealthCaseContainer(phcContainer);

        var pamContainer = new BasePamContainer();
        pageObj.setPageVO(pamContainer);

        // Execute
        Map<Object, Object> result = investigationNotificationService.validatePAMNotficationRequiredFieldsGivenPageProxy(
            pageObj, publicHealthCaseUid, reqFields, "TEST_FORM");

        // Verify
        assertNotNull(result); // Invalid data location should be ignored
    }

    @Test
    void validatePAMNotficationRequiredFieldsGivenPageProxy_WithMatchingPostalLocator() throws DataProcessingException {
        // Setup test data
        PageActProxyContainer pageObj = new PageActProxyContainer();
        Long publicHealthCaseUid = 1L;
        Map<Object, Object> reqFields = new HashMap<>();
        
        // Create required field for postal_locator with matching use code
        NbsQuestionMetadata metaData = new NbsQuestionMetadata();
        metaData.setNbsQuestionUid(1L);
        metaData.setDataLocation("postal_locator.streetAddr1");
        metaData.setQuestionLabel("Address Line 1");
        metaData.setQuestionIdentifier("ADDR1");
        metaData.setDataUseCd("HOME");
        reqFields.put(1L, metaData);
        
        // Setup PublicHealthCaseContainer
        PublicHealthCaseContainer phcContainer = new PublicHealthCaseContainer();
        PublicHealthCaseDto phcDto = new PublicHealthCaseDto();
        phcDto.setPublicHealthCaseUid(publicHealthCaseUid);
        phcContainer.setThePublicHealthCaseDto(phcDto);
        
        // Setup participation and person
        Collection<ParticipationDto> participationDTCollection = new ArrayList<>();
        ParticipationDto participation = new ParticipationDto();
        participation.setTypeCd("SubjOfPHC");
        participation.setSubjectEntityUid(1L);
        participationDTCollection.add(participation);
        phcContainer.setTheParticipationDTCollection(participationDTCollection);
        
        // Setup person with matching postal locator
        Collection<PersonContainer> personContainerCollection = new ArrayList<>();
        PersonContainer personContainer = new PersonContainer();
        PersonDto personDto = new PersonDto();
        personDto.setPersonUid(1L);
        personContainer.setThePersonDto(personDto);
        
        // Setup postal locator with matching use code
        Collection<EntityLocatorParticipationDto> entityLocatorCollection = new ArrayList<>();
        EntityLocatorParticipationDto entityLocator = new EntityLocatorParticipationDto();
        entityLocator.setUseCd("HOME");
        entityLocator.setClassCd("PST");
        PostalLocatorDto postalLocator = new PostalLocatorDto();
        postalLocator.setStreetAddr1(""); // Empty address
        entityLocator.setThePostalLocatorDto(postalLocator);
        entityLocatorCollection.add(entityLocator);
        personContainer.setTheEntityLocatorParticipationDtoCollection(entityLocatorCollection);
        
        personContainerCollection.add(personContainer);
        
        pageObj.setPublicHealthCaseContainer(phcContainer);
        pageObj.setThePersonContainerCollection(personContainerCollection);
        
        var pamContainer = new BasePamContainer();
        pageObj.setPageVO(pamContainer);
        
        // Execute
        Map<Object, Object> result = investigationNotificationService.validatePAMNotficationRequiredFieldsGivenPageProxy(
            pageObj, publicHealthCaseUid, reqFields, "TEST_FORM");
        
        // Verify
        assertNotNull(result);
        assertEquals("Address Line 1", result.get("ADDR1"));
    }

    @Test
    void validatePAMNotficationRequiredFieldsGivenPageProxy_WithNullPostalLocator() throws DataProcessingException {
        // Setup test data
        PageActProxyContainer pageObj = new PageActProxyContainer();
        Long publicHealthCaseUid = 1L;
        Map<Object, Object> reqFields = new HashMap<>();
        
        // Create required field for postal_locator
        NbsQuestionMetadata metaData = new NbsQuestionMetadata();
        metaData.setNbsQuestionUid(1L);
        metaData.setDataLocation("postal_locator.streetAddr1");
        metaData.setQuestionLabel("Address Line 1");
        metaData.setQuestionIdentifier("ADDR1");
        metaData.setDataUseCd("HOME");
        reqFields.put(1L, metaData);
        
        // Setup PublicHealthCaseContainer
        PublicHealthCaseContainer phcContainer = new PublicHealthCaseContainer();
        PublicHealthCaseDto phcDto = new PublicHealthCaseDto();
        phcDto.setPublicHealthCaseUid(publicHealthCaseUid);
        phcContainer.setThePublicHealthCaseDto(phcDto);
        
        // Setup participation and person
        Collection<ParticipationDto> participationDTCollection = new ArrayList<>();
        ParticipationDto participation = new ParticipationDto();
        participation.setTypeCd("SubjOfPHC");
        participation.setSubjectEntityUid(1L);
        participationDTCollection.add(participation);
        phcContainer.setTheParticipationDTCollection(participationDTCollection);
        
        // Setup person with null postal locator
        Collection<PersonContainer> personContainerCollection = new ArrayList<>();
        PersonContainer personContainer = new PersonContainer();
        PersonDto personDto = new PersonDto();
        personDto.setPersonUid(1L);
        personContainer.setThePersonDto(personDto);
        
        // Setup entity locator with null postal locator
        Collection<EntityLocatorParticipationDto> entityLocatorCollection = new ArrayList<>();
        EntityLocatorParticipationDto entityLocator = new EntityLocatorParticipationDto();
        entityLocator.setUseCd("HOME");
        entityLocator.setClassCd("PST");
        entityLocator.setThePostalLocatorDto(null);
        entityLocator.setTheTeleLocatorDto(null);
        entityLocatorCollection.add(entityLocator);
        personContainer.setTheEntityLocatorParticipationDtoCollection(entityLocatorCollection);
        
        personContainerCollection.add(personContainer);
        
        pageObj.setPublicHealthCaseContainer(phcContainer);
        pageObj.setThePersonContainerCollection(personContainerCollection);
        
        var pamContainer = new BasePamContainer();
        pageObj.setPageVO(pamContainer);
        
        // Execute
        Map<Object, Object> result = investigationNotificationService.validatePAMNotficationRequiredFieldsGivenPageProxy(
            pageObj, publicHealthCaseUid, reqFields, "TEST_FORM");
        
        // Verify
        assertNotNull(result);
        assertEquals("Address Line 1", result.get("ADDR1"));
    }

    @Test
    void validatePAMNotficationRequiredFieldsGivenPageProxy_WithMultiplePostalLocators() throws DataProcessingException {
        // Setup test data
        PageActProxyContainer pageObj = new PageActProxyContainer();
        Long publicHealthCaseUid = 1L;
        Map<Object, Object> reqFields = new HashMap<>();
        
        // Create required field for postal_locator
        NbsQuestionMetadata metaData = new NbsQuestionMetadata();
        metaData.setNbsQuestionUid(1L);
        metaData.setDataLocation("postal_locator.streetAddr1");
        metaData.setQuestionLabel("Address Line 1");
        metaData.setQuestionIdentifier("ADDR1");
        metaData.setDataUseCd("HOME");
        reqFields.put(1L, metaData);
        
        // Setup PublicHealthCaseContainer
        PublicHealthCaseContainer phcContainer = new PublicHealthCaseContainer();
        PublicHealthCaseDto phcDto = new PublicHealthCaseDto();
        phcDto.setPublicHealthCaseUid(publicHealthCaseUid);
        phcContainer.setThePublicHealthCaseDto(phcDto);
        
        // Setup participation and person
        Collection<ParticipationDto> participationDTCollection = new ArrayList<>();
        ParticipationDto participation = new ParticipationDto();
        participation.setTypeCd("SubjOfPHC");
        participation.setSubjectEntityUid(1L);
        participationDTCollection.add(participation);
        phcContainer.setTheParticipationDTCollection(participationDTCollection);
        
        // Setup person with multiple postal locators
        Collection<PersonContainer> personContainerCollection = new ArrayList<>();
        PersonContainer personContainer = new PersonContainer();
        PersonDto personDto = new PersonDto();
        personDto.setPersonUid(1L);
        personContainer.setThePersonDto(personDto);
        
        // Setup entity locator collection with multiple postal locators
        Collection<EntityLocatorParticipationDto> entityLocatorCollection = new ArrayList<>();
        
        // First postal locator - HOME
        EntityLocatorParticipationDto entityLocator1 = new EntityLocatorParticipationDto();
        entityLocator1.setUseCd("HOME");
        entityLocator1.setClassCd("PST");
        PostalLocatorDto postalLocator1 = new PostalLocatorDto();
        postalLocator1.setStreetAddr1(""); // Empty address
        entityLocator1.setThePostalLocatorDto(postalLocator1);
        entityLocatorCollection.add(entityLocator1);
        
        // Second postal locator - WORK
        EntityLocatorParticipationDto entityLocator2 = new EntityLocatorParticipationDto();
        entityLocator2.setUseCd("WORK");
        entityLocator2.setClassCd("PST");
        PostalLocatorDto postalLocator2 = new PostalLocatorDto();
        postalLocator2.setStreetAddr1("Work Address");
        entityLocator2.setThePostalLocatorDto(postalLocator2);
        entityLocatorCollection.add(entityLocator2);
        
        personContainer.setTheEntityLocatorParticipationDtoCollection(entityLocatorCollection);
        personContainerCollection.add(personContainer);
        
        pageObj.setPublicHealthCaseContainer(phcContainer);
        pageObj.setThePersonContainerCollection(personContainerCollection);
        
        var pamContainer = new BasePamContainer();
        pageObj.setPageVO(pamContainer);
        
        // Execute
        Map<Object, Object> result = investigationNotificationService.validatePAMNotficationRequiredFieldsGivenPageProxy(
            pageObj, publicHealthCaseUid, reqFields, "TEST_FORM");
        
        // Verify
        assertNotNull(result);
        assertEquals("Address Line 1", result.get("ADDR1"));
    }

}
