package gov.cdc.dataprocessing.service.implementation.observation;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.*;
import gov.cdc.dataprocessing.model.dto.act.ActRelationshipDto;
import gov.cdc.dataprocessing.model.dto.entity.RoleDto;
import gov.cdc.dataprocessing.model.dto.lab_result.EdxLabInformationDto;
import gov.cdc.dataprocessing.model.dto.observation.ObservationDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.model.dto.person.PersonDto;
import gov.cdc.dataprocessing.repository.nbs.msgoute.repos.stored_proc.ObservationMatchStoredProcRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.repository.nbs.odse.model.observation.Observation;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.observation.ObservationRepository;
import gov.cdc.dataprocessing.service.model.auth_user.AuthUserProfileInfo;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.time.TimeStampUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Optional;

import static gov.cdc.dataprocessing.constant.elr.EdxELRConstant.LOG_SENT_MESSAGE;
import static gov.cdc.dataprocessing.constant.elr.NEDSSConstant.LAB_REPORT_STR;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ObservationMatchingServiceTest {
    @Mock
    private ObservationMatchStoredProcRepository observationMatchStoredProcRepository;
    @Mock
    private ObservationRepository observationRepository;
    @InjectMocks
    private ObservationMatchingService observationMatchingService;
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
        Mockito.reset(observationMatchStoredProcRepository, observationRepository, authUtil);
    }

    @Test
    void checkingMatchObservation_Success_Matched_Exception_With_Accession() throws DataProcessingException {
        EdxLabInformationDto edxLabInformationDto = new EdxLabInformationDto();
        var rootObsConn = new ObservationContainer();
        var rootDto = new ObservationDto();
        rootDto.setStatusCd(EdxELRConstant.ELR_OBS_STATUS_CD_NEW);
        rootDto.setActivityToTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        rootObsConn.setTheObservationDto(rootDto);
        edxLabInformationDto.setRootObservationContainer(rootObsConn);
        edxLabInformationDto.setFillerNumber("123");

        // matchingObservation 47
        when(observationMatchStoredProcRepository.getMatchedObservation(edxLabInformationDto)).thenReturn(1L);
        var obs = new Observation();
        obs.setStatusCd(EdxELRConstant.ELR_OBS_STATUS_CD_NEW);
        obs.setActivityToTime(TimeStampUtil.getCurrentTimeStampPlusOneHour("UTC"));
        when(observationRepository.findById(1L)).thenReturn(Optional.of(obs));

        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            observationMatchingService.checkingMatchingObservation(edxLabInformationDto);
        });

        assertEquals("An Observation Lab test match was found for Accession # 123, but the activity time is out of sequence.", thrown.getMessage());

    }

    @Test
    void checkingMatchObservation_Success_Matched() throws DataProcessingException {
        EdxLabInformationDto edxLabInformationDto = new EdxLabInformationDto();
        var rootObsConn = new ObservationContainer();
        var rootDto = new ObservationDto();
        rootDto.setStatusCd(EdxELRConstant.ELR_OBS_STATUS_CD_NEW);
        rootDto.setActivityToTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        rootObsConn.setTheObservationDto(rootDto);
        edxLabInformationDto.setRootObservationContainer(rootObsConn);
        edxLabInformationDto.setFillerNumber("123");

        // matchingObservation 47
        when(observationMatchStoredProcRepository.getMatchedObservation(edxLabInformationDto)).thenReturn(1L);
        var obs = new Observation();
        obs.setStatusCd(EdxELRConstant.ELR_OBS_STATUS_CD_NEW);
        obs.setActivityToTime(null);
        when(observationRepository.findById(1L)).thenReturn(Optional.of(obs));

        var test = observationMatchingService.checkingMatchingObservation(edxLabInformationDto);

        assertNotNull(test);
        assertEquals("N", test.getStatusCd());
    }


    @Test
    void checkingMatchObservation_Exception_Else() throws DataProcessingException {
        EdxLabInformationDto edxLabInformationDto = new EdxLabInformationDto();
        var rootObsConn = new ObservationContainer();
        var rootDto = new ObservationDto();
        rootDto.setStatusCd("2121");
        rootDto.setActivityToTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        rootObsConn.setTheObservationDto(rootDto);
        edxLabInformationDto.setRootObservationContainer(rootObsConn);
        edxLabInformationDto.setFillerNumber("123");

        // matchingObservation 47
        when(observationMatchStoredProcRepository.getMatchedObservation(edxLabInformationDto)).thenReturn(1L);
        var obs = new Observation();
        obs.setStatusCd("2121");
        obs.setActivityToTime(null);
        obs.setLocalId("test");
        var obsDT = new ObservationDto(obs);
        when(observationRepository.findById(1L)).thenReturn(Optional.of(obs));


        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            observationMatchingService.checkingMatchingObservation(edxLabInformationDto);
        });
        var msg = LAB_REPORT_STR + obsDT.getLocalId() + " was not updated. Final report with Accession # " + "123" + LOG_SENT_MESSAGE;

        assertEquals(msg, thrown.getMessage());

    }


    @Test
    void processMatchedProxyVO_Success() {
        LabResultProxyContainer labResultProxyVO = new LabResultProxyContainer();

        LabResultProxyContainer matchedlabResultProxyVO = new LabResultProxyContainer();
        // Obs Dto
        var matchedObsObsCol = new ArrayList<ObservationContainer>();
        var matchedObsObsCon = new ObservationContainer();
        var matchedObsObsDt = new ObservationDto();
        matchedObsObsDt.setObsDomainCdSt1(EdxELRConstant.ELR_ORDER_CD);
        matchedObsObsDt.setObservationUid(10L);
        matchedObsObsCon.setTheObservationDto(matchedObsObsDt);
        // Act Relationship
        var matchedObsActReCol = new ArrayList<ActRelationshipDto>();
        var matchedObsActRe = new ActRelationshipDto();
        matchedObsActRe.setTypeCd(NEDSSConstant.LAB_REPORT);
        matchedObsActRe.setTargetClassCd(NEDSSConstant.CASE);
        matchedObsActReCol.add(matchedObsActRe);
        matchedObsActRe.setTargetActUid(12L);
        matchedObsActReCol = new ArrayList<ActRelationshipDto>();
        matchedObsActRe = new ActRelationshipDto();
        matchedObsActRe.setTypeCd(EdxELRConstant.ELR_AR_LAB_COMMENT);
        matchedObsActRe.setTargetActUid(13L);
        matchedObsActReCol.add(matchedObsActRe);
        matchedObsObsCon.setTheActRelationshipDtoCollection(matchedObsActReCol);
        // Participation
        var matchedObsPatCol = new ArrayList<ParticipationDto>();
        var matchedObsPat = new ParticipationDto();
        matchedObsPat.setTypeCd(EdxELRConstant.ELR_AUTHOR_CD);
        matchedObsPat.setCd(EdxELRConstant.ELR_SENDING_FACILITY_CD);
        matchedObsPatCol.add(matchedObsPat);
        matchedObsObsCon.setTheParticipationDtoCollection(matchedObsPatCol);

        // Obs Conn
        matchedObsObsCol.add(matchedObsObsCon);
        matchedObsObsCon = new ObservationContainer();
        matchedObsObsDt = new ObservationDto();
        matchedObsObsDt.setCtrlCdDisplayForm(EdxELRConstant.ELR_LAB_COMMENT);
        matchedObsObsDt.setCd(EdxELRConstant.ELR_LAB_CD);
        matchedObsObsDt.setObservationUid(11L);
        matchedObsObsCon.setTheObservationDto(matchedObsObsDt);
        matchedObsObsCol.add(matchedObsObsCon);

        // Person Conn
        var personConCol = new ArrayList<PersonContainer>();
        var personCon = new PersonContainer();
        var personDto = new PersonDto();
        personDto.setCdDescTxt(EdxELRConstant.ELR_PATIENT_DESC);
        personDto.setPersonUid(11L);
        personCon.setThePersonDto(personDto);
        // Role share with per, org, and mat, labResultProxy
        var perRoleCol = new ArrayList<RoleDto>();
        var perRole = new RoleDto();
        perRoleCol.add(perRole);
        personCon.setTheRoleDtoCollection(perRoleCol);
        personConCol.add(personCon);

        // Org Conn
        var orgConCol = new ArrayList<OrganizationContainer>();
        var orgCon = new OrganizationContainer();
        orgCon.setTheRoleDTCollection(perRoleCol);
        orgConCol.add(orgCon);

        // Material Conn
        var matConCol = new ArrayList<MaterialContainer>();
        var matCon = new MaterialContainer();
        matCon.setTheRoleDTCollection(perRoleCol);
        matConCol.add(matCon);

        matchedlabResultProxyVO.setTheOrganizationContainerCollection(orgConCol);
        matchedlabResultProxyVO.setThePersonContainerCollection(personConCol);
        matchedlabResultProxyVO.setTheObservationContainerCollection(matchedObsObsCol);

        labResultProxyVO.setTheRoleDtoCollection(perRoleCol);
        labResultProxyVO.setTheObservationContainerCollection(matchedObsObsCol);
        labResultProxyVO.setTheActRelationshipDtoCollection(matchedObsActReCol);

        EdxLabInformationDto edxLabInformationDT = new EdxLabInformationDto();
        var edxObsConn = new ObservationContainer();
        var edxObsDt = new ObservationDto();
        edxObsDt.setStatusCd("ACTTIVE");
        edxObsConn.setTheObservationDto(edxObsDt);
        edxLabInformationDT.setRootObservationContainer(edxObsConn);
        edxLabInformationDT.setRootObserbationUid(12L);

        observationMatchingService.processMatchedProxyVO(labResultProxyVO, matchedlabResultProxyVO, edxLabInformationDT);

        assertEquals(3, labResultProxyVO.getTheRoleDtoCollection().size());
        assertEquals(2, labResultProxyVO.getTheActRelationshipDtoCollection().size());
        assertEquals(2, labResultProxyVO.getTheObservationContainerCollection().size());
        assertEquals(1, labResultProxyVO.getTheParticipationDtoCollection().size());

    }

    @Test
    void checkingMatchingObservation_Test_Null() throws DataProcessingException {
        EdxLabInformationDto edxLabInformationDto = new EdxLabInformationDto();

        var res = observationMatchingService.checkingMatchingObservation(edxLabInformationDto);

        assertNull(res);
    }


    @Test
    void checkingMatchingObservation_Test_Null_1() throws DataProcessingException {
        EdxLabInformationDto edxLabInformationDto = new EdxLabInformationDto();
        edxLabInformationDto.setRootObservationContainer(new ObservationContainer());
        var res = observationMatchingService.checkingMatchingObservation(edxLabInformationDto);

        assertNull(res);
    }

    @Test
    void checkingMatchingObservation_Test_Null_2() throws DataProcessingException {
        EdxLabInformationDto edxLabInformationDto = new EdxLabInformationDto();
        edxLabInformationDto.setRootObservationContainer(new ObservationContainer());

        when(observationMatchStoredProcRepository.getMatchedObservation(any()))
                .thenReturn(10L);

        when(observationRepository.findById(10L)).thenReturn(Optional.empty());

        var res = observationMatchingService.checkingMatchingObservation(edxLabInformationDto);

        assertNull(res);
    }

    @Test
    void checkingMatchingObservation_Test_Null_3() throws DataProcessingException {
        EdxLabInformationDto edxLabInformationDto = new EdxLabInformationDto();
        edxLabInformationDto.setRootObservationContainer(new ObservationContainer());

        when(observationMatchStoredProcRepository.getMatchedObservation(any()))
                .thenReturn(10L);

        var obsDt = new Observation();
        when(observationRepository.findById(10L)).thenReturn(Optional.of(obsDt));

        var res = observationMatchingService.checkingMatchingObservation(edxLabInformationDto);

        assertNull(res);
    }



    private EdxLabInformationDto createDto(String msgStatus, Timestamp msgActivityTime) {
        ObservationDto obsDto = new ObservationDto();
        obsDto.setStatusCd(msgStatus);
        obsDto.setActivityToTime(msgActivityTime);

        ObservationContainer container = new ObservationContainer();
        container.setTheObservationDto(obsDto);

        EdxLabInformationDto dto = new EdxLabInformationDto();
        dto.setFillerNumber("F123");
        dto.setRootObservationContainer(container);
        return dto;
    }

    private ObservationDto createMatchedObs(String localId, Timestamp toTime) {
        ObservationDto match = new ObservationDto();
        match.setLocalId(localId);
        match.setActivityToTime(toTime);
        return match;
    }

    @Test
    void testStatusCombination_NewVsNew_MatchSuccess() throws Exception {
        EdxLabInformationDto dto = createDto(EdxELRConstant.ELR_OBS_STATUS_CD_NEW, new Timestamp(System.currentTimeMillis()));
        ObservationDto matched = createMatchedObs("LOC1", new Timestamp(System.currentTimeMillis() - 1000));
        ObservationMatchingService spy = spy(observationMatchingService);
        doReturn(matched).when(spy).matchingObservation(dto);

        var result = spy.checkingMatchingObservation(dto);
        assertTrue(dto.isObservationMatch());
        assertEquals("LOC1", result.getLocalId());
    }

    @Test
    void testStatusCombination_NewVsNew_ActivityTimeOutOfSequence() throws DataProcessingException {
        EdxLabInformationDto dto = createDto(EdxELRConstant.ELR_OBS_STATUS_CD_NEW, new Timestamp(System.currentTimeMillis()));
        ObservationDto matched = createMatchedObs("LOC5", new Timestamp(System.currentTimeMillis() + 10000));
        ObservationMatchingService spy = spy(observationMatchingService);
        doReturn(matched).when(spy).matchingObservation(dto);

        assertThrows(DataProcessingException.class, () -> spy.checkingMatchingObservation(dto));
        assertTrue(dto.isActivityTimeOutOfSequence());
        assertEquals("LOC5", dto.getLocalId());
    }

    @Test
    void testStatusCombination_Invalid_ThrowsDefaultBranch() throws DataProcessingException {
        EdxLabInformationDto dto = createDto("INVALID", new Timestamp(System.currentTimeMillis()));
        ObservationDto matched = createMatchedObs("LOC6", new Timestamp(System.currentTimeMillis() - 1000));
        ObservationMatchingService spy = spy(observationMatchingService);
        doReturn(matched).when(spy).matchingObservation(dto);

        assertThrows(DataProcessingException.class, () -> spy.checkingMatchingObservation(dto));
        assertTrue(dto.isFinalPostCorrected());
        assertEquals("LOC6", dto.getLocalId());
    }

    @Test
    void testCheckingMatchingObservation_WhenMatchedObsIsNull_ReturnsNull() throws DataProcessingException {
        // Arrange
        EdxLabInformationDto dto = new EdxLabInformationDto();
        ObservationContainer obsContainer = mock(ObservationContainer.class);
        ObservationDto obsDto = mock(ObservationDto.class);
        when(obsDto.getStatusCd()).thenReturn("NEW");
        when(obsContainer.getTheObservationDto()).thenReturn(obsDto);
        dto.setRootObservationContainer(obsContainer);
        dto.setFillerNumber("XYZ123");

        ObservationMatchingService spy = spy(observationMatchingService);
        doReturn(null).when(spy).matchingObservation(dto); // Force matchedObs == null

        // Act
        ObservationDto result = spy.checkingMatchingObservation(dto);

        // Assert
        assertNull(result);
        assertFalse(dto.isObservationMatch());
    }

    @Test
    void testHandleInvalidCombination_SupercededVsCompleted() {
        EdxLabInformationDto dto = new EdxLabInformationDto();
        ObservationDto matchedObs = new ObservationDto();
        matchedObs.setLocalId("LOC001");

        String odsStatus = EdxELRConstant.ELR_OBS_STATUS_CD_SUPERCEDED;
        String msgStatus = EdxELRConstant.ELR_OBS_STATUS_CD_COMPLETED;
        String fillerNumber = "ACC123";

        DataProcessingException ex = assertThrows(DataProcessingException.class,
                () -> observationMatchingService.handleInvalidCombination(odsStatus, msgStatus, dto, matchedObs, fillerNumber)
        );

        assertTrue(dto.isFinalPostCorrected());
        assertEquals("LOC001", dto.getLocalId());
        assertTrue(ex.getMessage().contains("Final report with Accession # ACC123 was sent after a corrected report"));
    }

    @Test
    void testHandleInvalidCombination_CompletedVsNew() {
        EdxLabInformationDto dto = new EdxLabInformationDto();
        ObservationDto matchedObs = new ObservationDto();
        matchedObs.setLocalId("LOC002");

        String odsStatus = EdxELRConstant.ELR_OBS_STATUS_CD_COMPLETED;
        String msgStatus = EdxELRConstant.ELR_OBS_STATUS_CD_NEW;
        String fillerNumber = "ACC456";

        DataProcessingException ex = assertThrows(DataProcessingException.class,
                () -> observationMatchingService.handleInvalidCombination(odsStatus, msgStatus, dto, matchedObs, fillerNumber)
        );

        assertTrue(dto.isPreliminaryPostFinal());
        assertEquals("LOC002", dto.getLocalId());
        assertTrue(ex.getMessage().contains("Preliminary report with Accession # ACC456 was sent after a final report"));
    }

    @Test
    void testHandleInvalidCombination_SupercededVsNew() {
        EdxLabInformationDto dto = new EdxLabInformationDto();
        ObservationDto matchedObs = new ObservationDto();
        matchedObs.setLocalId("LOC003");

        String odsStatus = EdxELRConstant.ELR_OBS_STATUS_CD_SUPERCEDED;
        String msgStatus = EdxELRConstant.ELR_OBS_STATUS_CD_NEW;
        String fillerNumber = "ACC789";

        assertThrows(DataProcessingException.class,
                () -> observationMatchingService.handleInvalidCombination(odsStatus, msgStatus, dto, matchedObs, fillerNumber)
        );

        assertTrue(dto.isPreliminaryPostCorrected());
        assertEquals("LOC003", dto.getLocalId());
    }

    @Test
    void testHandleInvalidCombination_DefaultInvalidCombo() {
        EdxLabInformationDto dto = new EdxLabInformationDto();
        ObservationDto matchedObs = new ObservationDto();
        matchedObs.setLocalId("LOC004");

        String odsStatus = EdxELRConstant.ELR_OBS_STATUS_CD_COMPLETED;
        String msgStatus = EdxELRConstant.ELR_OBS_STATUS_CD_SUPERCEDED;
        String fillerNumber = "ACC000";

        assertThrows(DataProcessingException.class,
                () -> observationMatchingService.handleInvalidCombination(odsStatus, msgStatus, dto, matchedObs, fillerNumber)
        );

        assertTrue(dto.isFinalPostCorrected());
        assertEquals("LOC004", dto.getLocalId());
    }


    @Test
    void testNewMatchesNew() {
        assertTrue(observationMatchingService.isValidStatusMatch("N", "N"));
    }

    @Test
    void testNewMatchesCompleted() {
        assertTrue(observationMatchingService.isValidStatusMatch("N", "D"));
    }

    @Test
    void testNewMatchesSuperceded() {
        assertTrue(observationMatchingService.isValidStatusMatch("N", "T"));
    }

    @Test
    void testCompletedMatchesCompleted() {
        assertTrue(observationMatchingService.isValidStatusMatch("D", "D"));
    }

    @Test
    void testCompletedMatchesSuperceded() {
        assertTrue(observationMatchingService.isValidStatusMatch("D", "T"));
    }

    @Test
    void testSupercededMatchesSuperceded() {
        assertTrue(observationMatchingService.isValidStatusMatch("T", "T"));
    }

    @Test
    void testInvalidCombinationReturnsFalse() {
        // e.g., Completed vs New
        assertFalse(observationMatchingService.isValidStatusMatch("D", "N"));
    }

    @Test
    void testNewDoesNotMatchOther() {
        assertFalse(observationMatchingService.isValidStatusMatch("N", "Z")); // invalid msg
    }

    @Test
    void testCompletedDoesNotMatchNew() {
        assertFalse(observationMatchingService.isValidStatusMatch("D", "N"));
    }

    @Test
    void testSupercededDoesNotMatchNewOrCompleted() {
        assertFalse(observationMatchingService.isValidStatusMatch("T", "N"));
        assertFalse(observationMatchingService.isValidStatusMatch("T", "D"));
    }

    @Test
    void testInvalidOdsDoesNotMatchValidMsg() {
        assertFalse(observationMatchingService.isValidStatusMatch("X", "N")); // invalid ods
    }


}
