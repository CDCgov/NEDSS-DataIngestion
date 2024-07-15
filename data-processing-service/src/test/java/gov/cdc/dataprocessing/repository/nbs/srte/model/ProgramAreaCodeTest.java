package gov.cdc.dataprocessing.repository.nbs.srte.model;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class ProgramAreaCodeTest {

    @Test
    void testSettersAndGetters() {
        ProgramAreaCode programAreaCode = new ProgramAreaCode();

        String progAreaCd = "PA01";
        String progAreaDescTxt = "Program Area Description";
        Integer nbsUid = 12345;
        String statusCd = "Active";
        Timestamp statusTime = new Timestamp(System.currentTimeMillis());
        String codeSetNm = "CodeSetName";
        Integer codeSeq = 1;

        programAreaCode.setProgAreaCd(progAreaCd);
        programAreaCode.setProgAreaDescTxt(progAreaDescTxt);
        programAreaCode.setNbsUid(nbsUid);
        programAreaCode.setStatusCd(statusCd);
        programAreaCode.setStatusTime(statusTime);
        programAreaCode.setCodeSetNm(codeSetNm);
        programAreaCode.setCodeSeq(codeSeq);

        assertEquals(progAreaCd, programAreaCode.getProgAreaCd());
        assertEquals(progAreaDescTxt, programAreaCode.getProgAreaDescTxt());
        assertEquals(nbsUid, programAreaCode.getNbsUid());
        assertEquals(statusCd, programAreaCode.getStatusCd());
        assertEquals(statusTime, programAreaCode.getStatusTime());
        assertEquals(codeSetNm, programAreaCode.getCodeSetNm());
        assertEquals(codeSeq, programAreaCode.getCodeSeq());
    }

    @Test
    void testDefaultConstructor() {
        ProgramAreaCode programAreaCode = new ProgramAreaCode();

        assertNull(programAreaCode.getProgAreaCd());
        assertNull(programAreaCode.getProgAreaDescTxt());
        assertNull(programAreaCode.getNbsUid());
        assertNull(programAreaCode.getStatusCd());
        assertNull(programAreaCode.getStatusTime());
        assertNull(programAreaCode.getCodeSetNm());
        assertNull(programAreaCode.getCodeSeq());
    }
}
