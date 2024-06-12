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
import gov.cdc.dataprocessing.repository.nbs.odse.model.person.Person;
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

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class ObservationMatchingServiceTest {
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
        rootDto.setActivityToTime(TimeStampUtil.getCurrentTimeStamp());
        rootObsConn.setTheObservationDto(rootDto);
        edxLabInformationDto.setRootObservationContainer(rootObsConn);
        edxLabInformationDto.setFillerNumber("123");

        // matchingObservation 47
        when(observationMatchStoredProcRepository.getMatchedObservation(edxLabInformationDto)).thenReturn(1L);
        var obs = new Observation();
        obs.setStatusCd(EdxELRConstant.ELR_OBS_STATUS_CD_NEW);
        obs.setActivityToTime(TimeStampUtil.getCurrentTimeStampPlusOneHour());
        var obsDT = new ObservationDto(obs);
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
        rootDto.setActivityToTime(TimeStampUtil.getCurrentTimeStamp());
        rootObsConn.setTheObservationDto(rootDto);
        edxLabInformationDto.setRootObservationContainer(rootObsConn);
        edxLabInformationDto.setFillerNumber("123");

        // matchingObservation 47
        when(observationMatchStoredProcRepository.getMatchedObservation(edxLabInformationDto)).thenReturn(1L);
        var obs = new Observation();
        obs.setStatusCd(EdxELRConstant.ELR_OBS_STATUS_CD_NEW);
        obs.setActivityToTime(null);
        var obsDT = new ObservationDto(obs);
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
        rootDto.setActivityToTime(TimeStampUtil.getCurrentTimeStamp());
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
        var msg = "Lab report " + obsDT.getLocalId() + " was not updated. Final report with Accession # " + "123" + " was sent after a corrected report was received.";

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
}
