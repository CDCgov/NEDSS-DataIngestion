package gov.cdc.dataprocessing.service.model.wds;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WdsValueNumericReportTest {

    @Test
    void testSettersAndGetters() {
        WdsValueNumericReport report = new WdsValueNumericReport();

        String codeType = "OBS_NUMERIC_VALUE";
        String inputCode1 = "10.0";
        String inputCode2 = "20.0";
        String operator = "EQUAL";
        String wdsCode = "15.0";
        boolean matchedFound = true;

        report.setCodeType(codeType);
        report.setInputCode1(inputCode1);
        report.setInputCode2(inputCode2);
        report.setOperator(operator);
        report.setWdsCode(wdsCode);
        report.setMatchedFound(matchedFound);

        assertEquals(codeType, report.getCodeType());
        assertEquals(inputCode1, report.getInputCode1());
        assertEquals(inputCode2, report.getInputCode2());
        assertEquals(operator, report.getOperator());
        assertEquals(wdsCode, report.getWdsCode());
        assertTrue(report.isMatchedFound());
    }

    @Test
    void testDefaultConstructor() {
        WdsValueNumericReport report = new WdsValueNumericReport();

        assertNull(report.getCodeType());
        assertNull(report.getInputCode1());
        assertNull(report.getInputCode2());
        assertNull(report.getOperator());
        assertNull(report.getWdsCode());
        assertFalse(report.isMatchedFound());
    }



}
