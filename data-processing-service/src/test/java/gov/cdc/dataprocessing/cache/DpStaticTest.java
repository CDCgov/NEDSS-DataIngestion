package gov.cdc.dataprocessing.cache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DpStaticTest {

    @BeforeEach
    void resetState() {
        // Ensure a clean state before each test
        DpStatic.setUuidPoolInitialized(false);
    }

    @Test
    void testIsUuidPoolInitialized_initiallyFalse() {
        assertFalse(DpStatic.isUuidPoolInitialized());
    }

    @Test
    void testSetUuidPoolInitialized_setsTrueAndFalseCorrectly() {
        DpStatic.setUuidPoolInitialized(true);
        assertTrue(DpStatic.isUuidPoolInitialized());

        DpStatic.setUuidPoolInitialized(false);
        assertFalse(DpStatic.isUuidPoolInitialized());
    }

    @Test
    void testCompareAndSetUuidPoolInitialized_successfulUpdate() {
        assertTrue(DpStatic.compareAndSetUuidPoolInitialized(false, true));
        assertTrue(DpStatic.isUuidPoolInitialized());
    }

    @Test
    void testCompareAndSetUuidPoolInitialized_unsuccessfulUpdate() {
        DpStatic.setUuidPoolInitialized(true);

        // Attempt to change from false to true but current is true, so should fail
        assertFalse(DpStatic.compareAndSetUuidPoolInitialized(false, true));
        assertTrue(DpStatic.isUuidPoolInitialized());
    }
}
