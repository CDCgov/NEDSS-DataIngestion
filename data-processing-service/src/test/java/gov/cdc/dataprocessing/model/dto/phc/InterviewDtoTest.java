package gov.cdc.dataprocessing.model.dto.phc;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class InterviewDtoTest {

    @Test
    void testGettersAndSetters() {
        InterviewDto dto = new InterviewDto();

        // Set values
        dto.setInterviewUid(1L);
        dto.setIntervieweeRoleCd("IntervieweeRoleCd");
        dto.setInterviewDate(new Timestamp(System.currentTimeMillis()));
        dto.setInterviewTypeCd("InterviewTypeCd");
        dto.setInterviewStatusCd("InterviewStatusCd");
        dto.setInterviewLocCd("InterviewLocCd");
        dto.setAddTime(new Timestamp(System.currentTimeMillis()));
        dto.setAddUserId(2L);
        dto.setLastChgTime(new Timestamp(System.currentTimeMillis()));
        dto.setLastChgUserId(3L);
        dto.setLocalId("LocalId");
        dto.setRecordStatusCd("RecordStatusCd");
        dto.setRecordStatusTime(new Timestamp(System.currentTimeMillis()));
        dto.setVersionCtrlNbr(4);
        dto.setAddUserName("AddUserName");
        dto.setLastChgUserName("LastChgUserName");
        dto.setAssociated(true);

        // Assert values
        assertEquals(1L, dto.getInterviewUid());
        assertEquals("IntervieweeRoleCd", dto.getIntervieweeRoleCd());
        assertNotNull(dto.getInterviewDate());
        assertEquals("InterviewTypeCd", dto.getInterviewTypeCd());
        assertEquals("InterviewStatusCd", dto.getInterviewStatusCd());
        assertEquals("InterviewLocCd", dto.getInterviewLocCd());
        assertNotNull(dto.getAddTime());
        assertEquals(2L, dto.getAddUserId());
        assertNotNull(dto.getLastChgTime());
        assertEquals(3L, dto.getLastChgUserId());
        assertEquals("LocalId", dto.getLocalId());
        assertEquals("RecordStatusCd", dto.getRecordStatusCd());
        assertNotNull(dto.getRecordStatusTime());
        assertEquals(4, dto.getVersionCtrlNbr());
        assertEquals("AddUserName", dto.getAddUserName());
        assertEquals("LastChgUserName", dto.getLastChgUserName());
        assertTrue(dto.isAssociated());
    }

    @Test
    void testSuperclassMethod() {
        InterviewDto dto = new InterviewDto();
        assertEquals("Act", dto.getSuperclass());
    }
}
