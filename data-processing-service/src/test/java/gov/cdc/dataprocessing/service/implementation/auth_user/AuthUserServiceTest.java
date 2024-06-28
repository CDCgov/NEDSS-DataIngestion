package gov.cdc.dataprocessing.service.implementation.auth_user;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUserRealizedRole;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.CustomAuthUserRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.auth.AuthUserRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class AuthUserServiceTest {
    @Mock
    private AuthUserRepository authUserRepository;
    @Mock
    private CustomAuthUserRepository customAuthUserRepository;
    @InjectMocks
    private AuthUserService authUserService;
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
        Mockito.reset(authUserRepository, customAuthUserRepository, authUtil);
    }

    @Test
    void getAuthUserInfo_Success() throws DataProcessingException {
        String authUserId = "Test";
        var authUser = new AuthUser();
        when(authUserRepository.findAuthUserByUserId(authUserId)).thenReturn(Optional.of(authUser));
        var roleCol = new ArrayList<AuthUserRealizedRole>();
        var role = new AuthUserRealizedRole();
        roleCol.add(role);
        when(customAuthUserRepository.getAuthUserRealizedRole(authUserId)).thenReturn(roleCol);

        var test = authUserService.getAuthUserInfo(authUserId);

        assertNotNull(test);

    }

    @Test
    void getAuthUserInfo_Exception()  {
        String authUserId = "Test";
        when(authUserRepository.findAuthUserByUserId(authUserId)).thenReturn(Optional.empty());

        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            authUserService.getAuthUserInfo(authUserId);
        });
        assertEquals("Auth User Not Found", thrown.getMessage());

    }
}
