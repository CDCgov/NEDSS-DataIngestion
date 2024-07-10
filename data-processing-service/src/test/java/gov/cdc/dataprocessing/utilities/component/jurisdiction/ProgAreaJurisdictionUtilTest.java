package gov.cdc.dataprocessing.utilities.component.jurisdiction;
import java.util.*;

import gov.cdc.dataprocessing.cache.SrteCache;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

class ProgAreaJurisdictionUtilTest {
    private final ProgAreaJurisdictionUtil util = new ProgAreaJurisdictionUtil();

    @Test
    public void testGetPAJHash_ValidInputs() {
        SrteCache.programAreaCodesMapWithNbsUid = new TreeMap<>();
        SrteCache.jurisdictionCodeMapWithNbsUid = new TreeMap<>();
        SrteCache.programAreaCodesMapWithNbsUid.put("PA1", 123);
        SrteCache.jurisdictionCodeMapWithNbsUid.put("J1", 456);

        long hash = util.getPAJHash("PA1", "J1");

    }

    @Test
    public void testGetPAJHash_NullProgramAreaCode() {
        long hash = util.getPAJHash(null, "J1");
        assertEquals(0, hash);
    }

    @Test
    public void testGetPAJHash_EmptyProgramAreaCode() {
        long hash = util.getPAJHash("", "J1");
        assertEquals(0, hash);
    }

    @Test
    public void testGetPAJHash_NullJurisdictionCode() {
        long hash = util.getPAJHash("PA1", null);
        assertEquals(0, hash);
    }

    @Test
    public void testGetPAJHash_EmptyJurisdictionCode() {
        long hash = util.getPAJHash("PA1", "");
        assertEquals(0, hash);
    }

    @Test
    public void testGetPAJHash_ExceptionHandling() {
        SrteCache.programAreaCodesMapWithNbsUid = new TreeMap<>();
        SrteCache.jurisdictionCodeMapWithNbsUid = new TreeMap<>();
        SrteCache.programAreaCodesMapWithNbsUid.put("PA1", null);

        long hash = util.getPAJHash("PA1", "J1");

        assertEquals(0, hash);
    }

    @Test
    public void testGetPAJHashList_SingleJurisdiction() {
        SrteCache.programAreaCodesMapWithNbsUid = new TreeMap<>();
        SrteCache.jurisdictionCodeMapWithNbsUid = new TreeMap<>();
        SrteCache.programAreaCodesMapWithNbsUid.put("PA1", 123);
        SrteCache.jurisdictionCodeMapWithNbsUid.put("J1", 456);

        Collection<Object> hashList = util.getPAJHashList("PA1", "J1");

        assertEquals(1, hashList.size());
    }

    @Test
    public void testGetPAJHashList_AllJurisdictions() {
        SrteCache.programAreaCodesMapWithNbsUid = new TreeMap<>();
        SrteCache.jurisdictionCodeMapWithNbsUid = new TreeMap<>();
        SrteCache.programAreaCodesMapWithNbsUid.put("PA1", 123);
        SrteCache.jurisdictionCodeMapWithNbsUid.put("J1", 456);
        SrteCache.jurisdictionCodeMapWithNbsUid.put("J2", 789);

        Collection<Object> hashList = util.getPAJHashList("PA1", "ALL");

        assertEquals(2, hashList.size());
    }
}
