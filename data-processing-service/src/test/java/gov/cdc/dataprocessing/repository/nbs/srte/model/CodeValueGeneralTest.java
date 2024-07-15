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

}
