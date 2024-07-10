package gov.cdc.dataprocessing.utilities.component.pam_and_page;

import gov.cdc.dataprocessing.constant.MessageConstants;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.NotificationSummaryContainer;
import gov.cdc.dataprocessing.model.container.model.PageActProxyContainer;
import gov.cdc.dataprocessing.model.container.model.PersonContainer;
import gov.cdc.dataprocessing.model.container.model.PublicHealthCaseContainer;
import gov.cdc.dataprocessing.model.dto.act.ActRelationshipDto;
import gov.cdc.dataprocessing.model.dto.edx.EDXEventProcessDto;
import gov.cdc.dataprocessing.model.dto.log.MessageLogDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.model.dto.person.PersonDto;
import gov.cdc.dataprocessing.model.dto.phc.PublicHealthCaseDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.repository.nbs.odse.model.person.Person;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.CustomAuthUserRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.CustomRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.auth.AuthUserRepository;
import gov.cdc.dataprocessing.service.implementation.auth_user.AuthUserService;
import gov.cdc.dataprocessing.service.implementation.person.base.PatientMatchingBaseService;
import gov.cdc.dataprocessing.service.interfaces.page_and_pam.IPamService;
import gov.cdc.dataprocessing.service.interfaces.public_health_case.IInvestigationService;
import gov.cdc.dataprocessing.service.interfaces.public_health_case.IPublicHealthCaseService;
import gov.cdc.dataprocessing.service.interfaces.public_health_case.IRetrieveSummaryService;
import gov.cdc.dataprocessing.service.interfaces.uid_generator.IUidService;
import gov.cdc.dataprocessing.service.model.auth_user.AuthUserProfileInfo;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.act.ActRelationshipRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.edx.EdxEventProcessRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.generic_helper.PrepareAssocModelHelper;
import gov.cdc.dataprocessing.utilities.component.nbs.NbsDocumentRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.nbs.NbsNoteRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.page_and_pam.PageRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.page_and_pam.PamRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.participation.ParticipationRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.patient.PatientRepositoryUtil;
import gov.cdc.dataprocessing.utilities.time.TimeStampUtil;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class PageRepositoryUtilTest {
    @Mock
    private IInvestigationService investigationService;
    @Mock
    private PatientRepositoryUtil patientRepositoryUtil;
    @Mock
    private IUidService uidService;
    @Mock
    private PamRepositoryUtil pamRepositoryUtil;
    @Mock
    private PrepareAssocModelHelper prepareAssocModelHelper;
    @Mock
    private IPublicHealthCaseService publicHealthCaseService;
    @Mock
    private IRetrieveSummaryService retrieveSummaryService;
    @Mock
    private ActRelationshipRepositoryUtil actRelationshipRepositoryUtil;
    @Mock
    private EdxEventProcessRepositoryUtil edxEventProcessRepositoryUtil;
    @Mock
    private NbsDocumentRepositoryUtil nbsDocumentRepositoryUtil;
    @Mock
    private ParticipationRepositoryUtil participationRepositoryUtil;
    @Mock
    private NbsNoteRepositoryUtil nbsNoteRepositoryUtil;
    @Mock
    private CustomRepository customRepository;
    @Mock
    private IPamService pamService;
    @Mock
    private PatientMatchingBaseService patientMatchingBaseService;
    
    @InjectMocks
    private PageRepositoryUtil pageRepositoryUtil;

    @Mock
    private PageActProxyContainer pageActProxyContainerMock;
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
        Mockito.reset(investigationService, patientRepositoryUtil,uidService, pamRepositoryUtil, prepareAssocModelHelper,
                publicHealthCaseService, retrieveSummaryService, actRelationshipRepositoryUtil, edxEventProcessRepositoryUtil,
                nbsDocumentRepositoryUtil, participationRepositoryUtil, nbsNoteRepositoryUtil,
                customRepository, pamService, patientMatchingBaseService, authUtil,
                pageActProxyContainerMock);
    }

    @Test
    void setPageActProxyVO_Test_Exp() {

        var phc = new PublicHealthCaseContainer();
        var phcDt = new PublicHealthCaseDto();
        phc.setThePublicHealthCaseDto(phcDt);
        phc.setCoinfectionCondition(true);
        when(pageActProxyContainerMock.getPublicHealthCaseContainer()).thenReturn(phc);
        when(pageActProxyContainerMock.isItNew()).thenReturn(false);
        when(pageActProxyContainerMock.isItDirty()).thenReturn(false);

        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            pageRepositoryUtil.setPageActProxyVO(pageActProxyContainerMock);
        });

        assertNotNull(thrown);

    }

    @Test
    void setPageActProxyVO_Test_Exp_2()  {

        var phc = new PublicHealthCaseContainer();
        var phcDt = new PublicHealthCaseDto();
        phc.setThePublicHealthCaseDto(phcDt);
        phc.setCoinfectionCondition(true);
        when(pageActProxyContainerMock.getPublicHealthCaseContainer()).thenReturn(phc);
        when(pageActProxyContainerMock.isItNew()).thenReturn(true);
        when(pageActProxyContainerMock.isItDirty()).thenReturn(false);
        when(pageActProxyContainerMock.isConversionHasModified()).thenReturn(false);

        // processingParticipationPatTypeForPageAct
        var patCol = new ArrayList<ParticipationDto>();
        var pat = new ParticipationDto();
        pat.setSubjectEntityUid(10L);
        pat.setSubjectClassCd(NEDSSConstant.PERSON);
        patCol.add(pat);
        when(pageActProxyContainerMock.getTheParticipationDtoCollection()).thenReturn(patCol);
        PersonContainer personContainer = new PersonContainer();
        PersonDto personDto = new PersonDto();
        personContainer.setThePersonDto(personDto);
        personDto.setRecordStatusCd(NEDSSConstant.RECORD_STATUS_LOGICAL_DELETE);
        when(patientRepositoryUtil.loadPerson(10L)).thenReturn(personContainer);


        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            pageRepositoryUtil.setPageActProxyVO(pageActProxyContainerMock);
        });

        assertNotNull(thrown);

    }


    // DIRTY PHC CON
    @Test
    void setPageActProxyVO_Test_1() throws DataProcessingException {

        var phc = new PublicHealthCaseContainer();
        var phcDt = new PublicHealthCaseDto();
        phcDt.setPublicHealthCaseUid(-1L);
        phcDt.setVersionCtrlNbr(1);
        phcDt.setCd("CODE");
        phcDt.setCaseClassCd("CASE");
        phcDt.setProgAreaCd("PROG");
        phcDt.setJurisdictionCd("JUS");
        phcDt.setSharedInd("Y");
        phc.setThePublicHealthCaseDto(phcDt);
        phc.setCoinfectionCondition(true);
        phc.setNbsAnswerCollection(new ArrayList<>());
        var actCol = new ArrayList<ActRelationshipDto>();
        var act = new ActRelationshipDto();
        act.setTypeCd(NEDSSConstant.DocToPHC);
        act.setSourceActUid(1L);
        act.setTargetActUid(1L);
        act.setStatusTime(TimeStampUtil.getCurrentTimeStamp());
        act.setStatusCd("CODE");
        act.setItDelete(true);
        actCol.add(act);
        phc.setTheActRelationshipDTCollection(actCol);
        var edxEventCol = new ArrayList<EDXEventProcessDto>();
        var edxEvent = new EDXEventProcessDto();
        edxEvent.setDocEventTypeCd(NEDSSConstant.CASE);
        edxEventCol.add(edxEvent);
        phc.setEdxEventProcessDtoCollection(edxEventCol);
        when(pageActProxyContainerMock.getPublicHealthCaseContainer()).thenReturn(phc);

        when(pageActProxyContainerMock.isItNew()).thenReturn(false);
        when(pageActProxyContainerMock.isItDirty()).thenReturn(true);
        when(pageActProxyContainerMock.isConversionHasModified()).thenReturn(false);

        // processingParticipationPatTypeForPageAct
        var patCol = new ArrayList<ParticipationDto>();
        var pat = new ParticipationDto();
        pat.setSubjectEntityUid(10L);
        pat.setSubjectClassCd(NEDSSConstant.PERSON);
        patCol.add(pat);
        when(pageActProxyContainerMock.getTheParticipationDtoCollection()).thenReturn(patCol);

        // processingPersonContainerForPageAct
        var perConArr = new ArrayList<PersonContainer>();
        var perCon = new PersonContainer();
        var perDt = new PersonDto();
        perDt.setCd(NEDSSConstant.PAT);
        perDt.setPersonUid(11L);
        perDt.setPersonParentUid(11L);
        perCon.setThePersonDto(perDt);
        perCon.setItNew(true);
        perCon.setItDirty(false);
        perConArr.add(perCon);
        when(pageActProxyContainerMock.getThePersonContainerCollection()).thenReturn(perConArr);
        when(patientMatchingBaseService.setPatientRevision(any(), eq(NEDSSConstant.PAT_CR), eq(NEDSSConstant.PAT))).thenReturn(11L);
        perCon = new PersonContainer();
        perDt = new PersonDto();
        perDt.setCd(NEDSSConstant.PRV);
        perDt.setPersonUid(-1L);
        perDt.setPersonParentUid(-1L);
        perCon.setThePersonDto(perDt);
        perCon.setItNew(true);
        perCon.setItDirty(false);
        perConArr.add(perCon);
        var prvDt =  SerializationUtils.clone(perDt);
        prvDt.setPersonParentUid(12L);
        prvDt.setPersonUid(12L);
        when(patientRepositoryUtil.createPerson(any())).thenReturn(new Person(prvDt));
        perCon = new PersonContainer();
        perDt = new PersonDto();
        perDt.setCd(NEDSSConstant.PAT);
        perDt.setPersonUid(13L);
        perDt.setPersonParentUid(13L);
        perCon.setThePersonDto(perDt);
        perCon.setItNew(false);
        perCon.setItDirty(true);
        perConArr.add(perCon);
        when(patientMatchingBaseService.setPatientRevision(any(),eq(NEDSSConstant.PAT_EDIT), eq( NEDSSConstant.PAT))).thenReturn(13L);
        perCon = new PersonContainer();
        perDt = new PersonDto();
        perDt.setCd(NEDSSConstant.PRV);
        perDt.setPersonUid(14L);
        perDt.setPersonParentUid(14L);
        perCon.setThePersonDto(perDt);
        perCon.setItNew(false);
        perCon.setItDirty(true);
        perConArr.add(perCon);


        // processingPhcContainerForPageAct
        when(prepareAssocModelHelper.prepareVO(any(),eq("INVESTIGATION"), eq("INV_EDIT"), eq("PUBLIC_HEALTH_CASE"), eq("BASE"),
                eq(1))).thenReturn(phcDt);
        when(publicHealthCaseService.setPublicHealthCase(any())).thenReturn(1L);

        Map<String, MessageLogDto > messageLogDTMap = new HashMap<>();
        var msgLog = new MessageLogDto();
        messageLogDTMap.put(MessageConstants.DISPOSITION_SPECIFIED_KEY, msgLog);
        msgLog = new MessageLogDto();
        msgLog.setEventUid(1L);
        messageLogDTMap.put("BLAH", msgLog);
        msgLog = new MessageLogDto();
        msgLog.setEventUid(-1L);
        messageLogDTMap.put("BLAH-2", msgLog);
        when(pageActProxyContainerMock.getMessageLogDTMap()).thenReturn(messageLogDTMap);


        // processingNotificationSummaryForPageAct
        var summaryArr = new ArrayList<>();
        var summary = new NotificationSummaryContainer();
        summary.setIsHistory("F");
        summary.setAutoResendInd("F");
        summary.setNotificationUid(1L);
        summary.setRecordStatusCd(NEDSSConstant.APPROVED_STATUS);
        summaryArr.add(summary);
        summary = new NotificationSummaryContainer();
        summary.setIsHistory("F");
        summary.setAutoResendInd("F");
        summary.setNotificationUid(2L);
        summary.setRecordStatusCd(NEDSSConstant.PENDING_APPROVAL_STATUS);
        summaryArr.add(summary);
        when(pageActProxyContainerMock.getTheNotificationSummaryVOCollection()).thenReturn(summaryArr);


        // processingPhcActRelationshipForPageAct
        // processingEventProcessForPageAct

        pageRepositoryUtil.setPageActProxyVO(pageActProxyContainerMock);


    }

}
