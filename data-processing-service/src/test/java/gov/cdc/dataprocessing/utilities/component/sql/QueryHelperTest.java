package gov.cdc.dataprocessing.utilities.component.sql;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
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

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.when;

class QueryHelperTest {
    @Mock
    private ProgAreaJurisdictionUtil progAreaJurisdictionUtil;
    @InjectMocks
    private QueryHelper queryHelper;

    @Mock
    private QueryHelper queryHelperMock;
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
        AuthUtil.authUserRealizedRoleCollection = new ArrayList<>();
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(progAreaJurisdictionUtil, authUtil);
        AuthUtil.authUserRealizedRoleCollection = null;
    }

    @Test
    void getDataAccessWhereClause_Test() throws DataProcessingException {
        String businessObjLookupName = "";
        String operation = "";
        String alias = "";

        var padCol = new ArrayList<>();
        padCol.add(10L);
        when(progAreaJurisdictionUtil.getPAJHashList(any(), any())).thenReturn(padCol);


        var res =  queryHelper.getDataAccessWhereClause(businessObjLookupName, operation, alias);
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
        assertNotNull( result);
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
        assertNotNull( result);
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
        assertNotNull( result);
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
        assertNotNull( result);
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
        assertNotNull( result);
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
        assertNotNull( result);
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
        assertNotNull( result);
    }

    @Test
    void testGetHashedPAJListEmpty() throws DataProcessingException {
        AuthUtil.authUserRealizedRoleCollection = new HashSet<>();
        String result = queryHelper.getHashedPAJList(true);
        assertEquals("", result);
    }

    @Test
    void getHashedPAJList_EmptyCollection_ReturnsEmptyString() throws DataProcessingException {
        // Setup
        AuthUtil.authUserRealizedRoleCollection = new ArrayList<>();

        // Execute
        String result = queryHelper.getHashedPAJList(false);

        // Verify
        assertEquals("", result);
    }

    @Test
    void getHashedPAJList_WithGuestRoles_ReturnsCorrectList() throws DataProcessingException {
        // Setup
        AuthUserRealizedRole guestRole1 = new AuthUserRealizedRole();
        guestRole1.setRoleGuestInd("Y");
        guestRole1.setProgAreaCd("PA1");
        guestRole1.setJurisdictionCd("J1");

        AuthUserRealizedRole guestRole2 = new AuthUserRealizedRole();
        guestRole2.setRoleGuestInd("Y");
        guestRole2.setProgAreaCd("PA2");
        guestRole2.setJurisdictionCd("J2");

        AuthUserRealizedRole nonGuestRole = new AuthUserRealizedRole();
        nonGuestRole.setRoleGuestInd("N");
        nonGuestRole.setProgAreaCd("PA3");
        nonGuestRole.setJurisdictionCd("J3");

        AuthUtil.authUserRealizedRoleCollection = Arrays.asList(guestRole1, guestRole2, nonGuestRole);

        // Mock PAJ hash list responses
        when(progAreaJurisdictionUtil.getPAJHashList("PA1", "J1")).thenReturn(Arrays.asList(1L, 2L));
        when(progAreaJurisdictionUtil.getPAJHashList("PA2", "J2")).thenReturn(Arrays.asList(3L, 4L));
        when(progAreaJurisdictionUtil.getPAJHashList("PA3", "J3")).thenReturn(Arrays.asList(5L, 6L));

        // Execute
        String result = queryHelper.getHashedPAJList(true);

        // Verify
        assertEquals("", result);
    }

    @Test
    void getHashedPAJList_WithNonGuestRoles_ReturnsCorrectList() throws DataProcessingException {
        // Setup
        AuthUserRealizedRole guestRole = new AuthUserRealizedRole();
        guestRole.setRoleGuestInd("Y");
        guestRole.setProgAreaCd("PA1");
        guestRole.setJurisdictionCd("J1");

        AuthUserRealizedRole nonGuestRole1 = new AuthUserRealizedRole();
        nonGuestRole1.setRoleGuestInd("N");
        nonGuestRole1.setProgAreaCd("PA2");
        nonGuestRole1.setJurisdictionCd("J2");

        AuthUserRealizedRole nonGuestRole2 = new AuthUserRealizedRole();
        nonGuestRole2.setRoleGuestInd("N");
        nonGuestRole2.setProgAreaCd("PA3");
        nonGuestRole2.setJurisdictionCd("J3");

        AuthUtil.authUserRealizedRoleCollection = Arrays.asList(guestRole, nonGuestRole1, nonGuestRole2);

        // Mock PAJ hash list responses
        when(progAreaJurisdictionUtil.getPAJHashList("PA1", "J1")).thenReturn(Arrays.asList(1L, 2L));
        when(progAreaJurisdictionUtil.getPAJHashList("PA2", "J2")).thenReturn(Arrays.asList(3L, 4L));
        when(progAreaJurisdictionUtil.getPAJHashList("PA3", "J3")).thenReturn(Arrays.asList(5L, 6L));

        // Execute
        String result = queryHelper.getHashedPAJList(false);

        // Verify
        assertEquals("1, 2, 3, 4, 5, 6", result);
    }


    @Test
    void getHashedPAJList_WithNonLongPAJHashList_HandlesGracefully() throws DataProcessingException {
        // Setup
        AuthUserRealizedRole role = new AuthUserRealizedRole();
        role.setRoleGuestInd("N");
        role.setProgAreaCd("PA1");
        role.setJurisdictionCd("J1");

        AuthUtil.authUserRealizedRoleCollection = List.of(role);

        // Mock PAJ hash list response with non-Long objects
        Collection<Object> pajCds = Arrays.asList("not a long", 123, 456L);
        when(progAreaJurisdictionUtil.getPAJHashList("PA1", "J1")).thenReturn(pajCds);

        // Execute
        String result = queryHelper.getHashedPAJList(false);

        // Verify
        assertEquals("456", result);
    }
}
