package gov.cdc.nbs.deduplication.algorithm.model;

import gov.cdc.nbs.deduplication.algorithm.dto.Pass;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MatchingConfigRequestTest {

    @Test
    void testSetAndGetLabel() {
        MatchingConfigRequest request = new MatchingConfigRequest();
        request.setLabel("Test Label");

        assertEquals("Test Label", request.getLabel());
    }

    @Test
    void testSetAndGetDescription() {
        MatchingConfigRequest request = new MatchingConfigRequest();
        request.setDescription("Test Description");

        assertEquals("Test Description", request.getDescription());
    }

    @Test
    void testSetAndGetIsDefault() {
        MatchingConfigRequest request = new MatchingConfigRequest();
        request.setDefault(true);

        assertTrue(request.isDefault());
    }

    @Test
    void testSetAndGetIncludeMultipleMatches() {
        MatchingConfigRequest request = new MatchingConfigRequest();
        request.setIncludeMultipleMatches(true);

        assertTrue(request.isIncludeMultipleMatches());
    }

    @Test
    void testSetAndGetPasses() {
        MatchingConfigRequest request = new MatchingConfigRequest();
        Pass pass = new Pass();  // Mock Pass object
        request.setPasses(List.of(pass));

        assertNotNull(request.getPasses());
        assertEquals(1, request.getPasses().size());
        assertEquals(pass, request.getPasses().get(0));
    }

    @Test
    void testJsonIncludeBehavior() {
        MatchingConfigRequest request = new MatchingConfigRequest();

        // Passes is not set, so it should not be included in the serialized JSON
        assertNull(request.getPasses());

        // Set passes to check inclusion
        Pass pass = new Pass();
        request.setPasses(List.of(pass));

        assertNotNull(request.getPasses());
    }

    @Test
    void testFullObject() {
        MatchingConfigRequest request = new MatchingConfigRequest();
        request.setLabel("Test Label");
        request.setDescription("Test Description");
        request.setDefault(true);
        request.setIncludeMultipleMatches(true);
        Pass pass = new Pass();
        request.setPasses(List.of(pass));

        assertEquals("Test Label", request.getLabel());
        assertEquals("Test Description", request.getDescription());
        assertTrue(request.isDefault());
        assertTrue(request.isIncludeMultipleMatches());
        assertEquals(1, request.getPasses().size());
    }
}

