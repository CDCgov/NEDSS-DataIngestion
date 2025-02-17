package gov.cdc.nbs.deduplication.algorithm.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MethodTest {

    @Test
    void testConstructorAndGetters() {
        // Create a Method instance using the constructor
        Method method = new Method("exact", "matcher");

        // Assert that the values are correctly set
        assertNotNull(method);
        assertEquals("exact", method.value());
        assertEquals("matcher", method.name());
    }
}
