package gov.cdc.dataprocessing.model.dto.log;


import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MessageLogDtoTest {

    @Test
    void testGettersAndSetters() {
        MessageLogDto dto = new MessageLogDto();

        Long messageLogUid = 1L;
        String messageTxt = "Sample message";
        String conditionCd = "Condition";
        Long personUid = 2L;
        Long assignedToUid = 3L;
        Long eventUid = 4L;
        String eventTypeCd = "Event Type";
        String messageStatusCd = "Status";
        String recordStatusCd = "Record Status";
        Timestamp recordStatusTime = new Timestamp(System.currentTimeMillis());
        Timestamp addTime = new Timestamp(System.currentTimeMillis());
        Long userId = 5L;
        Timestamp lastChgTime = new Timestamp(System.currentTimeMillis());
        Long lastChgUserId = 6L;

        dto.setMessageLogUid(messageLogUid);
        dto.setMessageTxt(messageTxt);
        dto.setConditionCd(conditionCd);
        dto.setPersonUid(personUid);
        dto.setAssignedToUid(assignedToUid);
        dto.setEventUid(eventUid);
        dto.setEventTypeCd(eventTypeCd);
        dto.setMessageStatusCd(messageStatusCd);
        dto.setRecordStatusCd(recordStatusCd);
        dto.setRecordStatusTime(recordStatusTime);
        dto.setAddTime(addTime);
        dto.setUserId(userId);
        dto.setLastChgTime(lastChgTime);
        dto.setLastChgUserId(lastChgUserId);

        assertEquals(messageLogUid, dto.getMessageLogUid());
        assertEquals(messageTxt, dto.getMessageTxt());
        assertEquals(conditionCd, dto.getConditionCd());
        assertEquals(personUid, dto.getPersonUid());
        assertEquals(assignedToUid, dto.getAssignedToUid());
        assertEquals(eventUid, dto.getEventUid());
        assertEquals(eventTypeCd, dto.getEventTypeCd());
        assertEquals(messageStatusCd, dto.getMessageStatusCd());
        assertEquals(recordStatusCd, dto.getRecordStatusCd());
        assertEquals(recordStatusTime, dto.getRecordStatusTime());
        assertEquals(addTime, dto.getAddTime());
        assertEquals(userId, dto.getUserId());
        assertEquals(lastChgTime, dto.getLastChgTime());
        assertEquals(lastChgUserId, dto.getLastChgUserId());
    }
}
