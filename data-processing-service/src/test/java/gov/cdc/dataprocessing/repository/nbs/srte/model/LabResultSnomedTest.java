package gov.cdc.dataprocessing.repository.nbs.srte.model;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class LabResultSnomedTest {

    @Test
    void testGettersAndSetters() {
        LabResultSnomed labResultSnomed = new LabResultSnomed();

        // Set values
        labResultSnomed.setLabResultCd("LabResultCd");
        labResultSnomed.setLaboratoryId("LaboratoryId");
        labResultSnomed.setSnomedCd("SnomedCd");
        labResultSnomed.setEffectiveFromTime(new Timestamp(System.currentTimeMillis()));
        labResultSnomed.setEffectiveToTime(new Timestamp(System.currentTimeMillis()));
        labResultSnomed.setStatusCd('A');
        labResultSnomed.setStatusTime(new Timestamp(System.currentTimeMillis()));

        // Assert values
        assertEquals("LabResultCd", labResultSnomed.getLabResultCd());
        assertEquals("LaboratoryId", labResultSnomed.getLaboratoryId());
        assertEquals("SnomedCd", labResultSnomed.getSnomedCd());
        assertNotNull(labResultSnomed.getEffectiveFromTime());
        assertNotNull(labResultSnomed.getEffectiveToTime());
        assertEquals('A', labResultSnomed.getStatusCd());
        assertNotNull(labResultSnomed.getStatusTime());
    }

    @Test
    void testEqualsAndHashCode() {
        LabResultSnomed labResultSnomed1 = new LabResultSnomed();
        labResultSnomed1.setLabResultCd("LabResultCd");
        labResultSnomed1.setLaboratoryId("LaboratoryId");

        LabResultSnomed labResultSnomed2 = new LabResultSnomed();
        labResultSnomed2.setLabResultCd("LabResultCd");
        labResultSnomed2.setLaboratoryId("LaboratoryId");

        LabResultSnomed labResultSnomed3 = new LabResultSnomed();
        labResultSnomed3.setLabResultCd("DifferentLabResultCd");
        labResultSnomed3.setLaboratoryId("DifferentLaboratoryId");

        // Assert equals and hashCode
        assertEquals(labResultSnomed1, labResultSnomed2);
        assertEquals(labResultSnomed1.hashCode(), labResultSnomed2.hashCode());

        assertNotEquals(labResultSnomed1, labResultSnomed3);
        assertNotEquals(labResultSnomed1.hashCode(), labResultSnomed3.hashCode());
    }

    @Test
    void testToString() {
        LabResultSnomed labResultSnomed = new LabResultSnomed();
        labResultSnomed.setLabResultCd("LabResultCd");
        labResultSnomed.setLaboratoryId("LaboratoryId");
        labResultSnomed.setSnomedCd("SnomedCd");
        labResultSnomed.setEffectiveFromTime(new Timestamp(System.currentTimeMillis()));
        labResultSnomed.setEffectiveToTime(new Timestamp(System.currentTimeMillis()));
        labResultSnomed.setStatusCd('A');
        labResultSnomed.setStatusTime(new Timestamp(System.currentTimeMillis()));

        String expectedString = "LabResultSnomed(labResultCd=LabResultCd, laboratoryId=LaboratoryId, snomedCd=SnomedCd, effectiveFromTime=" + labResultSnomed.getEffectiveFromTime() + ", effectiveToTime=" + labResultSnomed.getEffectiveToTime() + ", statusCd=A, statusTime=" + labResultSnomed.getStatusTime() + ")";
        assertEquals(expectedString, labResultSnomed.toString());
    }
}
