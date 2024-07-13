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

    @Test
    void testEqualsAndHashCode() {
        ElrXref elrXref1 = new ElrXref();
        elrXref1.setFromCodeSetNm("FromCodeSetNm");
        elrXref1.setFromSeqNum((short) 1);
        elrXref1.setFromCode("FromCode");

        ElrXref elrXref2 = new ElrXref();
        elrXref2.setFromCodeSetNm("FromCodeSetNm");
        elrXref2.setFromSeqNum((short) 1);
        elrXref2.setFromCode("FromCode");

        ElrXref elrXref3 = new ElrXref();
        elrXref3.setFromCodeSetNm("DifferentFromCodeSetNm");
        elrXref3.setFromSeqNum((short) 3);
        elrXref3.setFromCode("DifferentFromCode");

        // Assert equals and hashCode
        assertEquals(elrXref1, elrXref2);
        assertEquals(elrXref1.hashCode(), elrXref2.hashCode());

        assertNotEquals(elrXref1, elrXref3);
        assertNotEquals(elrXref1.hashCode(), elrXref3.hashCode());
    }

    @Test
    void testToString() {
        ElrXref elrXref = new ElrXref();
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

        String expectedString = "ElrXref(fromCodeSetNm=FromCodeSetNm, fromSeqNum=1, fromCode=FromCode, toCodeSetNm=ToCodeSetNm, toSeqNum=2, toCode=ToCode, effectiveFromTime=" + elrXref.getEffectiveFromTime() + ", effectiveToTime=" + elrXref.getEffectiveToTime() + ", statusCd=A, statusTime=" + elrXref.getStatusTime() + ", laboratoryId=LaboratoryId, nbsUid=123)";
        assertEquals(expectedString, elrXref.toString());
    }
}
