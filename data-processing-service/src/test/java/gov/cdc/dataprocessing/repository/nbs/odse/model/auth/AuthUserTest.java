package gov.cdc.dataprocessing.repository.nbs.odse.model.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AuthUserTest {

    private AuthUser authUser;
    private Timestamp timestamp;

    @BeforeEach
    void setUp() {
        authUser = new AuthUser();
        timestamp = new Timestamp(System.currentTimeMillis());
    }

    @Test
    void testGettersAndSetters() {
        // Set values
        authUser.setAuthUserUid(1L);
        authUser.setUserId("user123");
        authUser.setUserType("type");
        authUser.setUserTitle("title");
        authUser.setUserDepartment("department");
        authUser.setUserFirstNm("firstName");
        authUser.setUserLastNm("lastName");
        authUser.setUserWorkEmail("email@example.com");
        authUser.setUserWorkPhone("123-456-7890");
        authUser.setUserMobilePhone("098-765-4321");
        authUser.setMasterSecAdminInd("Y");
        authUser.setProgAreaAdminInd("N");
        authUser.setNedssEntryId(2L);
        authUser.setExternalOrgUid(3L);
        authUser.setUserPassword("password");
        authUser.setUserComments("comments");
        authUser.setAddTime(timestamp);
        authUser.setAddUserId(4L);
        authUser.setLastChgTime(timestamp);
        authUser.setLastChgUserId(5L);
        authUser.setRecordStatusCd("active");
        authUser.setRecordStatusTime(timestamp);
        authUser.setJurisdictionDerivationInd("Y");
        authUser.setProviderUid(6L);

        // Verify values
        assertEquals(1L, authUser.getAuthUserUid());
        assertEquals("user123", authUser.getUserId());
        assertEquals("type", authUser.getUserType());
        assertEquals("title", authUser.getUserTitle());
        assertEquals("department", authUser.getUserDepartment());
        assertEquals("firstName", authUser.getUserFirstNm());
        assertEquals("lastName", authUser.getUserLastNm());
        assertEquals("email@example.com", authUser.getUserWorkEmail());
        assertEquals("123-456-7890", authUser.getUserWorkPhone());
        assertEquals("098-765-4321", authUser.getUserMobilePhone());
        assertEquals("Y", authUser.getMasterSecAdminInd());
        assertEquals("N", authUser.getProgAreaAdminInd());
        assertEquals(2L, authUser.getNedssEntryId());
        assertEquals(3L, authUser.getExternalOrgUid());
        assertEquals("password", authUser.getUserPassword());
        assertEquals("comments", authUser.getUserComments());
        assertEquals(timestamp, authUser.getAddTime());
        assertEquals(4L, authUser.getAddUserId());
        assertEquals(timestamp, authUser.getLastChgTime());
        assertEquals(5L, authUser.getLastChgUserId());
        assertEquals("active", authUser.getRecordStatusCd());
        assertEquals(timestamp, authUser.getRecordStatusTime());
        assertEquals("Y", authUser.getJurisdictionDerivationInd());
        assertEquals(6L, authUser.getProviderUid());
    }

    @Test
    void testDefaultValues() {
        // Check default values
        assertNull(authUser.getAuthUserUid());
        assertNull(authUser.getUserId());
        assertNull(authUser.getUserType());
        assertNull(authUser.getUserTitle());
        assertNull(authUser.getUserDepartment());
        assertNull(authUser.getUserFirstNm());
        assertNull(authUser.getUserLastNm());
        assertNull(authUser.getUserWorkEmail());
        assertNull(authUser.getUserWorkPhone());
        assertNull(authUser.getUserMobilePhone());
        assertNull(authUser.getMasterSecAdminInd());
        assertNull(authUser.getProgAreaAdminInd());
        assertNull(authUser.getNedssEntryId());
        assertNull(authUser.getExternalOrgUid());
        assertNull(authUser.getUserPassword());
        assertNull(authUser.getUserComments());
        assertNull(authUser.getAddTime());
        assertNull(authUser.getAddUserId());
        assertNull(authUser.getLastChgTime());
        assertNull(authUser.getLastChgUserId());
        assertNull(authUser.getRecordStatusCd());
        assertNull(authUser.getRecordStatusTime());
        assertNull(authUser.getJurisdictionDerivationInd());
        assertNull(authUser.getProviderUid());
    }
}
