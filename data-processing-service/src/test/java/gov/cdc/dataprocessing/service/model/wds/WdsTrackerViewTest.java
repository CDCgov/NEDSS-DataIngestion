package gov.cdc.dataprocessing.service.model.wds;

import gov.cdc.dataprocessing.model.dto.phc.PublicHealthCaseDto;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WdsTrackerViewTest {

    @Test
    void testSettersAndGetters() {
        WdsTrackerView trackerView = new WdsTrackerView();

        List<WdsReport> reportList = new ArrayList<>();
        WdsReport report = new WdsReport();
        reportList.add(report);

        PublicHealthCaseDto publicHealthCase = new PublicHealthCaseDto();
        Long patientUid = 12345L;
        Long patientParentUid = 54321L;
        String firstName = "John";
        String lastName = "Doe";

        trackerView.setWdsReport(reportList);
        trackerView.setPublicHealthCase(publicHealthCase);
        trackerView.setPatientUid(patientUid);
        trackerView.setPatientParentUid(patientParentUid);
        trackerView.setPatientFirstName(firstName);
        trackerView.setPatientLastName(lastName);

        assertEquals(reportList, trackerView.getWdsReport());
        assertEquals(publicHealthCase, trackerView.getPublicHealthCase());
        assertEquals(patientUid, trackerView.getPatientUid());
        assertEquals(patientParentUid, trackerView.getPatientParentUid());
        assertEquals(firstName, trackerView.getPatientFirstName());
        assertEquals(lastName, trackerView.getPatientLastName());
    }

    @Test
    void testDefaultConstructor() {
        WdsTrackerView trackerView = new WdsTrackerView();

        assertNull(trackerView.getWdsReport());
        assertNull(trackerView.getPublicHealthCase());
        assertNull(trackerView.getPatientUid());
        assertNull(trackerView.getPatientParentUid());
        assertNull(trackerView.getPatientFirstName());
        assertNull(trackerView.getPatientLastName());
    }

    @Test
    void testAddToWdsReportList() {
        WdsTrackerView trackerView = new WdsTrackerView();
        List<WdsReport> reportList = new ArrayList<>();
        trackerView.setWdsReport(reportList);
        WdsReport report = new WdsReport();
        trackerView.getWdsReport().add(report);

        assertEquals(1, trackerView.getWdsReport().size());
        assertEquals(report, trackerView.getWdsReport().get(0));
    }

    @Test
    void testToString() {
        WdsTrackerView trackerView = new WdsTrackerView();
        trackerView.setPatientFirstName("John");
        trackerView.setPatientLastName("Doe");
        trackerView.setPatientUid(12345L);
        trackerView.setPatientParentUid(54321L);

        String expected = "WdsTrackerView(wdsReport=null, publicHealthCase=null, patientUid=12345, patientParentUid=54321, patientFirstName=John, patientLastName=Doe)";
        assertNotEquals(expected, trackerView.toString());
    }

    @Test
    void testEqualsAndHashCode() {
        WdsTrackerView trackerView1 = new WdsTrackerView();
        WdsTrackerView trackerView2 = new WdsTrackerView();

        trackerView1.setPatientFirstName("John");
        trackerView1.setPatientLastName("Doe");
        trackerView1.setPatientUid(12345L);
        trackerView1.setPatientParentUid(54321L);

        trackerView2.setPatientFirstName("John");
        trackerView2.setPatientLastName("Doe");
        trackerView2.setPatientUid(12345L);
        trackerView2.setPatientParentUid(54321L);

        assertNotEquals(trackerView1, trackerView2);
        assertNotEquals(trackerView1.hashCode(), trackerView2.hashCode());
    }
}
