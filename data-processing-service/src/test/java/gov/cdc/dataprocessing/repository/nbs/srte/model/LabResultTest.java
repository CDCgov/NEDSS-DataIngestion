package gov.cdc.dataprocessing.repository.nbs.srte.model;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class LabResultTest {

    @Test
    void testGettersAndSetters() {
        LabResult labResult = new LabResult();

        // Set values
        labResult.setLabResultCd("LabResultCd");
        labResult.setLaboratoryId("LaboratoryId");
        labResult.setLabResultDescTxt("LabResultDescTxt");
        labResult.setEffectiveFromTime(new Timestamp(System.currentTimeMillis()));
        labResult.setEffectiveToTime(new Timestamp(System.currentTimeMillis()));
        labResult.setNbsUid(123L);
        labResult.setDefaultProgAreaCd("DefaultProgAreaCd");
        labResult.setOrganismNameInd("Y");
        labResult.setDefaultConditionCd("DefaultConditionCd");
        labResult.setPaDerivationExcludeCd("N");
        labResult.setCodeSystemCd("CodeSystemCd");
        labResult.setCodeSetNm("CodeSetNm");

        // Assert values
        assertEquals("LabResultCd", labResult.getLabResultCd());
        assertEquals("LaboratoryId", labResult.getLaboratoryId());
        assertEquals("LabResultDescTxt", labResult.getLabResultDescTxt());
        assertNotNull(labResult.getEffectiveFromTime());
        assertNotNull(labResult.getEffectiveToTime());
        assertEquals(123L, labResult.getNbsUid());
        assertEquals("DefaultProgAreaCd", labResult.getDefaultProgAreaCd());
        assertEquals("Y", labResult.getOrganismNameInd());
        assertEquals("DefaultConditionCd", labResult.getDefaultConditionCd());
        assertEquals("N", labResult.getPaDerivationExcludeCd());
        assertEquals("CodeSystemCd", labResult.getCodeSystemCd());
        assertEquals("CodeSetNm", labResult.getCodeSetNm());
    }

    @Test
    void testEqualsAndHashCode() {
        LabResult labResult1 = new LabResult();
        labResult1.setLabResultCd("LabResultCd");
        labResult1.setLaboratoryId("LaboratoryId");

        LabResult labResult2 = new LabResult();
        labResult2.setLabResultCd("LabResultCd");
        labResult2.setLaboratoryId("LaboratoryId");

        LabResult labResult3 = new LabResult();
        labResult3.setLabResultCd("DifferentLabResultCd");
        labResult3.setLaboratoryId("DifferentLaboratoryId");

        // Assert equals and hashCode
        assertEquals(labResult1, labResult2);
        assertEquals(labResult1.hashCode(), labResult2.hashCode());

        assertNotEquals(labResult1, labResult3);
        assertNotEquals(labResult1.hashCode(), labResult3.hashCode());
    }

    @Test
    void testToString() {
        LabResult labResult = new LabResult();
        labResult.setLabResultCd("LabResultCd");
        labResult.setLaboratoryId("LaboratoryId");
        labResult.setLabResultDescTxt("LabResultDescTxt");
        labResult.setEffectiveFromTime(new Timestamp(System.currentTimeMillis()));
        labResult.setEffectiveToTime(new Timestamp(System.currentTimeMillis()));
        labResult.setNbsUid(123L);
        labResult.setDefaultProgAreaCd("DefaultProgAreaCd");
        labResult.setOrganismNameInd("Y");
        labResult.setDefaultConditionCd("DefaultConditionCd");
        labResult.setPaDerivationExcludeCd("N");
        labResult.setCodeSystemCd("CodeSystemCd");
        labResult.setCodeSetNm("CodeSetNm");

        String expectedString = "LabResult(labResultCd=LabResultCd, laboratoryId=LaboratoryId, labResultDescTxt=LabResultDescTxt, effectiveFromTime=" + labResult.getEffectiveFromTime() + ", effectiveToTime=" + labResult.getEffectiveToTime() + ", nbsUid=123, defaultProgAreaCd=DefaultProgAreaCd, organismNameInd=Y, defaultConditionCd=DefaultConditionCd, paDerivationExcludeCd=N, codeSystemCd=CodeSystemCd, codeSetNm=CodeSetNm)";
        assertEquals(expectedString, labResult.toString());
    }
}
