package gov.cdc.nbs.deduplication.algorithm.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MatchingConfigurationTest {

    @Test
    void testEqualsAndHashCode() {
        MatchingConfiguration config1 = new MatchingConfiguration(
                1L, "Test Label", "Test Description", true, List.of(), new Double[]{0.0, 1.0}
        );
        MatchingConfiguration config2 = new MatchingConfiguration(
                1L, "Test Label", "Test Description", true, List.of(), new Double[]{0.0, 1.0}
        );

        // Verify that two identical objects are equal
        assertEquals(config1, config2);

        // Verify that their hash codes are the same
        assertEquals(config1.hashCode(), config2.hashCode());
    }

    @Test
    void testToString() {
        MatchingConfiguration config = new MatchingConfiguration(
                1L, "Test Label", "Test Description", true, List.of(), new Double[]{0.0, 1.0}
        );

        String expectedString = "MatchingConfiguration{id=1, label='Test Label', description='Test Description', isDefault=true, passes=[], belongingnessRatio=[0.0, 1.0]}";
        assertEquals(expectedString, config.toString());
    }
}
