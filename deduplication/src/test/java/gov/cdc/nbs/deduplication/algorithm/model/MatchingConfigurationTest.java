package gov.cdc.nbs.deduplication.algorithm.model;

import gov.cdc.nbs.deduplication.algorithm.dto.Pass;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MatchingConfigurationTest {

    @Test
    void testEqualsAndHashCode() {
        MatchingConfiguration config1 = new MatchingConfiguration(
                1L,
                "Test Label",
                "Test Description",
                true,
                List.of(new Pass("passName", "description", "0.1", "0.9", List.of(), List.of())),
                new Double[]{0.0, 1.0}
        );

        MatchingConfiguration config2 = new MatchingConfiguration(
                1L,
                "Test Label",
                "Test Description",
                true,
                List.of(new Pass("passName", "description", "0.1", "0.9", List.of(), List.of())),
                new Double[]{0.0, 1.0}
        );

        // Verify that both objects are considered equal
        assertEquals(config1, config2);

        // Verify that the hash codes of both objects are the same
        assertEquals(config1.hashCode(), config2.hashCode());
    }

    @Test
    void testEqualsWithDifferentValues() {
        MatchingConfiguration config1 = new MatchingConfiguration(
                1L,
                "Test Label",
                "Test Description",
                true,
                List.of(new Pass("passName", "description", "0.1", "0.9", List.of(), List.of())),
                new Double[]{0.0, 1.0}
        );

        // Different label
        MatchingConfiguration config2 = new MatchingConfiguration(
                1L,
                "Different Label",
                "Test Description",
                true,
                List.of(new Pass("passName", "description", "0.1", "0.9", List.of(), List.of())),
                new Double[]{0.0, 1.0}
        );
        assertNotEquals(config1, config2);

        // Different id
        MatchingConfiguration config3 = new MatchingConfiguration(
                2L,
                "Test Label",
                "Test Description",
                true,
                List.of(new Pass("passName", "description", "0.1", "0.9", List.of(), List.of())),
                new Double[]{0.0, 1.0}
        );
        assertNotEquals(config1, config3);

        // Different belongingnessRatio
        MatchingConfiguration config4 = new MatchingConfiguration(
                1L,
                "Test Label",
                "Test Description",
                true,
                List.of(new Pass("passName", "description", "0.1", "0.9", List.of(), List.of())),
                new Double[]{0.1, 0.9}
        );
        assertNotEquals(config1, config4);
    }

    @Test
    void testEqualsWithDifferentClass() {
        MatchingConfiguration config = new MatchingConfiguration(
                1L,
                "Test Label",
                "Test Description",
                true,
                List.of(new Pass("passName", "description", "0.1", "0.9", List.of(), List.of())),
                new Double[]{0.0, 1.0}
        );

        // Compare with an object of a different class
        assertNotEquals(config, new Object());
    }

    @Test
    void testEqualsWithNull() {
        MatchingConfiguration config = new MatchingConfiguration(
                1L,
                "Test Label",
                "Test Description",
                true,
                List.of(new Pass("passName", "description", "0.1", "0.9", List.of(), List.of())),
                new Double[]{0.0, 1.0}
        );

        // Compare with null, should return false
        assertNotEquals(null, config);
    }

    @Test
    void testHashCodeWithDifferentValues() {
        MatchingConfiguration config1 = new MatchingConfiguration(
                1L,
                "Test Label",
                "Test Description",
                true,
                List.of(new Pass("passName", "description", "0.1", "0.9", List.of(), List.of())),
                new Double[]{0.0, 1.0}
        );

        MatchingConfiguration config2 = new MatchingConfiguration(
                2L,
                "Test Label",
                "Test Description",
                true,
                List.of(new Pass("passName", "description", "0.1", "0.9", List.of(), List.of())),
                new Double[]{0.0, 1.0}
        );

        // Verify that the hash codes are different for objects with different values
        assertNotEquals(config1.hashCode(), config2.hashCode());
    }

    @Test
    void testToString() {
        MatchingConfiguration config = new MatchingConfiguration(
                1L,
                "Test Label",
                "Test Description",
                true,
                List.of(new Pass("passName", "description", "0.1", "0.9", List.of(), List.of())),
                new Double[]{0.0, 1.0}
        );

        // Expected string representation of the object
        String expectedString = "MatchingConfiguration{id=1, label='Test Label', description='Test Description', " +
                "isDefault=true, passes=[Pass[name=passName, description=description, lowerBound=0.1, upperBound=0.9, " +
                "blockingCriteria=[], matchingCriteria=[]]], belongingnessRatio=[0.0, 1.0]}";

        // Verify the toString method
        assertEquals(expectedString, config.toString());
    }

    @Test
    void testConstructorAndGetters() {
        MatchingConfiguration config = new MatchingConfiguration(
                1L,
                "Test Label",
                "Test Description",
                true,
                List.of(new Pass("passName", "description", "0.1", "0.9", List.of(), List.of())),
                new Double[]{0.0, 1.0}
        );

        // Verify that the constructor correctly sets the values
        assertEquals(1L, config.id());
        assertEquals("Test Label", config.label());
        assertEquals("Test Description", config.description());
        assertTrue(config.isDefault());
        assertNotNull(config.passes());
        assertEquals(1, config.passes().size());
        assertArrayEquals(new Double[]{0.0, 1.0}, config.belongingnessRatio());
    }

    @Test
    void testEqualsWithNullBelongingnessRatio() {
        MatchingConfiguration config1 = new MatchingConfiguration(
                1L,
                "Test Label",
                "Test Description",
                true,
                List.of(new Pass("passName", "description", "0.1", "0.9", List.of(), List.of())),
                null  // belongingnessRatio is null
        );

        MatchingConfiguration config2 = new MatchingConfiguration(
                1L,
                "Test Label",
                "Test Description",
                true,
                List.of(new Pass("passName", "description", "0.1", "0.9", List.of(), List.of())),
                null  // belongingnessRatio is also null
        );

        assertEquals(config1, config2);  // Should be equal as both have null belongingnessRatio

        MatchingConfiguration config3 = new MatchingConfiguration(
                1L,
                "Test Label",
                "Test Description",
                true,
                List.of(new Pass("passName", "description", "0.1", "0.9", List.of(), List.of())),
                new Double[]{0.0, 1.0}  // belongingnessRatio is not null
        );

        assertNotEquals(config1, config3);  // Should not be equal because belongingnessRatio is different
    }

    @Test
    void testEqualsWithNullPasses() {
        MatchingConfiguration config1 = new MatchingConfiguration(
                1L,
                "Test Label",
                "Test Description",
                true,
                null,  // passes is null
                new Double[]{0.0, 1.0}
        );

        MatchingConfiguration config2 = new MatchingConfiguration(
                1L,
                "Test Label",
                "Test Description",
                true,
                null,  // passes is null
                new Double[]{0.0, 1.0}
        );

        assertEquals(config1, config2);  // Should be equal because both have null passes

        MatchingConfiguration config3 = new MatchingConfiguration(
                1L,
                "Test Label",
                "Test Description",
                true,
                List.of(new Pass("passName", "description", "0.1", "0.9", List.of(), List.of())),
                new Double[]{0.0, 1.0}
        );

        assertNotEquals(config1, config3);  // Should not be equal because passes is different (null vs non-null)
    }

    @Test
    void testEqualsWithIdenticalObjects() {
        MatchingConfiguration config = new MatchingConfiguration(
                1L,
                "Test Label",
                "Test Description",
                true,
                List.of(new Pass("passName", "description", "0.1", "0.9", List.of(), List.of())),
                new Double[]{0.0, 1.0}
        );

        // Should be equal because it's the same object
        assertEquals(config, config);
    }
}
