package gov.cdc.dataprocessing.utilities.component.patient;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.matching.EdxEntityMatchDto;
import gov.cdc.dataprocessing.model.dto.matching.EdxPatientMatchDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.matching.EdxEntityMatchRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.matching.EdxPatientMatchRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.stored_proc.EdxPatientMatchStoredProcRepository;
import gov.cdc.dataprocessing.service.model.auth_user.AuthUserProfileInfo;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.jdbc.DataModifierReposJdbc;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EdxPatientMatchRepositoryUtilTest {
    @Mock
    private EdxPatientMatchRepository edxPatientMatchRepository;
    @Mock
    private DataModifierReposJdbc dataModifierReposJdbc;
    @Mock
    private EdxEntityMatchRepository edxEntityMatchRepository;
    @Mock
    private EdxPatientMatchStoredProcRepository edxPatientMatchStoreProcRepository;
    @InjectMocks
    private EdxPatientMatchRepositoryUtil edxPatientMatchRepositoryUtil;
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
        Mockito.reset(edxPatientMatchRepository, edxEntityMatchRepository, edxPatientMatchStoreProcRepository, authUtil, dataModifierReposJdbc);
    }

    @Test
    void getEdxPatientMatchOnMatchString_Test() throws DataProcessingException {
        String typeCd = null;
        String matchString = null;

        var res = edxPatientMatchRepositoryUtil.getEdxPatientMatchOnMatchString(typeCd, matchString);

        assertNotNull(res);
    }

    @Test
    void getEdxPatientMatchOnMatchString_Test_2() throws DataProcessingException {
        String typeCd = "TEST";
        String matchString = "TEST";

        when(edxPatientMatchStoreProcRepository.getEdxPatientMatch(any(), any())).thenReturn(new EdxPatientMatchDto());

        var res = edxPatientMatchRepositoryUtil.getEdxPatientMatchOnMatchString(typeCd, matchString);

        assertNotNull(res);
    }

    @Test
    void getEdxPatientMatchOnMatchString_Test_3() throws DataProcessingException {
        String typeCd = "TEST";
        String matchString = "TEST";

        when(edxPatientMatchStoreProcRepository.getEdxPatientMatch(any(), any())).thenThrow(new RuntimeException("TEST"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            edxPatientMatchRepositoryUtil.getEdxPatientMatchOnMatchString(typeCd, matchString);
        });
        assertNotNull(thrown);
    }


    @Test
    void getEdxEntityMatchOnMatchString_Test() throws DataProcessingException {
        String typeCd = null;
        String matchString = null;

        var res = edxPatientMatchRepositoryUtil.getEdxEntityMatchOnMatchString(typeCd, matchString);

        assertNotNull(res);
    }

    @Test
    void getEdxEntityMatchOnMatchString_Test_2() throws DataProcessingException {
        String typeCd = "TEST";
        String matchString = "TEST";

        when(edxPatientMatchStoreProcRepository.getEdxEntityMatch(any(), any())).thenReturn(new EdxEntityMatchDto());

        var res = edxPatientMatchRepositoryUtil.getEdxEntityMatchOnMatchString(typeCd, matchString);

        assertNotNull(res);
    }

    @Test
    void getEdxEntityMatchOnMatchString_Test_3() throws DataProcessingException {
        String typeCd = "TEST";
        String matchString = "TEST";

        when(edxPatientMatchStoreProcRepository.getEdxEntityMatch(any(), any())).thenThrow(new RuntimeException("TEST"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            edxPatientMatchRepositoryUtil.getEdxEntityMatchOnMatchString(typeCd, matchString);
        });
        assertNotNull(thrown);
    }

    @Test
    void saveEdxEntityMatch_Test() {
        var edx = new EdxEntityMatchDto();
        edxPatientMatchRepositoryUtil.saveEdxEntityMatch(edx);

        verify(edxEntityMatchRepository, times(1)).save(any());

    }

    @Test
    void setEdxPatientMatchDT_Test() {
        var edx = new EdxPatientMatchDto();
        edxPatientMatchRepositoryUtil.setEdxPatientMatchDT(edx);

        verify(edxPatientMatchRepository, times(1)).save(any());

    }


    @Test
    void deleteEdxPatientMatchDTColl_Test() {
        var uid = 10L;
        edxPatientMatchRepositoryUtil.deleteEdxPatientMatchDTColl(uid);

        verify(dataModifierReposJdbc, times(1)).deleteByPatientUidAndMatchStringNotLike(any());

    }

}
