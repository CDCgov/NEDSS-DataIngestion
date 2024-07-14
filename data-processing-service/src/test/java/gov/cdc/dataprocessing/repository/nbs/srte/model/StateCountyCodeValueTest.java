package gov.cdc.dataprocessing.repository.nbs.srte.model;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class StateCountyCodeValueTest {

    @Test
    void testGettersAndSetters() {
        StateCountyCodeValue stateCountyCodeValue = new StateCountyCodeValue();

        Date currentDate = new Date();

        // Set values
        stateCountyCodeValue.setCode("001");
        stateCountyCodeValue.setAssigningAuthorityCd("AuthCd");
        stateCountyCodeValue.setAssigningAuthorityDescTxt("AuthorityDesc");
        stateCountyCodeValue.setCodeDescTxt("CodeDesc");
        stateCountyCodeValue.setCodeShortDescTxt("ShortDesc");
        stateCountyCodeValue.setEffectiveFromTime(currentDate);
        stateCountyCodeValue.setEffectiveToTime(currentDate);
        stateCountyCodeValue.setExcludedTxt("Excluded");
        stateCountyCodeValue.setIndentLevelNbr((short) 1);
        stateCountyCodeValue.setIsModifiableInd('Y');
        stateCountyCodeValue.setParentIsCd("ParentCd");
        stateCountyCodeValue.setStatusCd("A");
        stateCountyCodeValue.setStatusTime(currentDate);
        stateCountyCodeValue.setCodeSetNm("CodeSetNm");
        stateCountyCodeValue.setSeqNum((short) 1);
        stateCountyCodeValue.setNbsUid(123);
        stateCountyCodeValue.setSourceConceptId("SourceConceptId");
        stateCountyCodeValue.setCodeSystemCd("CodeSystemCd");
        stateCountyCodeValue.setCodeSystemDescTxt("CodeSystemDesc");

        // Assert values
        assertEquals("001", stateCountyCodeValue.getCode());
        assertEquals("AuthCd", stateCountyCodeValue.getAssigningAuthorityCd());
        assertEquals("AuthorityDesc", stateCountyCodeValue.getAssigningAuthorityDescTxt());
        assertEquals("CodeDesc", stateCountyCodeValue.getCodeDescTxt());
        assertEquals("ShortDesc", stateCountyCodeValue.getCodeShortDescTxt());
        assertEquals(currentDate, stateCountyCodeValue.getEffectiveFromTime());
        assertEquals(currentDate, stateCountyCodeValue.getEffectiveToTime());
        assertEquals("Excluded", stateCountyCodeValue.getExcludedTxt());
        assertEquals((short) 1, stateCountyCodeValue.getIndentLevelNbr());
        assertEquals('Y', stateCountyCodeValue.getIsModifiableInd());
        assertEquals("ParentCd", stateCountyCodeValue.getParentIsCd());
        assertEquals("A", stateCountyCodeValue.getStatusCd());
        assertEquals(currentDate, stateCountyCodeValue.getStatusTime());
        assertEquals("CodeSetNm", stateCountyCodeValue.getCodeSetNm());
        assertEquals((short) 1, stateCountyCodeValue.getSeqNum());
        assertEquals(123, stateCountyCodeValue.getNbsUid());
        assertEquals("SourceConceptId", stateCountyCodeValue.getSourceConceptId());
        assertEquals("CodeSystemCd", stateCountyCodeValue.getCodeSystemCd());
        assertEquals("CodeSystemDesc", stateCountyCodeValue.getCodeSystemDescTxt());
    }


}
