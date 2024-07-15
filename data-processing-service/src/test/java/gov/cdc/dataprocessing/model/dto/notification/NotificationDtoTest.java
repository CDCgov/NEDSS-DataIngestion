package gov.cdc.dataprocessing.model.dto.notification;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class NotificationDtoTest {
    @Test
    void testGetAndSet() {
        NotificationDto dto = new NotificationDto();
        dto.setNotificationUid(10L);
        dto.setReceiving_system_nm("TEST");
        dto.setNndInd("TEST");
        dto.setLabReportEnableInd("TEST");
        dto.setVaccineEnableInd("TEST");
        assertNotNull(dto.getUid());
        assertNotNull(dto.getSuperclass());

        assertNotNull(dto.getReceiving_system_nm());
        assertNotNull(dto.getNndInd());
        assertNotNull(dto.getLabReportEnableInd());
        assertNotNull(dto.getVaccineEnableInd());

    }
}
