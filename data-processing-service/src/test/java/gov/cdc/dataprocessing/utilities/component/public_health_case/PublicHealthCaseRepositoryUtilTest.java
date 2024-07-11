package gov.cdc.dataprocessing.utilities.component.public_health_case;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.PublicHealthCaseContainer;
import gov.cdc.dataprocessing.model.dto.act.ActIdDto;
import gov.cdc.dataprocessing.model.dto.act.ActivityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.phc.CaseManagementDto;
import gov.cdc.dataprocessing.model.dto.phc.ConfirmationMethodDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.repository.nbs.odse.model.generic_helper.LocalUidGenerator;
import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsActEntity;
import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsCaseAnswer;
import gov.cdc.dataprocessing.repository.nbs.odse.model.phc.*;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.act.ActIdRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.act.ActLocatorParticipationRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.act.ActRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.act.NbsActEntityRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.nbs.NbsCaseAnswerRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.phc.*;
import gov.cdc.dataprocessing.service.implementation.uid_generator.OdseIdGeneratorService;
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
import java.util.Optional;

import static gov.cdc.dataprocessing.constant.enums.LocalIdClass.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PublicHealthCaseRepositoryUtilTest {
    @Mock
    private PublicHealthCaseRepository publicHealthCaseRepository;
    @Mock
    private EntityGroupRepository entityGroupRepository;
    @Mock
    private PlaceRepository placeRepository;
    @Mock
    private NonPersonLivingSubjectRepository nonPersonLivingSubjectRepository;
    @Mock
    private ClinicalDocumentRepository clinicalDocumentRepository;
    @Mock
    private ReferralRepository referralRepository;
    @Mock
    private PatientEncounterRepository patientEncounterRepository;
    @Mock
    private OdseIdGeneratorService odseIdGeneratorService;
    @Mock
    private ActRepository actRepository;
    @Mock
    private ActIdRepository actIdRepository;
    @Mock
    private ConfirmationMethodRepository confirmationMethodRepository;
    @Mock
    private ActLocatorParticipationRepository actLocatorParticipationRepository;
    @Mock
    private CaseManagementRepository caseManagementRepository;
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
    private NbsCaseAnswerRepository nbsCaseAnswerRepository;
    @Mock
    private NbsActEntityRepository actEntityRepository;
    @InjectMocks
    private PublicHealthCaseRepositoryUtil publicHealthCaseRepositoryUtil;
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
        Mockito.reset(publicHealthCaseRepository, entityGroupRepository, authUtil,
                placeRepository, nonPersonLivingSubjectRepository, clinicalDocumentRepository,
                referralRepository, patientEncounterRepository, odseIdGeneratorService,
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

        var localId = new LocalUidGenerator();
        localId.setSeedValueNbr(10L);
        localId.setUidPrefixCd("TEST");
        localId.setUidSuffixCd("TEST");
        when(odseIdGeneratorService.getLocalIdAndUpdateSeed(EPILINK)).thenReturn(localId);

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


        var localId = new LocalUidGenerator();
        localId.setSeedValueNbr(10L);
        localId.setUidPrefixCd("TEST");
        localId.setUidSuffixCd("TEST");
        when(odseIdGeneratorService.getLocalIdAndUpdateSeed(PUBLIC_HEALTH_CASE)).thenReturn(localId);


        phc.getThePublicHealthCaseDto().setCoinfectionId(NEDSSConstant.COINFCTION_GROUP_ID_NEW_CODE);
        when(odseIdGeneratorService.getLocalIdAndUpdateSeed(COINFECTION_GROUP)).thenReturn(localId);


        var res = publicHealthCaseRepositoryUtil.create(phc);
        assertNotNull(res);
    }

    @Test
    void loadObject_Test() throws DataProcessingException {
        Long phcUid = 10L;

        var phcDt = new PublicHealthCase();
        when(publicHealthCaseRepository.findById(phcUid)).thenReturn(Optional.of(phcDt));
        when(confirmationMethodRepositoryUtil.getConfirmationMethodByPhc(phcUid)).thenReturn(new ArrayList<>());
        when(caseManagementRepositoryUtil.getCaseManagementPhc(phcUid)).thenReturn(new CaseManagementDto());
        when(actIdRepositoryUtil.GetActIdCollection(phcUid)).thenReturn(new ArrayList<>());
        when(actLocatorParticipationRepositoryUtil.getActLocatorParticipationCollection(phcUid)).thenReturn(new ArrayList<>());
        when(actRelationshipRepositoryUtil.selectActRelationshipDTCollectionFromActUid(phcUid)).thenReturn(new ArrayList<>());
        when(participationRepositoryUtil.getParticipationsByActUid(phcUid)).thenReturn(new ArrayList<>());

        var res = publicHealthCaseRepositoryUtil.loadObject(phcUid);
        assertNotNull(res);
    }

    @Test
    void loadObject_Test_2()   {
        Long phcUid = 10L;

        when(publicHealthCaseRepository.findById(phcUid)).thenReturn(Optional.empty());

        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            publicHealthCaseRepositoryUtil.loadObject(phcUid);
        });
        assertNotNull(thrown);

    }

    @Test
    void loadObject_Test_3()   {
        Long phcUid = 10L;

        when(publicHealthCaseRepository.findById(phcUid)).thenThrow(new RuntimeException("TEST"));

        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            publicHealthCaseRepositoryUtil.loadObject(phcUid);
        });
        assertNotNull(thrown);

    }

    @Test
    void getPublicHealthCaseContainer_Test() throws DataProcessingException {
        long phcUid = 10L;
        var phcDt = new PublicHealthCase();
        when(publicHealthCaseRepository.findById(phcUid)).thenReturn(Optional.of(phcDt));

        var res = publicHealthCaseRepositoryUtil.getPublicHealthCaseContainer(phcUid);
        assertNotNull(res);

    }


    @Test
    void getPublicHealthCaseContainer_Test_2()  {
        long phcUid = 10L;
        when(publicHealthCaseRepository.findById(phcUid)).thenReturn(Optional.empty());


        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            publicHealthCaseRepositoryUtil.getPublicHealthCaseContainer(phcUid);
        });
        assertNotNull(thrown);


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
        when(nbsCaseAnswerRepository.getNbsCaseAnswerByActUid(phcUid)).thenReturn(Optional.of(nbsCaseAnCol));

        var actCol = new ArrayList<NbsActEntity>();
        var act = new NbsActEntity();
        actCol.add(act);
        when(actEntityRepository.getNbsActEntitiesByActUid(phcUid)).thenReturn(Optional.of(actCol));


        var res= publicHealthCaseRepositoryUtil.getPamVO(phcUid);

        assertNotNull(res);
    }



    @Test
    void getPlace_Test() {
        var uid = 10L;
        when(placeRepository.findById(uid)).thenReturn(Optional.of(new Place()));
        var res= publicHealthCaseRepositoryUtil.getPlace(uid);
        assertNotNull(res);
    }

    @Test
    void getPlace_Test_2() {
        var uid = 10L;
        when(placeRepository.findById(uid)).thenReturn(Optional.empty());
        var res= publicHealthCaseRepositoryUtil.getPlace(uid);
        assertNull(res);
    }

    @Test
    void getNonPersonLivingSubject_Test() {
        var uid = 10L;
        when(nonPersonLivingSubjectRepository.findById(uid)).thenReturn(Optional.of(new NonPersonLivingSubject()));
        var res= publicHealthCaseRepositoryUtil.getNonPersonLivingSubject(uid);
        assertNotNull(res);
    }

    @Test
    void getNonPersonLivingSubject_Test_2() {
        var uid = 10L;
        when(nonPersonLivingSubjectRepository.findById(uid)).thenReturn(Optional.empty());
        var res= publicHealthCaseRepositoryUtil.getNonPersonLivingSubject(uid);
        assertNull(res);
    }

    @Test
    void getClinicalDocument_Test() {
        var uid = 10L;
        when(clinicalDocumentRepository.findById(uid)).thenReturn(Optional.of(new ClinicalDocument()));
        var res= publicHealthCaseRepositoryUtil.getClinicalDocument(uid);
        assertNotNull(res);
    }

    @Test
    void getClinicalDocument_Test_2() {
        var uid = 10L;
        when(clinicalDocumentRepository.findById(uid)).thenReturn(Optional.empty());
        var res= publicHealthCaseRepositoryUtil.getClinicalDocument(uid);
        assertNull(res);
    }

    @Test
    void getReferral_Test() {
        var uid = 10L;
        when(referralRepository.findById(uid)).thenReturn(Optional.of(new Referral()));
        var res= publicHealthCaseRepositoryUtil.getReferral(uid);
        assertNotNull(res);
    }

    @Test
    void getReferral_Test_2() {
        var uid = 10L;
        when(referralRepository.findById(uid)).thenReturn(Optional.empty());
        var res= publicHealthCaseRepositoryUtil.getReferral(uid);
        assertNull(res);
    }

    @Test
    void getPatientEncounter_Test() {
        var uid = 10L;
        when(patientEncounterRepository.findById(uid)).thenReturn(Optional.of(new PatientEncounter()));
        var res= publicHealthCaseRepositoryUtil.getPatientEncounter(uid);
        assertNotNull(res);
    }

    @Test
    void getPatientEncounter_Test_2() {
        var uid = 10L;
        when(patientEncounterRepository.findById(uid)).thenReturn(Optional.empty());
        var res= publicHealthCaseRepositoryUtil.getPatientEncounter(uid);
        assertNull(res);
    }

    @Test
    void updateCaseManagementWithEPIIDandFRNum_Test() throws DataProcessingException {
        CaseManagementDto caseManagementDto = new CaseManagementDto();
        var localId = new LocalUidGenerator();
        when(odseIdGeneratorService.getLocalIdAndUpdateSeed(EPILINK)).thenReturn(localId);

        publicHealthCaseRepositoryUtil.updateCaseManagementWithEPIIDandFRNum(caseManagementDto);

        verify(odseIdGeneratorService, times(1)).getLocalIdAndUpdateSeed(any());
    }

    @Test
    void updateCaseManagementWithEPIIDandFRNum_Test_2() throws DataProcessingException {
        CaseManagementDto caseManagementDto = new CaseManagementDto();
        caseManagementDto.setEpiLinkId("TEST");
        var localId = new LocalUidGenerator();
        when(odseIdGeneratorService.getLocalIdAndUpdateSeed(EPILINK)).thenReturn(localId);

        publicHealthCaseRepositoryUtil.updateCaseManagementWithEPIIDandFRNum(caseManagementDto);

        verify(odseIdGeneratorService, times(1)).getLocalIdAndUpdateSeed(any());
    }
}
