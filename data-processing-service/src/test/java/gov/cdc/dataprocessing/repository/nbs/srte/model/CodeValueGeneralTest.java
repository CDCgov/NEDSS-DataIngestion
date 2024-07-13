package gov.cdc.dataprocessing.repository.nbs.srte.model;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class CodeValueGeneralTest {

    @Test
    void testGettersAndSetters() {
        CodeValueGeneral codeValueGeneral = new CodeValueGeneral();

        // Set values
        codeValueGeneral.setCodeSetNm("CodeSetNm");
        codeValueGeneral.setCode("Code");
        codeValueGeneral.setCodeDescTxt("CodeDescTxt");
        codeValueGeneral.setCodeShortDescTxt("CodeShortDescTxt");
        codeValueGeneral.setCodeSystemCd("CodeSystemCd");
        codeValueGeneral.setCodeSystemDescTxt("CodeSystemDescTxt");
        codeValueGeneral.setEffectiveFromTime(new Date());
        codeValueGeneral.setEffectiveToTime(new Date());
        codeValueGeneral.setIndentLevelNbr((short) 1);
        codeValueGeneral.setIsModifiableInd('Y');
        codeValueGeneral.setNbsUid(123);
        codeValueGeneral.setParentIsCd("ParentIsCd");
        codeValueGeneral.setSourceConceptId("SourceConceptId");
        codeValueGeneral.setSuperCodeSetNm("SuperCodeSetNm");
        codeValueGeneral.setSuperCode("SuperCode");
        codeValueGeneral.setStatusCd('A');
        codeValueGeneral.setStatusTime(new Date());
        codeValueGeneral.setConceptTypeCd("ConceptTypeCd");
        codeValueGeneral.setConceptCode("ConceptCode");
        codeValueGeneral.setConceptNm("ConceptNm");
        codeValueGeneral.setConceptPreferredNm("ConceptPreferredNm");
        codeValueGeneral.setConceptStatusCd("ConceptStatusCd");
        codeValueGeneral.setConceptStatusTime(new Date());
        codeValueGeneral.setCodeSystemVersionNbr("CodeSystemVersionNbr");
        codeValueGeneral.setConceptOrderNbr(456);
        codeValueGeneral.setAdminComments("AdminComments");
        codeValueGeneral.setAddTime(new Date());
        codeValueGeneral.setAddUserId(789L);

        // Assert values
        assertEquals("CodeSetNm", codeValueGeneral.getCodeSetNm());
        assertEquals("Code", codeValueGeneral.getCode());
        assertEquals("CodeDescTxt", codeValueGeneral.getCodeDescTxt());
        assertEquals("CodeShortDescTxt", codeValueGeneral.getCodeShortDescTxt());
        assertEquals("CodeSystemCd", codeValueGeneral.getCodeSystemCd());
        assertEquals("CodeSystemDescTxt", codeValueGeneral.getCodeSystemDescTxt());
        assertNotNull(codeValueGeneral.getEffectiveFromTime());
        assertNotNull(codeValueGeneral.getEffectiveToTime());
        assertEquals((short) 1, codeValueGeneral.getIndentLevelNbr());
        assertEquals('Y', codeValueGeneral.getIsModifiableInd());
        assertEquals(123, codeValueGeneral.getNbsUid());
        assertEquals("ParentIsCd", codeValueGeneral.getParentIsCd());
        assertEquals("SourceConceptId", codeValueGeneral.getSourceConceptId());
        assertEquals("SuperCodeSetNm", codeValueGeneral.getSuperCodeSetNm());
        assertEquals("SuperCode", codeValueGeneral.getSuperCode());
        assertEquals('A', codeValueGeneral.getStatusCd());
        assertNotNull(codeValueGeneral.getStatusTime());
        assertEquals("ConceptTypeCd", codeValueGeneral.getConceptTypeCd());
        assertEquals("ConceptCode", codeValueGeneral.getConceptCode());
        assertEquals("ConceptNm", codeValueGeneral.getConceptNm());
        assertEquals("ConceptPreferredNm", codeValueGeneral.getConceptPreferredNm());
        assertEquals("ConceptStatusCd", codeValueGeneral.getConceptStatusCd());
        assertNotNull(codeValueGeneral.getConceptStatusTime());
        assertEquals("CodeSystemVersionNbr", codeValueGeneral.getCodeSystemVersionNbr());
        assertEquals(456, codeValueGeneral.getConceptOrderNbr());
        assertEquals("AdminComments", codeValueGeneral.getAdminComments());
        assertNotNull(codeValueGeneral.getAddTime());
        assertEquals(789L, codeValueGeneral.getAddUserId());
    }

    @Test
    void testEqualsAndHashCode() {
        CodeValueGeneral codeValueGeneral1 = new CodeValueGeneral();
        codeValueGeneral1.setCodeSetNm("CodeSetNm");
        codeValueGeneral1.setCode("Code");

        CodeValueGeneral codeValueGeneral2 = new CodeValueGeneral();
        codeValueGeneral2.setCodeSetNm("CodeSetNm");
        codeValueGeneral2.setCode("Code");

        CodeValueGeneral codeValueGeneral3 = new CodeValueGeneral();
        codeValueGeneral3.setCodeSetNm("DifferentCodeSetNm");
        codeValueGeneral3.setCode("DifferentCode");

        // Assert equals and hashCode
        assertEquals(codeValueGeneral1, codeValueGeneral2);
        assertEquals(codeValueGeneral1.hashCode(), codeValueGeneral2.hashCode());

        assertNotEquals(codeValueGeneral1, codeValueGeneral3);
        assertNotEquals(codeValueGeneral1.hashCode(), codeValueGeneral3.hashCode());
    }

    @Test
    void testToString() {
        CodeValueGeneral codeValueGeneral = new CodeValueGeneral();
        codeValueGeneral.setCodeSetNm("CodeSetNm");
        codeValueGeneral.setCode("Code");
        codeValueGeneral.setCodeDescTxt("CodeDescTxt");
        codeValueGeneral.setCodeShortDescTxt("CodeShortDescTxt");
        codeValueGeneral.setCodeSystemCd("CodeSystemCd");
        codeValueGeneral.setCodeSystemDescTxt("CodeSystemDescTxt");
        codeValueGeneral.setEffectiveFromTime(new Date());
        codeValueGeneral.setEffectiveToTime(new Date());
        codeValueGeneral.setIndentLevelNbr((short) 1);
        codeValueGeneral.setIsModifiableInd('Y');
        codeValueGeneral.setNbsUid(123);
        codeValueGeneral.setParentIsCd("ParentIsCd");
        codeValueGeneral.setSourceConceptId("SourceConceptId");
        codeValueGeneral.setSuperCodeSetNm("SuperCodeSetNm");
        codeValueGeneral.setSuperCode("SuperCode");
        codeValueGeneral.setStatusCd('A');
        codeValueGeneral.setStatusTime(new Date());
        codeValueGeneral.setConceptTypeCd("ConceptTypeCd");
        codeValueGeneral.setConceptCode("ConceptCode");
        codeValueGeneral.setConceptNm("ConceptNm");
        codeValueGeneral.setConceptPreferredNm("ConceptPreferredNm");
        codeValueGeneral.setConceptStatusCd("ConceptStatusCd");
        codeValueGeneral.setConceptStatusTime(new Date());
        codeValueGeneral.setCodeSystemVersionNbr("CodeSystemVersionNbr");
        codeValueGeneral.setConceptOrderNbr(456);
        codeValueGeneral.setAdminComments("AdminComments");
        codeValueGeneral.setAddTime(new Date());
        codeValueGeneral.setAddUserId(789L);

        String expectedString = "CodeValueGeneral(codeSetNm=CodeSetNm, code=Code, codeDescTxt=CodeDescTxt, codeShortDescTxt=CodeShortDescTxt, codeSystemCd=CodeSystemCd, codeSystemDescTxt=CodeSystemDescTxt, effectiveFromTime=" + codeValueGeneral.getEffectiveFromTime() + ", effectiveToTime=" + codeValueGeneral.getEffectiveToTime() + ", indentLevelNbr=1, isModifiableInd=Y, nbsUid=123, parentIsCd=ParentIsCd, sourceConceptId=SourceConceptId, superCodeSetNm=SuperCodeSetNm, superCode=SuperCode, statusCd=A, statusTime=" + codeValueGeneral.getStatusTime() + ", conceptTypeCd=ConceptTypeCd, conceptCode=ConceptCode, conceptNm=ConceptNm, conceptPreferredNm=ConceptPreferredNm, conceptStatusCd=ConceptStatusCd, conceptStatusTime=" + codeValueGeneral.getConceptStatusTime() + ", codeSystemVersionNbr=CodeSystemVersionNbr, conceptOrderNbr=456, adminComments=AdminComments, addTime=" + codeValueGeneral.getAddTime() + ", addUserId=789)";
        assertEquals(expectedString, codeValueGeneral.toString());
    }
}
