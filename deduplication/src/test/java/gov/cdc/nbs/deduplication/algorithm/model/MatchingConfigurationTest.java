package gov.cdc.nbs.deduplication.algorithm.model;

import gov.cdc.nbs.deduplication.algorithm.dto.Pass;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class MatchingConfigurationTest {

    @Test
    void testConstructorAndFields() {
        // Test constructor and field initialization
        Double[] ratio = {0.1, 0.9};
        Pass pass = new Pass("pass1", "description", "0", "1", Collections.emptyList(), Collections.emptyList());
        MatchingConfiguration config = new MatchingConfiguration(1L, "Test Label", "Test Description", true,
                Collections.singletonList(pass), ratio);

        assertEquals(1L, config.id());
        assertEquals("Test Label", config.label());
        assertEquals("Test Description", config.description());
        assertTrue(config.isDefault());
        assertEquals(Collections.singletonList(pass), config.passes());
        assertArrayEquals(ratio, config.belongingnessRatio());
    }

    @Test
    void testEquals() {
        Double[] ratio1 = {0.1, 0.9};
        Double[] ratio2 = {0.1, 0.9};
        Pass pass = new Pass("pass1", "description", "0", "1", Collections.emptyList(), Collections.emptyList());

        MatchingConfiguration config1 = new MatchingConfiguration(1L, "Label", "Description", true,
                Collections.singletonList(pass), ratio1);
        MatchingConfiguration config2 = new MatchingConfiguration(1L, "Label", "Description", true,
                Collections.singletonList(pass), ratio2);
        MatchingConfiguration config3 = new MatchingConfiguration(2L, "Different Label", "Description", false,
                Collections.emptyList(), new Double[]{0.0, 1.0});

        // Positive case
        assertEquals(config1, config2);

        // Negative case (different object)
        assertNotEquals(config1, config3);
    }

    @Test
    void testHashCode() {
        Double[] ratio1 = {0.1, 0.9};
        Double[] ratio2 = {0.1, 0.9};
        Pass pass = new Pass("pass1", "description", "0", "1", Collections.emptyList(), Collections.emptyList());

        MatchingConfiguration config1 = new MatchingConfiguration(1L, "Label", "Description", true,
                Collections.singletonList(pass), ratio1);
        MatchingConfiguration config2 = new MatchingConfiguration(1L, "Label", "Description", true,
                Collections.singletonList(pass), ratio2);

        // Ensure hash codes are equal for equal objects
        assertEquals(config1.hashCode(), config2.hashCode());
    }

    @Test
    void testToString() {
        Double[] ratio = {0.1, 0.9};
        Pass pass = new Pass("pass1", "description", "0", "1", Collections.emptyList(), Collections.emptyList());

        MatchingConfiguration config = new MatchingConfiguration(1L, "Label", "Description", true,
                Collections.singletonList(pass), ratio);

        String expectedToString = "MatchingConfiguration{id=1, label='Label', description='Description', " +
                "isDefault=true, passes=[Pass[name=pass1, description=description, lowerBound=0, " +
                "upperBound=1, blockingCriteria=[], matchingCriteria=[]]], belongingnessRatio=[0.1, 0.9]}";

        assertEquals(expectedToString, config.toString());
    }
}
