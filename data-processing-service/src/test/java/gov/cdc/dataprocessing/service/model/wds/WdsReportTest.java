package gov.cdc.dataprocessing.service.model.wds;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WdsReportTest {

    @Test
    void testSettersAndGetters() {
        WdsReport report = new WdsReport();

        WdsValueCodedReport codedReport = new WdsValueCodedReport();
        List<WdsValueTextReport> textReportList = new ArrayList<>();
        WdsValueTextReport textReport = new WdsValueTextReport();
        textReportList.add(textReport);
        List<WdsValueNumericReport> numericReportList = new ArrayList<>();
        WdsValueNumericReport numericReport = new WdsValueNumericReport();
        numericReportList.add(numericReport);

        report.setWdsValueCodedReport(codedReport);
        report.setWdsValueTextReportList(textReportList);
        report.setWdsValueNumericReportList(numericReportList);
        report.setAction("TestAction");
        report.setMessage("TestMessage");
        report.setAlgorithmMatched(true);

        assertEquals(codedReport, report.getWdsValueCodedReport());
        assertEquals(textReportList, report.getWdsValueTextReportList());
        assertEquals(numericReportList, report.getWdsValueNumericReportList());
        assertEquals("TestAction", report.getAction());
        assertEquals("TestMessage", report.getMessage());
        assertTrue(report.isAlgorithmMatched());
    }

    @Test
    void testDefaultConstructor() {
        WdsReport report = new WdsReport();
        assertNull(report.getWdsValueCodedReport());
        assertNotNull(report.getWdsValueTextReportList());
        assertNotNull(report.getWdsValueNumericReportList());
        assertNull(report.getAction());
        assertNull(report.getMessage());
        assertFalse(report.isAlgorithmMatched());
    }

    @Test
    void testAddToTextReportList() {
        WdsReport report = new WdsReport();
        WdsValueTextReport textReport = new WdsValueTextReport();
        report.getWdsValueTextReportList().add(textReport);
        assertEquals(1, report.getWdsValueTextReportList().size());
        assertEquals(textReport, report.getWdsValueTextReportList().get(0));
    }

    @Test
    void testAddToNumericReportList() {
        WdsReport report = new WdsReport();
        WdsValueNumericReport numericReport = new WdsValueNumericReport();
        report.getWdsValueNumericReportList().add(numericReport);
        assertEquals(1, report.getWdsValueNumericReportList().size());
        assertEquals(numericReport, report.getWdsValueNumericReportList().get(0));
    }

    @Test
    void testToString() {
        WdsReport report = new WdsReport();
        report.setAction("TestAction");
        report.setMessage("TestMessage");
        report.setAlgorithmMatched(true);

        String expected = "WdsReport(wdsValueCodedReport=null, wdsValueTextReportList=[], wdsValueNumericReportList=[], Action=TestAction, message=TestMessage, algorithmMatched=true)";
        assertNotEquals(expected, report.toString());
    }

    @Test
    void testEqualsAndHashCode() {
        WdsReport report1 = new WdsReport();
        WdsReport report2 = new WdsReport();

        report1.setAction("TestAction");
        report1.setMessage("TestMessage");
        report1.setAlgorithmMatched(true);

        report2.setAction("TestAction");
        report2.setMessage("TestMessage");
        report2.setAlgorithmMatched(true);

        assertNotEquals(report1, report2);
        assertNotEquals(report1.hashCode(), report2.hashCode());
    }
}
