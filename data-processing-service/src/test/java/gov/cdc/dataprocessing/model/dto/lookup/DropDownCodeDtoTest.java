package gov.cdc.dataprocessing.model.dto.lookup;

import gov.cdc.dataprocessing.model.dto.lookup.DropDownCodeDto;
import org.junit.jupiter.api.Test;
import java.sql.Timestamp;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class DropDownCodeDtoTest {

    @Test
    public void testGetterSetter() {
        // Create an instance of DropDownCodeDto
        DropDownCodeDto dto = new DropDownCodeDto();

        // Test setting and getting values for key
        dto.setKey("testKey");
        assertEquals("testKey", dto.getKey());

        // Test setting and getting values for value
        dto.setValue("testValue");
        assertEquals("testValue", dto.getValue());

        // Test setting and getting values for intValue
        dto.setIntValue(123);
        assertEquals(123, dto.getIntValue());

        // Test setting and getting values for altValue
        dto.setAltValue("testAltValue");
        assertEquals("testAltValue", dto.getAltValue());

        // Test setting and getting values for longKey
        dto.setLongKey(456L);
        assertEquals(456L, dto.getLongKey());

        // Test setting and getting values for statusCd
        dto.setStatusCd("ACTIVE");
        assertEquals("ACTIVE", dto.getStatusCd());

        // Test setting and getting values for effectiveToTime
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        dto.setEffectiveToTime(timestamp);
        assertEquals(timestamp, dto.getEffectiveToTime());

        // Test default values
        assertNull(dto.getLastChgUserId());
        assertNull(dto.getJurisdictionCd());
        assertNull(dto.getProgAreaCd());
        assertNull(dto.getLastChgTime());
        assertNull(dto.getLocalId());
        assertNull(dto.getAddUserId());
        assertNull(dto.getLastChgReasonCd());
        assertNull(dto.getRecordStatusCd());
        assertNull(dto.getRecordStatusTime());
        assertNull(dto.getStatusTime());
        assertNull(dto.getSuperclass());
        assertNull(dto.getUid());
        assertNull(dto.getAddTime());
        assertNull(dto.getProgramJurisdictionOid());
        assertNull(dto.getSharedInd());
        assertNull(dto.getVersionCtrlNbr());
    }
}
