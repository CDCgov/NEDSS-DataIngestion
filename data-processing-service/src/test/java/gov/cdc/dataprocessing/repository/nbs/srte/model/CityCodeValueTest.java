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

    @Test
    void testToString() {
        CityCodeValue cityCodeValue = new CityCodeValue();
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

        String expectedString = "CityCodeValue(code=Code, assigningAuthorityCd=AssigningAuthorityCd, assigningAuthorityDescTxt=AssigningAuthorityDescTxt, codeDescTxt=CodeDescTxt, codeShortDescTxt=CodeShortDescTxt, effectiveFromTime=" + cityCodeValue.getEffectiveFromTime() + ", effectiveToTime=" + cityCodeValue.getEffectiveToTime() + ", excludedTxt=ExcludedTxt, indentLevelNbr=1, isModifiableInd=IsModifiableInd, parentIsCd=ParentIsCd, statusCd=StatusCd, statusTime=" + cityCodeValue.getStatusTime() + ", codeSetNm=CodeSetNm, seqNum=2, nbsUid=3, sourceConceptId=SourceConceptId)";
        assertEquals(expectedString, cityCodeValue.toString());
    }

    @Test
    void testEqualsAndHashCode() {
        CityCodeValue cityCodeValue1 = new CityCodeValue();
        cityCodeValue1.setCode("Code");
        cityCodeValue1.setAssigningAuthorityCd("AssigningAuthorityCd");
        cityCodeValue1.setAssigningAuthorityDescTxt("AssigningAuthorityDescTxt");
        cityCodeValue1.setCodeDescTxt("CodeDescTxt");
        cityCodeValue1.setCodeShortDescTxt("CodeShortDescTxt");
        cityCodeValue1.setEffectiveFromTime(new Timestamp(System.currentTimeMillis()));
        cityCodeValue1.setEffectiveToTime(new Timestamp(System.currentTimeMillis()));
        cityCodeValue1.setExcludedTxt("ExcludedTxt");
        cityCodeValue1.setIndentLevelNbr(1);
        cityCodeValue1.setIsModifiableInd("IsModifiableInd");
        cityCodeValue1.setParentIsCd("ParentIsCd");
        cityCodeValue1.setStatusCd("StatusCd");
        cityCodeValue1.setStatusTime(new Timestamp(System.currentTimeMillis()));
        cityCodeValue1.setCodeSetNm("CodeSetNm");
        cityCodeValue1.setSeqNum(2);
        cityCodeValue1.setNbsUid(3);
        cityCodeValue1.setSourceConceptId("SourceConceptId");

        CityCodeValue cityCodeValue2 = new CityCodeValue();
        cityCodeValue2.setCode("Code");
        cityCodeValue2.setAssigningAuthorityCd("AssigningAuthorityCd");
        cityCodeValue2.setAssigningAuthorityDescTxt("AssigningAuthorityDescTxt");
        cityCodeValue2.setCodeDescTxt("CodeDescTxt");
        cityCodeValue2.setCodeShortDescTxt("CodeShortDescTxt");
        cityCodeValue2.setEffectiveFromTime(new Timestamp(System.currentTimeMillis()));
        cityCodeValue2.setEffectiveToTime(new Timestamp(System.currentTimeMillis()));
        cityCodeValue2.setExcludedTxt("ExcludedTxt");
        cityCodeValue2.setIndentLevelNbr(1);
        cityCodeValue2.setIsModifiableInd("IsModifiableInd");
        cityCodeValue2.setParentIsCd("ParentIsCd");
        cityCodeValue2.setStatusCd("StatusCd");
        cityCodeValue2.setStatusTime(new Timestamp(System.currentTimeMillis()));
        cityCodeValue2.setCodeSetNm("CodeSetNm");
        cityCodeValue2.setSeqNum(2);
        cityCodeValue2.setNbsUid(3);
        cityCodeValue2.setSourceConceptId("SourceConceptId");

        CityCodeValue cityCodeValue3 = new CityCodeValue();
        cityCodeValue3.setCode("DifferentCode");

        // Assert equals and hashCode
        assertEquals(cityCodeValue1, cityCodeValue2);
        assertEquals(cityCodeValue1.hashCode(), cityCodeValue2.hashCode());

        assertNotEquals(cityCodeValue1, cityCodeValue3);
        assertNotEquals(cityCodeValue1.hashCode(), cityCodeValue3.hashCode());
    }
}
