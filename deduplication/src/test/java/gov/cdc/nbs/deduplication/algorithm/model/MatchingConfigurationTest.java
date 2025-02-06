package gov.cdc.nbs.deduplication.algorithm.model;

import gov.cdc.nbs.deduplication.algorithm.dto.Pass;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class MatchingConfigurationTest {

    @Test
    void testConstructorAndFields() {
        // Creating a sample MatchingConfiguration
        Pass pass = new Pass("pass1", "description", "0", "1", Collections.emptyList(), Collections.emptyList());
        MatchingConfiguration config = new MatchingConfiguration(
                1L,
                "Test Label",
                "Test Description",
                true,
                Collections.singletonList(pass),
                new Double[]{0.0, 1.0}
        );

        // Verify the constructor initializes all fields correctly
        assertEquals(1L, config.id());
        assertEquals("Test Label", config.label());
        assertEquals("Test Description", config.description());
        assertTrue(config.isDefault());
        assertEquals(Collections.singletonList(pass), config.passes());
        assertArrayEquals(new Double[]{0.0, 1.0}, config.belongingnessRatio());
    }

    @Test
    void testEquals() {
        // Creating two MatchingConfiguration objects with the same values
        Pass pass = new Pass("pass1", "description", "0", "1", Collections.emptyList(), Collections.emptyList());
        MatchingConfiguration config1 = new MatchingConfiguration(
                1L,
                "Test Label",
                "Test Description",
                true,
                Collections.singletonList(pass),
                new Double[]{0.0, 1.0}
        );

        MatchingConfiguration config2 = new MatchingConfiguration(
                1L,
                "Test Label",
                "Test Description",
                true,
                Collections.singletonList(pass),
                new Double[]{0.0, 1.0}
        );

        // Verify that the two objects are equal
        assertEquals(config1, config2);

        // Modify one field (e.g., label) and verify that the objects are no longer equal
        MatchingConfiguration config3 = new MatchingConfiguration(
                1L,
                "Different Label", // Different label
                "Test Description",
                true,
                Collections.singletonList(pass),
                new Double[]{0.0, 1.0}
        );
        assertNotEquals(config1, config3);
    }

    @Test
    void testHashCode() {
        // Creating two MatchingConfiguration objects with the same values
        Pass pass = new Pass("pass1", "description", "0", "1", Collections.emptyList(), Collections.emptyList());
        MatchingConfiguration config1 = new MatchingConfiguration(
                1L,
                "Test Label",
                "Test Description",
                true,
                Collections.singletonList(pass),
                new Double[]{0.0, 1.0}
        );

        MatchingConfiguration config2 = new MatchingConfiguration(
                1L,
                "Test Label",
                "Test Description",
                true,
                Collections.singletonList(pass),
                new Double[]{0.0, 1.0}
        );

        // Ensure that equal objects have the same hash code
        assertEquals(config1.hashCode(), config2.hashCode());
    }

    @Test
    void testToString() {
        // Creating a sample MatchingConfiguration
        Pass pass = new Pass("pass1", "description", "0", "1", Collections.emptyList(), Collections.emptyList());
        MatchingConfiguration config = new MatchingConfiguration(
                1L,
                "Test Label",
                "Test Description",
                true,
                Collections.singletonList(pass),
                new Double[]{0.0, 1.0}
        );

        // Expected string representation of the object
        String expectedString = "MatchingConfiguration{id=1, label='Test Label', description='Test Description', " +
                "isDefault=true, passes=[Pass[name=pass1, description=description, lowerBound=0, " +
                "upperBound=1, blockingCriteria=[], matchingCriteria=[]]], belongingnessRatio=[0.0, 1.0]}";

        // Verify the toString method
        assertEquals(expectedString, config.toString());
    }
}
