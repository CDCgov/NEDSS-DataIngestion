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

}
