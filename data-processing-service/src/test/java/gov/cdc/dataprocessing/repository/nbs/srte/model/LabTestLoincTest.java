package gov.cdc.dataprocessing.repository.nbs.srte.model;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class LabTestLoincTest {

    @Test
    void testGettersAndSetters() {
        LabTestLoinc labTestLoinc = new LabTestLoinc();

        // Set values
        labTestLoinc.setLabTestCd("LabTestCd");
        labTestLoinc.setLaboratoryId("LaboratoryId");
        labTestLoinc.setLoincCd("LoincCd");
        labTestLoinc.setEffectiveFromTime(new Timestamp(System.currentTimeMillis()));
        labTestLoinc.setEffectiveToTime(new Timestamp(System.currentTimeMillis()));
        labTestLoinc.setStatusCd('A');
        labTestLoinc.setStatusTime(new Timestamp(System.currentTimeMillis()));

        // Assert values
        assertEquals("LabTestCd", labTestLoinc.getLabTestCd());
        assertEquals("LaboratoryId", labTestLoinc.getLaboratoryId());
        assertEquals("LoincCd", labTestLoinc.getLoincCd());
        assertNotNull(labTestLoinc.getEffectiveFromTime());
        assertNotNull(labTestLoinc.getEffectiveToTime());
        assertEquals('A', labTestLoinc.getStatusCd());
        assertNotNull(labTestLoinc.getStatusTime());
    }

    @Test
    void testEqualsAndHashCode() {
        LabTestLoinc labTestLoinc1 = new LabTestLoinc();
        labTestLoinc1.setLabTestCd("LabTestCd");
        labTestLoinc1.setLaboratoryId("LaboratoryId");
        labTestLoinc1.setLoincCd("LoincCd");

        LabTestLoinc labTestLoinc2 = new LabTestLoinc();
        labTestLoinc2.setLabTestCd("LabTestCd");
        labTestLoinc2.setLaboratoryId("LaboratoryId");
        labTestLoinc2.setLoincCd("LoincCd");

        LabTestLoinc labTestLoinc3 = new LabTestLoinc();
        labTestLoinc3.setLabTestCd("DifferentLabTestCd");
        labTestLoinc3.setLaboratoryId("DifferentLaboratoryId");
        labTestLoinc3.setLoincCd("DifferentLoincCd");

        // Assert equals and hashCode
        assertEquals(labTestLoinc1, labTestLoinc2);
        assertEquals(labTestLoinc1.hashCode(), labTestLoinc2.hashCode());

        assertNotEquals(labTestLoinc1, labTestLoinc3);
        assertNotEquals(labTestLoinc1.hashCode(), labTestLoinc3.hashCode());
    }

    @Test
    void testToString() {
        LabTestLoinc labTestLoinc = new LabTestLoinc();
        labTestLoinc.setLabTestCd("LabTestCd");
        labTestLoinc.setLaboratoryId("LaboratoryId");
        labTestLoinc.setLoincCd("LoincCd");
        labTestLoinc.setEffectiveFromTime(new Timestamp(System.currentTimeMillis()));
        labTestLoinc.setEffectiveToTime(new Timestamp(System.currentTimeMillis()));
        labTestLoinc.setStatusCd('A');
        labTestLoinc.setStatusTime(new Timestamp(System.currentTimeMillis()));

        String expectedString = "LabTestLoinc(labTestCd=LabTestCd, laboratoryId=LaboratoryId, loincCd=LoincCd, effectiveFromTime=" + labTestLoinc.getEffectiveFromTime() + ", effectiveToTime=" + labTestLoinc.getEffectiveToTime() + ", statusCd=A, statusTime=" + labTestLoinc.getStatusTime() + ")";
        assertEquals(expectedString, labTestLoinc.toString());
    }
}
