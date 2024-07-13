package gov.cdc.dataprocessing.repository.nbs.srte.model;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void testEqualsAndHashCode() {
        StateCountyCodeValue stateCountyCodeValue1 = new StateCountyCodeValue();
        stateCountyCodeValue1.setCode("001");
        stateCountyCodeValue1.setAssigningAuthorityCd("AuthCd");

        StateCountyCodeValue stateCountyCodeValue2 = new StateCountyCodeValue();
        stateCountyCodeValue2.setCode("001");
        stateCountyCodeValue2.setAssigningAuthorityCd("AuthCd");

        StateCountyCodeValue stateCountyCodeValue3 = new StateCountyCodeValue();
        stateCountyCodeValue3.setCode("002");
        stateCountyCodeValue3.setAssigningAuthorityCd("DiffAuthCd");

        // Assert equals and hashCode
        assertEquals(stateCountyCodeValue1, stateCountyCodeValue2);
        assertEquals(stateCountyCodeValue1.hashCode(), stateCountyCodeValue2.hashCode());

        assertNotEquals(stateCountyCodeValue1, stateCountyCodeValue3);
        assertNotEquals(stateCountyCodeValue1.hashCode(), stateCountyCodeValue3.hashCode());
    }

    @Test
    void testToString() {
        StateCountyCodeValue stateCountyCodeValue = new StateCountyCodeValue();
        stateCountyCodeValue.setCode("001");
        stateCountyCodeValue.setAssigningAuthorityCd("AuthCd");
        stateCountyCodeValue.setAssigningAuthorityDescTxt("AuthorityDesc");
        stateCountyCodeValue.setCodeDescTxt("CodeDesc");
        stateCountyCodeValue.setCodeShortDescTxt("ShortDesc");
        stateCountyCodeValue.setEffectiveFromTime(new Date());
        stateCountyCodeValue.setEffectiveToTime(new Date());
        stateCountyCodeValue.setExcludedTxt("Excluded");
        stateCountyCodeValue.setIndentLevelNbr((short) 1);
        stateCountyCodeValue.setIsModifiableInd('Y');
        stateCountyCodeValue.setParentIsCd("ParentCd");
        stateCountyCodeValue.setStatusCd("A");
        stateCountyCodeValue.setStatusTime(new Date());
        stateCountyCodeValue.setCodeSetNm("CodeSetNm");
        stateCountyCodeValue.setSeqNum((short) 1);
        stateCountyCodeValue.setNbsUid(123);
        stateCountyCodeValue.setSourceConceptId("SourceConceptId");
        stateCountyCodeValue.setCodeSystemCd("CodeSystemCd");
        stateCountyCodeValue.setCodeSystemDescTxt("CodeSystemDesc");

        String expectedString = "StateCountyCodeValue(code=001, assigningAuthorityCd=AuthCd, assigningAuthorityDescTxt=AuthorityDesc, codeDescTxt=CodeDesc, codeShortDescTxt=ShortDesc, effectiveFromTime=" + stateCountyCodeValue.getEffectiveFromTime() + ", effectiveToTime=" + stateCountyCodeValue.getEffectiveToTime() + ", excludedTxt=Excluded, indentLevelNbr=1, isModifiableInd=Y, parentIsCd=ParentCd, statusCd=A, statusTime=" + stateCountyCodeValue.getStatusTime() + ", codeSetNm=CodeSetNm, seqNum=1, nbsUid=123, sourceConceptId=SourceConceptId, codeSystemCd=CodeSystemCd, codeSystemDescTxt=CodeSystemDesc)";
        assertEquals(expectedString, stateCountyCodeValue.toString());
    }
}
