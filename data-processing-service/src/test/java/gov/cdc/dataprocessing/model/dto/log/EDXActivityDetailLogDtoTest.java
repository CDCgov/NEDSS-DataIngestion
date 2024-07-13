package gov.cdc.dataprocessing.model.dto.log;


import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class EDXActivityDetailLogDtoTest {

    @Test
    void testGettersAndSetters() {
        EDXActivityDetailLogDto dto = new EDXActivityDetailLogDto();

        Long edxActivityDetailLogUid = 1L;
        Long edxActivityLogUid = 2L;
        String recordId = "recordId";
        String recordType = "recordType";
        String recordName = "recordName";
        String logType = "logType";
        String comment = "comment";
        String logTypeHtml = "logTypeHtml";
        String commentHtml = "commentHtml";
        Long lastChgUserId = 3L;
        Timestamp lastChgTime = new Timestamp(System.currentTimeMillis());
        Long addUserId = 4L;
        Timestamp addTime = new Timestamp(System.currentTimeMillis() - 1000);
        Integer publishVersionNbr = 5;

        dto.setEdxActivityDetailLogUid(edxActivityDetailLogUid);
        dto.setEdxActivityLogUid(edxActivityLogUid);
        dto.setRecordId(recordId);
        dto.setRecordType(recordType);
        dto.setRecordName(recordName);
        dto.setLogType(logType);
        dto.setComment(comment);
        dto.setLogTypeHtml(logTypeHtml);
        dto.setCommentHtml(commentHtml);
        dto.setLastChgUserId(lastChgUserId);
        dto.setLastChgTime(lastChgTime);
        dto.setAddUserId(addUserId);
        dto.setAddTime(addTime);
        dto.setPublishVersionNbr(publishVersionNbr);

        assertEquals(edxActivityDetailLogUid, dto.getEdxActivityDetailLogUid());
        assertEquals(edxActivityLogUid, dto.getEdxActivityLogUid());
        assertEquals(recordId, dto.getRecordId());
        assertEquals(recordType, dto.getRecordType());
        assertEquals(recordName, dto.getRecordName());
        assertEquals(logType, dto.getLogType());
        assertEquals(comment, dto.getComment());
        assertEquals(logTypeHtml, dto.getLogTypeHtml());
        assertEquals(commentHtml, dto.getCommentHtml());
        assertEquals(lastChgUserId, dto.getLastChgUserId());
        assertEquals(lastChgTime, dto.getLastChgTime());
        assertEquals(addUserId, dto.getAddUserId());
        assertEquals(addTime, dto.getAddTime());
        assertEquals(publishVersionNbr, dto.getPublishVersionNbr());
    }
}
