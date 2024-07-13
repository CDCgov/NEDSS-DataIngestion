package gov.cdc.dataprocessing.model.dto.log;

import gov.cdc.dataprocessing.repository.nbs.odse.model.log.MessageLog;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class MessageLogDtoTest {

    @Test
    void testGettersAndSetters() {
        MessageLogDto dto = new MessageLogDto();

        // Set values
        dto.setMessageLogUid(1L);
        dto.setMessageTxt("MessageTxt");
        dto.setConditionCd("ConditionCd");
        dto.setPersonUid(2L);
        dto.setAssignedToUid(3L);
        dto.setEventUid(4L);
        dto.setEventTypeCd("EventTypeCd");
        dto.setMessageStatusCd("MessageStatusCd");
        dto.setRecordStatusCd("RecordStatusCd");
        dto.setRecordStatusTime(new Timestamp(System.currentTimeMillis()));
        dto.setAddTime(new Timestamp(System.currentTimeMillis()));
        dto.setUserId(5L);
        dto.setLastChgTime(new Timestamp(System.currentTimeMillis()));
        dto.setLastChgUserId(6L);

        // Assert values
        assertEquals(1L, dto.getMessageLogUid());
        assertEquals("MessageTxt", dto.getMessageTxt());
        assertEquals("ConditionCd", dto.getConditionCd());
        assertEquals(2L, dto.getPersonUid());
        assertEquals(3L, dto.getAssignedToUid());
        assertEquals(4L, dto.getEventUid());
        assertEquals("EventTypeCd", dto.getEventTypeCd());
        assertEquals("MessageStatusCd", dto.getMessageStatusCd());
        assertEquals("RecordStatusCd", dto.getRecordStatusCd());
        assertNotNull(dto.getRecordStatusTime());
        assertNotNull(dto.getAddTime());
        assertEquals(5L, dto.getUserId());
        assertNotNull(dto.getLastChgTime());
        assertEquals(6L, dto.getLastChgUserId());
    }

    @Test
    void testConstructor() {
        MessageLog messageLog = new MessageLog();
        messageLog.setMessageLogUid(1L);
        messageLog.setMessageTxt("MessageTxt");
        messageLog.setConditionCd("ConditionCd");
        messageLog.setPersonUid(2L);
        messageLog.setAssignedToUid(3L);
        messageLog.setEventUid(4L);
        messageLog.setEventTypeCd("EventTypeCd");
        messageLog.setMessageStatusCd("MessageStatusCd");
        messageLog.setRecordStatusCd("RecordStatusCd");
        messageLog.setRecordStatusTime(new Timestamp(System.currentTimeMillis()));
        messageLog.setAddTime(new Timestamp(System.currentTimeMillis()));
        messageLog.setAddUserId(5L);
        messageLog.setLastChgTime(new Timestamp(System.currentTimeMillis()));
        messageLog.setLastChgUserId(6L);

        MessageLogDto dto = new MessageLogDto(messageLog);

        // Assert values
        assertEquals(1L, dto.getMessageLogUid());
        assertEquals("MessageTxt", dto.getMessageTxt());
        assertEquals("ConditionCd", dto.getConditionCd());
        assertEquals(2L, dto.getPersonUid());
        assertEquals(3L, dto.getAssignedToUid());
        assertEquals(4L, dto.getEventUid());
        assertEquals("EventTypeCd", dto.getEventTypeCd());
        assertEquals("MessageStatusCd", dto.getMessageStatusCd());
        assertEquals("RecordStatusCd", dto.getRecordStatusCd());
        assertNotNull(dto.getRecordStatusTime());
        assertNotNull(dto.getAddTime());
        assertEquals(5L, dto.getUserId());
        assertNotNull(dto.getLastChgTime());
        assertEquals(6L, dto.getLastChgUserId());
    }

    @Test
    void testSpecialConstructor() {
        MessageLog messageLog = new MessageLog();
        messageLog.setMessageLogUid(1L);
        messageLog.setMessageTxt("MessageTxt");
        messageLog.setConditionCd("ConditionCd");
        messageLog.setPersonUid(2L);
        messageLog.setAssignedToUid(3L);
        messageLog.setEventUid(4L);
        messageLog.setEventTypeCd("EventTypeCd");
        messageLog.setMessageStatusCd("MessageStatusCd");
        messageLog.setRecordStatusCd("RecordStatusCd");
        messageLog.setRecordStatusTime(new Timestamp(System.currentTimeMillis()));
        messageLog.setAddTime(new Timestamp(System.currentTimeMillis()));
        messageLog.setAddUserId(5L);
        messageLog.setLastChgTime(new Timestamp(System.currentTimeMillis()));
        messageLog.setLastChgUserId(6L);

        MessageLogDto dto = new MessageLogDto(messageLog);

        // Assert values
        assertEquals(messageLog.getMessageLogUid(), dto.getMessageLogUid());
        assertEquals(messageLog.getMessageTxt(), dto.getMessageTxt());
        assertEquals(messageLog.getConditionCd(), dto.getConditionCd());
        assertEquals(messageLog.getPersonUid(), dto.getPersonUid());
        assertEquals(messageLog.getAssignedToUid(), dto.getAssignedToUid());
        assertEquals(messageLog.getEventUid(), dto.getEventUid());
        assertEquals(messageLog.getEventTypeCd(), dto.getEventTypeCd());
        assertEquals(messageLog.getMessageStatusCd(), dto.getMessageStatusCd());
        assertEquals(messageLog.getRecordStatusCd(), dto.getRecordStatusCd());
        assertEquals(messageLog.getRecordStatusTime(), dto.getRecordStatusTime());
        assertEquals(messageLog.getAddTime(), dto.getAddTime());
        assertEquals(messageLog.getAddUserId(), dto.getUserId());
        assertEquals(messageLog.getLastChgTime(), dto.getLastChgTime());
        assertEquals(messageLog.getLastChgUserId(), dto.getLastChgUserId());
    }
}
