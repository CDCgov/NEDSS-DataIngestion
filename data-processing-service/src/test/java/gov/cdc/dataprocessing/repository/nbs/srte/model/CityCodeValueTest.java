package gov.cdc.dataprocessing.repository.nbs.srte.model;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class CityCodeValueTest {

    @Test
    void testGettersAndSetters() {
        CityCodeValue cityCodeValue = new CityCodeValue();

        // Set values
        cityCodeValue.setCode("Code");
        cityCodeValue.setAssigningAuthorityCd("AssigningAuthorityCd");
        cityCodeValue.setAssigningAuthorityDescTxt("AssigningAuthorityDescTxt");
        cityCodeValue.setCodeDescTxt("CodeDescTxt");
        cityCodeValue.setCodeShortDescTxt("CodeShortDescTxt");
        cityCodeValue.setEffectiveFromTime(new Timestamp(System.currentTimeMillis()));
        cityCodeValue.setEffectiveToTime(new Timestamp(System.currentTimeMillis()));
        cityCodeValue.setExcludedTxt("ExcludedTxt");
        cityCodeValue.setIndentLevelNbr(1);
        cityCodeValue.setIsModifiableInd("IsModifiableInd");
        cityCodeValue.setParentIsCd("ParentIsCd");
        cityCodeValue.setStatusCd("StatusCd");
        cityCodeValue.setStatusTime(new Timestamp(System.currentTimeMillis()));
        cityCodeValue.setCodeSetNm("CodeSetNm");
        cityCodeValue.setSeqNum(2);
        cityCodeValue.setNbsUid(3);
        cityCodeValue.setSourceConceptId("SourceConceptId");

        // Assert values
        assertEquals("Code", cityCodeValue.getCode());
        assertEquals("AssigningAuthorityCd", cityCodeValue.getAssigningAuthorityCd());
        assertEquals("AssigningAuthorityDescTxt", cityCodeValue.getAssigningAuthorityDescTxt());
        assertEquals("CodeDescTxt", cityCodeValue.getCodeDescTxt());
        assertEquals("CodeShortDescTxt", cityCodeValue.getCodeShortDescTxt());
        assertNotNull(cityCodeValue.getEffectiveFromTime());
        assertNotNull(cityCodeValue.getEffectiveToTime());
        assertEquals("ExcludedTxt", cityCodeValue.getExcludedTxt());
        assertEquals(1, cityCodeValue.getIndentLevelNbr());
        assertEquals("IsModifiableInd", cityCodeValue.getIsModifiableInd());
        assertEquals("ParentIsCd", cityCodeValue.getParentIsCd());
        assertEquals("StatusCd", cityCodeValue.getStatusCd());
        assertNotNull(cityCodeValue.getStatusTime());
        assertEquals("CodeSetNm", cityCodeValue.getCodeSetNm());
        assertEquals(2, cityCodeValue.getSeqNum());
        assertEquals(3, cityCodeValue.getNbsUid());
        assertEquals("SourceConceptId", cityCodeValue.getSourceConceptId());
    }


}
