package gov.cdc.dataprocessing.utilities.model;

import gov.cdc.dataprocessing.utilities.model.Coded;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CodedTest {

    @Test
    void testCode() {
        Coded coded = new Coded();
        coded.setCode("testCode");
        assertEquals("testCode", coded.getCode());
    }

    @Test
    void testCodeDescription() {
        Coded coded = new Coded();
        coded.setCodeDescription("testDescription");
        assertEquals("testDescription", coded.getCodeDescription());
    }

    @Test
    void testCodeSystemCd() {
        Coded coded = new Coded();
        coded.setCodeSystemCd("testSystemCd");
        assertEquals("testSystemCd", coded.getCodeSystemCd());
    }

    @Test
    void testLocalCode() {
        Coded coded = new Coded();
        coded.setLocalCode("testLocalCode");
        assertEquals("testLocalCode", coded.getLocalCode());
    }

    @Test
    void testLocalCodeDescription() {
        Coded coded = new Coded();
        coded.setLocalCodeDescription("testLocalDescription");
        assertEquals("testLocalDescription", coded.getLocalCodeDescription());
    }

    @Test
    void testLocalCodeSystemCd() {
        Coded coded = new Coded();
        coded.setLocalCodeSystemCd("testLocalSystemCd");
        assertEquals("testLocalSystemCd", coded.getLocalCodeSystemCd());
    }

    @Test
    void testCodesetGroupId() {
        Coded coded = new Coded();
        coded.setCodesetGroupId(12345L);
        assertEquals(12345L, coded.getCodesetGroupId());
    }

    @Test
    void testCodesetName() {
        Coded coded = new Coded();
        coded.setCodesetName("testCodesetName");
        assertEquals("testCodesetName", coded.getCodesetName());
    }

    @Test
    void testCodesetTableName() {
        Coded coded = new Coded();
        coded.setCodesetTableName("testTableName");
        assertEquals("testTableName", coded.getCodesetTableName());
    }

    @Test
    void testFlagNotFound() {
        Coded coded = new Coded();
        coded.setFlagNotFound(true);
        assertTrue(coded.isFlagNotFound());
        coded.setFlagNotFound(false);
        assertFalse(coded.isFlagNotFound());
    }
}
