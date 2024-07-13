package gov.cdc.dataprocessing.model.dto.notification;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class UpdatedNotificationDtoTest {

    @Test
    void testGettersAndSetters() {
        UpdatedNotificationDto dto = new UpdatedNotificationDto();

        // Set values
        dto.setNotificationUid(1L);
        dto.setCaseStatusChg(true);
        dto.setAddTime(new Timestamp(System.currentTimeMillis()));
        dto.setAddUserId(2L);
        dto.setLastChgTime(new Timestamp(System.currentTimeMillis()));
        dto.setLastChgUserId(3L);
        dto.setVersionCtrlNbr(1);
        dto.setStatusCd("StatusCd");
        dto.setCaseClassCd("CaseClassCd");

        // Assert values
        assertEquals(1L, dto.getNotificationUid());
        assertTrue(dto.isCaseStatusChg());
        assertNotNull(dto.getAddTime());
        assertEquals(2L, dto.getAddUserId());
        assertNotNull(dto.getLastChgTime());
        assertEquals(3L, dto.getLastChgUserId());
        assertEquals(1, dto.getVersionCtrlNbr());
        assertEquals("StatusCd", dto.getStatusCd());
        assertEquals("CaseClassCd", dto.getCaseClassCd());
    }
}
