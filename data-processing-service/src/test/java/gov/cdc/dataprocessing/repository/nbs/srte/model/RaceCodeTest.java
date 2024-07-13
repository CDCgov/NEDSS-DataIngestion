package gov.cdc.dataprocessing.repository.nbs.srte.model;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class RaceCodeTest {

    @Test
    void testGettersAndSetters() {
        RaceCode raceCode = new RaceCode();

        // Set values
        raceCode.setCode("Code");
        raceCode.setAssigningAuthorityCd("AssigningAuthorityCd");
        raceCode.setAssigningAuthorityDescTxt("AssigningAuthorityDescTxt");
        raceCode.setCodeDescTxt("CodeDescTxt");
        raceCode.setCodeShortDescTxt("CodeShortDescTxt");
        raceCode.setEffectiveFromTime(new Date());
        raceCode.setEffectiveToTime(new Date());
        raceCode.setExcludedTxt("ExcludedTxt");
        raceCode.setKeyInfoTxt("KeyInfoTxt");
        raceCode.setIndentLevelNbr(1);
        raceCode.setIsModifiableInd("Y");
        raceCode.setParentIsCd("ParentIsCd");
        raceCode.setStatusCd("A");
        raceCode.setStatusTime(new Date());
        raceCode.setCodeSetNm("CodeSetNm");
        raceCode.setSeqNum(1);
        raceCode.setNbsUid(123);
        raceCode.setSourceConceptId("SourceConceptId");
        raceCode.setCodeSystemCd("CodeSystemCd");
        raceCode.setCodeSystemDescTxt("CodeSystemDescTxt");

        // Assert values
        assertEquals("Code", raceCode.getCode());
        assertEquals("AssigningAuthorityCd", raceCode.getAssigningAuthorityCd());
        assertEquals("AssigningAuthorityDescTxt", raceCode.getAssigningAuthorityDescTxt());
        assertEquals("CodeDescTxt", raceCode.getCodeDescTxt());
        assertEquals("CodeShortDescTxt", raceCode.getCodeShortDescTxt());
        assertNotNull(raceCode.getEffectiveFromTime());
        assertNotNull(raceCode.getEffectiveToTime());
        assertEquals("ExcludedTxt", raceCode.getExcludedTxt());
        assertEquals("KeyInfoTxt", raceCode.getKeyInfoTxt());
        assertEquals(1, raceCode.getIndentLevelNbr());
        assertEquals("Y", raceCode.getIsModifiableInd());
        assertEquals("ParentIsCd", raceCode.getParentIsCd());
        assertEquals("A", raceCode.getStatusCd());
        assertNotNull(raceCode.getStatusTime());
        assertEquals("CodeSetNm", raceCode.getCodeSetNm());
        assertEquals(1, raceCode.getSeqNum());
        assertEquals(123, raceCode.getNbsUid());
        assertEquals("SourceConceptId", raceCode.getSourceConceptId());
        assertEquals("CodeSystemCd", raceCode.getCodeSystemCd());
        assertEquals("CodeSystemDescTxt", raceCode.getCodeSystemDescTxt());
    }

    @Test
    void testEqualsAndHashCode() {
        RaceCode raceCode1 = new RaceCode();
        raceCode1.setCode("Code");
        raceCode1.setAssigningAuthorityCd("AssigningAuthorityCd");

        RaceCode raceCode2 = new RaceCode();
        raceCode2.setCode("Code");
        raceCode2.setAssigningAuthorityCd("AssigningAuthorityCd");

        RaceCode raceCode3 = new RaceCode();
        raceCode3.setCode("DifferentCode");
        raceCode3.setAssigningAuthorityCd("DifferentAssigningAuthorityCd");

        // Assert equals and hashCode
        assertEquals(raceCode1, raceCode2);
        assertEquals(raceCode1.hashCode(), raceCode2.hashCode());

        assertNotEquals(raceCode1, raceCode3);
        assertNotEquals(raceCode1.hashCode(), raceCode3.hashCode());
    }

    @Test
    void testToString() {
        RaceCode raceCode = new RaceCode();
        raceCode.setCode("Code");
        raceCode.setAssigningAuthorityCd("AssigningAuthorityCd");
        raceCode.setAssigningAuthorityDescTxt("AssigningAuthorityDescTxt");
        raceCode.setCodeDescTxt("CodeDescTxt");
        raceCode.setCodeShortDescTxt("CodeShortDescTxt");
        raceCode.setEffectiveFromTime(new Date());
        raceCode.setEffectiveToTime(new Date());
        raceCode.setExcludedTxt("ExcludedTxt");
        raceCode.setKeyInfoTxt("KeyInfoTxt");
        raceCode.setIndentLevelNbr(1);
        raceCode.setIsModifiableInd("Y");
        raceCode.setParentIsCd("ParentIsCd");
        raceCode.setStatusCd("A");
        raceCode.setStatusTime(new Date());
        raceCode.setCodeSetNm("CodeSetNm");
        raceCode.setSeqNum(1);
        raceCode.setNbsUid(123);
        raceCode.setSourceConceptId("SourceConceptId");
        raceCode.setCodeSystemCd("CodeSystemCd");
        raceCode.setCodeSystemDescTxt("CodeSystemDescTxt");

        String expectedString = "RaceCode(code=Code, assigningAuthorityCd=AssigningAuthorityCd, assigningAuthorityDescTxt=AssigningAuthorityDescTxt, codeDescTxt=CodeDescTxt, codeShortDescTxt=CodeShortDescTxt, effectiveFromTime=" + raceCode.getEffectiveFromTime() + ", effectiveToTime=" + raceCode.getEffectiveToTime() + ", excludedTxt=ExcludedTxt, keyInfoTxt=KeyInfoTxt, indentLevelNbr=1, isModifiableInd=Y, parentIsCd=ParentIsCd, statusCd=A, statusTime=" + raceCode.getStatusTime() + ", codeSetNm=CodeSetNm, seqNum=1, nbsUid=123, sourceConceptId=SourceConceptId, codeSystemCd=CodeSystemCd, codeSystemDescTxt=CodeSystemDescTxt)";
        assertEquals(expectedString, raceCode.toString());
    }
}
