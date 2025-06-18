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
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActRelationship;
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
import org.mockito.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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

    @Test
    void testAddActivityLocatorParticipations_callsUpdateOnUpdateOperation() {
        // Arrange
        ActivityLocatorParticipationDto dto = new ActivityLocatorParticipationDto();

        Collection<ActivityLocatorParticipationDto> dtoList = Collections.singletonList(dto);
        Long obsUid = 123L;

        // Act
        observationRepositoryUtil.addActivityLocatorParticipations(obsUid, dtoList, "UPDATE");

        // Assert
        verify(actLocatorParticipationRepository, times(1))
                .updateActLocatorParticipation(any(ActLocatorParticipation.class));
        verify(actLocatorParticipationRepository, never())
                .insertActLocatorParticipation(any());
    }

    @Test
    void testAddActivityLocatorParticipations_doesNothingWhenCollectionIsNull() {
        // Act
        observationRepositoryUtil.addActivityLocatorParticipations(123L, null, "UPDATE");

        // Assert
        verify(actLocatorParticipationRepository, never()).insertActLocatorParticipation(any());
        verify(actLocatorParticipationRepository, never()).updateActLocatorParticipation(any());
    }

    @Test
    void testAddActivityLocatorParticipations_doesNothingWhenOperationIsInvalid() {
        // Arrange
        ActivityLocatorParticipationDto dto = new ActivityLocatorParticipationDto();

        var dtoList = Collections.singletonList(dto);

        // Act
        observationRepositoryUtil.addActivityLocatorParticipations(999L, dtoList, "DELETE");

        // Assert
        verify(actLocatorParticipationRepository, never()).insertActLocatorParticipation(any());
        verify(actLocatorParticipationRepository, never()).updateActLocatorParticipation(any());
    }

    @Test
    void testUpdateObsValueNumerics_callsDeleteWhenItDeleteIsTrue() {
        // Arrange
        ObsValueNumericDto dto = new ObsValueNumericDto();
        dto.setItDelete(true); // triggers the else branch

        var dtoList = Collections.singletonList(dto);

        ObservationRepositoryUtil spyUtil = spy(observationRepositoryUtil);

        // Act
        spyUtil.updateObsValueNumerics(111L, dtoList);

        // Assert
        verify(observationRepository, never()).insertObsValueNumeric(any());
    }

    @Test
    void testSaveObsValueNumeric_callsUpdateWhenOperationIsUpdate() {
        // Arrange
        ObsValueNumericDto dto = new ObsValueNumericDto();
        dto.setObsValueNumericSeq(101); // example field

        // Act
        observationRepositoryUtil.saveObsValueNumeric(dto, "NA");

        // Assert
        verify(observationRepository, never()).insertObsValueNumeric(any());
    }


    @Test
    void addObsValueNumericInvalidOperation() {

        // Act
        observationRepositoryUtil.addObsValueNumeric(null, null);

        // Assert
        verify(observationRepository, never()).insertObsValueNumeric(any());
    }

    @Test
    void testUpdateObsValueDates_callsDeleteOnlyWhenItDeleteIsTrue() {
        // Arrange
        ObsValueDateDto dto = new ObsValueDateDto();
        dto.setItDelete(true); // this triggers the ELSE branch

        var collection = Collections.singletonList(dto);

        // Spy on the class so we can verify internal protected method calls
        ObservationRepositoryUtil spyUtil = spy(observationRepositoryUtil);

        // Act
        spyUtil.updateObsValueDates(555L, collection);

        // Assert
        verify(observationRepository, times(1)).deleteObsValueDate(any(ObsValueDate.class));
    }

    @Test
    void saveObsValueDate_Invalid() {
        // Arrange
        ObsValueDateDto dto = new ObsValueDateDto();

        // Act
        observationRepositoryUtil.saveObsValueDate(dto, "NA");

        // Assert
        verify(observationRepository, never()).updateObsValueDate(any());
    }

    @Test
    void addObsValueDates_Null() {

        // Act
        observationRepositoryUtil.addObsValueDates(null, null);

        // Assert
        verify(observationRepository, never()).insertObsValueNumeric(any());
    }

    @Test
    void testUpdateObsValueTxts_callsDeleteWhenItDeleteIsTrue() {
        // Arrange
        ObsValueTxtDto dto = new ObsValueTxtDto();
        dto.setItDelete(true);  // Trigger else

        var collection = Collections.singletonList(dto);

        // Spy to check internal method call
        ObservationRepositoryUtil spyUtil = spy(observationRepositoryUtil);

        // Act
        spyUtil.updateObsValueTxts(777L, collection);

        // Assert
        verify(observationRepository, times(1)).deleteObsValueTxt(any(ObsValueTxt.class));
    }

    @Test
    void testSaveObsValueTxt_callsUpdateWhenOperationIsUpdate() {
        // Arrange
        ObsValueTxtDto dto = new ObsValueTxtDto();

        // Act
        observationRepositoryUtil.saveObsValueTxt(dto, "NA");

        // Assert
        verify(observationRepository, never()).insertObsValueTxt(any());
    }

    @Test
    void testAddObsValueTxts_doesNothingWhenCollectionIsNull() {
        observationRepositoryUtil.addObsValueTxts(100L, null);
        verify(observationRepository, never()).insertObsValueTxt(any());
        verify(observationRepository, never()).updateObsValueTxt(any());
    }

    @Test
    void testUpdateObsValueCoded_callsDeleteWhenItDeleteIsTrue() {
        ObsValueCodedDto dto = new ObsValueCodedDto();
        dto.setItDelete(true);
        var collection = Collections.singletonList(dto);

        ObservationRepositoryUtil spyUtil = spy(observationRepositoryUtil);
        spyUtil.updateObsValueCoded(101L, collection);

        verify(observationRepository, times(1)).deleteObsValueCoded(any());
        verify(spyUtil, never()).saveObsValueCoded(any(), eq("UPDATE"));
    }

    @Test
    void testSaveObsValueCoded_doesNothingWhenOperationIsUnknown() {
        ObsValueCodedDto dto = new ObsValueCodedDto();

        observationRepositoryUtil.saveObsValueCoded(dto, "NA");

        verify(observationRepository, never()).insertObsValueCoded(any());
        verify(observationRepository, never()).updateObsValueCoded(any());
    }

    @Test
    void testAddObsValueCoded_doesNothingWhenCollectionIsNull() {
        observationRepositoryUtil.addObsValueCoded(200L, null);
        verify(observationRepository, never()).insertObsValueCoded(any());
    }

    @Test
    void testUpdateObservationInterps_callsDeleteWhenItDeleteIsTrue() {
        ObservationInterpDto dto = new ObservationInterpDto();
        dto.setItDelete(true);
        var collection = Collections.singletonList(dto);

        ObservationRepositoryUtil spyUtil = spy(observationRepositoryUtil);
        spyUtil.updateObservationInterps(300L, collection);

        verify(observationRepository, times(1)).deleteObservationInterp(any());
        verify(spyUtil, never()).saveObservationInterp(any(), eq("UPDATE"));
    }

    @Test
    void testSaveObservationInterp_doesNothingWhenOperationIsUnknown() {
        ObservationInterpDto dto = new ObservationInterpDto();

        observationRepositoryUtil.saveObservationInterp(dto, "NA");

        verify(observationRepository, never()).insertObservationInterp(any());
        verify(observationRepository, never()).updateObservationInterp(any());
    }

    @Test
    void testAddObservationInterps_doesNothingWhenCollectionIsNull() {
        observationRepositoryUtil.addObservationInterps(500L, null);
        verify(observationRepository, never()).insertObservationInterp(any());
    }

    @Test
    void testGetMaxSegId_returnsMaxSegmentId() throws DataProcessingException {
        ActId a1 = new ActId();
        a1.setActIdSeq(3);
        ActId a2 = new ActId();
        a2.setActIdSeq(7);
        ActId a3 = new ActId();
        a3.setActIdSeq(5);

        when(actIdRepository.findRecordsByActUid(111L)).thenReturn(List.of(a1, a2, a3));

        int result = observationRepositoryUtil.getMaxSegId(111L);
        assertEquals(7, result);
    }

    @Test
    void testAddActivityId_doesNothingWhenCollectionIsNull() throws DataProcessingException {
        observationRepositoryUtil.addActivityId(999L, null, false);
        verify(actIdRepository, never()).mergeActId(any());
    }

    @Test
    void testAddActivityId_doesNothingWhenCollectionIsEmpty() throws DataProcessingException {
        observationRepositoryUtil.addActivityId(999L, Collections.emptyList(), false);
        verify(actIdRepository, never()).mergeActId(any());
    }

    @Test
    void testUpdateObservationReason_callsDeleteWhenItDeleteIsTrue() {
        ObservationReasonDto dto = new ObservationReasonDto();
        dto.setItDelete(true); // triggers else

        var collection = Collections.singletonList(dto);
        ObservationRepositoryUtil spyUtil = spy(observationRepositoryUtil);

        spyUtil.updateObservationReason(101L, collection);

        verify(observationRepository, times(1)).deleteObservationReason(any());
        verify(spyUtil, never()).saveObservationReason(any(), eq("UPDATE"));
    }

    @Test
    void testSaveObservationReason_doesNothingWhenOperationIsUnknown() {
        ObservationReasonDto dto = new ObservationReasonDto();

        observationRepositoryUtil.saveObservationReason(dto, "NA");

        verify(observationRepository, never()).insertObservationReason(any());
        verify(observationRepository, never()).updateObservationReason(any());
    }

    @Test
    void testAddObservationReasons_doesNothingWhenCollectionIsNull() {
        observationRepositoryUtil.addObservationReasons(300L, null);
        verify(observationRepository, never()).insertObservationReason(any());
    }

    @Test
    void testSelectParticipationDTCollection_returnsEmptyWhenRepositoryReturnsNull() {
        when(participationRepository.findByActUid(400L)).thenReturn(null);

        var result = observationRepositoryUtil.selectParticipationDTCollection(400L);
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void testSelectParticipationDTCollection_returnsEmptyWhenRepositoryReturnsEmptyList() {
        when(participationRepository.findByActUid(401L)).thenReturn(Collections.emptyList());

        var result = observationRepositoryUtil.selectParticipationDTCollection(401L);
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void testSelectActivityLocatorParticipations_returnsEmptyWhenRepositoryReturnsNull() {
        when(actLocatorParticipationRepository.findByActUid(500L)).thenReturn(null);

        var result = observationRepositoryUtil.selectActivityLocatorParticipations(500L);
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void testSelectActivityLocatorParticipations_returnsEmptyWhenRepositoryReturnsEmpty() {
        when(actLocatorParticipationRepository.findByActUid(501L)).thenReturn(Collections.emptyList());

        var result = observationRepositoryUtil.selectActivityLocatorParticipations(501L);
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void testSelectObsValueNumerics_returnsDtosWhenRepositoryReturnsData() {
        ObsValueNumeric entity1 = new ObsValueNumeric();
        entity1.setObsValueNumericSeq(1);
        ObsValueNumeric entity2 = new ObsValueNumeric();
        entity2.setObsValueNumericSeq(2);

        when(observationRepository.findByObservationNumericUid(600L))
                .thenReturn(List.of(entity1, entity2));

        var result = observationRepositoryUtil.selectObsValueNumerics(600L);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testSelectObsValueDates_returnsDtosWhenRepositoryReturnsData() {
        ObsValueDate date1 = new ObsValueDate();
        date1.setObsValueDateSeq(1);
        ObsValueDate date2 = new ObsValueDate();
        date2.setObsValueDateSeq(2);

        when(observationRepository.findByObservationDateUid(101L))
                .thenReturn(List.of(date1, date2));

        var result = observationRepositoryUtil.selectObsValueDates(101L);

        assertNotNull(result);
        assertEquals(2, result.size());
    }


    @Test
    void testSelectObsValueTxts_returnsDtosWhenRepositoryReturnsData() {
        ObsValueTxt txt1 = new ObsValueTxt();
        txt1.setObsValueTxtSeq(1);
        ObsValueTxt txt2 = new ObsValueTxt();
        txt2.setObsValueTxtSeq(2);

        when(observationRepository.findByObservationTxtUid(102L))
                .thenReturn(List.of(txt1, txt2));

        var result = observationRepositoryUtil.selectObsValueTxts(102L);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testSelectObsValueCodeds_returnsDtosWhenRepositoryReturnsData() {
        ObsValueCoded c1 = new ObsValueCoded();
        ObsValueCoded c2 = new ObsValueCoded();

        when(observationRepository.findByObservationCodedUid(103L))
                .thenReturn(List.of(c1, c2));

        var result = observationRepositoryUtil.selectObsValueCodeds(103L);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testSelectObservationInterps_returnsDtosWhenRepositoryReturnsData() {
        ObservationInterp i1 = new ObservationInterp();
        ObservationInterp i2 = new ObservationInterp();

        when(observationRepository.findByObservationInterp(104L))
                .thenReturn(List.of(i1, i2));

        var result = observationRepositoryUtil.selectObservationInterps(104L);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testSelectActivityIDs_returnsEmptyWhenRepositoryReturnsNull() {
        when(actIdRepository.findRecordsByActUid(201L)).thenReturn(null);

        var result = observationRepositoryUtil.selectActivityIDs(201L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }


    @Test
    void testSelectActivityIDs_returnsEmptyWhenRepositoryReturnsEmptyList() {
        when(actIdRepository.findRecordsByActUid(202L)).thenReturn(Collections.emptyList());

        var result = observationRepositoryUtil.selectActivityIDs(202L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
    @Test
    void testSelectObservationReasons_returnsDtosWhenRepositoryReturnsData() {
        ObservationReason r1 = new ObservationReason();
        r1.setObservationUid(1L);
        ObservationReason r2 = new ObservationReason();
        r2.setObservationUid(2L);

        when(observationRepository.findByObservationReasons(203L))
                .thenReturn(List.of(r1, r2));

        var result = observationRepositoryUtil.selectObservationReasons(203L);

        assertNotNull(result);
        assertEquals(2, result.size());
    }


    @Test
    void testSetObservationInfo_loadObjectReturnsNull_newContainerCreatedAndSaved() throws DataProcessingException {
        // Arrange
        ObservationDto dto = new ObservationDto();
        dto.setObservationUid(123L);

        ObservationRepositoryUtil spyUtil = Mockito.spy(observationRepositoryUtil);

        // loadObject returns null
        doReturn(null).when(spyUtil).loadObject(123L);

        // Stub saveObservation for any call
        doAnswer(invocation -> null).when(spyUtil).saveObservation(any());

        // Act
        spyUtil.setObservationInfo(dto);

        // Assert
        ArgumentCaptor<ObservationContainer> captor = ArgumentCaptor.forClass(ObservationContainer.class);
        verify(spyUtil, atLeastOnce()).saveObservation(captor.capture());

        ObservationContainer saved = captor.getValue();
        assertNotNull(saved);
        assertEquals(dto, saved.getTheObservationDto());
        assertTrue(saved.isItDirty());
    }

    @Test
    void testSetObservationInfo_whenDtoUidIsNull_createsNewContainerAndSaves() throws DataProcessingException {
        // Arrange
        ObservationDto dto = new ObservationDto(); // UID is null
        ObservationRepositoryUtil spyUtil = Mockito.spy(observationRepositoryUtil);
        doAnswer(invocation -> null).when(spyUtil).saveObservation(any());

        // Act
        spyUtil.setObservationInfo(dto);

        // Assert
        verify(spyUtil).saveObservation(argThat(container ->
                container.getTheObservationDto() == dto && container.isItDirty()
        ));
    }

    @Test
    void testSetObservationInfo_whenLoadReturnsNonNull_setsDtoOnLoadedObject() throws DataProcessingException {
        ObservationDto dto = new ObservationDto();
        dto.setObservationUid(123L);

        ObservationContainer loaded = new ObservationContainer(); // simulated loaded object
        ObservationRepositoryUtil spyUtil = Mockito.spy(observationRepositoryUtil);

        doReturn(loaded).when(spyUtil).loadObject(123L);
        doAnswer(invocation -> null).when(spyUtil).saveObservation(any());

        // Act
        spyUtil.setObservationInfo(dto);

        // Assert
        assertEquals(dto, loaded.getTheObservationDto());
        verify(spyUtil).saveObservation(loaded);
    }


    @Test
    void testSetObservationInfo_whenLoadReturnsNull_createsNewContainerAndSetsDto() throws DataProcessingException {
        ObservationDto dto = new ObservationDto();
        dto.setObservationUid(456L);

        ObservationRepositoryUtil spyUtil = Mockito.spy(observationRepositoryUtil);
        doReturn(null).when(spyUtil).loadObject(456L);
        doAnswer(invocation -> null).when(spyUtil).saveObservation(any());

        // Act
        spyUtil.setObservationInfo(dto);

        // Assert
        verify(spyUtil).saveObservation(argThat(container ->
                container.getTheObservationDto() == dto &&
                        container.isItDirty()
        ));
    }

    @Test
    void testSaveActRelationship_whenDirtyAndFieldsPresent_updatesRelationship() {
        // Arrange
        ActRelationshipDto dto = new ActRelationshipDto();
        dto.setItNew(false);
        dto.setItDelete(false);
        dto.setItDirty(true);
        dto.setTargetActUid(100L);
        dto.setSourceActUid(200L);
        dto.setTypeCd("TEST_TYPE");

        ActRelationship expected = new ActRelationship(dto);

        // Act
        observationRepositoryUtil.saveActRelationship(dto);

        // Assert
        verify(actRelationshipRepository).updateActRelationship(refEq(expected));
    }

    @Test
    void testSaveActRelationship_whenDirtyAndFieldsPresent_updatesRelationship_Invalid() {
        // Arrange
        ActRelationshipDto dto = new ActRelationshipDto();
        dto.setItNew(false);
        dto.setItDelete(false);
        dto.setItDirty(false);
        dto.setTargetActUid(100L);
        dto.setSourceActUid(200L);
        dto.setTypeCd("TEST_TYPE");


        // Act
        observationRepositoryUtil.saveActRelationship(dto);

        // Assert
        verify(actRelationshipRepository, never()).updateActRelationship(any());
    }

    @Test
    void testUpdateObservation_whenUidIsNull_callsSaveNewObservation() throws DataProcessingException {
        // Arrange
        ObservationDto dto = new ObservationDto(); // UID is null
        ObservationContainer container = new ObservationContainer();
        container.setTheObservationDto(dto);

        ObservationRepositoryUtil spyUtil = Mockito.spy(observationRepositoryUtil);
        doReturn(101L).when(spyUtil).saveNewObservation(dto);

        // Stub all other method calls (so only the UID block is tested)
        doReturn(null).when(spyUtil).saveObservation(any());
        doNothing().when(spyUtil).updateObservationReason(anyLong(), any());
        doNothing().when(spyUtil).addActivityId(anyLong(), any(), anyBoolean());
        doNothing().when(spyUtil).updateObservationInterps(anyLong(), any());
        doNothing().when(spyUtil).updateObsValueCoded(anyLong(), any());
        doNothing().when(spyUtil).updateObsValueTxts(anyLong(), any());
        doNothing().when(spyUtil).updateObsValueDates(anyLong(), any());
        doNothing().when(spyUtil).updateObsValueNumerics(anyLong(), any());
        doNothing().when(spyUtil).addActivityLocatorParticipations(anyLong(), any(), any());

        // Act
        Long result = spyUtil.updateObservation(container);

        // Assert
        assertEquals(101L, result);
        assertFalse(dto.isItNew());
        assertFalse(dto.isItDirty());
        verify(spyUtil).saveNewObservation(dto);
        verify(spyUtil, never()).saveObservation(any());
    }


    @Test
    void testSaveObservation_whenNotNewAndDtoNotNull_callsUpdateObservation() throws DataProcessingException {
        // Arrange
        ObservationDto dto = null;

        ObservationContainer container = new ObservationContainer();
        container.setItNew(false); // triggers the else
        container.setTheObservationDto(dto);




        // Act
        Long result = observationRepositoryUtil.saveObservation(container); // fails here unless mockUtil is partial

        // Assert
        assertNotEquals(789L, result);
    }


}
