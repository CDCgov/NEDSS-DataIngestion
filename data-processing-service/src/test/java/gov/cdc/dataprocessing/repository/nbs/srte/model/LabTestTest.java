package gov.cdc.dataprocessing.repository.nbs.srte.model;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class LabTestTest {

    @Test
    void testGettersAndSetters() {
        LabTest labTest = new LabTest();

        // Set values
        labTest.setLabTestCd("LabTestCd");
        labTest.setLaboratoryId("LaboratoryId");
        labTest.setLabResultDescTxt("LabResultDescTxt");
        labTest.setTestTypeCd("TestTypeCd");
        labTest.setNbsUid(123L);
        labTest.setEffectiveFromTime(new Timestamp(System.currentTimeMillis()));
        labTest.setEffectiveToTime(new Timestamp(System.currentTimeMillis()));
        labTest.setDefaultProgAreaCd("DefaultProgAreaCd");
        labTest.setDefaultConditionCd("DefaultConditionCd");
        labTest.setDrugTestInd("Y");
        labTest.setOrganismResultTestInd("N");
        labTest.setIndentLevelNbr(1);
        labTest.setPaDerivationExcludeCd("N");

        // Assert values
        assertEquals("LabTestCd", labTest.getLabTestCd());
        assertEquals("LaboratoryId", labTest.getLaboratoryId());
        assertEquals("LabResultDescTxt", labTest.getLabResultDescTxt());
        assertEquals("TestTypeCd", labTest.getTestTypeCd());
        assertEquals(123L, labTest.getNbsUid());
        assertNotNull(labTest.getEffectiveFromTime());
        assertNotNull(labTest.getEffectiveToTime());
        assertEquals("DefaultProgAreaCd", labTest.getDefaultProgAreaCd());
        assertEquals("DefaultConditionCd", labTest.getDefaultConditionCd());
        assertEquals("Y", labTest.getDrugTestInd());
        assertEquals("N", labTest.getOrganismResultTestInd());
        assertEquals(1, labTest.getIndentLevelNbr());
        assertEquals("N", labTest.getPaDerivationExcludeCd());
    }



}
