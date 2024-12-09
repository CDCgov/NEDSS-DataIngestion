package gov.cdc.dataprocessing.utilities.component.pam_and_page;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.base.BasePamContainer;
import gov.cdc.dataprocessing.model.container.model.*;
import gov.cdc.dataprocessing.model.dto.act.ActRelationshipDto;
import gov.cdc.dataprocessing.model.dto.edx.EDXEventProcessDto;
import gov.cdc.dataprocessing.model.dto.nbs.NBSDocumentDto;
import gov.cdc.dataprocessing.model.dto.nbs.NbsActEntityDto;
import gov.cdc.dataprocessing.model.dto.nbs.NbsCaseAnswerDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.model.dto.person.PersonDto;
import gov.cdc.dataprocessing.model.dto.phc.CaseManagementDto;
import gov.cdc.dataprocessing.model.dto.phc.PublicHealthCaseDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.CustomRepository;
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
     //   SrteCache.investigationFormConditionCode.clear();
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

     // SrteCache.investigationFormConditionCode.put("CODE", "CODE");
     //   SrteCache.investigationFormConditionCode.put("COND", "COND");


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

}
