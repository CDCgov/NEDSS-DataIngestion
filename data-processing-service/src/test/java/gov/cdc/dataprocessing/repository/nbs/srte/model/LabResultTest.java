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


}
