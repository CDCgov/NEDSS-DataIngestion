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


}
