package gov.cdc.dataprocessing.utilities.component.page_and_pam;

import gov.cdc.dataprocessing.constant.MessageConstants;
import gov.cdc.dataprocessing.constant.elr.NBSBOLookup;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.base.BasePamContainer;
import gov.cdc.dataprocessing.model.container.model.*;
import gov.cdc.dataprocessing.model.dto.act.ActRelationshipDto;
import gov.cdc.dataprocessing.model.dto.edx.EDXEventProcessDto;
import gov.cdc.dataprocessing.model.dto.log.MessageLogDto;
import gov.cdc.dataprocessing.model.dto.nbs.NBSDocumentDto;
import gov.cdc.dataprocessing.model.dto.nbs.NbsActEntityDto;
import gov.cdc.dataprocessing.model.dto.nbs.NbsCaseAnswerDto;
import gov.cdc.dataprocessing.model.dto.nbs.NbsNoteDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.model.dto.person.PersonDto;
import gov.cdc.dataprocessing.model.dto.phc.CaseManagementDto;
import gov.cdc.dataprocessing.model.dto.phc.PublicHealthCaseDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.repository.nbs.odse.model.person.Person;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.CustomRepository;
import gov.cdc.dataprocessing.service.implementation.person.base.PatientMatchingBaseService;
import gov.cdc.dataprocessing.service.interfaces.page_and_pam.IPamService;
import gov.cdc.dataprocessing.service.interfaces.public_health_case.IInvestigationService;
import gov.cdc.dataprocessing.service.interfaces.public_health_case.IPublicHealthCaseService;
import gov.cdc.dataprocessing.service.interfaces.public_health_case.IRetrieveSummaryService;
import gov.cdc.dataprocessing.service.interfaces.uid_generator.IUidService;
import gov.cdc.dataprocessing.service.model.action.PageActPatient;
import gov.cdc.dataprocessing.service.model.action.PageActPhc;
import gov.cdc.dataprocessing.service.model.auth_user.AuthUserProfileInfo;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.act.ActRelationshipRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.edx.EdxEventProcessRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.generic_helper.PrepareAssocModelHelper;
import gov.cdc.dataprocessing.utilities.component.nbs.NbsDocumentRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.nbs.NbsNoteRepositoryUtil;
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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

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
        user.setNedssEntryId(1L);
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

    @Test
    void updateForConInfectionId_Test_1() throws DataProcessingException {
        PageActProxyContainer pageActProxyContainer = new PageActProxyContainer();
        PageActProxyContainer supersededProxyVO = new PageActProxyContainer();
        Long mprUid = 1L;
        Map<Object, Object> coInSupersededEpliLinkIdMap = new HashMap<>();
        Long currentPhclUid = 1L;
        ArrayList<Object> coinfectionSummaryVOCollection = new ArrayList<>();
        String coinfectionIdToUpdate = "TEST";

        var phc = new PublicHealthCaseContainer();
        var phcDt = new PublicHealthCaseDto();
        phcDt.setPublicHealthCaseUid(1L);
        phcDt.setVersionCtrlNbr(1);
        phcDt.setCd("CODE");
        phcDt.setCaseClassCd("CASE");
        phcDt.setProgAreaCd("PROG");
        phcDt.setJurisdictionCd("JUS");
        phcDt.setSharedInd("Y");
        phcDt.setCoinfectionId("1");
        phcDt.setInvestigationStatusCd("A");
        phc.setThePublicHealthCaseDto(phcDt);
        phc.setCoinfectionCondition(true);
        phc.setNbsAnswerCollection(new ArrayList<>());

        var caseMgDt=  new CaseManagementDto();
        caseMgDt.setEpiLinkId("EPI");
        phc.setTheCaseManagementDto(caseMgDt);
        var actCol = new ArrayList<ActRelationshipDto>();
        var act = new ActRelationshipDto();
        act.setTypeCd(NEDSSConstant.DocToPHC);
        act.setSourceActUid(1L);
        act.setTargetActUid(1L);
        act.setStatusTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        act.setStatusCd("CODE");
        act.setItDelete(true);
        actCol.add(act);
        phc.setTheActRelationshipDTCollection(actCol);
        var edxEventCol = new ArrayList<EDXEventProcessDto>();
        var edxEvent = new EDXEventProcessDto();
        edxEvent.setDocEventTypeCd(NEDSSConstant.CASE);
        edxEventCol.add(edxEvent);
        phc.setEdxEventProcessDtoCollection(edxEventCol);
        pageActProxyContainer.setPublicHealthCaseContainer(phc);
        supersededProxyVO.setPublicHealthCaseContainer(phc);

        Map<Object, Object>pamAsn = new HashMap<>();
        var nbsAns = new NbsCaseAnswerDto();
        nbsAns.setItDirty(false);
        nbsAns.setItDelete(false);
        nbsAns.setItNew(false);
        pamAsn.put("TEST",nbsAns );
        var arr = new ArrayList<>();
        arr.add(nbsAns);
        pamAsn.put("TEST-2", arr);
        BasePamContainer page = new BasePamContainer();
        page.setPamAnswerDTMap(pamAsn);
        page.setPageRepeatingAnswerDTMap(pamAsn);
        var actNbsCol = new ArrayList<NbsActEntityDto>();
        var actNbs = new NbsActEntityDto();
        actNbsCol.add(actNbs);
        page.setActEntityDTCollection(actNbsCol);
        pageActProxyContainer.setPageVO(page);

        CoinfectionSummaryContainer coInfect = new CoinfectionSummaryContainer();
        coInfect.setPublicHealthCaseUid(2L);
        coInfect.setConditionCd("COND");
        coinfectionSummaryVOCollection.add(coInfect);

        var perConCol = new ArrayList<PersonContainer>();
        var perCon = new PersonContainer();
        var perDt = new PersonDto();
        perDt.setCd(NEDSSConstant.PAT);
        perCon.setThePersonDto(perDt);
        perConCol.add(perCon);
        pageActProxyContainer.setThePersonContainerCollection(perConCol);
        pageActProxyContainer.setItDirty(false);

        PageActProxyContainer pageActProxyContainer1 = SerializationUtils.clone(pageActProxyContainer);
        pageActProxyContainer1.getPublicHealthCaseContainer().getThePublicHealthCaseDto().setInvestigationStatusCd(NEDSSConstant.STATUS_OPEN);
        when(investigationService.getPageProxyVO("CASE", 2L)).thenReturn(pageActProxyContainer1);

        when(prepareAssocModelHelper.prepareVO(any(),eq("INVESTIGATION"), eq("INV_EDIT"), eq("PUBLIC_HEALTH_CASE"), eq("BASE"),
                eq(1))).thenReturn(phcDt);


        NbsDocumentContainer docConn = new NbsDocumentContainer();
        NBSDocumentDto docDt = new NBSDocumentDto();
        docDt.setJurisdictionCd("");
        docConn.setNbsDocumentDT(docDt);
        when(nbsDocumentRepositoryUtil.getNBSDocumentWithoutActRelationship(any())).thenReturn(docConn);


        pageRepositoryUtil.updateForConInfectionId(pageActProxyContainer, supersededProxyVO, mprUid,
                coInSupersededEpliLinkIdMap, currentPhclUid,
                coinfectionSummaryVOCollection, coinfectionIdToUpdate);

        verify(investigationService, times(1)).getPageProxyVO("CASE", 2L);
        verify(prepareAssocModelHelper, times(1)).prepareVO(any(),eq("INVESTIGATION"), eq("INV_EDIT"), eq("PUBLIC_HEALTH_CASE"), eq("BASE"),
                eq(1));
    }


    @Test
    void testHandlingCoInfection_ShouldInvokeUpdate() throws DataProcessingException {
        // Arrange
        var dto = new PublicHealthCaseDto();
        dto.setCoinfectionId("GROUP-123");
        dto.setInvestigationStatusCd("OPEN");

        var phcContainer = mock(PublicHealthCaseContainer.class);
        when(phcContainer.getThePublicHealthCaseDto()).thenReturn(dto);
        when(pageActProxyContainerMock.getPublicHealthCaseContainer()).thenReturn(phcContainer);
        when(pageActProxyContainerMock.isRenterant()).thenReturn(false);
        when(pageActProxyContainerMock.isMergeCase()).thenReturn(false);

        PageRepositoryUtil spyUtil = Mockito.spy(pageRepositoryUtil);
        doNothing().when(spyUtil).updatForConInfectionId(any(), any(), any());

        // Act
        spyUtil.handlingCoInfectionAndContactDisposition(pageActProxyContainerMock, 1L, 2L);

        // Assert
        verify(spyUtil, times(1)).updatForConInfectionId(pageActProxyContainerMock, 1L, 2L);
    }

    @Test
    void testRenterant_ExitsEarly() throws DataProcessingException {
        when(pageActProxyContainerMock.isRenterant()).thenReturn(true);
        var dto = new PublicHealthCaseDto();
        dto.setCoinfectionId("X");
        dto.setInvestigationStatusCd("OPEN");

        var phc = mock(PublicHealthCaseContainer.class);
        when(phc.getThePublicHealthCaseDto()).thenReturn(dto);
        when(pageActProxyContainerMock.getPublicHealthCaseContainer()).thenReturn(phc);

        PageRepositoryUtil spyUtil = Mockito.spy(pageRepositoryUtil);

        spyUtil.handlingCoInfectionAndContactDisposition(pageActProxyContainerMock, 1L, 2L);
        verify(spyUtil, never()).updatForConInfectionId(any(), any(), any());
    }

    @Test
    void testCoinfectionIdNull_ExitsEarly() throws DataProcessingException {
        when(pageActProxyContainerMock.isRenterant()).thenReturn(false);
        var dto = new PublicHealthCaseDto();
        dto.setCoinfectionId(null);
        dto.setInvestigationStatusCd("OPEN");

        var phc = mock(PublicHealthCaseContainer.class);
        when(phc.getThePublicHealthCaseDto()).thenReturn(dto);
        when(pageActProxyContainerMock.getPublicHealthCaseContainer()).thenReturn(phc);
        when(pageActProxyContainerMock.isMergeCase()).thenReturn(false);

        PageRepositoryUtil spyUtil = Mockito.spy(pageRepositoryUtil);
        spyUtil.handlingCoInfectionAndContactDisposition(pageActProxyContainerMock, 1L, 2L);

        verify(spyUtil, never()).updatForConInfectionId(any(), any(), any());
    }


    @Test
    void testMprUidIsNull_ExitsEarly() throws DataProcessingException {
        when(pageActProxyContainerMock.isRenterant()).thenReturn(false);
        var dto = new PublicHealthCaseDto();
        dto.setCoinfectionId("X");
        dto.setInvestigationStatusCd("OPEN");

        var phc = mock(PublicHealthCaseContainer.class);
        when(phc.getThePublicHealthCaseDto()).thenReturn(dto);
        when(pageActProxyContainerMock.getPublicHealthCaseContainer()).thenReturn(phc);
        when(pageActProxyContainerMock.isMergeCase()).thenReturn(false);

        PageRepositoryUtil spyUtil = Mockito.spy(pageRepositoryUtil);
        spyUtil.handlingCoInfectionAndContactDisposition(pageActProxyContainerMock, null, 2L);

        verify(spyUtil, never()).updatForConInfectionId(any(), any(), any());
    }

    @Test
    void testMergeCaseTrue_ExitsEarly() throws DataProcessingException {
        when(pageActProxyContainerMock.isRenterant()).thenReturn(false);
        var dto = new PublicHealthCaseDto();
        dto.setCoinfectionId("X");
        dto.setInvestigationStatusCd("OPEN");

        var phc = mock(PublicHealthCaseContainer.class);
        when(phc.getThePublicHealthCaseDto()).thenReturn(dto);
        when(pageActProxyContainerMock.getPublicHealthCaseContainer()).thenReturn(phc);
        when(pageActProxyContainerMock.isMergeCase()).thenReturn(true);

        PageRepositoryUtil spyUtil = Mockito.spy(pageRepositoryUtil);
        spyUtil.handlingCoInfectionAndContactDisposition(pageActProxyContainerMock, 1L, 2L);

        verify(spyUtil, never()).updatForConInfectionId(any(), any(), any());
    }



    @Test
    void testProcessingNbsDocument_NullDocUid_NoOp() throws DataProcessingException {
        pageRepositoryUtil.processingNbsDocumentForPageAct(pageActProxyContainerMock, null);
        verify(nbsDocumentRepositoryUtil, never()).getNBSDocumentWithoutActRelationship(any());
    }

    @Test
    void testProcessingNbsDocument_NullNbsDocumentDT_CallsUpdate() throws DataProcessingException {
        Long docUid = 100L;
        NbsDocumentContainer container = mock(NbsDocumentContainer.class);
        when(nbsDocumentRepositoryUtil.getNBSDocumentWithoutActRelationship(docUid)).thenReturn(container);
        when(container.getNbsDocumentDT()).thenReturn(null);

        pageRepositoryUtil.processingNbsDocumentForPageAct(pageActProxyContainerMock, docUid);

        verify(nbsDocumentRepositoryUtil).updateDocumentWithOutthePatient(container);
    }

    @Test
    void testProcessingNbsDocument_JurisdictionCdIsNull_SetsFromProxy() throws DataProcessingException {
        Long docUid = 101L;
        var docDto = new NBSDocumentDto();
        docDto.setJurisdictionCd(null);

        var container = mock(NbsDocumentContainer.class);
        when(container.getNbsDocumentDT()).thenReturn(docDto);
        when(nbsDocumentRepositoryUtil.getNBSDocumentWithoutActRelationship(docUid)).thenReturn(container);

        var phcDto = new PublicHealthCaseDto();
        phcDto.setJurisdictionCd("JURIS");

        var phc = mock(PublicHealthCaseContainer.class);
        when(phc.getThePublicHealthCaseDto()).thenReturn(phcDto);
        when(pageActProxyContainerMock.getPublicHealthCaseContainer()).thenReturn(phc);

        pageRepositoryUtil.processingNbsDocumentForPageAct(pageActProxyContainerMock, docUid);

        assertEquals("JURIS", docDto.getJurisdictionCd());
        verify(nbsDocumentRepositoryUtil).updateDocumentWithOutthePatient(container);
    }

    @Test
    void testProcessingNbsDocument_JurisdictionCdIsEmpty_SetsFromProxy() throws DataProcessingException {
        Long docUid = 102L;
        var docDto = new NBSDocumentDto();
        docDto.setJurisdictionCd("");

        var container = mock(NbsDocumentContainer.class);
        when(container.getNbsDocumentDT()).thenReturn(docDto);
        when(nbsDocumentRepositoryUtil.getNBSDocumentWithoutActRelationship(docUid)).thenReturn(container);

        var phcDto = new PublicHealthCaseDto();
        phcDto.setJurisdictionCd("J2");

        var phc = mock(PublicHealthCaseContainer.class);
        when(phc.getThePublicHealthCaseDto()).thenReturn(phcDto);
        when(pageActProxyContainerMock.getPublicHealthCaseContainer()).thenReturn(phc);

        pageRepositoryUtil.processingNbsDocumentForPageAct(pageActProxyContainerMock, docUid);

        assertEquals("J2", docDto.getJurisdictionCd());
        verify(nbsDocumentRepositoryUtil).updateDocumentWithOutthePatient(container);
    }

    @Test
    void testProcessingNbsDocument_JurisdictionCdPresent_NoChange() throws DataProcessingException {
        Long docUid = 103L;
        var docDto = new NBSDocumentDto();
        docDto.setJurisdictionCd("EXISTING");

        var container = mock(NbsDocumentContainer.class);
        when(container.getNbsDocumentDT()).thenReturn(docDto);
        when(nbsDocumentRepositoryUtil.getNBSDocumentWithoutActRelationship(docUid)).thenReturn(container);

        pageRepositoryUtil.processingNbsDocumentForPageAct(pageActProxyContainerMock, docUid);

        assertEquals("EXISTING", docDto.getJurisdictionCd());
        verify(nbsDocumentRepositoryUtil).updateDocumentWithOutthePatient(container);
    }


    @Test
    void testProcessingNotificationSummary_CollectionNull_NoOp() throws DataProcessingException {
        when(pageActProxyContainerMock.getTheNotificationSummaryVOCollection()).thenReturn(null);
        pageRepositoryUtil.processingNotificationSummaryForPageAct(pageActProxyContainerMock, new PublicHealthCaseDto());
        verifyNoInteractions(retrieveSummaryService);
    }

    @Test
    void testProcessingNotificationSummary_IsHistoryTrue_SkipsUpdate() throws DataProcessingException {
        NotificationSummaryContainer summary = new NotificationSummaryContainer();
        summary.setIsHistory("T");
        summary.setAutoResendInd("F");

        when(pageActProxyContainerMock.getTheNotificationSummaryVOCollection()).thenReturn(List.of(summary));

        pageRepositoryUtil.processingNotificationSummaryForPageAct(pageActProxyContainerMock, new PublicHealthCaseDto());

        verifyNoInteractions(retrieveSummaryService);
    }

    @Test
    void testProcessingNotificationSummary_AutoResendTrue_SkipsUpdate() throws DataProcessingException {
        NotificationSummaryContainer summary = new NotificationSummaryContainer();
        summary.setIsHistory("F");
        summary.setAutoResendInd("T");

        when(pageActProxyContainerMock.getTheNotificationSummaryVOCollection()).thenReturn(List.of(summary));

        pageRepositoryUtil.processingNotificationSummaryForPageAct(pageActProxyContainerMock, new PublicHealthCaseDto());

        verifyNoInteractions(retrieveSummaryService);
    }

    @Test
    void testProcessingNotificationSummary_StatusNull_SkipsUpdate() throws DataProcessingException {
        NotificationSummaryContainer summary = new NotificationSummaryContainer();
        summary.setIsHistory("F");
        summary.setAutoResendInd("F");
        summary.setRecordStatusCd(null);

        when(pageActProxyContainerMock.getTheNotificationSummaryVOCollection()).thenReturn(List.of(summary));

        pageRepositoryUtil.processingNotificationSummaryForPageAct(pageActProxyContainerMock, new PublicHealthCaseDto());

        verifyNoInteractions(retrieveSummaryService);
    }

    @Test
    void testProcessingNotificationSummary_StatusApproved_CallsUpdate() throws DataProcessingException {
        NotificationSummaryContainer summary = new NotificationSummaryContainer();
        summary.setIsHistory("F");
        summary.setAutoResendInd("F");
        summary.setRecordStatusCd(NEDSSConstant.APPROVED_STATUS);
        summary.setNotificationUid(123L);

        PublicHealthCaseDto phc = new PublicHealthCaseDto();
        phc.setCd("phcCd");
        phc.setCaseClassCd("classCd");
        phc.setProgAreaCd("prog");
        phc.setJurisdictionCd("jur");
        phc.setSharedInd("Y");

        when(pageActProxyContainerMock.getTheNotificationSummaryVOCollection()).thenReturn(List.of(summary));

        pageRepositoryUtil.processingNotificationSummaryForPageAct(pageActProxyContainerMock, phc);

        verify(retrieveSummaryService).updateNotification(
                123L, NEDSSConstant.NOT_CR_APR, "phcCd", "classCd", "prog", "jur", "Y");
    }

    @Test
    void testProcessingNotificationSummary_StatusPending_CallsUpdate() throws DataProcessingException {
        NotificationSummaryContainer summary = new NotificationSummaryContainer();
        summary.setIsHistory("F");
        summary.setAutoResendInd("F");
        summary.setRecordStatusCd(NEDSSConstant.PENDING_APPROVAL_STATUS);
        summary.setNotificationUid(456L);

        PublicHealthCaseDto phc = new PublicHealthCaseDto();
        phc.setCd("phcCd2");
        phc.setCaseClassCd("classCd2");
        phc.setProgAreaCd("prog2");
        phc.setJurisdictionCd("jur2");
        phc.setSharedInd("N");

        when(pageActProxyContainerMock.getTheNotificationSummaryVOCollection()).thenReturn(List.of(summary));

        pageRepositoryUtil.processingNotificationSummaryForPageAct(pageActProxyContainerMock, phc);

        verify(retrieveSummaryService).updateNotification(
                456L, NEDSSConstant.NOT_CR_PEND_APR, "phcCd2", "classCd2", "prog2", "jur2", "N");
    }


    @Test
    void testProcessingParticipation_NullCollection_NoOp() throws DataProcessingException {
        when(pageActProxyContainerMock.getTheParticipationDtoCollection()).thenReturn(null);
        pageRepositoryUtil.processingParticipationForPageAct(pageActProxyContainerMock);
        verifyNoInteractions(participationRepositoryUtil);
    }

    @Test
    void testProcessingParticipation_WithDeleteFlag_CallsInsertAndStore() throws DataProcessingException {
        ParticipationDto dto = mock(ParticipationDto.class);
        when(dto.isItDelete()).thenReturn(true);

        when(pageActProxyContainerMock.getTheParticipationDtoCollection()).thenReturn(List.of(dto));

        pageRepositoryUtil.processingParticipationForPageAct(pageActProxyContainerMock);

        verify(participationRepositoryUtil).insertParticipationHist(dto);
        verify(participationRepositoryUtil).storeParticipation(dto);
    }

    @Test
    void testProcessingParticipation_WithoutDeleteFlag_CallsOnlyStore() throws DataProcessingException {
        ParticipationDto dto = mock(ParticipationDto.class);
        when(dto.isItDelete()).thenReturn(false);

        when(pageActProxyContainerMock.getTheParticipationDtoCollection()).thenReturn(List.of(dto));

        pageRepositoryUtil.processingParticipationForPageAct(pageActProxyContainerMock);

        verify(participationRepositoryUtil, never()).insertParticipationHist(any());
        verify(participationRepositoryUtil).storeParticipation(dto);
    }

    @Test
    void testProcessingPhcContainer_NewWithCoInfectionAndNegativeUid() throws DataProcessingException {
        // Mocks and Setup
        PublicHealthCaseDto phcDto = new PublicHealthCaseDto();
        phcDto.setPublicHealthCaseUid(-100L);  // triggers falsePublicHealthCaseUid check
        phcDto.setVersionCtrlNbr(1);
        phcDto.setCoinfectionId(null); // triggers coinfection setting

        PublicHealthCaseDto preparedDto = new PublicHealthCaseDto();
        preparedDto.setPublicHealthCaseUid(-100L); // same as false UID
        preparedDto.setVersionCtrlNbr(1);

        PublicHealthCaseContainer phcContainer = mock(PublicHealthCaseContainer.class);
        when(phcContainer.getThePublicHealthCaseDto()).thenReturn(phcDto);
        doAnswer(inv -> {
            // simulate updated DTO storage
            when(phcContainer.getThePublicHealthCaseDto()).thenReturn(preparedDto);
            return null;
        }).when(phcContainer).setThePublicHealthCaseDto(any());

        when(phcContainer.getNbsAnswerCollection()).thenReturn(null);
        when(pageActProxyContainerMock.getPublicHealthCaseContainer()).thenReturn(phcContainer);
        when(pageActProxyContainerMock.isItDirty()).thenReturn(false);
        when(pageActProxyContainerMock.isItNew()).thenReturn(true);

        // prepareAssocModelHelper returns prepared DTO
        when(prepareAssocModelHelper.prepareVO(
                any(), eq(NBSBOLookup.INVESTIGATION), eq("INV_CR"), eq("PUBLIC_HEALTH_CASE"), eq("BASE"), anyInt())
        ).thenReturn(preparedDto);

        // publicHealthCaseService returns actual UID
        when(publicHealthCaseService.setPublicHealthCase(phcContainer)).thenReturn(999L);

        // Execute
        PageActPhc result = pageRepositoryUtil.processingPhcContainerForPageAct(pageActProxyContainerMock, true);

        // Assertions
        assertEquals(999L, result.getActualUid());
        assertEquals(999L, result.getPhcUid());
        assertEquals(-100L, result.getFalsePublicHealthCaseUid());

        // Verifications
        assertEquals(NEDSSConstant.COINFCTION_GROUP_ID_NEW_CODE, phcDto.getCoinfectionId());
        verify(uidService).setFalseToNewForPageAct(pageActProxyContainerMock, -100L, 999L);
    }

    @Test
    void testProcessingPersonContainer_NewPatientWithNegativeUid() throws DataProcessingException {
        // Setup PersonDto
        PersonDto personDto = new PersonDto();
        personDto.setCd(NEDSSConstant.PAT);
        personDto.setPersonUid(-101L); // fake ID
        personDto.setPersonParentUid(5000L);

        // Setup PersonContainer
        PersonContainer personContainer = mock(PersonContainer.class);
        when(personContainer.getThePersonDto()).thenReturn(personDto);
        when(personContainer.isItNew()).thenReturn(true);
        when(personContainer.isItDirty()).thenReturn(false);

        // Setup patient revision UID
        when(patientMatchingBaseService.setPatientRevision(any(), eq(NEDSSConstant.PAT_CR), eq(NEDSSConstant.PAT)))
                .thenReturn(8888L);

        // Setup pageActProxy
        when(pageActProxyContainerMock.getThePersonContainerCollection()).thenReturn(List.of(personContainer));

        // Input PHC DTO
        PublicHealthCaseDto phcDto = new PublicHealthCaseDto();

        // Execute
        PageActPatient result = pageRepositoryUtil.processingPersonContainerForPageAct(pageActProxyContainerMock, phcDto);

        // Assertions
        assertEquals(8888L, result.getPatientRevisionUid());
        assertEquals(8888L, result.getPhcDT().getCurrentPatientUid());
        verify(uidService).setFalseToNewForPageAct(pageActProxyContainerMock, -101L, 8888L);
    }

    @Test
    void testProcessingPersonContainer_NewProviderWithNegativeUid() throws DataProcessingException {
        // Setup PersonDto
        PersonDto personDto = new PersonDto();
        personDto.setCd(NEDSSConstant.PRV);
        personDto.setPersonUid(-202L);

        // Setup returned parent UID from createPerson
        Person returnedDto = new Person();
        returnedDto.setPersonParentUid(9999L);


        // Setup PersonContainer
        PersonContainer personContainer = mock(PersonContainer.class);
        when(personContainer.getThePersonDto()).thenReturn(personDto);
        when(personContainer.isItNew()).thenReturn(true);
        when(personContainer.isItDirty()).thenReturn(false);

        // Setup patientRepositoryUtil behavior
        when(patientRepositoryUtil.createPerson(personContainer)).thenReturn(returnedDto);

        // Setup pageActProxy
        when(pageActProxyContainerMock.getThePersonContainerCollection()).thenReturn(List.of(personContainer));

        // Input PHC DTO
        PublicHealthCaseDto phcDto = new PublicHealthCaseDto();

        // Execute
        pageRepositoryUtil.processingPersonContainerForPageAct(pageActProxyContainerMock, phcDto);

        // Assertions
        verify(uidService).setFalseToNewForPageAct(pageActProxyContainerMock, -202L, 9999L);
    }

    @Test
    void testProcessingPersonContainer_DirtyPatient() throws DataProcessingException {
        // Setup PersonDto
        PersonDto personDto = new PersonDto();
        personDto.setCd(NEDSSConstant.PAT);
        personDto.setPersonUid(123L);
        personDto.setPersonParentUid(456L);

        // Setup PersonContainer
        PersonContainer personContainer = mock(PersonContainer.class);
        when(personContainer.getThePersonDto()).thenReturn(personDto);
        when(personContainer.isItNew()).thenReturn(false);
        when(personContainer.isItDirty()).thenReturn(true);

        // Patient revision call
        when(patientMatchingBaseService.setPatientRevision(personContainer, NEDSSConstant.PAT_EDIT, NEDSSConstant.PAT))
                .thenReturn(456L);

        // Person collection
        when(pageActProxyContainerMock.getThePersonContainerCollection()).thenReturn(List.of(personContainer));

        // PHC input
        PublicHealthCaseDto phcDto = new PublicHealthCaseDto();

        // Execute
        PageActPatient result = pageRepositoryUtil.processingPersonContainerForPageAct(pageActProxyContainerMock, phcDto);

        // Assert
        assertEquals(456L, result.getPatientRevisionUid());
        verify(patientMatchingBaseService).setPatientRevision(personContainer, NEDSSConstant.PAT_EDIT, NEDSSConstant.PAT);
    }

    @Test
    void testProcessingPersonContainer_DirtyProvider() throws DataProcessingException {
        // Setup PersonDto
        PersonDto personDto = new PersonDto();
        personDto.setCd(NEDSSConstant.PRV);
        personDto.setPersonUid(789L);
        personDto.setPersonParentUid(1011L);

        // Setup PersonContainer
        PersonContainer personContainer = mock(PersonContainer.class);
        when(personContainer.getThePersonDto()).thenReturn(personDto);
        when(personContainer.isItNew()).thenReturn(false);
        when(personContainer.isItDirty()).thenReturn(true);

        // Person collection
        when(pageActProxyContainerMock.getThePersonContainerCollection()).thenReturn(List.of(personContainer));

        // PHC input
        PublicHealthCaseDto phcDto = new PublicHealthCaseDto();

        // Execute
         pageRepositoryUtil.processingPersonContainerForPageAct(pageActProxyContainerMock, phcDto);

        // Assert
        verify(patientRepositoryUtil).updateExistingPerson(personContainer);
    }

    @Test
    void testProcessingParticipationPatType_NewNotDirty_SubjectEntityNull_NoException()  {
        ParticipationDto dto = new ParticipationDto();
        dto.setSubjectEntityUid(null); // edge: null UID

        when(pageActProxyContainerMock.isItNew()).thenReturn(true);
        when(pageActProxyContainerMock.isItDirty()).thenReturn(false);
        when(pageActProxyContainerMock.getTheParticipationDtoCollection()).thenReturn(List.of(dto));

        assertDoesNotThrow(() ->
                pageRepositoryUtil.processingParticipationPatTypeForPageAct(pageActProxyContainerMock)
        );
    }

    @Test
    void testProcessingParticipationPatType_NewNotDirty_SubjectEntityZero_NoException()  {
        ParticipationDto dto = new ParticipationDto();
        dto.setSubjectEntityUid(0L); // edge: 0 UID

        when(pageActProxyContainerMock.isItNew()).thenReturn(true);
        when(pageActProxyContainerMock.isItDirty()).thenReturn(false);
        when(pageActProxyContainerMock.getTheParticipationDtoCollection()).thenReturn(List.of(dto));

        assertDoesNotThrow(() ->
                pageRepositoryUtil.processingParticipationPatTypeForPageAct(pageActProxyContainerMock)
        );
    }

    @Test
    void testProcessingParticipationPatType_NewNotDirty_NonPersonClassCd_NoLookup() throws DataProcessingException {
        ParticipationDto dto = new ParticipationDto();
        dto.setSubjectEntityUid(10L);
        dto.setSubjectClassCd("ORG"); // edge: classCd not PERSON

        when(pageActProxyContainerMock.isItNew()).thenReturn(true);
        when(pageActProxyContainerMock.isItDirty()).thenReturn(false);
        when(pageActProxyContainerMock.getTheParticipationDtoCollection()).thenReturn(List.of(dto));

        pageRepositoryUtil.processingParticipationPatTypeForPageAct(pageActProxyContainerMock);

        verify(patientRepositoryUtil, never()).loadPerson(any());
    }

    @Test
    void testProcessingParticipationPatType_NewNotDirty_RecordStatusLogicalDelete_Throws() {
        ParticipationDto dto = new ParticipationDto();
        dto.setSubjectEntityUid(100L);
        dto.setSubjectClassCd(NEDSSConstant.PERSON);

        PersonDto personDto = new PersonDto();
        personDto.setRecordStatusCd(NEDSSConstant.RECORD_STATUS_LOGICAL_DELETE); // edge: logical delete

        PersonContainer personContainer = mock(PersonContainer.class);
        when(personContainer.getThePersonDto()).thenReturn(personDto);
        when(patientRepositoryUtil.loadPerson(100L)).thenReturn(personContainer);

        when(pageActProxyContainerMock.isItNew()).thenReturn(true);
        when(pageActProxyContainerMock.isItDirty()).thenReturn(false);
        when(pageActProxyContainerMock.getTheParticipationDtoCollection()).thenReturn(List.of(dto));

        assertThrows(DataProcessingException.class, () ->
                pageRepositoryUtil.processingParticipationPatTypeForPageAct(pageActProxyContainerMock)
        );
    }

    @Test
    void testProcessingParticipationPatType_NewNotDirty_RecordStatusCdNull_NoException()  {
        ParticipationDto dto = new ParticipationDto();
        dto.setSubjectEntityUid(101L);
        dto.setSubjectClassCd(NEDSSConstant.PERSON);

        PersonDto personDto = new PersonDto();
        personDto.setRecordStatusCd(null); // edge: null record status

        PersonContainer personContainer = mock(PersonContainer.class);
        when(personContainer.getThePersonDto()).thenReturn(personDto);
        when(patientRepositoryUtil.loadPerson(101L)).thenReturn(personContainer);

        when(pageActProxyContainerMock.isItNew()).thenReturn(true);
        when(pageActProxyContainerMock.isItDirty()).thenReturn(false);
        when(pageActProxyContainerMock.getTheParticipationDtoCollection()).thenReturn(List.of(dto));

        assertDoesNotThrow(() ->
                pageRepositoryUtil.processingParticipationPatTypeForPageAct(pageActProxyContainerMock)
        );
    }


    @Test
    void testGetInvListForCoInfectionId_ReturnsList() throws DataProcessingException {
        Long mprUid = 100L;
        String coInfectionId = "COINF-001";
        ArrayList<Object> expectedList = new ArrayList<>();
        expectedList.add(new Object());

        when(customRepository.getInvListForCoInfectionId(mprUid, coInfectionId)).thenReturn(expectedList);

        ArrayList<Object> result = pageRepositoryUtil.getInvListForCoInfectionId(mprUid, coInfectionId);

        assertEquals(expectedList, result);
        verify(customRepository).getInvListForCoInfectionId(mprUid, coInfectionId);
    }

    @Test
    void testGetInvListForCoInfectionId_ReturnsEmptyList() throws DataProcessingException {
        Long mprUid = 200L;
        String coInfectionId = "COINF-002";

        when(customRepository.getInvListForCoInfectionId(mprUid, coInfectionId)).thenReturn(new ArrayList<>());

        ArrayList<Object> result = pageRepositoryUtil.getInvListForCoInfectionId(mprUid, coInfectionId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(customRepository).getInvListForCoInfectionId(mprUid, coInfectionId);
    }

    @Test
    void testGetInvListForCoInfectionId_ThrowsException() throws DataProcessingException {
        Long mprUid = 300L;
        String coInfectionId = "COINF-003";

        when(customRepository.getInvListForCoInfectionId(mprUid, coInfectionId))
                .thenThrow(new DataProcessingException("DB error"));

        assertThrows(DataProcessingException.class, () ->
                pageRepositoryUtil.getInvListForCoInfectionId(mprUid, coInfectionId));
    }

    @Test
    void testUpdateForConInfectionId_CoinfectionSummaryNull_FetchesFromRepo() throws DataProcessingException {
        String coinfectionId = "COINF-001";
        Long mprUid = 100L;
        Long currentPhclUid = 999L;

        PublicHealthCaseDto phcDto = new PublicHealthCaseDto();
        phcDto.setCoinfectionId(coinfectionId);

        PublicHealthCaseContainer phcContainer = mock(PublicHealthCaseContainer.class);
        when(phcContainer.getThePublicHealthCaseDto()).thenReturn(phcDto);

        when(pageActProxyContainerMock.getPublicHealthCaseContainer()).thenReturn(phcContainer);
        when(customRepository.getInvListForCoInfectionId(mprUid, coinfectionId)).thenReturn(new ArrayList<>());

        pageRepositoryUtil.updateForConInfectionId(pageActProxyContainerMock, mock(PageActProxyContainer.class),
                mprUid, new HashMap<>(), currentPhclUid, null, null);

        verify(customRepository).getInvListForCoInfectionId(mprUid, coinfectionId);
    }

    @Test
    void testUpdateForConInfectionId_CoinfectionSummaryEmpty_NoUpdateCall() throws DataProcessingException {
        String coinfectionId = "COINF-002";
        Long mprUid = 200L;

        PublicHealthCaseDto phcDto = new PublicHealthCaseDto();
        phcDto.setCoinfectionId(coinfectionId);

        PublicHealthCaseContainer phcContainer = mock(PublicHealthCaseContainer.class);
        when(phcContainer.getThePublicHealthCaseDto()).thenReturn(phcDto);
        when(pageActProxyContainerMock.getPublicHealthCaseContainer()).thenReturn(phcContainer);

        pageRepositoryUtil.updateForConInfectionId(pageActProxyContainerMock, mock(PageActProxyContainer.class),
                mprUid, new HashMap<>(), 300L, new ArrayList<>(), null);

        verify(customRepository, never()).getInvListForCoInfectionId(any(), any());
    }

    @Test
    void testUpdateForConInfectionId_CoinfectionIdToUpdateIsNull_CallsUpdateWithoutSuperseded() throws DataProcessingException {
        Long mprUid = 123L;
        Long currentPhclUid = 999L;

        PublicHealthCaseDto phcDto = new PublicHealthCaseDto();
        phcDto.setCoinfectionId("COINF-XYZ");

        CoinfectionSummaryContainer summary = new CoinfectionSummaryContainer();
        summary.setPublicHealthCaseUid(888L); // different than currentPhclUid → triggers update

        PublicHealthCaseContainer phcContainer = mock(PublicHealthCaseContainer.class);
        when(phcContainer.getThePublicHealthCaseDto()).thenReturn(phcDto);
        when(pageActProxyContainerMock.getPublicHealthCaseContainer()).thenReturn(phcContainer);

        Collection<Object> coinfectionSummary = List.of(summary);

        PageRepositoryUtil spyUtil = Mockito.spy(pageRepositoryUtil);
        doNothing().when(spyUtil).updateCoInfectionInvest(
                any(), any(), any(), any(), isNull(), isNull(), eq(summary), isNull(), any());

        spyUtil.updateForConInfectionId(pageActProxyContainerMock, mock(PageActProxyContainer.class),
                mprUid, new HashMap<>(), currentPhclUid, coinfectionSummary, null);

        verify(spyUtil).updateCoInfectionInvest(any(), any(), any(), any(), isNull(), isNull(), eq(summary), isNull(), any());
    }

    @Test
    void testUpdatForConInfectionId_DelegatesToFullMethod() throws DataProcessingException {
        Long mprUid = 100L;
        Long currentPhclUid = 200L;

        PageRepositoryUtil spyUtil = Mockito.spy(pageRepositoryUtil);

        doNothing().when(spyUtil).updateForConInfectionId(
                eq(pageActProxyContainerMock),
                isNull(),
                eq(mprUid),
                isNull(),
                eq(currentPhclUid),
                isNull(),
                isNull()
        );

        spyUtil.updatForConInfectionId(pageActProxyContainerMock, mprUid, currentPhclUid);

        verify(spyUtil).updateForConInfectionId(
                eq(pageActProxyContainerMock),
                isNull(),
                eq(mprUid),
                isNull(),
                eq(currentPhclUid),
                isNull(),
                isNull()
        );
    }

    @Test
    void testSetPageActProxyVO_UpdateAutoResendNotificationsThrowsException_HandledGracefully() throws DataProcessingException {
        when(pageActProxyContainerMock.isItNew()).thenReturn(false);
        when(pageActProxyContainerMock.isItDirty()).thenReturn(true);
        when(pageActProxyContainerMock.isConversionHasModified()).thenReturn(false);

        doThrow(new RuntimeException("Simulated")).when(investigationService).updateAutoResendNotificationsAsync(pageActProxyContainerMock);

        setupMinimalPhcAndPersonMocks();

        assertDoesNotThrow(() -> pageRepositoryUtil.setPageActProxyVO(pageActProxyContainerMock));
    }

    @Test
    void testSetPageActProxyVO_MessageLogMap_SkipDispositionSpecifiedKey() throws DataProcessingException {
        when(pageActProxyContainerMock.isItNew()).thenReturn(true);
        when(pageActProxyContainerMock.isItDirty()).thenReturn(false);

        MessageLogDto dto1 = new MessageLogDto();
        dto1.setEventUid(null);
        Map<String, MessageLogDto> logMap = new HashMap<>();
        logMap.put("skip." + MessageConstants.DISPOSITION_SPECIFIED_KEY, dto1);
        logMap.put("normal", dto1);

        when(pageActProxyContainerMock.getMessageLogDTMap()).thenReturn(logMap);

        setupMinimalPhcAndPersonMocks();

        Long result = pageRepositoryUtil.setPageActProxyVO(pageActProxyContainerMock);
        assertNotNull(result);
    }


    @Test
    void testSetPageActProxyVO_StoreNotesCalled() throws DataProcessingException {
        when(pageActProxyContainerMock.isItNew()).thenReturn(true);
        when(pageActProxyContainerMock.isItDirty()).thenReturn(false);

        NbsNoteDto noteDto = new NbsNoteDto();
        Collection<NbsNoteDto> notes = List.of(noteDto);

        when(pageActProxyContainerMock.isUnsavedNote()).thenReturn(true);
        when(pageActProxyContainerMock.getNbsNoteDTColl()).thenReturn(notes);

        setupMinimalPhcAndPersonMocks();

        pageRepositoryUtil.setPageActProxyVO(pageActProxyContainerMock);

        verify(nbsNoteRepositoryUtil).storeNotes(anyLong(), eq(notes));
    }

    @Test
    void testSetPageActProxyVO_InsertPamVO_CalledWhenNew() throws DataProcessingException {
        when(pageActProxyContainerMock.isItNew()).thenReturn(true);
        when(pageActProxyContainerMock.isItDirty()).thenReturn(false);

        BasePamContainer pageVO = new BasePamContainer();
        when(pageActProxyContainerMock.getPageVO()).thenReturn(pageVO);

        setupMinimalPhcAndPersonMocks();

        pageRepositoryUtil.setPageActProxyVO(pageActProxyContainerMock);

        verify(pamService).insertPamVO(eq(pageVO), any());
    }

    @Test
    void testSetPageActProxyVO_PageVO_Null_LogsError() throws DataProcessingException {
        when(pageActProxyContainerMock.isItNew()).thenReturn(false);
        when(pageActProxyContainerMock.isItDirty()).thenReturn(true);
        when(pageActProxyContainerMock.getPageVO()).thenReturn(null);

        setupMinimalPhcAndPersonMocks();

        pageRepositoryUtil.setPageActProxyVO(pageActProxyContainerMock);

        // Can't assert logs, but can verify insertPamVO was not called
        verify(pamService, never()).insertPamVO(any(), any());
    }




    private void setupMinimalPhcAndPersonMocks() throws DataProcessingException {
        PublicHealthCaseDto phcDto = new PublicHealthCaseDto();
        phcDto.setPublicHealthCaseUid(999L);
        phcDto.setVersionCtrlNbr(1);

        PublicHealthCaseContainer phcContainer = mock(PublicHealthCaseContainer.class);
        when(phcContainer.getThePublicHealthCaseDto()).thenReturn(phcDto);
        when(pageActProxyContainerMock.getPublicHealthCaseContainer()).thenReturn(phcContainer);

        when(pageActProxyContainerMock.getThePersonContainerCollection()).thenReturn(new ArrayList<>());

        when(patientMatchingBaseService.setPatientRevision(any(), any(), any())).thenReturn(1001L);
        when(publicHealthCaseService.setPublicHealthCase(any())).thenReturn(999L);
        when(prepareAssocModelHelper.prepareVO(any(), any(), any(), any(), any(), anyInt())).thenReturn(phcDto);

        when(pageActProxyContainerMock.getMessageLogDTMap()).thenReturn(new HashMap<>());
    }


    @Test
    void testSetPageActProxyVO_MessageLogDTMap_ProcessedCorrectly() throws DataProcessingException {
        // Setup minimal valid state
        when(pageActProxyContainerMock.isItNew()).thenReturn(true);
        when(pageActProxyContainerMock.isItDirty()).thenReturn(false);

        // Prepare PHC DTO
        PublicHealthCaseDto phcDto = new PublicHealthCaseDto();
        phcDto.setPublicHealthCaseUid(1111L);
        phcDto.setVersionCtrlNbr(1);

        PublicHealthCaseContainer phcContainer = mock(PublicHealthCaseContainer.class);
        when(phcContainer.getThePublicHealthCaseDto()).thenReturn(phcDto);
        when(pageActProxyContainerMock.getPublicHealthCaseContainer()).thenReturn(phcContainer);

        // Prepare MessageLogDto map
        MessageLogDto dtoToUpdate = new MessageLogDto();
        dtoToUpdate.setEventUid(null); // triggers update
        Map<String, MessageLogDto> logMap = new HashMap<>();
        logMap.put("msg.key.valid", dtoToUpdate); // should be processed
        logMap.put("skip." + MessageConstants.DISPOSITION_SPECIFIED_KEY, new MessageLogDto()); // should be skipped

        when(pageActProxyContainerMock.getMessageLogDTMap()).thenReturn(logMap);

        // Prepare Person container and revision
        PersonDto personDto = new PersonDto();
        personDto.setCd(NEDSSConstant.PAT);
        personDto.setPersonUid(-123L);
        personDto.setPersonParentUid(777L);

        PersonContainer personContainer = mock(PersonContainer.class);
        when(personContainer.getThePersonDto()).thenReturn(personDto);
        when(personContainer.isItNew()).thenReturn(true);

        when(pageActProxyContainerMock.getThePersonContainerCollection()).thenReturn(List.of(personContainer));
        when(patientMatchingBaseService.setPatientRevision(any(), any(), any())).thenReturn(777L);
        when(publicHealthCaseService.setPublicHealthCase(any())).thenReturn(1111L);
        when(prepareAssocModelHelper.prepareVO(any(), any(), any(), any(), any(), anyInt())).thenReturn(phcDto);

        // Execute
        pageRepositoryUtil.setPageActProxyVO(pageActProxyContainerMock);

        // Assert that dto was updated
        assertEquals(777L, dtoToUpdate.getPersonUid());
        assertEquals(1111L, dtoToUpdate.getEventUid());
    }


}
