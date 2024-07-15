package gov.cdc.dataprocessing.repository.nbs.odse.model.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AuthUserRealizedRoleTest {

    private AuthUserRealizedRole authUserRealizedRole;
    private Timestamp timestamp;

    @BeforeEach
    void setUp() {
        authUserRealizedRole = new AuthUserRealizedRole();
        timestamp = new Timestamp(System.currentTimeMillis());
    }

    @Test
    void testGettersAndSetters() {
        // Set values
        authUserRealizedRole.setPermSetNm("PermissionSetName");
        authUserRealizedRole.setAuthUserRoleUid(1L);
        authUserRealizedRole.setAuthRoleNm("RoleName");
        authUserRealizedRole.setProgAreaCd("ProgramAreaCode");
        authUserRealizedRole.setJurisdictionCd("JurisdictionCode");
        authUserRealizedRole.setAuthUserUid(2L);
        authUserRealizedRole.setAuthPermSetUid(3L);
        authUserRealizedRole.setRoleGuestInd("Y");
        authUserRealizedRole.setReadOnlyInd("N");
        authUserRealizedRole.setDispSeqNbr(1);
        authUserRealizedRole.setAddTime(timestamp);
        authUserRealizedRole.setAddUserId(4L);
        authUserRealizedRole.setLastChgTime(timestamp);
        authUserRealizedRole.setLastChgUserId(5L);
        authUserRealizedRole.setRecordStatusCd("Active");
        authUserRealizedRole.setRecordStatusTime(timestamp);

        // Verify values
        assertEquals("PermissionSetName", authUserRealizedRole.getPermSetNm());
        assertEquals(1L, authUserRealizedRole.getAuthUserRoleUid());
        assertEquals("RoleName", authUserRealizedRole.getAuthRoleNm());
        assertEquals("ProgramAreaCode", authUserRealizedRole.getProgAreaCd());
        assertEquals("JurisdictionCode", authUserRealizedRole.getJurisdictionCd());
        assertEquals(2L, authUserRealizedRole.getAuthUserUid());
        assertEquals(3L, authUserRealizedRole.getAuthPermSetUid());
        assertEquals("Y", authUserRealizedRole.getRoleGuestInd());
        assertEquals("N", authUserRealizedRole.getReadOnlyInd());
        assertEquals(1, authUserRealizedRole.getDispSeqNbr());
        assertEquals(timestamp, authUserRealizedRole.getAddTime());
        assertEquals(4L, authUserRealizedRole.getAddUserId());
        assertEquals(timestamp, authUserRealizedRole.getLastChgTime());
        assertEquals(5L, authUserRealizedRole.getLastChgUserId());
        assertEquals("Active", authUserRealizedRole.getRecordStatusCd());
        assertEquals(timestamp, authUserRealizedRole.getRecordStatusTime());
    }

    @Test
    void testDefaultValues() {
        // Check default values
        assertNull(authUserRealizedRole.getPermSetNm());
        assertNull(authUserRealizedRole.getAuthUserRoleUid());
        assertNull(authUserRealizedRole.getAuthRoleNm());
        assertNull(authUserRealizedRole.getProgAreaCd());
        assertNull(authUserRealizedRole.getJurisdictionCd());
        assertNull(authUserRealizedRole.getAuthUserUid());
        assertNull(authUserRealizedRole.getAuthPermSetUid());
        assertNull(authUserRealizedRole.getRoleGuestInd());
        assertNull(authUserRealizedRole.getReadOnlyInd());
        assertNull(authUserRealizedRole.getDispSeqNbr());
        assertNull(authUserRealizedRole.getAddTime());
        assertNull(authUserRealizedRole.getAddUserId());
        assertNull(authUserRealizedRole.getLastChgTime());
        assertNull(authUserRealizedRole.getLastChgUserId());
        assertNull(authUserRealizedRole.getRecordStatusCd());
        assertNull(authUserRealizedRole.getRecordStatusTime());
    }
}
