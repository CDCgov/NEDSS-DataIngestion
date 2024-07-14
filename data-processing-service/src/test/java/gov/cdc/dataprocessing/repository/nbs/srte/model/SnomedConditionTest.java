package gov.cdc.dataprocessing.repository.nbs.srte.model;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class SnomedConditionTest {

    @Test
    void testGettersAndSetters() {
        SnomedCondition snomedCondition = new SnomedCondition();

        // Set values
        snomedCondition.setSnomedCd("SnomedCd");
        snomedCondition.setConditionCd("ConditionCd");
        snomedCondition.setDiseaseNm("DiseaseNm");
        snomedCondition.setOrganismSetNm("OrganismSetNm");
        snomedCondition.setStatusCd("A");
        snomedCondition.setStatusTime(new Timestamp(System.currentTimeMillis()));
        snomedCondition.setEffectiveFromTime(new Timestamp(System.currentTimeMillis()));
        snomedCondition.setEffectiveToTime(new Timestamp(System.currentTimeMillis()));

        // Assert values
        assertEquals("SnomedCd", snomedCondition.getSnomedCd());
        assertEquals("ConditionCd", snomedCondition.getConditionCd());
        assertEquals("DiseaseNm", snomedCondition.getDiseaseNm());
        assertEquals("OrganismSetNm", snomedCondition.getOrganismSetNm());
        assertEquals("A", snomedCondition.getStatusCd());
        assertNotNull(snomedCondition.getStatusTime());
        assertNotNull(snomedCondition.getEffectiveFromTime());
        assertNotNull(snomedCondition.getEffectiveToTime());
    }

}
