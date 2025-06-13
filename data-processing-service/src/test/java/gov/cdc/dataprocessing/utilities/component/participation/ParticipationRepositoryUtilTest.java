package gov.cdc.dataprocessing.utilities.component.participation;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template.ParticipationJdbcRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.repository.nbs.odse.model.participation.Participation;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ParticipationRepositoryUtilTest {
    @Mock
    private ParticipationJdbcRepository participationRepository;
    @InjectMocks
    private ParticipationRepositoryUtil participationRepositoryUtil;
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
        Mockito.reset(participationRepository, authUtil);
    }

    @Test
    void getParticipationCollection_Test() {
        Long uid = 10L;
        var patCol = new ArrayList<Participation>();
        var pat = new Participation();
        patCol.add(pat);
        when(participationRepository.findByActUid(any())).thenReturn(patCol);

        var res = participationRepositoryUtil.getParticipationCollection(uid);

        assertNotNull(res);
    }

    @Test
    void insertParticipationHist_Test() {
        ParticipationDto pat = new ParticipationDto();
        participationRepositoryUtil.insertParticipationHist(pat);

        verify(participationRepository, times(1)).mergeParticipationHist(any());
    }

    @Test
    void storeParticipation_Test()  {
        ParticipationDto pat = null;


        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            participationRepositoryUtil.storeParticipation(pat);
        });

        assertNotNull(thrown);
    }

    @Test
    void storeParticipation_Test_1() throws DataProcessingException {
        ParticipationDto pat = new ParticipationDto();
        pat.setItNew(true);

        participationRepositoryUtil.storeParticipation(pat);

        verify(participationRepository, times(1)).createParticipation(any());

    }

    @Test
    void storeParticipation_Test_2() throws DataProcessingException {
        ParticipationDto pat = new ParticipationDto();
        pat.setItDelete(true);

        participationRepositoryUtil.storeParticipation(pat);

        verify(participationRepository, times(1)).deleteParticipation(any(), any(), any());

    }

    @Test
    void storeParticipation_Test_3() throws DataProcessingException {
        ParticipationDto pat = new ParticipationDto();
        pat.setItDirty(true);

        participationRepositoryUtil.storeParticipation(pat);

        verify(participationRepository, times(1)).updateParticipation(any());

    }


    @Test
    void getParticipation_Test() {
        Long subjectEntityUid = 10L;
        Long actUid = 10L;

        var patCol = new ArrayList<Participation>();
        var pat = new Participation();
        pat.setActUid(10L);
        patCol.add(pat);
        when(participationRepository.findBySubjectUid(any())).thenReturn(patCol);

        var res = participationRepositoryUtil.getParticipation(subjectEntityUid, actUid);

        assertNotNull(res);
    }
    @Test
    void getParticipation_Test_2() {
        Long subjectEntityUid = 10L;
        Long actUid = 11L;

        var patCol = new ArrayList<Participation>();
        var pat = new Participation();
        pat.setActUid(10L);
        patCol.add(pat);
        when(participationRepository.findBySubjectUid(any())).thenReturn(patCol);

        var res = participationRepositoryUtil.getParticipation(subjectEntityUid, actUid);

        assertNull(res);
    }

    @Test
    void getParticipationsByActUid_Test() {
        Long actUid = 10L;
        var patCol = new ArrayList<Participation>();
        var pat = new Participation();
        patCol.add(pat);
        when(participationRepository.findByActUid(any())).thenReturn(patCol);

        var res = participationRepositoryUtil.getParticipationsByActUid(actUid);
        assertNotNull(res);


    }
}
