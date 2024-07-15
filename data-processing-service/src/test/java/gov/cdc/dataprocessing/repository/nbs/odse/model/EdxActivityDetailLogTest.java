package gov.cdc.dataprocessing.repository.nbs.odse.model;


import gov.cdc.dataprocessing.model.dto.log.EDXActivityDetailLogDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.log.EdxActivityDetailLog;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class EdxActivityDetailLogTest {

    @Test
    void testEDXActivityDetailLogDtoConstructor() {
        // Arrange
        EDXActivityDetailLogDto dto = new EDXActivityDetailLogDto();
        dto.setEdxActivityLogUid(1L);
        dto.setRecordId("rec-001");
        dto.setRecordType("TypeA");
        dto.setRecordName("RecordNameA");
        dto.setLogType("LogTypeA");
        dto.setComment("This is a log comment");

        // Act
        EdxActivityDetailLog log = new EdxActivityDetailLog(dto);

        // Assert
        assertEquals(dto.getEdxActivityLogUid(), log.getEdxActivityLogUid());
        assertEquals(dto.getRecordId(), log.getRecordId());
        assertEquals(dto.getRecordType(), log.getRecordType());
        assertEquals(dto.getRecordName(), log.getRecordNm());
        assertEquals(dto.getLogType(), log.getLogType());
        assertEquals(dto.getComment(), log.getLogComment());
    }

    @Test
    void testDefaultConstructor() {
        // Arrange & Act
        EdxActivityDetailLog log = new EdxActivityDetailLog();

        // Assert
        assertNull(log.getId());
        assertNull(log.getEdxActivityLogUid());
        assertNull(log.getRecordId());
        assertNull(log.getRecordType());
        assertNull(log.getRecordNm());
        assertNull(log.getLogType());
        assertNull(log.getLogComment());
    }
}
