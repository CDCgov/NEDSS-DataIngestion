package gov.cdc.dataprocessing.utilities.component.sql;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUserRealizedRole;
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
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.when;

class QueryHelperTest {
    @Mock
    AuthUtil authUtil;
    @Mock
    private ProgAreaJurisdictionUtil progAreaJurisdictionUtil;
    @InjectMocks
    private QueryHelper queryHelper;
    @Mock
    private QueryHelper queryHelperMock;

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

        AuthUtil.setGlobalAuthUser(userInfo);
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


        var res = queryHelper.getDataAccessWhereClause(businessObjLookupName, operation, alias);
        assertNotNull(res);
    }

    @SuppressWarnings("java:S5976")
    @Test
    void testBuildWhereClause_BothNonEmpty() {
        // Arrange
        when(queryHelperMock.buildOwnerWhereClause("ownerList", "columnName", "alias", true, "businessObjLookupName"))
                .thenReturn("ownerWhereClause");
        when(queryHelperMock.buildGuestWhereClause("guestList", "columnName", "alias", true, "businessObjLookupName"))
                .thenReturn("guestWhereClause");

        // Act
        String result = queryHelper.buildWhereClause("ownerList", "guestList", "columnName", "alias", true, "businessObjLookupName");

        // Assert
        assertNotNull(result);
    }

    @SuppressWarnings("java:S5976")
    @Test
    void testBuildWhereClause_OnlyOwnerNonEmpty() {
        // Arrange
        when(queryHelperMock.buildOwnerWhereClause("ownerList", "columnName", "alias", true, "businessObjLookupName"))
                .thenReturn("ownerWhereClause");
        when(queryHelperMock.buildGuestWhereClause("guestList", "columnName", "alias", true, "businessObjLookupName"))
                .thenReturn("");

        // Act
        String result = queryHelper.buildWhereClause("ownerList", "guestList", "columnName", "alias", true, "businessObjLookupName");

        // Assert
        assertNotNull(result);
    }

    @SuppressWarnings("java:S5976")
    @Test
    void testBuildWhereClause_OnlyGuestNonEmpty() {
        // Arrange
        when(queryHelperMock.buildOwnerWhereClause("ownerList", "columnName", "alias", true, "businessObjLookupName"))
                .thenReturn("");
        when(queryHelperMock.buildGuestWhereClause("guestList", "columnName", "alias", true, "businessObjLookupName"))
                .thenReturn("guestWhereClause");

        // Act
        String result = queryHelper.buildWhereClause("ownerList", "guestList", "columnName", "alias", true, "businessObjLookupName");

        // Assert
        assertNotNull(result);
    }

    @SuppressWarnings("java:S5976")
    @Test
    void testBuildWhereClause_BothEmpty() {
        // Arrange
        when(queryHelperMock.buildOwnerWhereClause("ownerList", "columnName", "alias", true, "businessObjLookupName"))
                .thenReturn("");
        when(queryHelperMock.buildGuestWhereClause("guestList", "columnName", "alias", true, "businessObjLookupName"))
                .thenReturn("");

        // Act
        String result = queryHelper.buildWhereClause("ownerList", "guestList", "columnName", "alias", true, "businessObjLookupName");

        // Assert
        assertNotNull(result);
    }

    @Test
    void testBuildWhereClause_OnlyGuestNonEmpty_1() {
        // Arrange
        when(queryHelperMock.buildOwnerWhereClause(any(), any(), any(), anyBoolean(), any()))
                .thenReturn("");
        when(queryHelperMock.buildGuestWhereClause(any(), any(), any(), anyBoolean(), any()))
                .thenReturn("guestWhereClause");

        // Act
        String result = queryHelper.buildWhereClause("", "guestList", "columnName", "alias", true, "businessObjLookupName");

        // Assert
        assertNotNull(result);
    }

    @Test
    void testBuildWhereClause_BothEmpty_1() {
        // Arrange
        // Arrange
        when(queryHelperMock.buildOwnerWhereClause(any(), any(), any(), anyBoolean(), any()))
                .thenReturn("guestWhereClause");
        when(queryHelperMock.buildGuestWhereClause(any(), any(), any(), anyBoolean(), any()))
                .thenReturn("");
        // Act
        String result = queryHelper.buildWhereClause("ownerList", "", "columnName", "alias", true, "businessObjLookupName");

        // Assert
        assertNotNull(result);
    }

    @Test
    void testBuildWhereClause_BothEmpty_2() {
        // Arrange
        // Arrange
        when(queryHelperMock.buildOwnerWhereClause(any(), any(), any(), anyBoolean(), any()))
                .thenReturn("");
        when(queryHelperMock.buildGuestWhereClause(any(), any(), any(), anyBoolean(), any()))
                .thenReturn("");
        // Act
        String result = queryHelper.buildWhereClause("", "", "columnName", "alias", true, "businessObjLookupName");

        // Assert
        assertNotNull(result);
    }
}
