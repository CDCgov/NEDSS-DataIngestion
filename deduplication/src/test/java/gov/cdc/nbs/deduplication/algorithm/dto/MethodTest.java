package gov.cdc.nbs.deduplication.algorithm.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MethodTest {

    @Test
    void testSetAndGetValue() {
        Method method = new Method();
        method.setValue("exact");

        assertEquals("exact", method.getValue());
    }

    @Test
    void testSetAndGetName() {
        Method method = new Method();
        method.setName("compare_match_any");

        assertEquals("compare_match_any", method.getName());
    }

    @Test
    void testFullObject() {
        Method method = new Method();
        method.setValue("compare_probabilistic_fuzzy_match");
        method.setName("compare_match_all");

        assertNotNull(method.getValue());
        assertEquals("compare_probabilistic_fuzzy_match", method.getValue());

        assertNotNull(method.getName());
        assertEquals("compare_match_all", method.getName());
    }
}

