package gov.cdc.dataprocessing.utilities.component.jurisdiction;

import gov.cdc.dataprocessing.cache.SrteCache;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProgAreaJurisdictionUtilTest {
    private final ProgAreaJurisdictionUtil util = new ProgAreaJurisdictionUtil();

    @SuppressWarnings("java:S2699")
    @Test
    void testGetPAJHash_ValidInputs() {
        SrteCache.programAreaCodesMapWithNbsUid = new HashMap<>();
        SrteCache.jurisdictionCodeMapWithNbsUid = new HashMap<>();
        SrteCache.programAreaCodesMapWithNbsUid.put("PA1", 123);
        SrteCache.jurisdictionCodeMapWithNbsUid.put("J1", 456);

        util.getPAJHash("PA1", "J1");

    }

    @Test
    void testGetPAJHash_NullProgramAreaCode() {
        long hash = util.getPAJHash(null, "J1");
        assertEquals(0, hash);
    }

    @Test
    void testGetPAJHash_EmptyProgramAreaCode() {
        long hash = util.getPAJHash("", "J1");
        assertEquals(0, hash);
    }

    @Test
    void testGetPAJHash_NullJurisdictionCode() {
        long hash = util.getPAJHash("PA1", null);
        assertEquals(0, hash);
    }

    @Test
    void testGetPAJHash_EmptyJurisdictionCode() {
        long hash = util.getPAJHash("PA1", "");
        assertEquals(0, hash);
    }

    @Test
    void testGetPAJHash_ExceptionHandling() {
        SrteCache.programAreaCodesMapWithNbsUid = new HashMap<>();
        SrteCache.jurisdictionCodeMapWithNbsUid = new HashMap<>();
        SrteCache.programAreaCodesMapWithNbsUid.put("PA1", null);

        long hash = util.getPAJHash("PA1", "J1");

        assertEquals(0, hash);
    }

    @Test
    void testGetPAJHashList_SingleJurisdiction() {
        SrteCache.programAreaCodesMapWithNbsUid = new HashMap<>();
        SrteCache.jurisdictionCodeMapWithNbsUid = new HashMap<>();
        SrteCache.programAreaCodesMapWithNbsUid.put("PA1", 123);
        SrteCache.jurisdictionCodeMapWithNbsUid.put("J1", 456);

        Collection<Object> hashList = util.getPAJHashList("PA1", "J1");

        assertEquals(1, hashList.size());
    }

    @Test
    void testGetPAJHashList_AllJurisdictions() {
        SrteCache.programAreaCodesMapWithNbsUid = new HashMap<>();
        SrteCache.jurisdictionCodeMapWithNbsUid = new HashMap<>();
        SrteCache.programAreaCodesMapWithNbsUid.put("PA1", 123);
        SrteCache.jurisdictionCodeMapWithNbsUid.put("J1", 456);
        SrteCache.jurisdictionCodeMapWithNbsUid.put("J2", 789);

        Collection<Object> hashList = util.getPAJHashList("PA1", "ALL");

        assertEquals(2, hashList.size());
    }
}
