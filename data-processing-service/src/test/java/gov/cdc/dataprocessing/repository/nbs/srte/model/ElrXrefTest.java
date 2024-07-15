package gov.cdc.dataprocessing.repository.nbs.srte.model;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class ElrXrefTest {

    @Test
    void testGettersAndSetters() {
        ElrXref elrXref = new ElrXref();

        // Set values
        elrXref.setFromCodeSetNm("FromCodeSetNm");
        elrXref.setFromSeqNum((short) 1);
        elrXref.setFromCode("FromCode");
        elrXref.setToCodeSetNm("ToCodeSetNm");
        elrXref.setToSeqNum((short) 2);
        elrXref.setToCode("ToCode");
        elrXref.setEffectiveFromTime(new Date());
        elrXref.setEffectiveToTime(new Date());
        elrXref.setStatusCd('A');
        elrXref.setStatusTime(new Date());
        elrXref.setLaboratoryId("LaboratoryId");
        elrXref.setNbsUid(123);

        // Assert values
        assertEquals("FromCodeSetNm", elrXref.getFromCodeSetNm());
        assertEquals((short) 1, elrXref.getFromSeqNum());
        assertEquals("FromCode", elrXref.getFromCode());
        assertEquals("ToCodeSetNm", elrXref.getToCodeSetNm());
        assertEquals((short) 2, elrXref.getToSeqNum());
        assertEquals("ToCode", elrXref.getToCode());
        assertNotNull(elrXref.getEffectiveFromTime());
        assertNotNull(elrXref.getEffectiveToTime());
        assertEquals('A', elrXref.getStatusCd());
        assertNotNull(elrXref.getStatusTime());
        assertEquals("LaboratoryId", elrXref.getLaboratoryId());
        assertEquals(123, elrXref.getNbsUid());
    }


}
