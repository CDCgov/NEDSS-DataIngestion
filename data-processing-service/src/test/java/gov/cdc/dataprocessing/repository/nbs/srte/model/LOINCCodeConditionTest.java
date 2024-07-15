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

}
