package gov.cdc.dataprocessing.utilities.component.generic_helper;
import gov.cdc.dataprocessing.cache.PropertyUtilCache;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.StringTokenizer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class PropertyUtilTest {
    @InjectMocks
    private PropertyUtil propertyUtil;

    @BeforeEach
    public void setUp() {
        propertyUtil = new PropertyUtil();
        PropertyUtilCache.cachedHivList.clear();
        ReflectionTestUtils.setField(propertyUtil, "hivProgArea", "PA1,PA2,PA3");
    }

    @Test
    public void testIsHIVProgramArea_ValidProgramArea() {
        assertTrue(propertyUtil.isHIVProgramArea("PA1"));
    }

    @Test
    public void testIsHIVProgramArea_InvalidProgramArea() {
        assertFalse(propertyUtil.isHIVProgramArea("INVALID"));
    }

    @Test
    public void testIsHIVProgramArea_NullProgramArea() {
        assertFalse(propertyUtil.isHIVProgramArea(null));
    }

    @Test
    public void testIsHIVProgramArea_EmptyProgramArea() {
        assertFalse(propertyUtil.isHIVProgramArea(""));
    }

    @Test
    public void testIsHIVProgramArea_EmptyHivProgArea() {
        ReflectionTestUtils.setField(propertyUtil, "hivProgArea", "");
        assertFalse(propertyUtil.isHIVProgramArea("PA1"));
    }

    @Test
    public void testCachedHivProgramArea_Success() {
        propertyUtil.isHIVProgramArea("PA1"); // This will trigger the caching

        assertTrue(PropertyUtilCache.cachedHivList.contains("PA1"));
        assertTrue(PropertyUtilCache.cachedHivList.contains("PA2"));
        assertTrue(PropertyUtilCache.cachedHivList.contains("PA3"));
    }

}
