package gov.cdc.dataprocessing.model.dto.matching;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EdxPatientMatchDtoTest {

    @Test
    public void testGettersAndSetters() {
        // Create an instance of EdxPatientMatchDto
        EdxPatientMatchDto dto = new EdxPatientMatchDto();

        // Set values using setters
        dto.setEdxPatientMatchUid(1L);
        dto.setPatientUid(2L);
        dto.setMatchString("Test Match");
        dto.setTypeCd("Type A");
        dto.setMatchStringHashCode(12345L);
        dto.setAddUserId(100L);
        dto.setLastChgUserId(101L);
        Timestamp addTime = Timestamp.valueOf("2023-01-01 12:00:00");
        dto.setAddTime(addTime);
        Timestamp lastChgTime = Timestamp.valueOf("2023-01-02 12:00:00");
        dto.setLastChgTime(lastChgTime);
        dto.setMultipleMatch(true);

        // Assert values using getters
        assertEquals(1L, dto.getEdxPatientMatchUid());
        assertEquals(2L, dto.getPatientUid());
        assertEquals("Test Match", dto.getMatchString());
        assertEquals("Type A", dto.getTypeCd());
        assertEquals(12345L, dto.getMatchStringHashCode());
        assertEquals(100L, dto.getAddUserId());
        assertEquals(101L, dto.getLastChgUserId());
        assertEquals(addTime, dto.getAddTime());
        assertEquals(lastChgTime, dto.getLastChgTime());
        assertTrue(dto.isMultipleMatch());
    }
}
