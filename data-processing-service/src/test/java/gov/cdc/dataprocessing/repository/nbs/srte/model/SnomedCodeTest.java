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


}
