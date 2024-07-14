package gov.cdc.dataprocessing.service.model.wds;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WdsValueCodedReportTest {

    @Test
    void testSettersAndGetters() {
        WdsValueCodedReport report = new WdsValueCodedReport();

        String codeType = "OBS_VALUE_CODED";
        String inputCode = "12345";
        String wdsCode = "54321";
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
        WdsValueCodedReport report = new WdsValueCodedReport();

        assertNull(report.getCodeType());
        assertNull(report.getInputCode());
        assertNull(report.getWdsCode());
        assertFalse(report.isMatchedFound());
    }


}
