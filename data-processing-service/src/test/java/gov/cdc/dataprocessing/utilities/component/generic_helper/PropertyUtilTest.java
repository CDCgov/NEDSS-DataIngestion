package gov.cdc.dataprocessing.utilities.component.generic_helper;

import gov.cdc.dataprocessing.cache.PropertyUtilCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    void testIsHIVProgramArea_ValidProgramArea() {
        assertTrue(propertyUtil.isHIVProgramArea("PA1"));
    }

    @Test
    void testIsHIVProgramArea_InvalidProgramArea() {
        assertFalse(propertyUtil.isHIVProgramArea("INVALID"));
    }

    @Test
    void testIsHIVProgramArea_NullProgramArea() {
        assertFalse(propertyUtil.isHIVProgramArea(null));
    }

    @Test
    void testIsHIVProgramArea_EmptyProgramArea() {
        assertFalse(propertyUtil.isHIVProgramArea(""));
    }

    @Test
    void testIsHIVProgramArea_EmptyHivProgArea() {
        ReflectionTestUtils.setField(propertyUtil, "hivProgArea", "");
        assertFalse(propertyUtil.isHIVProgramArea("PA1"));
    }

    @Test
    void testCachedHivProgramArea_Success() {
        propertyUtil.isHIVProgramArea("PA1"); // This will trigger the caching

        assertTrue(PropertyUtilCache.cachedHivList.contains("PA1"));
        assertTrue(PropertyUtilCache.cachedHivList.contains("PA2"));
        assertTrue(PropertyUtilCache.cachedHivList.contains("PA3"));
    }

}
