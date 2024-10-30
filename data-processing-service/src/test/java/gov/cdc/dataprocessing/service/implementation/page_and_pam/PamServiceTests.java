package gov.cdc.dataprocessing.service.implementation.page_and_pam;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.base.BasePamContainer;
import gov.cdc.dataprocessing.model.container.model.NotificationSummaryContainer;
import gov.cdc.dataprocessing.model.container.model.PamProxyContainer;
import gov.cdc.dataprocessing.model.container.model.PersonContainer;
import gov.cdc.dataprocessing.model.container.model.PublicHealthCaseContainer;
import gov.cdc.dataprocessing.model.dto.act.ActRelationshipDto;
import gov.cdc.dataprocessing.model.dto.nbs.NbsNoteDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.model.dto.person.PersonDto;
import gov.cdc.dataprocessing.model.dto.phc.PublicHealthCaseDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.repository.nbs.odse.model.person.Person;
import gov.cdc.dataprocessing.service.implementation.person.base.PatientMatchingBaseService;
import gov.cdc.dataprocessing.service.interfaces.answer.IAnswerService;
import gov.cdc.dataprocessing.service.interfaces.public_health_case.IInvestigationService;
import gov.cdc.dataprocessing.service.interfaces.public_health_case.IPublicHealthCaseService;
import gov.cdc.dataprocessing.service.interfaces.public_health_case.IRetrieveSummaryService;
import gov.cdc.dataprocessing.service.interfaces.uid_generator.IUidService;
import gov.cdc.dataprocessing.service.model.auth_user.AuthUserProfileInfo;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.act.ActRelationshipRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.generic_helper.PrepareAssocModelHelper;
import gov.cdc.dataprocessing.utilities.component.nbs.NbsNoteRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.participation.ParticipationRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.patient.PatientRepositoryUtil;
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

