package gov.cdc.dataprocessing.utilities.component.public_health_case;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.PublicHealthCaseContainer;
import gov.cdc.dataprocessing.model.dto.act.ActIdDto;
import gov.cdc.dataprocessing.model.dto.act.ActivityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.phc.CaseManagementDto;
import gov.cdc.dataprocessing.model.dto.phc.ConfirmationMethodDto;
import gov.cdc.dataprocessing.model.dto.uid.LocalUidGeneratorDto;
import gov.cdc.dataprocessing.model.dto.uid.LocalUidModel;
import gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template.*;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsActEntity;
import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsCaseAnswer;
import gov.cdc.dataprocessing.repository.nbs.odse.model.phc.*;
import gov.cdc.dataprocessing.service.implementation.uid_generator.UidPoolManager;
import gov.cdc.dataprocessing.service.interfaces.uid_generator.IOdseIdGeneratorWCacheService;
import gov.cdc.dataprocessing.service.model.auth_user.AuthUserProfileInfo;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.act.ActIdRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.act.ActLocatorParticipationRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.act.ActRelationshipRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.participation.ParticipationRepositoryUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import static gov.cdc.dataprocessing.constant.enums.LocalIdClass.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PublicHealthCaseRepositoryUtilTest {
    @Mock
    private PublicHealthCaseJdbcRepository publicHealthCaseRepository;
    @Mock
    private SupportForPhcJdbcRepository supportForPhcJdbcRepository;
    @Mock
    private IOdseIdGeneratorWCacheService odseIdGeneratorService;
    @Mock
    private ActJdbcRepository actRepository;
    @Mock
    private ActIdJdbcRepository actIdRepository;
    @Mock
    private ConfirmationMethodJdbcRepository confirmationMethodRepository;
    @Mock
    private ActLocatorParticipationJdbcRepository actLocatorParticipationRepository;
    @Mock
    private CaseManagementJdbcRepository caseManagementRepository;
    @Mock
    private ConfirmationMethodRepositoryUtil confirmationMethodRepositoryUtil;
    @Mock
    private CaseManagementRepositoryUtil caseManagementRepositoryUtil;
    @Mock
    private ActIdRepositoryUtil actIdRepositoryUtil;
    @Mock
    private ActLocatorParticipationRepositoryUtil actLocatorParticipationRepositoryUtil;
    @Mock
    private ActRelationshipRepositoryUtil actRelationshipRepositoryUtil;
    @Mock
    private ParticipationRepositoryUtil participationRepositoryUtil;
    @Mock
    private NbsCaseAnswerJdbcRepository nbsCaseAnswerRepository;
    @Mock
    private NbsActJdbcRepository actEntityRepository;
    @InjectMocks
    private PublicHealthCaseRepositoryUtil publicHealthCaseRepositoryUtil;
    @Mock
    AuthUtil authUtil;

    @Mock
    UidPoolManager uidPoolManager;

    @BeforeEach
    void setUp() throws DataProcessingException {
        MockitoAnnotations.openMocks(this);
        AuthUserProfileInfo userInfo = new AuthUserProfileInfo();
        AuthUser user = new AuthUser();
        user.setAuthUserUid(1L);
        user.setUserType(NEDSSConstant.SEC_USERTYPE_EXTERNAL);
        userInfo.setAuthUser(user);

        var model = new LocalUidModel();
        LocalUidGeneratorDto dto = new LocalUidGeneratorDto();
        dto.setClassNameCd("TEST");
        dto.setTypeCd("TEST");
        dto.setUidPrefixCd("TEST");
        dto.setUidSuffixCd("TEST");
        dto.setSeedValueNbr(1L);
        dto.setCounter(3);
        dto.setUsedCounter(2);
        model.setClassTypeUid(dto);
        model.setGaTypeUid(dto);
        model.setPrimaryClassName("TEST");
        when(uidPoolManager.getNextUid(any(), anyBoolean())).thenReturn(model);

        authUtil.setGlobalAuthUser(userInfo);
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(publicHealthCaseRepository, authUtil,odseIdGeneratorService,
                actRepository, actIdRepository, confirmationMethodRepository, caseManagementRepository,
                confirmationMethodRepositoryUtil, caseManagementRepositoryUtil, actIdRepositoryUtil,
                actLocatorParticipationRepositoryUtil,
                actRelationshipRepositoryUtil, participationRepositoryUtil, nbsCaseAnswerRepository,
                actEntityRepository);
    }

    @Test
    void update_Test() throws DataProcessingException {
        PublicHealthCaseContainer phc = null;

        var res = publicHealthCaseRepositoryUtil.update(phc);
        assertNull(res);
    }

    @Test
    void update_Test_2() throws DataProcessingException {
        PublicHealthCaseContainer phc = new PublicHealthCaseContainer();
        phc.getThePublicHealthCaseDto().setPublicHealthCaseUid(10L);
        var confirmCol = new ArrayList<ConfirmationMethodDto>();
        var confirm = new ConfirmationMethodDto();
        confirmCol.add(confirm);
        phc.setTheConfirmationMethodDTCollection(confirmCol);


        var caseDt = new CaseManagementDto();
        caseDt.setCaseManagementDTPopulated(true);
        caseDt.setEpiLinkId("TEST");
        caseDt.setFieldRecordNumber("TEST");
        phc.setTheCaseManagementDto(caseDt);

        var localId = new LocalUidModel();
        localId.setGaTypeUid(new LocalUidGeneratorDto());
        localId.setClassTypeUid(new LocalUidGeneratorDto());
        localId.getClassTypeUid().setSeedValueNbr(10L);
        localId.getGaTypeUid().setSeedValueNbr(10L);

        localId.getClassTypeUid().setUidPrefixCd("TEST");
        localId.getClassTypeUid().setUidSuffixCd("TEST");

        localId.getGaTypeUid().setUidPrefixCd("TEST");
        localId.getGaTypeUid().setUidSuffixCd("TEST");

        when(odseIdGeneratorService.getValidLocalUid(EPILINK, false)).thenReturn(localId);

        var actIdCol = new ArrayList<ActIdDto>();
        var actId = new ActIdDto();
        actIdCol.add(actId);
        phc.setTheActIdDTCollection(actIdCol);

        var patCol = new ArrayList<ActivityLocatorParticipationDto>();
        var pat = new ActivityLocatorParticipationDto();
        pat.setLocatorUid(10L);
        pat.setEntityUid(10L);
        patCol.add(pat);
        phc.setTheActivityLocatorParticipationDTCollection(patCol);

        var res = publicHealthCaseRepositoryUtil.update(phc);
        assertNotNull(res);
    }

    @Test
    void create_Test()  {
        PublicHealthCaseContainer phc = new PublicHealthCaseContainer();
        phc.getThePublicHealthCaseDto().setPublicHealthCaseUid(10L);
        var confirmCol = new ArrayList<ConfirmationMethodDto>();
        var confirm = new ConfirmationMethodDto();
        confirmCol.add(confirm);
        phc.setTheConfirmationMethodDTCollection(confirmCol);


        var caseDt = new CaseManagementDto();
        caseDt.setCaseManagementDTPopulated(true);
        caseDt.setEpiLinkId("TEST");
        caseDt.setFieldRecordNumber("TEST");
        phc.setTheCaseManagementDto(caseDt);

        var actIdCol = new ArrayList<ActIdDto>();
        var actId = new ActIdDto();
        actIdCol.add(actId);
        phc.setTheActIdDTCollection(actIdCol);

        var patCol = new ArrayList<ActivityLocatorParticipationDto>();
        var pat = new ActivityLocatorParticipationDto();
        pat.setLocatorUid(10L);
        pat.setEntityUid(10L);
        patCol.add(pat);
        phc.setTheActivityLocatorParticipationDTCollection(patCol);

        phc.getThePublicHealthCaseDto().setSharedInd("T");
        phc.getThePublicHealthCaseDto().setCaseTypeCd(NEDSSConstant.I);

        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            publicHealthCaseRepositoryUtil.create(phc);
        });
        assertNotNull(thrown);
    }

    @Test
    void create_Test_2() throws DataProcessingException {
        PublicHealthCaseContainer phc = new PublicHealthCaseContainer();
        phc.getThePublicHealthCaseDto().setPublicHealthCaseUid(10L);
        var confirmCol = new ArrayList<ConfirmationMethodDto>();
        var confirm = new ConfirmationMethodDto();
        confirmCol.add(confirm);
        phc.setTheConfirmationMethodDTCollection(confirmCol);


        var caseDt = new CaseManagementDto();
        caseDt.setCaseManagementDTPopulated(true);
        caseDt.setEpiLinkId("TEST");
        caseDt.setFieldRecordNumber("TEST");
        phc.setTheCaseManagementDto(caseDt);

        var actIdCol = new ArrayList<ActIdDto>();
        var actId = new ActIdDto();
        actIdCol.add(actId);
        phc.setTheActIdDTCollection(actIdCol);

        var patCol = new ArrayList<ActivityLocatorParticipationDto>();
        var pat = new ActivityLocatorParticipationDto();
        pat.setLocatorUid(10L);
        pat.setEntityUid(10L);
        patCol.add(pat);
        phc.setTheActivityLocatorParticipationDTCollection(patCol);

        phc.getThePublicHealthCaseDto().setSharedInd("T");
        phc.getThePublicHealthCaseDto().setCaseTypeCd("BLAH");




        var localId = new LocalUidModel();
        localId .setGaTypeUid(new LocalUidGeneratorDto());
        localId .setClassTypeUid(new LocalUidGeneratorDto());
        localId .getClassTypeUid().setSeedValueNbr(10L);
        localId .getGaTypeUid().setSeedValueNbr(10L);

        localId.getGaTypeUid().setUidPrefixCd("TEST");
        localId.getGaTypeUid().setUidSuffixCd("TEST");

        localId.getClassTypeUid().setUidPrefixCd("TEST");
        localId.getClassTypeUid().setUidSuffixCd("TEST");
        when(odseIdGeneratorService.getValidLocalUid(PUBLIC_HEALTH_CASE, true)).thenReturn(localId);


        phc.getThePublicHealthCaseDto().setCoinfectionId(NEDSSConstant.COINFCTION_GROUP_ID_NEW_CODE);
        when(odseIdGeneratorService.getValidLocalUid(COINFECTION_GROUP, false)).thenReturn(localId);


        var res = publicHealthCaseRepositoryUtil.create(phc);
        assertNotNull(res);
    }

    @Test
    void loadObject_Test() throws DataProcessingException {
        Long phcUid = 10L;

        var phcDt = new PublicHealthCase();
        when(publicHealthCaseRepository.findById(phcUid)).thenReturn(phcDt);
        when(confirmationMethodRepositoryUtil.getConfirmationMethodByPhc(phcUid)).thenReturn(new ArrayList<>());
        when(caseManagementRepositoryUtil.getCaseManagementPhc(phcUid)).thenReturn(new CaseManagementDto());
        when(actIdRepositoryUtil.getActIdCollection(phcUid)).thenReturn(new ArrayList<>());
        when(actLocatorParticipationRepositoryUtil.getActLocatorParticipationCollection(phcUid)).thenReturn(new ArrayList<>());
        when(actRelationshipRepositoryUtil.selectActRelationshipDTCollectionFromActUid(phcUid)).thenReturn(new ArrayList<>());
        when(participationRepositoryUtil.getParticipationsByActUid(phcUid)).thenReturn(new ArrayList<>());

        var res = publicHealthCaseRepositoryUtil.loadObject(phcUid);
        assertNotNull(res);
    }




    @Test
    void getPublicHealthCaseContainer_Test() throws DataProcessingException {
        long phcUid = 10L;
        var phcDt = new PublicHealthCase();
        when(publicHealthCaseRepository.findById(phcUid)).thenReturn(phcDt);

        var res = publicHealthCaseRepositoryUtil.getPublicHealthCaseContainer(phcUid);
        assertNotNull(res);

    }

    @Test
    void getPamVO_Test() throws DataProcessingException {
        Long phcUid = 10L;

        var nbsCaseAnCol = new ArrayList<NbsCaseAnswer>();
        var nsbCaseAn = new NbsCaseAnswer();
        nsbCaseAn.setNbsQuestionUid(10L);
        nsbCaseAn.setAnswerGroupSeqNbr(2);
        nbsCaseAnCol.add(nsbCaseAn);
        nsbCaseAn = new NbsCaseAnswer();
        nsbCaseAn.setNbsQuestionUid(10L);
        nsbCaseAn.setAnswerGroupSeqNbr(2);
        nbsCaseAnCol.add(nsbCaseAn);
        nsbCaseAn = new NbsCaseAnswer();
        nsbCaseAn.setNbsQuestionUid(10L);
        nsbCaseAn.setAnswerGroupSeqNbr(-2);
        nsbCaseAn.setSeqNbr(2);
        nbsCaseAnCol.add(nsbCaseAn);
        nsbCaseAn = new NbsCaseAnswer();
        nsbCaseAn.setNbsQuestionUid(11L);
        nsbCaseAn.setAnswerGroupSeqNbr(-2);
        nsbCaseAn.setSeqNbr(2);
        nbsCaseAnCol.add(nsbCaseAn);
        nsbCaseAn = new NbsCaseAnswer();
        nsbCaseAn.setNbsQuestionUid(11L);
        nsbCaseAn.setAnswerGroupSeqNbr(-2);
        nsbCaseAn.setSeqNbr(-2);
        nbsCaseAnCol.add(nsbCaseAn);
        when(nbsCaseAnswerRepository.getNbsCaseAnswerByActUid(phcUid)).thenReturn(nbsCaseAnCol);

        var actCol = new ArrayList<NbsActEntity>();
        var act = new NbsActEntity();
        actCol.add(act);
        when(actEntityRepository.getNbsActEntitiesByActUid(phcUid)).thenReturn(actCol);


        var res= publicHealthCaseRepositoryUtil.getPamVO(phcUid);

        assertNotNull(res);
    }



    @Test
    void getPlace_Test() {
        var uid = 10L;
        when(supportForPhcJdbcRepository.findPlaceById(uid)).thenReturn(new Place());
        var res= publicHealthCaseRepositoryUtil.getPlace(uid);
        assertNotNull(res);
    }

    @Test
    void getPlace_Test_2() {
        var uid = 10L;
        when(supportForPhcJdbcRepository.findPlaceById(uid)).thenReturn(null);
        var res= publicHealthCaseRepositoryUtil.getPlace(uid);
        assertNull(res);
    }

    @Test
    void getNonPersonLivingSubject_Test() {
        var uid = 10L;
        when(supportForPhcJdbcRepository.findNonPersonLivingSubjectById(uid)).thenReturn(new NonPersonLivingSubject());
        var res= publicHealthCaseRepositoryUtil.getNonPersonLivingSubject(uid);
        assertNotNull(res);
    }

    @Test
    void getNonPersonLivingSubject_Test_2() {
        var uid = 10L;
        when(supportForPhcJdbcRepository.findNonPersonLivingSubjectById(uid)).thenReturn(null);
        var res= publicHealthCaseRepositoryUtil.getNonPersonLivingSubject(uid);
        assertNull(res);
    }

    @Test
    void getClinicalDocument_Test() {
        var uid = 10L;
        when(supportForPhcJdbcRepository.findClinicalDocumentById(uid)).thenReturn(new ClinicalDocument());
        var res= publicHealthCaseRepositoryUtil.getClinicalDocument(uid);
        assertNotNull(res);
    }

    @Test
    void getClinicalDocument_Test_2() {
        var uid = 10L;
        when(supportForPhcJdbcRepository.findClinicalDocumentById(uid)).thenReturn(null);
        var res= publicHealthCaseRepositoryUtil.getClinicalDocument(uid);
        assertNull(res);
    }

    @Test
    void getReferral_Test() {
        var uid = 10L;
        when(supportForPhcJdbcRepository.findReferralById(uid)).thenReturn(new Referral());
        var res= publicHealthCaseRepositoryUtil.getReferral(uid);
        assertNotNull(res);
    }

    @Test
    void getReferral_Test_2() {
        var uid = 10L;
        when(supportForPhcJdbcRepository.findReferralById(uid)).thenReturn(null);
        var res= publicHealthCaseRepositoryUtil.getReferral(uid);
        assertNull(res);
    }

    @Test
    void getPatientEncounter_Test() {
        var uid = 10L;
        when(supportForPhcJdbcRepository.findPatientEncounterById(uid)).thenReturn(new PatientEncounter());
        var res= publicHealthCaseRepositoryUtil.getPatientEncounter(uid);
        assertNotNull(res);
    }

    @Test
    void getPatientEncounter_Test_2() {
        var uid = 10L;
        when(supportForPhcJdbcRepository.findPatientEncounterById(uid)).thenReturn(null);
        var res= publicHealthCaseRepositoryUtil.getPatientEncounter(uid);
        assertNull(res);
    }

    @Test
    void updateCaseManagementWithEPIIDandFRNum_Test() throws DataProcessingException {
        CaseManagementDto caseManagementDto = new CaseManagementDto();
        var localId = new LocalUidModel();
        localId.setGaTypeUid(new LocalUidGeneratorDto());
        localId.setClassTypeUid(new LocalUidGeneratorDto());

        publicHealthCaseRepositoryUtil.updateCaseManagementWithEPIIDandFRNum(caseManagementDto);

        verify(uidPoolManager, times(1)).getNextUid(any(), anyBoolean());
    }

    @Test
    void updateCaseManagementWithEPIIDandFRNum_Test_2() throws DataProcessingException {
        CaseManagementDto caseManagementDto = new CaseManagementDto();
        caseManagementDto.setEpiLinkId("TEST");
        var localId = new LocalUidModel();
        localId.setGaTypeUid(new LocalUidGeneratorDto());
        localId.setClassTypeUid(new LocalUidGeneratorDto());

        publicHealthCaseRepositoryUtil.updateCaseManagementWithEPIIDandFRNum(caseManagementDto);

        verify(uidPoolManager, times(1)).getNextUid(any(), anyBoolean());
    }
}
