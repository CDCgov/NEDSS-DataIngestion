package gov.cdc.dataprocessing.model.dto.lookup;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class DropDownCodeDtoTest {

    @Test
    void testGettersAndSetters() {
        DropDownCodeDto dto = new DropDownCodeDto();

        // Set values
        dto.setKey("Key");
        dto.setValue("Value");
        dto.setIntValue(1);
        dto.setAltValue("AltValue");
        dto.setLongKey(1L);
        dto.setStatusCd("StatusCd");
        dto.setEffectiveToTime(new Timestamp(System.currentTimeMillis()));

        // Assert values
        assertEquals("Key", dto.getKey());
        assertEquals("Value", dto.getValue());
        assertEquals(1, dto.getIntValue());
        assertEquals("AltValue", dto.getAltValue());
        assertEquals(1L, dto.getLongKey());
        assertEquals("StatusCd", dto.getStatusCd());
        assertNotNull(dto.getEffectiveToTime());
    }

    @Test
    void testOverriddenMethods() {
        DropDownCodeDto dto = new DropDownCodeDto();

        dto.setSharedInd("TEST");
        dto.setProgramJurisdictionOid(100L);
        dto.setAddTime(null);
        dto.setStatusTime(null);
        dto.setRecordStatusTime(null);
        dto.setRecordStatusCd(null);
        dto.setLastChgReasonCd(null);
        dto.setAddUserId(null);
        dto.setLocalId(null);
        dto.setLastChgTime(null);
        dto.setProgAreaCd(null);
        dto.setJurisdictionCd(null);
        dto.setLastChgUserId(null);

        // Test overridden methods that return null
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

        // Set and assert statusCd (special case)
        dto.setStatusCd("StatusCd");
        assertEquals("StatusCd", dto.getStatusCd());
    }


}