class PamServiceTests {
    @Mock
    private IInvestigationService investigationService;
    @Mock
    private PatientRepositoryUtil patientRepositoryUtil;
    @Mock
    private PrepareAssocModelHelper prepareAssocModelHelper;
    @Mock
    private IRetrieveSummaryService retrieveSummaryService;
    @Mock
    private IPublicHealthCaseService publicHealthCaseService;
    @Mock
    private IUidService uidService;
    @Mock
    private ParticipationRepositoryUtil participationRepositoryUtil;
    @Mock
    private ActRelationshipRepositoryUtil actRelationshipRepositoryUtil;
    @Mock
    private NbsNoteRepositoryUtil nbsNoteRepositoryUtil;
    @Mock
    private IAnswerService answerService;
    @Mock
    private PatientMatchingBaseService patientMatchingBaseService;
    @InjectMocks
    private PamService pamService;
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
        Mockito.reset(investigationService,patientRepositoryUtil, prepareAssocModelHelper,
                retrieveSummaryService, publicHealthCaseService, uidService,
                participationRepositoryUtil, actRelationshipRepositoryUtil,
                nbsNoteRepositoryUtil, answerService, patientMatchingBaseService, authUtil);
    }

    @Test
    void setPamProxyWithAutoAssoc_Success_PamIsNew() throws DataProcessingException {

        PamProxyContainer pamProxyVO = new PamProxyContainer();

        pamProxyVO.setItNew(true);
        long observationUid = 10L;
        String observationTypeCd = NEDSSConstant.LAB_DISPALY_FORM;

        var perCol = new ArrayList<PersonContainer>();
        var perConn = new PersonContainer();
        var perDt = new PersonDto();
        perConn.setItNew(true);
        perDt.setCd(NEDSSConstant.PAT);
        perDt.setPersonUid(20L);
        perDt.setPersonParentUid(201L);
        perConn.setThePersonDto(perDt);
        perCol.add(perConn);

        perConn = new PersonContainer();
        perDt = new PersonDto();
        perConn.setItNew(true);
        perDt.setCd(NEDSSConstant.PRV);
        perDt.setPersonUid(21L);
        perDt.setPersonParentUid(202L);
        perConn.setThePersonDto(perDt);
        perCol.add(perConn);

        perConn = new PersonContainer();
        perDt = new PersonDto();
        perConn.setItDirty(true);
        perDt.setCd(NEDSSConstant.PAT);
        perDt.setPersonUid(22L);
        perDt.setPersonParentUid(203L);
        perConn.setThePersonDto(perDt);
        perCol.add(perConn);

        perConn = new PersonContainer();
        perDt = new PersonDto();
        perConn.setItDirty(true);
        perDt.setCd(NEDSSConstant.PRV);
        perDt.setPersonUid(23L);
        perDt.setPersonParentUid(204L);
        perDt.setRecordStatusCd("ACTIVE");
        perConn.setThePersonDto(perDt);
        perCol.add(perConn);

        pamProxyVO.setThePersonVOCollection(perCol);

        pamProxyVO.setUnsavedNote(true);
        var noteCol = new ArrayList<NbsNoteDto>();
        var note = new NbsNoteDto();
        noteCol.add(note);
        pamProxyVO.setNbsNoteDTColl(noteCol);

        var noteSumCol = new ArrayList<>();
        var noteSumConn = new NotificationSummaryContainer();
        noteSumConn.setIsHistory("F");
        noteSumConn.setAutoResendInd("F");
        noteSumConn.setNotificationUid(11L);
        noteSumConn.setRecordStatusCd(NEDSSConstant.APPROVED_STATUS);
        noteSumCol.add(noteSumConn);
        noteSumConn = new NotificationSummaryContainer();
        noteSumConn.setIsHistory("F");
        noteSumConn.setAutoResendInd("F");
        noteSumConn.setNotificationUid(12L);
        noteSumConn.setRecordStatusCd(NEDSSConstant.PENDING_APPROVAL_STATUS);
        noteSumCol.add(noteSumConn);
        pamProxyVO.setTheNotificationSummaryVOCollection(noteSumCol);


        var actCol = new ArrayList<ActRelationshipDto>();
        var act = new ActRelationshipDto();
        act.setTypeCd(NEDSSConstant.DocToPHC);
        act.setSourceActUid(13L);
        act.setItDelete(true);
        actCol.add(act);

        var phcConn = new PublicHealthCaseContainer();
        var phcDt = new PublicHealthCaseDto();
        phcDt.setVersionCtrlNbr(1);
        phcDt.setPublicHealthCaseUid(10L);
        phcConn.setTheActRelationshipDTCollection(actCol);
        phcConn.setThePublicHealthCaseDto(phcDt);
        pamProxyVO.setPublicHealthCaseContainer(phcConn);


        var patCol = new ArrayList<ParticipationDto>();
        var pat = new ParticipationDto();
        pat.setItDelete(true);
        pat.setSubjectEntityUid(101L);
        pat.setSubjectClassCd(NEDSSConstant.PERSON);
        patCol.add(pat);
        pamProxyVO.setTheParticipationDTCollection(patCol);

        when(patientRepositoryUtil.loadPerson(101L))
                .thenReturn(perConn);

        when(patientMatchingBaseService.setPatientRevision(any(), eq( NEDSSConstant.PAT_CR), eq( NEDSSConstant.PAT)))
                .thenReturn(20L);
        var patObj = new Person();
        patObj.setPersonParentUid(201L);
        patObj.setPersonUid(21L);
        when(patientRepositoryUtil.createPerson(any()))
                .thenReturn(patObj);

        when(prepareAssocModelHelper.prepareVO(any(), eq("INVESTIGATION"),
                eq("INV_CR"), eq("PUBLIC_HEALTH_CASE"), eq("BASE"), eq(1)))
                .thenReturn(phcDt);

        var res = pamService.setPamProxyWithAutoAssoc(pamProxyVO, observationUid,  observationTypeCd);

        assertNotNull(res);
        assertEquals(0, res);

    }

    @Test
    void setPamProxyWithAutoAssoc_Success() throws DataProcessingException {

        PamProxyContainer pamProxyVO = new PamProxyContainer();

        pamProxyVO.setItDirty(true);
        long observationUid = 10L;
        String observationTypeCd = NEDSSConstant.LAB_DISPALY_FORM;

        var perCol = new ArrayList<PersonContainer>();
        var perConn = new PersonContainer();
        var perDt = new PersonDto();
        perConn.setItNew(true);
        perDt.setCd(NEDSSConstant.PAT);
        perDt.setPersonUid(20L);
        perDt.setPersonParentUid(201L);
        perConn.setThePersonDto(perDt);
        perCol.add(perConn);

        perConn = new PersonContainer();
        perDt = new PersonDto();
        perConn.setItNew(true);
        perDt.setCd(NEDSSConstant.PRV);
        perDt.setPersonUid(21L);
        perDt.setPersonParentUid(202L);
        perConn.setThePersonDto(perDt);
        perCol.add(perConn);

        perConn = new PersonContainer();
        perDt = new PersonDto();
        perConn.setItDirty(true);
        perDt.setCd(NEDSSConstant.PAT);
        perDt.setPersonUid(22L);
        perDt.setPersonParentUid(203L);
        perConn.setThePersonDto(perDt);
        perCol.add(perConn);

        perConn = new PersonContainer();
        perDt = new PersonDto();
        perConn.setItDirty(true);
        perDt.setCd(NEDSSConstant.PRV);
        perDt.setPersonUid(23L);
        perDt.setPersonParentUid(204L);
        perConn.setThePersonDto(perDt);
        perCol.add(perConn);

        pamProxyVO.setThePersonVOCollection(perCol);

        pamProxyVO.setUnsavedNote(true);
        var noteCol = new ArrayList<NbsNoteDto>();
        var note = new NbsNoteDto();
        noteCol.add(note);
        pamProxyVO.setNbsNoteDTColl(noteCol);

        var noteSumCol = new ArrayList<>();
        var noteSumConn = new NotificationSummaryContainer();
        noteSumConn.setIsHistory("F");
        noteSumConn.setAutoResendInd("F");
        noteSumConn.setNotificationUid(11L);
        noteSumConn.setRecordStatusCd(NEDSSConstant.APPROVED_STATUS);
        noteSumCol.add(noteSumConn);
        noteSumConn = new NotificationSummaryContainer();
        noteSumConn.setIsHistory("F");
        noteSumConn.setAutoResendInd("F");
        noteSumConn.setNotificationUid(12L);
        noteSumConn.setRecordStatusCd(NEDSSConstant.PENDING_APPROVAL_STATUS);
        noteSumCol.add(noteSumConn);
        pamProxyVO.setTheNotificationSummaryVOCollection(noteSumCol);


        var actCol = new ArrayList<ActRelationshipDto>();
        var act = new ActRelationshipDto();
        act.setTypeCd(NEDSSConstant.DocToPHC);
        act.setSourceActUid(13L);
        act.setItDelete(true);
        actCol.add(act);

        var phcConn = new PublicHealthCaseContainer();
        var phcDt = new PublicHealthCaseDto();
        phcDt.setVersionCtrlNbr(1);
        phcDt.setPublicHealthCaseUid(10L);
        phcConn.setTheActRelationshipDTCollection(actCol);
        phcConn.setThePublicHealthCaseDto(phcDt);
        pamProxyVO.setPublicHealthCaseContainer(phcConn);


        var patCol = new ArrayList<ParticipationDto>();
        var pat = new ParticipationDto();
        pat.setItDelete(true);
        patCol.add(pat);
        pamProxyVO.setTheParticipationDTCollection(patCol);


        when(patientMatchingBaseService.setPatientRevision(any(), eq( NEDSSConstant.PAT_CR), eq( NEDSSConstant.PAT)))
                .thenReturn(20L);
        var patObj = new Person();
        patObj.setPersonParentUid(201L);
        patObj.setPersonUid(21L);
        when(patientRepositoryUtil.createPerson(any()))
                .thenReturn(patObj);

        when(prepareAssocModelHelper.prepareVO(any(), eq("INVESTIGATION"),
                eq("INV_EDIT"), eq("PUBLIC_HEALTH_CASE"), eq("BASE"), eq(1)))
                .thenReturn(phcDt);

        var res = pamService.setPamProxyWithAutoAssoc(pamProxyVO, observationUid,  observationTypeCd);

        assertNotNull(res);
        assertEquals(0, res);

    }


    @Test
    void setPamProxyWithAutoAssoc_Exception_1() {
        PamProxyContainer pamProxyVO = new PamProxyContainer();
        pamProxyVO.setItDirty(false);
        pamProxyVO.setItNew(false);
        long observationUid = 10L;
        String observationTypeCd = NEDSSConstant.LAB_DISPALY_FORM;

        pamProxyVO.setUnsavedNote(true);

        var phcConn = new PublicHealthCaseContainer();
        var phcDt = new PublicHealthCaseDto();
        phcDt.setVersionCtrlNbr(1);
        phcDt.setPublicHealthCaseUid(10L);
        phcConn.setThePublicHealthCaseDto(phcDt);
        pamProxyVO.setPublicHealthCaseContainer(phcConn);

        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            pamService.setPamProxyWithAutoAssoc(pamProxyVO, observationUid,  observationTypeCd);
        });

        assertNotNull(thrown.getMessage());
    }

    @Test
    void insertPamVO_Success() throws DataProcessingException {
        BasePamContainer pamVO = new BasePamContainer();
        var mapAnswer = new HashMap<>();
        mapAnswer.put("1","1");
        pamVO.setPamAnswerDTMap(mapAnswer);
        pamVO.setPageRepeatingAnswerDTMap(mapAnswer);
        PublicHealthCaseContainer publichHealthCaseVO = new PublicHealthCaseContainer();
        var dt = new PublicHealthCaseDto();
        publichHealthCaseVO.setThePublicHealthCaseDto(dt);

        pamService.insertPamVO(pamVO, publichHealthCaseVO);

        verify(answerService, times(1))
                .storeActEntityDTCollectionWithPublicHealthCase(any(), any());

    }

}
