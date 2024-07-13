package gov.cdc.dataprocessing.service.model.wds;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WdsValueTextReportTest {

    @Test
    void testSettersAndGetters() {
        WdsValueTextReport report = new WdsValueTextReport();

        String codeType = "OBS_VALUE_TEXT";
        String inputCode = "Sample Input Code";
        String wdsCode = "Sample WDS Code";
        boolean matchedFound = true;

        report.setCodeType(codeType);
        report.setInputCode(inputCode);
        report.setWdsCode(wdsCode);
        report.setMatchedFound(matchedFound);

        assertEquals(codeType, report.getCodeType());
        assertEquals(inputCode, report.getInputCode());
        assertEquals(wdsCode, report.getWdsCode());
        assertTrue(report.isMatchedFound());
    }

    @Test
    void testDefaultConstructor() {
        WdsValueTextReport report = new WdsValueTextReport();

        assertNull(report.getCodeType());
        assertNull(report.getInputCode());
        assertNull(report.getWdsCode());
        assertFalse(report.isMatchedFound());
    }


}
