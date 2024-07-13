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

    @Test
    void testEqualsAndHashCode() {
        LabTest labTest1 = new LabTest();
        labTest1.setLabTestCd("LabTestCd");
        labTest1.setLaboratoryId("LaboratoryId");

        LabTest labTest2 = new LabTest();
        labTest2.setLabTestCd("LabTestCd");
        labTest2.setLaboratoryId("LaboratoryId");

        LabTest labTest3 = new LabTest();
        labTest3.setLabTestCd("DifferentLabTestCd");
        labTest3.setLaboratoryId("DifferentLaboratoryId");

        // Assert equals and hashCode
        assertEquals(labTest1, labTest2);
        assertEquals(labTest1.hashCode(), labTest2.hashCode());

        assertNotEquals(labTest1, labTest3);
        assertNotEquals(labTest1.hashCode(), labTest3.hashCode());
    }

    @Test
    void testToString() {
        LabTest labTest = new LabTest();
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

        String expectedString = "LabTest(labTestCd=LabTestCd, laboratoryId=LaboratoryId, labResultDescTxt=LabResultDescTxt, testTypeCd=TestTypeCd, nbsUid=123, effectiveFromTime=" + labTest.getEffectiveFromTime() + ", effectiveToTime=" + labTest.getEffectiveToTime() + ", defaultProgAreaCd=DefaultProgAreaCd, defaultConditionCd=DefaultConditionCd, drugTestInd=Y, organismResultTestInd=N, indentLevelNbr=1, paDerivationExcludeCd=N)";
        assertEquals(expectedString, labTest.toString());
    }
}
