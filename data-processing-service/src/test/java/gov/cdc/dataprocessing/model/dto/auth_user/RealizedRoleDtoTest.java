package gov.cdc.dataprocessing.model.dto.auth_user;


import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUserRealizedRole;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RealizedRoleDtoTest {

    @Test
    void testGettersAndSetters() {
        RealizedRoleDto dto = new RealizedRoleDto();

        String roleName = "Admin";
        String programAreaCode = "PA001";
        String jurisdictionCode = "JC001";
        String oldProgramAreaCode = "OPA001";
        String oldJurisdictionCode = "OJC001";
        boolean guest = true;
        boolean readOnly = false;
        int seqNum = 1;
        String recordStatus = "Active";
        String guestString = "Y";

        dto.setRoleName(roleName);
        dto.setProgramAreaCode(programAreaCode);
        dto.setJurisdictionCode(jurisdictionCode);
        dto.setOldProgramAreaCode(oldProgramAreaCode);
        dto.setOldJurisdictionCode(oldJurisdictionCode);
        dto.setGuest(guest);
        dto.setReadOnly(readOnly);
        dto.setSeqNum(seqNum);
        dto.setRecordStatus(recordStatus);
        dto.setGuestString(guestString);

        assertEquals(roleName, dto.getRoleName());
        assertEquals(programAreaCode, dto.getProgramAreaCode());
        assertEquals(jurisdictionCode, dto.getJurisdictionCode());
        assertEquals(oldProgramAreaCode, dto.getOldProgramAreaCode());
        assertEquals(oldJurisdictionCode, dto.getOldJurisdictionCode());
        assertTrue(dto.isGuest());
        assertEquals(readOnly, dto.isReadOnly());
        assertEquals(seqNum, dto.getSeqNum());
        assertEquals(recordStatus, dto.getRecordStatus());
        assertEquals(guestString, dto.getGuestString());
    }

    @Test
    void testConstructor() {
        AuthUserRealizedRole role = new AuthUserRealizedRole();
        role.setAuthRoleNm("Admin");
        role.setProgAreaCd("PA001");
        role.setJurisdictionCd("JC001");

        RealizedRoleDto dto = new RealizedRoleDto(role);

        assertEquals(role.getAuthRoleNm(), dto.getRoleName());
        assertEquals(role.getProgAreaCd(), dto.getProgramAreaCode());
        assertEquals(role.getJurisdictionCd(), dto.getJurisdictionCode());
    }
}
