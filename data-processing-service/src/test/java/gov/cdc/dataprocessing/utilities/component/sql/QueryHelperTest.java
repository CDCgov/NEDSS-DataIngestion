package gov.cdc.dataprocessing.utilities.component.sql;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUserRealizedRole;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.CustomAuthUserRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.auth.AuthUserRepository;
import gov.cdc.dataprocessing.service.implementation.auth_user.AuthUserService;
import gov.cdc.dataprocessing.service.model.auth_user.AuthUserProfileInfo;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.jurisdiction.ProgAreaJurisdictionUtil;
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

class QueryHelperTest {
    @Mock
    private ProgAreaJurisdictionUtil progAreaJurisdictionUtil;
    @InjectMocks
    private QueryHelper queryHelper;
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

        var roleCol = new ArrayList<AuthUserRealizedRole>();
        var role = new AuthUserRealizedRole();
        role.setProgAreaCd("TEST");
        role.setJurisdictionCd("TEST");
        roleCol.add(role);
        userInfo.setAuthUserRealizedRoleCollection(roleCol);

        authUtil.setGlobalAuthUser(userInfo);
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(progAreaJurisdictionUtil, authUtil);
    }

    @Test
    void getDataAccessWhereClause_Test() {
        String businessObjLookupName = "";
        String operation = "";
        String alias = "";

        var padCol = new ArrayList<>();
        padCol.add(10L);
        when(progAreaJurisdictionUtil.getPAJHashList(any(), any())).thenReturn(padCol);


        var res =  queryHelper.getDataAccessWhereClause(businessObjLookupName, operation, alias);
        assertNotNull(res);
    }
}
