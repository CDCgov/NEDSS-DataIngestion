package gov.cdc.dataprocessing.repository.nbs.srte.model;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class StateCodeTest {

    @Test
    void testGettersAndSetters() {
        StateCode stateCode = new StateCode();

        Date currentDate = new Date();

        // Set values
        stateCode.setStateCd("CA");
        stateCode.setAssigningAuthorityCd("AuthCd");
        stateCode.setAssigningAuthorityDescTxt("AuthorityDesc");
        stateCode.setStateNm("California");
        stateCode.setCodeDescTxt("CodeDesc");
        stateCode.setEffectiveFromTime(currentDate);
        stateCode.setEffectiveToTime(currentDate);
        stateCode.setExcludedTxt("Excluded");
        stateCode.setIndentLevelNbr((short) 1);
        stateCode.setIsModifiableInd('Y');
        stateCode.setKeyInfoTxt("KeyInfo");
        stateCode.setParentIsCd("ParentCd");
        stateCode.setStatusCd('A');
        stateCode.setStatusTime(currentDate);
        stateCode.setCodeSetNm("CodeSetNm");
        stateCode.setSeqNum((short) 1);
        stateCode.setNbsUid(123);
        stateCode.setSourceConceptId("SourceConceptId");
        stateCode.setCodeSystemCd("CodeSystemCd");
        stateCode.setCodeSystemDescTxt("CodeSystemDesc");

        // Assert values
        assertEquals("CA", stateCode.getStateCd());
        assertEquals("AuthCd", stateCode.getAssigningAuthorityCd());
        assertEquals("AuthorityDesc", stateCode.getAssigningAuthorityDescTxt());
        assertEquals("California", stateCode.getStateNm());
        assertEquals("CodeDesc", stateCode.getCodeDescTxt());
        assertEquals(currentDate, stateCode.getEffectiveFromTime());
        assertEquals(currentDate, stateCode.getEffectiveToTime());
        assertEquals("Excluded", stateCode.getExcludedTxt());
        assertEquals((short) 1, stateCode.getIndentLevelNbr());
        assertEquals('Y', stateCode.getIsModifiableInd());
        assertEquals("KeyInfo", stateCode.getKeyInfoTxt());
        assertEquals("ParentCd", stateCode.getParentIsCd());
        assertEquals('A', stateCode.getStatusCd());
        assertEquals(currentDate, stateCode.getStatusTime());
        assertEquals("CodeSetNm", stateCode.getCodeSetNm());
        assertEquals((short) 1, stateCode.getSeqNum());
        assertEquals(123, stateCode.getNbsUid());
        assertEquals("SourceConceptId", stateCode.getSourceConceptId());
        assertEquals("CodeSystemCd", stateCode.getCodeSystemCd());
        assertEquals("CodeSystemDesc", stateCode.getCodeSystemDescTxt());
    }

}
