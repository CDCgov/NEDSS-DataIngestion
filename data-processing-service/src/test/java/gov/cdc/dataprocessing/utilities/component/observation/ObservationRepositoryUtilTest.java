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
import gov.cdc.dataprocessing.model.dto.uid.LocalUidGeneratorDto;
import gov.cdc.dataprocessing.model.dto.uid.LocalUidModel;
import gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template.*;
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActId;
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActLocatorParticipation;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.repository.nbs.odse.model.observation.*;
import gov.cdc.dataprocessing.repository.nbs.odse.model.participation.Participation;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.observation.*;
import gov.cdc.dataprocessing.service.implementation.uid_generator.UidPoolManager;
import gov.cdc.dataprocessing.service.interfaces.uid_generator.IOdseIdGeneratorWCacheService;
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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ObservationRepositoryUtilTest {
    @Mock
    private ObservationJdbcRepository observationRepository;
    @Mock
    private ObservationReasonRepository observationReasonRepository;
    @Mock
    private ActIdJdbcRepository actIdRepository;
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
    private ActLocatorParticipationJdbcRepository actLocatorParticipationRepository;
    @Mock
    private ActRelationshipJdbcRepository actRelationshipRepository;
    @Mock
    private ParticipationJdbcRepository participationRepository;
    @Mock
    private EntityHelper entityHelper;
    @Mock
    private IOdseIdGeneratorWCacheService odseIdGeneratorService;
    @Mock
    private ActRelationshipRepositoryUtil actRelationshipRepositoryUtil;
    @Mock
    private ActJdbcRepository actRepository;

    @InjectMocks
    private ObservationRepositoryUtil observationRepositoryUtil;
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

        authUtil.setGlobalAuthUser(userInfo);

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
        when(observationRepository.findObservationByUid(obUid)).thenReturn(obs);

        var obsReasonCol = new ArrayList<ObservationReason>();
        var obsReason = new ObservationReason();
        obsReasonCol.add(obsReason);
        when(observationReasonRepository.findRecordsById(obUid)).thenReturn(obsReasonCol);

        var actIdCol = new ArrayList<ActId>();
        var actId = new ActId();
        actIdCol.add(actId);
        when(actIdRepository.findRecordsByActUid(obUid)).thenReturn(actIdCol);

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
        when(actLocatorParticipationRepository.findByActUid(obUid)).thenReturn(actLocCol);

        var actReCol = new ArrayList<ActRelationshipDto>();
        var actRe = new ActRelationshipDto();
        actReCol.add(actRe);
        when(actRelationshipRepositoryUtil.selectActRelationshipDTCollectionFromActUid(obUid)).thenReturn(actReCol);

        var patCol = new ArrayList<Participation>();
        var pat = new Participation();
        patCol.add(pat);
        when(participationRepository.findByActUid(obUid)).thenReturn(patCol);


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

        var localId = new LocalUidModel();

        localId.setGaTypeUid(new LocalUidGeneratorDto());
        localId.setClassTypeUid(new LocalUidGeneratorDto());
        localId.getClassTypeUid().setSeedValueNbr(1L);
        localId.getGaTypeUid().setSeedValueNbr(1L);
        localId.getClassTypeUid().setUidPrefixCd("TEST");
        localId.getClassTypeUid().setUidSuffixCd("TEST");
        localId.getGaTypeUid().setUidPrefixCd("TEST");
        localId.getGaTypeUid().setUidSuffixCd("TEST");
        when(odseIdGeneratorService.getValidLocalUid(LocalIdClass.OBSERVATION, true)).thenReturn(localId);



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
        when(actIdRepository.findRecordsByActUid(any())).thenReturn(actId1Col);


        var res = observationRepositoryUtil.saveObservation(observationContainer);

        assertNotNull(res);
    }

    @Test
    void saveActRelationship_Test() {
        ActRelationshipDto actRelationshipDto = new ActRelationshipDto();
        actRelationshipDto.setItNew(true);

        observationRepositoryUtil.saveActRelationship(actRelationshipDto);

        verify(actRelationshipRepository, times(1)).insertActRelationship(any());

    }

    @Test
    void saveActRelationship_Test_2() {
        ActRelationshipDto actRelationshipDto = new ActRelationshipDto();
        actRelationshipDto.setItDelete(true);

        observationRepositoryUtil.saveActRelationship(actRelationshipDto);

        verify(actRelationshipRepository, times(1)).deleteActRelationship(any());

    }

    @Test
    void saveActRelationship_Test_3() {
        ActRelationshipDto actRelationshipDto = new ActRelationshipDto();
        actRelationshipDto.setItDirty(true);
        actRelationshipDto.setTargetActUid(10L);
        actRelationshipDto.setSourceActUid(10L);
        actRelationshipDto.setTypeCd("TEST");

        observationRepositoryUtil.saveActRelationship(actRelationshipDto);

        verify(actRelationshipRepository, times(1)).updateActRelationship(any());

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

        when(observationRepository.retrieveObservationQuestion(targetUid)).thenReturn(obsQuesCol);

        var res = observationRepositoryUtil.retrieveObservationQuestion(targetUid);

        assertNotNull(res);
    }


    @Test
    void addActivityLocatorParticipations_Test()  {
        Long obsUid = 10L;
        ArrayList<ActivityLocatorParticipationDto> activityLocatorParticipationDtoCollection = new ArrayList<>();
        ActivityLocatorParticipationDto activityLocatorParticipationDto = new ActivityLocatorParticipationDto();
        activityLocatorParticipationDtoCollection.add(activityLocatorParticipationDto);

        observationRepositoryUtil.addActivityLocatorParticipations(obsUid, activityLocatorParticipationDtoCollection, "CREATE");
        verify(actLocatorParticipationRepository, times(1)).insertActLocatorParticipation(any());

    }

}
