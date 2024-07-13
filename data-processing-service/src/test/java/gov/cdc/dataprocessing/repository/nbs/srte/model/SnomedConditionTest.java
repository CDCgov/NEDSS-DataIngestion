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

    @Test
    void testEqualsAndHashCode() {
        SnomedCondition snomedCondition1 = new SnomedCondition();
        snomedCondition1.setSnomedCd("SnomedCd");
        snomedCondition1.setConditionCd("ConditionCd");

        SnomedCondition snomedCondition2 = new SnomedCondition();
        snomedCondition2.setSnomedCd("SnomedCd");
        snomedCondition2.setConditionCd("ConditionCd");

        SnomedCondition snomedCondition3 = new SnomedCondition();
        snomedCondition3.setSnomedCd("DifferentSnomedCd");
        snomedCondition3.setConditionCd("DifferentConditionCd");

        // Assert equals and hashCode
        assertEquals(snomedCondition1, snomedCondition2);
        assertEquals(snomedCondition1.hashCode(), snomedCondition2.hashCode());

        assertNotEquals(snomedCondition1, snomedCondition3);
        assertNotEquals(snomedCondition1.hashCode(), snomedCondition3.hashCode());
    }

    @Test
    void testToString() {
        SnomedCondition snomedCondition = new SnomedCondition();
        snomedCondition.setSnomedCd("SnomedCd");
        snomedCondition.setConditionCd("ConditionCd");
        snomedCondition.setDiseaseNm("DiseaseNm");
        snomedCondition.setOrganismSetNm("OrganismSetNm");
        snomedCondition.setStatusCd("A");
        snomedCondition.setStatusTime(new Timestamp(System.currentTimeMillis()));
        snomedCondition.setEffectiveFromTime(new Timestamp(System.currentTimeMillis()));
        snomedCondition.setEffectiveToTime(new Timestamp(System.currentTimeMillis()));

        String expectedString = "SnomedCondition(snomedCd=SnomedCd, conditionCd=ConditionCd, diseaseNm=DiseaseNm, organismSetNm=OrganismSetNm, statusCd=A, statusTime=" + snomedCondition.getStatusTime() + ", effectiveFromTime=" + snomedCondition.getEffectiveFromTime() + ", effectiveToTime=" + snomedCondition.getEffectiveToTime() + ")";
        assertEquals(expectedString, snomedCondition.toString());
    }
}
