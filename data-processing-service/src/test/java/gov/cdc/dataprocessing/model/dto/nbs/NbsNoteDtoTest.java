package gov.cdc.dataprocessing.model.dto.nbs;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class NbsNoteDtoTest {

    @Test
    void testGettersAndSetters() {
        NbsNoteDto dto = new NbsNoteDto();

        // Set values
        dto.setNbsNoteUid(1L);
        dto.setNoteParentUid(2L);
        dto.setAddTime(new Timestamp(System.currentTimeMillis()));
        dto.setAddUserId(3L);
        dto.setRecordStatusCode("RecordStatusCode");
        dto.setRecordStatusTime(new Timestamp(System.currentTimeMillis()));
        dto.setLastChgTime(new Timestamp(System.currentTimeMillis()));
        dto.setLastChgUserId(4L);
        dto.setNote("Note");
        dto.setPrivateIndCd("PrivateIndCd");
        dto.setTypeCd("TypeCd");
        dto.setLastChgUserNm("LastChgUserNm");
        dto.setJurisdictionCd("TEST");
        dto.setProgAreaCd("TEST");
        dto.setLocalId("TEST");
        dto.setLastChgReasonCd("TEST");
        dto.setStatusTime(null);
        dto.setProgramJurisdictionOid(null);
        dto.setSharedInd(null);

        // Assert values
        assertEquals(1L, dto.getNbsNoteUid());
        assertEquals(2L, dto.getNoteParentUid());
        assertNotNull(dto.getAddTime());
        assertEquals(3L, dto.getAddUserId());
        assertEquals("RecordStatusCode", dto.getRecordStatusCode());
        assertNotNull(dto.getRecordStatusTime());
        assertNotNull(dto.getLastChgTime());
        assertEquals(4L, dto.getLastChgUserId());
        assertEquals("Note", dto.getNote());
        assertEquals("PrivateIndCd", dto.getPrivateIndCd());
        assertEquals("TypeCd", dto.getTypeCd());
        assertEquals("LastChgUserNm", dto.getLastChgUserNm());
    }

    @Test
    void testOverriddenMethods() {
        NbsNoteDto dto = new NbsNoteDto();

        // Test overridden methods that return null
        assertNull(dto.getLastChgUserId());
        assertNull(dto.getJurisdictionCd());
        assertNull(dto.getProgAreaCd());
        assertNull(dto.getLastChgTime());
        assertNull(dto.getLocalId());
        assertNull(dto.getAddUserId());
        assertNull(dto.getLastChgReasonCd());
        assertNull(dto.getRecordStatusCd());
        assertNull(dto.getRecordStatusTime());
        assertNull(dto.getStatusCd());
        assertNull(dto.getStatusTime());
        assertNull(dto.getSuperclass());
        assertNull(dto.getUid());
        assertNull(dto.getAddTime());
        assertNull(dto.getProgramJurisdictionOid());
        assertNull(dto.getSharedInd());
        assertNull(dto.getVersionCtrlNbr());

        // Test setting and getting statusCd
        dto.setStatusCd("StatusCd");
        assertNull(dto.getStatusCd());
    }
}
