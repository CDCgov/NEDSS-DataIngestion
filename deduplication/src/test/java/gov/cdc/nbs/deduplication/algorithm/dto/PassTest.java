package gov.cdc.nbs.deduplication.algorithm.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

class PassTest {

    @Test
    void testPass() {
        // Define blocking criteria as a Map<String, Boolean>
        Map<String, Boolean> blockingCriteria = Map.of(
                "FIRST_NAME", true,
                "LAST_NAME", false
        );

        // Define matching criteria
        MatchingCriteria matchingCriteria = new MatchingCriteria(
                new Field("LAST_NAME", "Last name"),
                new Method("exact", "Exact Match")
        );

        // Create Pass object with updated structure
        Pass pass = new Pass("Test Name", "Test Description", "0.1", "0.9",
                blockingCriteria, List.of(matchingCriteria));

        // Validate basic properties
        assertEquals("Test Name", pass.name());
        assertEquals("Test Description", pass.description());
        assertEquals("0.1", pass.lowerBound());
        assertEquals("0.9", pass.upperBound());

        // Validate blocking criteria map
        assertEquals(2, pass.blockingCriteria().size());
        assertTrue(pass.blockingCriteria().get("FIRST_NAME"));
        assertFalse(pass.blockingCriteria().get("LAST_NAME"));

        // Validate matching criteria list
        assertEquals(1, pass.matchingCriteria().size());
        assertEquals("LAST_NAME", pass.matchingCriteria().get(0).field().value());
        assertEquals("Last name", pass.matchingCriteria().get(0).field().name());
        assertEquals("exact", pass.matchingCriteria().get(0).method().value());
        assertEquals("Exact Match", pass.matchingCriteria().get(0).method().name());
    }
}