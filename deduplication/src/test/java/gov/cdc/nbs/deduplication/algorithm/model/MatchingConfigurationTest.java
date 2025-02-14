package gov.cdc.nbs.deduplication.algorithm.model;

import gov.cdc.nbs.deduplication.algorithm.dto.Pass;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MatchingConfigurationTest {

    @Test
    void testEqualsAndHashCode() {
        Map<String, Boolean> blockingCriteria = Map.of("FIRST_NAME", true, "LAST_NAME", false);

        MatchingConfiguration config1 = new MatchingConfiguration(
                1L,
                "Test Label",
                "Test Description",
                true,
                List.of(new Pass("passName", "description", "0.1", "0.9", blockingCriteria, List.of())),
                new Double[]{0.0, 1.0}
        );

        MatchingConfiguration config2 = new MatchingConfiguration(
                1L,
                "Test Label",
                "Test Description",
                true,
                List.of(new Pass("passName", "description", "0.1", "0.9", blockingCriteria, List.of())),
                new Double[]{0.0, 1.0}
        );

        assertEquals(config1, config2);
        assertEquals(config1.hashCode(), config2.hashCode());
    }

    @Test
    void testEqualsWithDifferentValues() {
        Map<String, Boolean> blockingCriteria = Map.of("FIRST_NAME", true, "LAST_NAME", false);

        MatchingConfiguration config1 = new MatchingConfiguration(
                1L,
                "Test Label",
                "Test Description",
                true,
                List.of(new Pass("passName", "description", "0.1", "0.9", blockingCriteria, List.of())),
                new Double[]{0.0, 1.0}
        );

        MatchingConfiguration config2 = new MatchingConfiguration(
                1L,
                "Different Label",  // Different label
                "Test Description",
                true,
                List.of(new Pass("passName", "description", "0.1", "0.9", blockingCriteria, List.of())),
                new Double[]{0.0, 1.0}
        );
        assertNotEquals(config1, config2);

        MatchingConfiguration config3 = new MatchingConfiguration(
                2L,  // Different id
                "Test Label",
                "Test Description",
                true,
                List.of(new Pass("passName", "description", "0.1", "0.9", blockingCriteria, List.of())),
                new Double[]{0.0, 1.0}
        );
        assertNotEquals(config1, config3);

        MatchingConfiguration config4 = new MatchingConfiguration(
                1L,
                "Test Label",
                "Test Description",
                true,
                List.of(new Pass("passName", "description", "0.1", "0.9", blockingCriteria, List.of())),
                new Double[]{0.1, 0.9}  // Different belongingnessRatio
        );
        assertNotEquals(config1, config4);
    }

    @Test
    void testEqualsWithNull() {
        MatchingConfiguration config = new MatchingConfiguration(
                1L,
                "Test Label",
                "Test Description",
                true,
                null,  // Null passes
                new Double[]{0.0, 1.0}
        );

        assertNotEquals(null, config);
    }

    @Test
    void testToString() {
        Map<String, Boolean> blockingCriteria = Map.of("FIRST_NAME", true, "LAST_NAME", false);

        MatchingConfiguration config = new MatchingConfiguration(
                1L,
                "Test Label",
                "Test Description",
                true,
                List.of(new Pass("passName", "description", "0.1", "0.9", blockingCriteria, List.of())),
                new Double[]{0.0, 1.0}
        );

        String actualString = config.toString();

        // Allow either order in the assertion
        assertTrue(
                actualString.contains("blockingCriteria={FIRST_NAME=true, LAST_NAME=false}") ||
                        actualString.contains("blockingCriteria={LAST_NAME=false, FIRST_NAME=true}"),
                "Unexpected blockingCriteria order: " + actualString
        );
    }


    @Test
    void testConstructorAndGetters() {
        Map<String, Boolean> blockingCriteria = Map.of("FIRST_NAME", true, "LAST_NAME", false);

        MatchingConfiguration config = new MatchingConfiguration(
                1L,
                "Test Label",
                "Test Description",
                true,
                List.of(new Pass("passName", "description", "0.1", "0.9", blockingCriteria, List.of())),
                new Double[]{0.0, 1.0}
        );

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
        Map<String, Boolean> blockingCriteria = Map.of("FIRST_NAME", true, "LAST_NAME", false);

        MatchingConfiguration config1 = new MatchingConfiguration(
                1L,
                "Test Label",
                "Test Description",
                true,
                List.of(new Pass("passName", "description", "0.1", "0.9", blockingCriteria, List.of())),
                null  // belongingnessRatio is null
        );

        MatchingConfiguration config2 = new MatchingConfiguration(
                1L,
                "Test Label",
                "Test Description",
                true,
                List.of(new Pass("passName", "description", "0.1", "0.9", blockingCriteria, List.of())),
                null  // belongingnessRatio is also null
        );

        assertEquals(config1, config2);

        MatchingConfiguration config3 = new MatchingConfiguration(
                1L,
                "Test Label",
                "Test Description",
                true,
                List.of(new Pass("passName", "description", "0.1", "0.9", blockingCriteria, List.of())),
                new Double[]{0.0, 1.0}
        );

        assertNotEquals(config1, config3);
    }

    @Test
    void testEqualsWithIdenticalObjects() {
        Map<String, Boolean> blockingCriteria = Map.of("FIRST_NAME", true, "LAST_NAME", false);

        MatchingConfiguration config = new MatchingConfiguration(
                1L,
                "Test Label",
                "Test Description",
                true,
                List.of(new Pass("passName", "description", "0.1", "0.9", blockingCriteria, List.of())),
                new Double[]{0.0, 1.0}
        );

        assertEquals(config, config);
    }
}
