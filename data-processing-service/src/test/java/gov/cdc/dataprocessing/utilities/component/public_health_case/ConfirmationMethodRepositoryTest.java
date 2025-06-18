package gov.cdc.dataprocessing.utilities.component.public_health_case;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template.ConfirmationMethodJdbcRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.repository.nbs.odse.model.phc.ConfirmationMethod;
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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ConfirmationMethodRepositoryTest {
    @Mock
    private ConfirmationMethodJdbcRepository confirmationMethodRepository;
    @InjectMocks
    private ConfirmationMethodRepositoryUtil confirmationMethodRepositoryUtil;
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
        Mockito.reset(confirmationMethodRepository, authUtil);
    }

    @Test
    void getConfirmationMethodByPhc_Test() {
        var uid = 10L;
        var methodArr = new ArrayList<ConfirmationMethod>();
        var method = new ConfirmationMethod();
        methodArr.add(method);
        when(confirmationMethodRepository.findByPublicHealthCaseUid(any())).thenReturn(methodArr);

        var res = confirmationMethodRepositoryUtil.getConfirmationMethodByPhc(uid);
        assertNotNull(res);
    }

    @Test
    void getConfirmationMethodByPhc_Test_2() {
        var uid = 10L;

        when(confirmationMethodRepository.findByPublicHealthCaseUid(any())).thenReturn(new ArrayList<>());

        var res = confirmationMethodRepositoryUtil.getConfirmationMethodByPhc(uid);
        assertNotNull(res);
    }
}
