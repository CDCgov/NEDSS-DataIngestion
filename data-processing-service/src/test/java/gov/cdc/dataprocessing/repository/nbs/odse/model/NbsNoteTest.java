package gov.cdc.dataprocessing.repository.nbs.odse.model;


import gov.cdc.dataprocessing.model.dto.nbs.NbsNoteDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsNote;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class NbsNoteTest {

    @Test
    void testDefaultConstructor() {
        // Arrange & Act
        NbsNote nbsNote = new NbsNote();

        // Assert
        assertNull(nbsNote.getNbsNoteUid());
        assertNull(nbsNote.getNoteParentUid());
        assertNull(nbsNote.getRecordStatusCd());
        assertNull(nbsNote.getRecordStatusTime());
        assertNull(nbsNote.getAddTime());
        assertNull(nbsNote.getAddUserId());
        assertNull(nbsNote.getLastChgTime());
        assertNull(nbsNote.getLastChgUserId());
        assertNull(nbsNote.getNote());
        assertNull(nbsNote.getPrivateIndCd());
        assertNull(nbsNote.getTypeCd());
    }

    @Test
    void testDtoConstructor() {
        // Arrange
        Long nbsNoteUid = 1L;
        Long noteParentUid = 2L;
        String recordStatusCd = "Active";
        Timestamp recordStatusTime = new Timestamp(System.currentTimeMillis());
        Timestamp addTime = new Timestamp(System.currentTimeMillis());
        Long addUserId = 3L;
        Timestamp lastChgTime = new Timestamp(System.currentTimeMillis());
        Long lastChgUserId = 4L;
        String note = "Sample Note";
        String privateIndCd = "Y";
        String typeCd = "Type1";

        NbsNoteDto dto = new NbsNoteDto();
        dto.setNbsNoteUid(nbsNoteUid);
        dto.setNoteParentUid(noteParentUid);
        dto.setRecordStatusCd(recordStatusCd);
        dto.setRecordStatusTime(recordStatusTime);
        dto.setAddTime(addTime);
        dto.setAddUserId(addUserId);
        dto.setLastChgTime(lastChgTime);
        dto.setLastChgUserId(lastChgUserId);
        dto.setNote(note);
        dto.setPrivateIndCd(privateIndCd);
        dto.setTypeCd(typeCd);

        // Act
        NbsNote nbsNote = new NbsNote(dto);

        // Assert
        assertEquals(nbsNoteUid, nbsNote.getNbsNoteUid());
        assertEquals(noteParentUid, nbsNote.getNoteParentUid());
        assertEquals(recordStatusCd, nbsNote.getRecordStatusCd());
        assertEquals(recordStatusTime, nbsNote.getRecordStatusTime());
        assertEquals(addTime, nbsNote.getAddTime());
        assertEquals(addUserId, nbsNote.getAddUserId());
        assertEquals(lastChgTime, nbsNote.getLastChgTime());
        assertEquals(lastChgUserId, nbsNote.getLastChgUserId());
        assertEquals(note, nbsNote.getNote());
        assertEquals(privateIndCd, nbsNote.getPrivateIndCd());
        assertEquals(typeCd, nbsNote.getTypeCd());
    }

    @Test
    void testSettersAndGetters() {
        // Arrange
        NbsNote nbsNote = new NbsNote();

        Long nbsNoteUid = 1L;
        Long noteParentUid = 2L;
        String recordStatusCd = "Active";
        Timestamp recordStatusTime = new Timestamp(System.currentTimeMillis());
        Timestamp addTime = new Timestamp(System.currentTimeMillis());
        Long addUserId = 3L;
        Timestamp lastChgTime = new Timestamp(System.currentTimeMillis());
        Long lastChgUserId = 4L;
        String note = "Sample Note";
        String privateIndCd = "Y";
        String typeCd = "Type1";

        // Act
        nbsNote.setNbsNoteUid(nbsNoteUid);
        nbsNote.setNoteParentUid(noteParentUid);
        nbsNote.setRecordStatusCd(recordStatusCd);
        nbsNote.setRecordStatusTime(recordStatusTime);
        nbsNote.setAddTime(addTime);
        nbsNote.setAddUserId(addUserId);
        nbsNote.setLastChgTime(lastChgTime);
        nbsNote.setLastChgUserId(lastChgUserId);
        nbsNote.setNote(note);
        nbsNote.setPrivateIndCd(privateIndCd);
        nbsNote.setTypeCd(typeCd);

        // Assert
        assertEquals(nbsNoteUid, nbsNote.getNbsNoteUid());
        assertEquals(noteParentUid, nbsNote.getNoteParentUid());
        assertEquals(recordStatusCd, nbsNote.getRecordStatusCd());
        assertEquals(recordStatusTime, nbsNote.getRecordStatusTime());
        assertEquals(addTime, nbsNote.getAddTime());
        assertEquals(addUserId, nbsNote.getAddUserId());
        assertEquals(lastChgTime, nbsNote.getLastChgTime());
        assertEquals(lastChgUserId, nbsNote.getLastChgUserId());
        assertEquals(note, nbsNote.getNote());
        assertEquals(privateIndCd, nbsNote.getPrivateIndCd());
        assertEquals(typeCd, nbsNote.getTypeCd());
    }
}
