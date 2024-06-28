package gov.cdc.dataprocessing.service.implementation.investigation;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.repository.nbs.odse.model.dsm.DsmAlgorithm;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.dsm.DsmAlgorithmRepository;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

class DsmAlgorithmServiceTest {
    @Mock
    private DsmAlgorithmRepository dsmAlgorithmRepository;
    @InjectMocks
    private DsmAlgorithmService dsmAlgorithmService;
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
        Mockito.reset(dsmAlgorithmRepository , authUtil);
    }

    @Test
    void findActiveDsmAlgorithm_Success() {
        when(dsmAlgorithmRepository.findDsmAlgorithmByStatusCode("A")).thenReturn(Optional.of(new ArrayList<DsmAlgorithm>()));

        var res = dsmAlgorithmService.findActiveDsmAlgorithm();

        assertNotNull(res);

    }
}
