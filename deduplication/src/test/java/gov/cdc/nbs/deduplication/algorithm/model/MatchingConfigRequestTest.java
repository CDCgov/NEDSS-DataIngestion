package gov.cdc.nbs.deduplication.algorithm.model;

import gov.cdc.nbs.deduplication.algorithm.dto.Kwargs;
import gov.cdc.nbs.deduplication.algorithm.dto.Pass;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MatchingConfigRequestTest {

    @Test
    void testEqualsAndHashCode() {
        Map<String, Boolean> blockingCriteria = Map.of("FIRST_NAME", true, "LAST_NAME", false);

        Kwargs kwargs = new Kwargs("JaroWinkler",Map.of("FIRST_NAME", 0.35), 12.2, Map.of("LAST_NAME", 0.35) );

        MatchingConfigRequest request1 = new MatchingConfigRequest(
                "Test Label",
                "Test Description",
                true,
                true,
                List.of(new Pass("passName", "description", "0.1", "0.9", blockingCriteria, List.of(), kwargs))
        );

        MatchingConfigRequest request2 = new MatchingConfigRequest(
                "Test Label",
                "Test Description",
                true,
                true,
                List.of(new Pass("passName", "description", "0.1", "0.9", blockingCriteria, List.of(), kwargs))
        );

        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    void testEqualsWithDifferentValues() {
        Map<String, Boolean> blockingCriteria = Map.of("FIRST_NAME", true, "LAST_NAME", false);

        Kwargs kwargs = new Kwargs("JaroWinkler",Map.of("FIRST_NAME", 0.35), 12.2, Map.of("LAST_NAME", 0.35) );

        MatchingConfigRequest request1 = new MatchingConfigRequest(
                "Test Label", "Test Description", true, true,
                List.of(new Pass("passName", "description", "0.1", "0.9", blockingCriteria, List.of(), kwargs))
        );

        // Different label
        MatchingConfigRequest request2 = new MatchingConfigRequest(
                "Different Label", "Test Description", true, true,
                List.of(new Pass("passName", "description", "0.1", "0.9", blockingCriteria, List.of(), kwargs))
        );
        assertNotEquals(request1, request2);

        // Different isDefault value
        MatchingConfigRequest request3 = new MatchingConfigRequest(
                "Test Label", "Test Description", false, true,
                List.of(new Pass("passName", "description", "0.1", "0.9", blockingCriteria, List.of(), kwargs))
        );
        assertNotEquals(request1, request3);

        // Different includeMultipleMatches value
        MatchingConfigRequest request4 = new MatchingConfigRequest(
                "Test Label", "Test Description", true, false,
                List.of(new Pass("passName", "description", "0.1", "0.9", blockingCriteria, List.of(), kwargs))
        );
        assertNotEquals(request1, request4);

        // Different passes list
        MatchingConfigRequest request5 = new MatchingConfigRequest(
                "Test Label", "Test Description", true, true,
                List.of()
        );
        assertNotEquals(request1, request5);
    }

    Kwargs kwargs = new Kwargs("JaroWinkler",Map.of("FIRST_NAME", 0.35), 12.2, Map.of("LAST_NAME", 0.35) );

    @Test
    void testEqualsWithNull() {
        MatchingConfigRequest request = new MatchingConfigRequest(
                "Test Label", "Test Description", true, true,
                List.of(new Pass("passName", "description", "0.1", "0.9", Map.of(), List.of(), kwargs))
        );

        assertNotEquals(null, request);
    }

    @Test
    void testToString() {
        Map<String, Boolean> blockingCriteria = Map.of("FIRST_NAME", true, "LAST_NAME", false);
        Kwargs kwargs = new Kwargs("JaroWinkler",Map.of("FIRST_NAME", 0.35), 12.2, Map.of("LAST_NAME", 0.35) );

        MatchingConfigRequest request = new MatchingConfigRequest(
                "Test Label", "Test Description", true, true,
                List.of(new Pass("passName", "description", "0.1", "0.9", blockingCriteria, List.of(), kwargs))
        );

        String actualString = request.toString();

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

        Kwargs kwargs = new Kwargs("JaroWinkler",Map.of("FIRST_NAME", 0.35), 12.2, Map.of("LAST_NAME", 0.35) );

        MatchingConfigRequest request = new MatchingConfigRequest(
                "Test Label", "Test Description", true, false,
                List.of(new Pass("passName", "description", "0.1", "0.9", blockingCriteria, List.of(), kwargs))
        );

        assertEquals("Test Label", request.label());
        assertEquals("Test Description", request.description());
        assertTrue(request.isDefault());
        assertFalse(request.includeMultipleMatches());
        assertNotNull(request.passes());
        assertEquals(1, request.passes().size());
    }

    @Test
    void testEqualsWithNullPasses() {
        MatchingConfigRequest request1 = new MatchingConfigRequest(
                "Test Label", "Test Description", true, true, null  // Null passes
        );

        MatchingConfigRequest request2 = new MatchingConfigRequest(
                "Test Label", "Test Description", true, true, null  // Null passes
        );

        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
    }
}
