package gov.cdc.dataingestion.odse.repository.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EdxActivityDetailLogTest {
    @Test
    void testEdxActivityDetailLog() {
        EdxActivityDetailLog edxActivityDetailLog = new EdxActivityDetailLog();
        edxActivityDetailLog.setId(123L);
        edxActivityDetailLog.setLogType("Test Log Type");
        edxActivityDetailLog.setLogComment("Test Log Comment");
        edxActivityDetailLog.setRecordType("Test Record Type");
        edxActivityDetailLog.setRecordId("Test Record Id");
        edxActivityDetailLog.setRecordNm("Test Rocord Name");

        assertEquals(123L, edxActivityDetailLog.getId());
        assertEquals("Test Log Type", edxActivityDetailLog.getLogType());
        assertEquals("Test Log Comment", edxActivityDetailLog.getLogComment());
        assertEquals("Test Record Type", edxActivityDetailLog.getRecordType());
        assertEquals("Test Record Id", edxActivityDetailLog.getRecordId());
        assertEquals("Test Rocord Name", edxActivityDetailLog.getRecordNm());
    }
}