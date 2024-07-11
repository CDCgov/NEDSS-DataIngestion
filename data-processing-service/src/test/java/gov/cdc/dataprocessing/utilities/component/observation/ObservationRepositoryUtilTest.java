package gov.cdc.dataprocessing.utilities.component.observation;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.constant.enums.LocalIdClass;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.ObservationContainer;
import gov.cdc.dataprocessing.model.dto.act.ActIdDto;
import gov.cdc.dataprocessing.model.dto.act.ActRelationshipDto;
import gov.cdc.dataprocessing.model.dto.act.ActivityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.observation.*;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActId;
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActLocatorParticipation;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.repository.nbs.odse.model.generic_helper.LocalUidGenerator;
import gov.cdc.dataprocessing.repository.nbs.odse.model.observation.*;
import gov.cdc.dataprocessing.repository.nbs.odse.model.participation.Participation;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.act.ActIdRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.act.ActLocatorParticipationRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.act.ActRelationshipRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.act.ActRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.observation.*;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.participation.ParticipationRepository;
import gov.cdc.dataprocessing.service.implementation.uid_generator.OdseIdGeneratorService;
import gov.cdc.dataprocessing.service.model.auth_user.AuthUserProfileInfo;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.act.ActRelationshipRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.entity.EntityHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ObservationRepositoryUtilTest {
    @Mock
    private ObservationRepository observationRepository;
    @Mock
    private ObservationReasonRepository observationReasonRepository;
    @Mock
    private ActIdRepository actIdRepository;
    @Mock
    private ObservationInterpRepository observationInterpRepository;
    @Mock
    private ObsValueCodedRepository obsValueCodedRepository;
    @Mock
    private ObsValueTxtRepository obsValueTxtRepository;
    @Mock
    private ObsValueDateRepository obsValueDateRepository;
    @Mock
    private ObsValueNumericRepository obsValueNumericRepository;
    @Mock
    private ActLocatorParticipationRepository actLocatorParticipationRepository;
    @Mock
    private ActRelationshipRepository actRelationshipRepository;
    @Mock
    private ParticipationRepository participationRepository;
    @Mock
    private EntityHelper entityHelper;
    @Mock
    private OdseIdGeneratorService odseIdGeneratorService;
    @Mock
    private ActRelationshipRepositoryUtil actRelationshipRepositoryUtil;
    @Mock
    private ActRepository actRepository;

    @InjectMocks
    private ObservationRepositoryUtil observationRepositoryUtil;
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
        Mockito.reset(observationRepository, observationReasonRepository, authUtil,
                actIdRepository, observationInterpRepository, obsValueCodedRepository,
                obsValueTxtRepository, obsValueDateRepository, obsValueNumericRepository,
                actLocatorParticipationRepository, actRelationshipRepository, participationRepository,
                entityHelper, odseIdGeneratorService, actRelationshipRepositoryUtil,
                actRepository);
    }

    @Test
    void loadObject_Test() throws DataProcessingException {
        long obUid = 10L;

        var obs = new Observation();
        when(observationRepository.findById(obUid)).thenReturn(Optional.of(obs));

        var obsReasonCol = new ArrayList<ObservationReason>();
        var obsReason = new ObservationReason();
        obsReasonCol.add(obsReason);
        when(observationReasonRepository.findRecordsById(obUid)).thenReturn(obsReasonCol);

        var actIdCol = new ArrayList<ActId>();
        var actId = new ActId();
        actIdCol.add(actId);
        when(actIdRepository.findRecordsById(obUid)).thenReturn(Optional.of(actIdCol));

        var interCol = new ArrayList<ObservationInterp>();
        var inter = new ObservationInterp();
        interCol.add(inter);
        when(observationInterpRepository.findRecordsById(obUid)).thenReturn(interCol);

        var codedCol = new ArrayList<ObsValueCoded>();
        var coded = new ObsValueCoded();
        codedCol.add(coded);
        when(obsValueCodedRepository.findRecordsById(obUid)).thenReturn(codedCol);


        var valueCol = new ArrayList<ObsValueTxt>();
        var value = new ObsValueTxt();
        valueCol.add(value);
        when(obsValueTxtRepository.findRecordsById(obUid)).thenReturn(valueCol);

        var dateCol = new ArrayList<ObsValueDate>();
        var date = new ObsValueDate();
        dateCol.add(date);
        when(obsValueDateRepository.findRecordsById(obUid)).thenReturn(dateCol);

        var numCol = new ArrayList<ObsValueNumeric>();
        var num = new ObsValueNumeric();
        numCol.add(num);
        when(obsValueNumericRepository.findRecordsById(obUid)).thenReturn(numCol);

        var actLocCol = new ArrayList<ActLocatorParticipation>();
        var actLoc = new ActLocatorParticipation();
        actLocCol.add(actLoc);
        when(actLocatorParticipationRepository.findRecordsById(obUid)).thenReturn(actLocCol);

        var actReCol = new ArrayList<ActRelationshipDto>();
        var actRe = new ActRelationshipDto();
        actReCol.add(actRe);
        when(actRelationshipRepositoryUtil.selectActRelationshipDTCollectionFromActUid(obUid)).thenReturn(actReCol);

        var patCol = new ArrayList<Participation>();
        var pat = new Participation();
        patCol.add(pat);
        when(participationRepository.findByActUid(obUid)).thenReturn(Optional.of(patCol));


        var res =  observationRepositoryUtil.loadObject(obUid);
        assertNotNull(res);

    }


    @Test
    void saveObservation_Test() throws DataProcessingException {
        ObservationContainer observationContainer = new ObservationContainer();


        var obsReasonCol = new ArrayList<ObservationReasonDto>();
        var obsReason = new ObservationReasonDto();
        obsReasonCol.add(obsReason);
        observationContainer.setTheObservationReasonDtoCollection(obsReasonCol);


        var actIdCol = new ArrayList<ActIdDto>();
        var actId = new ActIdDto();
        actIdCol.add(actId);
        observationContainer.setTheActIdDtoCollection(actIdCol);

        var interCol = new ArrayList<ObservationInterpDto>();
        var inter = new ObservationInterpDto();
        interCol.add(inter);
        observationContainer.setTheObservationInterpDtoCollection(interCol);

        var codedCol = new ArrayList<ObsValueCodedDto>();
        var coded = new ObsValueCodedDto();
        codedCol.add(coded);
        observationContainer.setTheObsValueCodedDtoCollection(codedCol);


        var valueCol = new ArrayList<ObsValueTxtDto>();
        var value = new ObsValueTxtDto();
        valueCol.add(value);
        observationContainer.setTheObsValueTxtDtoCollection(valueCol);

        var dateCol = new ArrayList<ObsValueDateDto>();
        var date = new ObsValueDateDto();
        dateCol.add(date);
        observationContainer.setTheObsValueDateDtoCollection(dateCol);

        var numCol = new ArrayList<ObsValueNumericDto>();
        var num = new ObsValueNumericDto();
        numCol.add(num);
        observationContainer.setTheObsValueNumericDtoCollection(numCol);

        var actLocCol = new ArrayList<ActivityLocatorParticipationDto>();
        var actLoc = new ActivityLocatorParticipationDto();
        actLocCol.add(actLoc);
        observationContainer.setTheActivityLocatorParticipationDtoCollection(actLocCol);

        var actReCol = new ArrayList<ActRelationshipDto>();
        var actRe = new ActRelationshipDto();
        actReCol.add(actRe);
        observationContainer.setTheActRelationshipDtoCollection(actReCol);

        var patCol = new ArrayList<ParticipationDto>();
        var pat = new ParticipationDto();
        patCol.add(pat);
        observationContainer.setTheParticipationDtoCollection(patCol);

        observationContainer.setItNew(true);

        var localId = new LocalUidGenerator();
        localId.setSeedValueNbr(10L);
        localId.setUidPrefixCd("TEST");
        localId.setUidSuffixCd("TEST");
        when(odseIdGeneratorService.getLocalIdAndUpdateSeed(LocalIdClass.OBSERVATION)).thenReturn(localId);



        var res = observationRepositoryUtil.saveObservation(observationContainer);

        assertNotNull(res);
    }


    @Test
    void updateObservation_Test() throws DataProcessingException {
        ObservationContainer observationContainer = new ObservationContainer();


        var obsReasonCol = new ArrayList<ObservationReasonDto>();
        var obsReason = new ObservationReasonDto();
        obsReasonCol.add(obsReason);
        observationContainer.setTheObservationReasonDtoCollection(obsReasonCol);


        var actIdCol = new ArrayList<ActIdDto>();
        var actId = new ActIdDto();
        actIdCol.add(actId);
        observationContainer.setTheActIdDtoCollection(actIdCol);

        var interCol = new ArrayList<ObservationInterpDto>();
        var inter = new ObservationInterpDto();
        interCol.add(inter);
        observationContainer.setTheObservationInterpDtoCollection(interCol);

        var codedCol = new ArrayList<ObsValueCodedDto>();
        var coded = new ObsValueCodedDto();
        codedCol.add(coded);
        observationContainer.setTheObsValueCodedDtoCollection(codedCol);


        var valueCol = new ArrayList<ObsValueTxtDto>();
        var value = new ObsValueTxtDto();
        valueCol.add(value);
        observationContainer.setTheObsValueTxtDtoCollection(valueCol);

        var dateCol = new ArrayList<ObsValueDateDto>();
        var date = new ObsValueDateDto();
        dateCol.add(date);
        observationContainer.setTheObsValueDateDtoCollection(dateCol);

        var numCol = new ArrayList<ObsValueNumericDto>();
        var num = new ObsValueNumericDto();
        numCol.add(num);
        observationContainer.setTheObsValueNumericDtoCollection(numCol);

        var actLocCol = new ArrayList<ActivityLocatorParticipationDto>();
        var actLoc = new ActivityLocatorParticipationDto();
        actLocCol.add(actLoc);
        observationContainer.setTheActivityLocatorParticipationDtoCollection(actLocCol);

        var actReCol = new ArrayList<ActRelationshipDto>();
        var actRe = new ActRelationshipDto();
        actReCol.add(actRe);
        observationContainer.setTheActRelationshipDtoCollection(actReCol);

        var patCol = new ArrayList<ParticipationDto>();
        var pat = new ParticipationDto();
        patCol.add(pat);
        observationContainer.setTheParticipationDtoCollection(patCol);

        observationContainer.setItNew(false);


        observationContainer.getTheObservationDto().setObservationUid(10L);

        var actId1Col = new ArrayList<ActId>();
        var actId1 = new ActId();
        actId1Col.add(actId1);
        when(actIdRepository.findRecordsById(any())).thenReturn(Optional.of(actId1Col));


        var res = observationRepositoryUtil.saveObservation(observationContainer);

        assertNotNull(res);
    }

    @Test
    void saveActRelationship_Test() {
        ActRelationshipDto actRelationshipDto = new ActRelationshipDto();
        actRelationshipDto.setItNew(true);

        observationRepositoryUtil.saveActRelationship(actRelationshipDto);

        verify(actRelationshipRepository, times(2)).save(any());

    }

    @Test
    void saveActRelationship_Test_2() {
        ActRelationshipDto actRelationshipDto = new ActRelationshipDto();
        actRelationshipDto.setItDelete(true);

        observationRepositoryUtil.saveActRelationship(actRelationshipDto);

        verify(actRelationshipRepository, times(1)).save(any());
        verify(actRelationshipRepository, times(1)).delete(any());

    }

    @Test
    void saveActRelationship_Test_3() {
        ActRelationshipDto actRelationshipDto = new ActRelationshipDto();
        actRelationshipDto.setItDirty(true);
        actRelationshipDto.setTargetActUid(10L);
        actRelationshipDto.setSourceActUid(10L);
        actRelationshipDto.setTypeCd("TEST");

        observationRepositoryUtil.saveActRelationship(actRelationshipDto);

        verify(actRelationshipRepository, times(2)).save(any());

    }


    @Test
    void setObservationInfo_Test()  {
        ObservationDto observationDto = new ObservationDto();
        observationDto.setObservationUid(10L);

        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            observationRepositoryUtil.setObservationInfo(observationDto);
        });

        assertNotNull(thrown);

    }

    @Test
    void setObservationInfo_Test_2()  {
        ObservationDto observationDto = new ObservationDto();
        observationDto.setObservationUid(null);

        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            observationRepositoryUtil.setObservationInfo(observationDto);
        });

        assertNotNull(thrown);

    }

    @Test
    void setObservationInfo_Test_3()  {

        NullPointerException thrown = assertThrows(NullPointerException.class, () -> {
            observationRepositoryUtil.setObservationInfo(null);
        });

        assertNotNull(thrown);

    }

    @Test
    void retrieveObservationQuestion_Test() {
        Long targetUid = 10L;

        var obsQuesCol = new ArrayList<Observation_Question>();
        var obsQues = new Observation_Question();
        obsQues.setTargetActUid(10L);
        obsQues.setObservationUid(10L);
        obsQues.setObsCodeUid(10L);
        obsQues.setObsDateUid(10L);
        obsQues.setObsNumericUid(10L);
        obsQues.setObsTxtUid(10L);
        obsQuesCol.add(obsQues);
        obsQues = new Observation_Question();
        obsQues.setTargetActUid(10L);
        obsQues.setObservationUid(10L);
        obsQues.setObsCodeUid(10L);
        obsQues.setObsDateUid(10L);
        obsQues.setObsNumericUid(10L);
        obsQues.setObsTxtUid(10L);
        obsQuesCol.add(obsQues);

        when(observationRepository.retrieveObservationQuestion(targetUid)).thenReturn(Optional.of(obsQuesCol));

        var res = observationRepositoryUtil.retrieveObservationQuestion(targetUid);

        assertNotNull(res);
    }


    @Test
    void addActivityLocatorParticipations_Test() throws DataProcessingException {
        Long obsUid = 10L;
        ArrayList<ActivityLocatorParticipationDto> activityLocatorParticipationDtoCollection = new ArrayList<>();
        ActivityLocatorParticipationDto activityLocatorParticipationDto = new ActivityLocatorParticipationDto();
        activityLocatorParticipationDtoCollection.add(activityLocatorParticipationDto);

        observationRepositoryUtil.addActivityLocatorParticipations(obsUid, activityLocatorParticipationDtoCollection);
        verify(actLocatorParticipationRepository, times(1)).save(any());

    }

}
