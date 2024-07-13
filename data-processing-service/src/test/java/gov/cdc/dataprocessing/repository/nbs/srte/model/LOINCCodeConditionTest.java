package gov.cdc.dataprocessing.repository.nbs.srte.model;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class LOINCCodeConditionTest {

    @Test
    void testGettersAndSetters() {
        LOINCCodeCondition loincCodeCondition = new LOINCCodeCondition();

        // Set values
        loincCodeCondition.setLoincCd("LoincCd");
        loincCodeCondition.setConditionCd("ConditionCd");
        loincCodeCondition.setDiseaseNm("DiseaseNm");
        loincCodeCondition.setReportedValue("ReportedValue");
        loincCodeCondition.setReportedNumericValue("ReportedNumericValue");
        loincCodeCondition.setStatusCd("A");
        loincCodeCondition.setStatusTime(new Timestamp(System.currentTimeMillis()));
        loincCodeCondition.setEffectiveFromTime(new Timestamp(System.currentTimeMillis()));
        loincCodeCondition.setEffectiveToTime(new Timestamp(System.currentTimeMillis()));

        // Assert values
        assertEquals("LoincCd", loincCodeCondition.getLoincCd());
        assertEquals("ConditionCd", loincCodeCondition.getConditionCd());
        assertEquals("DiseaseNm", loincCodeCondition.getDiseaseNm());
        assertEquals("ReportedValue", loincCodeCondition.getReportedValue());
        assertEquals("ReportedNumericValue", loincCodeCondition.getReportedNumericValue());
        assertEquals("A", loincCodeCondition.getStatusCd());
        assertNotNull(loincCodeCondition.getStatusTime());
        assertNotNull(loincCodeCondition.getEffectiveFromTime());
        assertNotNull(loincCodeCondition.getEffectiveToTime());
    }

    @Test
    void testEqualsAndHashCode() {
        LOINCCodeCondition loincCodeCondition1 = new LOINCCodeCondition();
        loincCodeCondition1.setLoincCd("LoincCd");
        loincCodeCondition1.setConditionCd("ConditionCd");

        LOINCCodeCondition loincCodeCondition2 = new LOINCCodeCondition();
        loincCodeCondition2.setLoincCd("LoincCd");
        loincCodeCondition2.setConditionCd("ConditionCd");

        LOINCCodeCondition loincCodeCondition3 = new LOINCCodeCondition();
        loincCodeCondition3.setLoincCd("DifferentLoincCd");
        loincCodeCondition3.setConditionCd("DifferentConditionCd");

        // Assert equals and hashCode
        assertEquals(loincCodeCondition1, loincCodeCondition2);
        assertEquals(loincCodeCondition1.hashCode(), loincCodeCondition2.hashCode());

        assertNotEquals(loincCodeCondition1, loincCodeCondition3);
        assertNotEquals(loincCodeCondition1.hashCode(), loincCodeCondition3.hashCode());
    }

    @Test
    void testToString() {
        LOINCCodeCondition loincCodeCondition = new LOINCCodeCondition();
        loincCodeCondition.setLoincCd("LoincCd");
        loincCodeCondition.setConditionCd("ConditionCd");
        loincCodeCondition.setDiseaseNm("DiseaseNm");
        loincCodeCondition.setReportedValue("ReportedValue");
        loincCodeCondition.setReportedNumericValue("ReportedNumericValue");
        loincCodeCondition.setStatusCd("A");
        loincCodeCondition.setStatusTime(new Timestamp(System.currentTimeMillis()));
        loincCodeCondition.setEffectiveFromTime(new Timestamp(System.currentTimeMillis()));
        loincCodeCondition.setEffectiveToTime(new Timestamp(System.currentTimeMillis()));

        String expectedString = "LOINCCodeCondition(loincCd=LoincCd, conditionCd=ConditionCd, diseaseNm=DiseaseNm, reportedValue=ReportedValue, reportedNumericValue=ReportedNumericValue, statusCd=A, statusTime=" + loincCodeCondition.getStatusTime() + ", effectiveFromTime=" + loincCodeCondition.getEffectiveFromTime() + ", effectiveToTime=" + loincCodeCondition.getEffectiveToTime() + ")";
        assertEquals(expectedString, loincCodeCondition.toString());
    }
}
