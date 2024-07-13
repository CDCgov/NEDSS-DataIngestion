package gov.cdc.dataprocessing.repository.nbs.srte.model;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void testEqualsAndHashCode() {
        StateCode stateCode1 = new StateCode();
        stateCode1.setStateCd("CA");
        stateCode1.setAssigningAuthorityCd("AuthCd");

        StateCode stateCode2 = new StateCode();
        stateCode2.setStateCd("CA");
        stateCode2.setAssigningAuthorityCd("AuthCd");

        StateCode stateCode3 = new StateCode();
        stateCode3.setStateCd("TX");
        stateCode3.setAssigningAuthorityCd("DiffAuthCd");

        // Assert equals and hashCode
        assertEquals(stateCode1, stateCode2);
        assertEquals(stateCode1.hashCode(), stateCode2.hashCode());

        assertNotEquals(stateCode1, stateCode3);
        assertNotEquals(stateCode1.hashCode(), stateCode3.hashCode());
    }

    @Test
    void testToString() {
        StateCode stateCode = new StateCode();
        stateCode.setStateCd("CA");
        stateCode.setAssigningAuthorityCd("AuthCd");
        stateCode.setAssigningAuthorityDescTxt("AuthorityDesc");
        stateCode.setStateNm("California");
        stateCode.setCodeDescTxt("CodeDesc");
        stateCode.setEffectiveFromTime(new Date());
        stateCode.setEffectiveToTime(new Date());
        stateCode.setExcludedTxt("Excluded");
        stateCode.setIndentLevelNbr((short) 1);
        stateCode.setIsModifiableInd('Y');
        stateCode.setKeyInfoTxt("KeyInfo");
        stateCode.setParentIsCd("ParentCd");
        stateCode.setStatusCd('A');
        stateCode.setStatusTime(new Date());
        stateCode.setCodeSetNm("CodeSetNm");
        stateCode.setSeqNum((short) 1);
        stateCode.setNbsUid(123);
        stateCode.setSourceConceptId("SourceConceptId");
        stateCode.setCodeSystemCd("CodeSystemCd");
        stateCode.setCodeSystemDescTxt("CodeSystemDesc");

        String expectedString = "StateCode(stateCd=CA, assigningAuthorityCd=AuthCd, assigningAuthorityDescTxt=AuthorityDesc, stateNm=California, codeDescTxt=CodeDesc, effectiveFromTime=" + stateCode.getEffectiveFromTime() + ", effectiveToTime=" + stateCode.getEffectiveToTime() + ", excludedTxt=Excluded, indentLevelNbr=1, isModifiableInd=Y, keyInfoTxt=KeyInfo, parentIsCd=ParentCd, statusCd=A, statusTime=" + stateCode.getStatusTime() + ", codeSetNm=CodeSetNm, seqNum=1, nbsUid=123, sourceConceptId=SourceConceptId, codeSystemCd=CodeSystemCd, codeSystemDescTxt=CodeSystemDesc)";
        assertEquals(expectedString, stateCode.toString());
    }
}
