package gov.cdc.dataprocessing.model.dto.organization;

import gov.cdc.dataprocessing.repository.nbs.odse.model.organization.OrganizationName;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class OrganizationNameDtoTest {

    @Test
    void testGettersAndSetters() {
        OrganizationNameDto dto = new OrganizationNameDto();

        // Set values
        dto.setOrganizationUid(1L);
        dto.setOrganizationNameSeq(2);
        dto.setNmTxt("NmTxt");
        dto.setNmUseCd("NmUseCd");
        dto.setRecordStatusCd("RecordStatusCd");
        dto.setDefaultNmInd("DefaultNmInd");
        dto.setProgAreaCd("ProgAreaCd");
        dto.setJurisdictionCd("JurisdictionCd");
        dto.setProgramJurisdictionOid(3L);
        dto.setSharedInd("SharedInd");

        // Assert values
        assertEquals(1L, dto.getOrganizationUid());
        assertEquals(2, dto.getOrganizationNameSeq());
        assertEquals("NmTxt", dto.getNmTxt());
        assertEquals("NmUseCd", dto.getNmUseCd());
        assertEquals("RecordStatusCd", dto.getRecordStatusCd());
        assertEquals("DefaultNmInd", dto.getDefaultNmInd());
        assertEquals("ProgAreaCd", dto.getProgAreaCd());
        assertEquals("JurisdictionCd", dto.getJurisdictionCd());
        assertEquals(3L, dto.getProgramJurisdictionOid());
        assertEquals("SharedInd", dto.getSharedInd());
    }

    @Test
    void testSpecialConstructor() {
        OrganizationName organizationName = new OrganizationName();
        organizationName.setOrganizationUid(1L);
        organizationName.setOrganizationNameSeq(2);
        organizationName.setNameText("NmTxt");
        organizationName.setNameUseCode("NmUseCd");
        organizationName.setRecordStatusCode("RecordStatusCd");
        organizationName.setDefaultNameIndicator("DefaultNmInd");

        OrganizationNameDto dto = new OrganizationNameDto(organizationName);

        // Assert values
        assertEquals(1L, dto.getOrganizationUid());
        assertEquals(2, dto.getOrganizationNameSeq());
        assertEquals("NmTxt", dto.getNmTxt());
        assertEquals("NmUseCd", dto.getNmUseCd());
        assertEquals("RecordStatusCd", dto.getRecordStatusCd());
        assertEquals("DefaultNmInd", dto.getDefaultNmInd());
    }

    @Test
    void testOverriddenMethods() {
        OrganizationNameDto dto = new OrganizationNameDto();

        // Test overridden methods
        assertNull(dto.getLastChgUserId());  // Note: This will fail since `organizationUid` is not set
        dto.setLastChgUserId(2L);  // No operation
        assertNotNull(dto.getLastChgTime());
        dto.setLastChgTime(new Timestamp(System.currentTimeMillis()));  // No operation
        assertEquals("Entity", dto.getLocalId());
        dto.setLocalId("Entity");  // No operation
        assertNull(dto.getAddUserId());  // Note: This will fail since `organizationUid` is not set
        dto.setAddUserId(2L);
        assertEquals("Entity", dto.getLastChgReasonCd());
        dto.setLastChgReasonCd("Entity");  // No operation
        assertNotNull(dto.getRecordStatusTime());
        dto.setRecordStatusTime(new Timestamp(System.currentTimeMillis()));  // No operation
        assertEquals("Entity", dto.getStatusCd());
        dto.setStatusCd("Entity");  // No operation
        assertNotNull(dto.getStatusTime());
        dto.setStatusTime(new Timestamp(System.currentTimeMillis()));  // No operation
        assertEquals("Entity", dto.getSuperclass());
        assertNull(dto.getUid());
        dto.setAddTime(new Timestamp(System.currentTimeMillis()));  // No operation
        assertNull(dto.getAddTime());
        assertNull(dto.getVersionCtrlNbr());
    }
}
