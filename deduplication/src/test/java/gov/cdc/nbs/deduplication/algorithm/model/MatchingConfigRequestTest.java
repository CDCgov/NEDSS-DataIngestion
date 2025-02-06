package gov.cdc.nbs.deduplication.algorithm.model;

import gov.cdc.nbs.deduplication.algorithm.dto.Pass;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MatchingConfigRequestTest {

    @Test
    void testEqualsAndHashCode() {
        // Create sample data for MatchingConfigRequest
        Pass pass1 = new Pass("TestPass", "Description", "0.1", "0.9", List.of(), List.of());
        MatchingConfigRequest config1 = new MatchingConfigRequest(
                "Test Label", "Test Description", true, true, List.of(pass1)
        );
        MatchingConfigRequest config2 = new MatchingConfigRequest(
                "Test Label", "Test Description", true, true, List.of(pass1)
        );

        // Verify that two identical MatchingConfigRequest objects are equal
        assertEquals(config1, config2);

        // Verify that their hash codes are the same
        assertEquals(config1.hashCode(), config2.hashCode());
    }

    @Test
    void testNotEquals() {
        // Create different MatchingConfigRequest objects
        Pass pass1 = new Pass("TestPass", "Description", "0.1", "0.9", List.of(), List.of());
        MatchingConfigRequest config1 = new MatchingConfigRequest(
                "Test Label", "Test Description", true, true, List.of(pass1)
        );
        MatchingConfigRequest config2 = new MatchingConfigRequest(
                "Different Label", "Test Description", true, true, List.of(pass1)
        );

        // Verify that the two objects are not equal
        assertNotEquals(config1, config2);
    }

    @Test
    void testToString() {
        // Create a MatchingConfigRequest object
        Pass pass1 = new Pass("TestPass", "Description", "0.1", "0.9", List.of(), List.of());
        MatchingConfigRequest config = new MatchingConfigRequest(
                "Test Label", "Test Description", true, true, List.of(pass1)
        );

        // Verify the string representation
        String expectedString = "MatchingConfigRequest {label='Test Label', description='Test Description', isDefault=true, includeMultipleMatches=true, passes=[" +
                "Pass[name=TestPass, description=Description, lowerBound=0.1, upperBound=0.9, blockingCriteria=[], matchingCriteria=[]]]}";
        assertEquals(expectedString, config.toString());
    }
}
