package gov.cdc.dataprocessing.service.implementation.public_health_case;

import gov.cdc.dataprocessing.cache.SrteCache;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.base.BasePamContainer;
import gov.cdc.dataprocessing.model.container.model.PageActProxyContainer;
import gov.cdc.dataprocessing.model.container.model.PamProxyContainer;
import gov.cdc.dataprocessing.model.container.model.PersonContainer;
import gov.cdc.dataprocessing.model.container.model.PublicHealthCaseContainer;
import gov.cdc.dataprocessing.model.dto.act.ActIdDto;
import gov.cdc.dataprocessing.model.dto.entity.EntityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.locator.PostalLocatorDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.model.dto.person.PersonDto;
import gov.cdc.dataprocessing.model.dto.person.PersonRaceDto;
import gov.cdc.dataprocessing.model.dto.phc.PublicHealthCaseDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.repository.nbs.odse.model.custom_model.QuestionRequiredNnd;
import gov.cdc.dataprocessing.repository.nbs.odse.model.dsm.DsmAlgorithm;
import gov.cdc.dataprocessing.repository.nbs.odse.model.person.Person;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.CustomNbsQuestionRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.dsm.DsmAlgorithmRepository;
import gov.cdc.dataprocessing.service.implementation.investigation.DsmAlgorithmService;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class InvestigationNotificationServiceTests {
    @Mock
    private IInvestigationService investigationService;
    @Mock
    private INotificationService notificationService;
    @Mock
    private CustomNbsQuestionRepository customNbsQuestionRepository;
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

        SrteCache.investigationFormConditionCode.put("CODE", "investigationFormCd");

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
        colRetri.setQuestionLabel("state");
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
        personRaceCol.add(personRace);;
        personConn.setThePersonRaceDtoCollection(personRaceCol);

        personCol.add(personConn);
        pageAct.setThePersonContainerCollection(personCol);
        when(investigationService.getPageProxyVO(NEDSSConstant.CASE, 10L)).thenReturn(pageAct);

        var test = investigationNotificationService.sendNotification(obj, nndComment);

        assertNotNull(test);
        assertEquals("PHCR_IMPORT", test.getRecordName());
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
    }

}
