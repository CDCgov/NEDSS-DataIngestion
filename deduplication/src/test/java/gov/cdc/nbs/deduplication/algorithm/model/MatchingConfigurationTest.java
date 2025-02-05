package gov.cdc.nbs.deduplication.algorithm.model;

import gov.cdc.nbs.deduplication.algorithm.dto.Pass;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MatchingConfigurationTest {

    @Test
    void testSetAndGetId() {
        MatchingConfiguration config = new MatchingConfiguration();
        config.setId(1L);

        assertEquals(1L, config.getId());
    }

    @Test
    void testSetAndGetLabel() {
        MatchingConfiguration config = new MatchingConfiguration();
        config.setLabel("Test Label");

        assertEquals("Test Label", config.getLabel());
    }

    @Test
    void testSetAndGetDescription() {
        MatchingConfiguration config = new MatchingConfiguration();
        config.setDescription("Test Description");

        assertEquals("Test Description", config.getDescription());
    }

    @Test
    void testSetAndGetIsDefault() {
        MatchingConfiguration config = new MatchingConfiguration();
        config.setDefault(true);

        assertTrue(config.isDefault());
    }

    @Test
    void testSetAndGetIncludeMultipleMatches() {
        MatchingConfiguration config = new MatchingConfiguration();
        config.setIncludeMultipleMatches(true);

        assertTrue(config.isIncludeMultipleMatches());
    }

    @Test
    void testSetAndGetPasses() {
        MatchingConfiguration config = new MatchingConfiguration();
        Pass pass = new Pass();  // Mock Pass object
        config.setPasses(List.of(pass));

        assertNotNull(config.getPasses());
        assertEquals(1, config.getPasses().size());
        assertEquals(pass, config.getPasses().get(0));
    }

    @Test
    void testSetAndGetBelongingnessRatio() {
        MatchingConfiguration config = new MatchingConfiguration();
        Double[] ratio = {0.2, 0.8};
        config.setBelongingnessRatio(ratio);

        assertArrayEquals(ratio, config.getBelongingnessRatio());
    }

    @Test
    void testConstructorWithFields() {
        Pass pass = new Pass();
        Double[] ratio = {0.2, 0.8};
        MatchingConfiguration config = new MatchingConfiguration(1L, "Test Label", "Test Description", true, true, List.of(pass), ratio);

        assertEquals(1L, config.getId());
        assertEquals("Test Label", config.getLabel());
        assertEquals("Test Description", config.getDescription());
        assertTrue(config.isDefault());
        assertTrue(config.isIncludeMultipleMatches());
        assertEquals(1, config.getPasses().size());
        assertArrayEquals(ratio, config.getBelongingnessRatio());
    }

    @Test
    void testFullObject() {
        Pass pass = new Pass();
        MatchingConfiguration config = new MatchingConfiguration();
        config.setId(1L);
        config.setLabel("Test Label");
        config.setDescription("Test Description");
        config.setDefault(true);
        config.setIncludeMultipleMatches(true);
        config.setPasses(List.of(pass));
        config.setBelongingnessRatio(new Double[]{0.1, 0.9});

        assertEquals(1L, config.getId());
        assertEquals("Test Label", config.getLabel());
        assertEquals("Test Description", config.getDescription());
        assertTrue(config.isDefault());
        assertTrue(config.isIncludeMultipleMatches());
        assertEquals(1, config.getPasses().size());
        assertArrayEquals(new Double[]{0.1, 0.9}, config.getBelongingnessRatio());
    }
}

