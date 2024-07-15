package gov.cdc.dataprocessing.repository.nbs.srte.model;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class JurisdictionCodeTest {

    @Test
    void testGettersAndSetters() {
        JurisdictionCode jurisdictionCode = new JurisdictionCode();

        // Set values
        jurisdictionCode.setCode("Code");
        jurisdictionCode.setTypeCd("TypeCd");
        jurisdictionCode.setAssigningAuthorityCd("AssigningAuthorityCd");
        jurisdictionCode.setAssigningAuthorityDescTxt("AssigningAuthorityDescTxt");
        jurisdictionCode.setCodeDescTxt("CodeDescTxt");
        jurisdictionCode.setCodeShortDescTxt("CodeShortDescTxt");
        jurisdictionCode.setEffectiveFromTime(new Timestamp(System.currentTimeMillis()));
        jurisdictionCode.setEffectiveToTime(new Timestamp(System.currentTimeMillis()));
        jurisdictionCode.setIndentLevelNbr(1);
        jurisdictionCode.setIsModifiableInd("Y");
        jurisdictionCode.setParentIsCd("ParentIsCd");
        jurisdictionCode.setStateDomainCd("StateDomainCd");
        jurisdictionCode.setStatusCd("A");
        jurisdictionCode.setStatusTime(new Timestamp(System.currentTimeMillis()));
        jurisdictionCode.setCodeSetNm("CodeSetNm");
        jurisdictionCode.setCodeSeqNum(2);
        jurisdictionCode.setNbsUid(123);
        jurisdictionCode.setSourceConceptId("SourceConceptId");
        jurisdictionCode.setCodeSystemCd("CodeSystemCd");
        jurisdictionCode.setCodeSystemDescTxt("CodeSystemDescTxt");
        jurisdictionCode.setExportInd("Y");

        // Assert values
        assertEquals("Code", jurisdictionCode.getCode());
        assertEquals("TypeCd", jurisdictionCode.getTypeCd());
        assertEquals("AssigningAuthorityCd", jurisdictionCode.getAssigningAuthorityCd());
        assertEquals("AssigningAuthorityDescTxt", jurisdictionCode.getAssigningAuthorityDescTxt());
        assertEquals("CodeDescTxt", jurisdictionCode.getCodeDescTxt());
        assertEquals("CodeShortDescTxt", jurisdictionCode.getCodeShortDescTxt());
        assertNotNull(jurisdictionCode.getEffectiveFromTime());
        assertNotNull(jurisdictionCode.getEffectiveToTime());
        assertEquals(1, jurisdictionCode.getIndentLevelNbr());
        assertEquals("Y", jurisdictionCode.getIsModifiableInd());
        assertEquals("ParentIsCd", jurisdictionCode.getParentIsCd());
        assertEquals("StateDomainCd", jurisdictionCode.getStateDomainCd());
        assertEquals("A", jurisdictionCode.getStatusCd());
        assertNotNull(jurisdictionCode.getStatusTime());
        assertEquals("CodeSetNm", jurisdictionCode.getCodeSetNm());
        assertEquals(2, jurisdictionCode.getCodeSeqNum());
        assertEquals(123, jurisdictionCode.getNbsUid());
        assertEquals("SourceConceptId", jurisdictionCode.getSourceConceptId());
        assertEquals("CodeSystemCd", jurisdictionCode.getCodeSystemCd());
        assertEquals("CodeSystemDescTxt", jurisdictionCode.getCodeSystemDescTxt());
        assertEquals("Y", jurisdictionCode.getExportInd());
    }


}
