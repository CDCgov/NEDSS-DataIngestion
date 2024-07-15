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



}
