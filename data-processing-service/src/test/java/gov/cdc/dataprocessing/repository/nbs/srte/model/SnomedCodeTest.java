package gov.cdc.dataprocessing.repository.nbs.srte.model;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class SnomedCodeTest {

    @Test
    void testGettersAndSetters() {
        SnomedCode snomedCode = new SnomedCode();

        // Set values
        snomedCode.setSnomedCd("SnomedCd");
        snomedCode.setSnomedDescTxt("SnomedDescTxt");
        snomedCode.setSourceConceptId("SourceConceptId");
        snomedCode.setSourceVersionId("SourceVersionId");
        snomedCode.setStatusCd("A");
        snomedCode.setStatusTime(new Timestamp(System.currentTimeMillis()));
        snomedCode.setNbsUid(123);
        snomedCode.setEffectiveFromTime(new Timestamp(System.currentTimeMillis()));
        snomedCode.setEffectiveToTime(new Timestamp(System.currentTimeMillis()));
        snomedCode.setPaDerivationExcludeCd("ExcludeCd");

        // Assert values
        assertEquals("SnomedCd", snomedCode.getSnomedCd());
        assertEquals("SnomedDescTxt", snomedCode.getSnomedDescTxt());
        assertEquals("SourceConceptId", snomedCode.getSourceConceptId());
        assertEquals("SourceVersionId", snomedCode.getSourceVersionId());
        assertEquals("A", snomedCode.getStatusCd());
        assertNotNull(snomedCode.getStatusTime());
        assertEquals(123, snomedCode.getNbsUid());
        assertNotNull(snomedCode.getEffectiveFromTime());
        assertNotNull(snomedCode.getEffectiveToTime());
        assertEquals("ExcludeCd", snomedCode.getPaDerivationExcludeCd());
    }

    @Test
    void testEqualsAndHashCode() {
        SnomedCode snomedCode1 = new SnomedCode();
        snomedCode1.setSnomedCd("SnomedCd");
        snomedCode1.setSnomedDescTxt("SnomedDescTxt");

        SnomedCode snomedCode2 = new SnomedCode();
        snomedCode2.setSnomedCd("SnomedCd");
        snomedCode2.setSnomedDescTxt("SnomedDescTxt");

        SnomedCode snomedCode3 = new SnomedCode();
        snomedCode3.setSnomedCd("DifferentSnomedCd");
        snomedCode3.setSnomedDescTxt("DifferentSnomedDescTxt");

        // Assert equals and hashCode
        assertEquals(snomedCode1, snomedCode2);
        assertEquals(snomedCode1.hashCode(), snomedCode2.hashCode());

        assertNotEquals(snomedCode1, snomedCode3);
        assertNotEquals(snomedCode1.hashCode(), snomedCode3.hashCode());
    }

    @Test
    void testToString() {
        SnomedCode snomedCode = new SnomedCode();
        snomedCode.setSnomedCd("SnomedCd");
        snomedCode.setSnomedDescTxt("SnomedDescTxt");
        snomedCode.setSourceConceptId("SourceConceptId");
        snomedCode.setSourceVersionId("SourceVersionId");
        snomedCode.setStatusCd("A");
        snomedCode.setStatusTime(new Timestamp(System.currentTimeMillis()));
        snomedCode.setNbsUid(123);
        snomedCode.setEffectiveFromTime(new Timestamp(System.currentTimeMillis()));
        snomedCode.setEffectiveToTime(new Timestamp(System.currentTimeMillis()));
        snomedCode.setPaDerivationExcludeCd("ExcludeCd");

        String expectedString = "SnomedCode(snomedCd=SnomedCd, snomedDescTxt=SnomedDescTxt, sourceConceptId=SourceConceptId, sourceVersionId=SourceVersionId, statusCd=A, statusTime=" + snomedCode.getStatusTime() + ", nbsUid=123, effectiveFromTime=" + snomedCode.getEffectiveFromTime() + ", effectiveToTime=" + snomedCode.getEffectiveToTime() + ", paDerivationExcludeCd=ExcludeCd)";
        assertEquals(expectedString, snomedCode.toString());
    }
}
