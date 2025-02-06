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

        // Create another object with an empty passes list
        MatchingConfigRequest config3 = new MatchingConfigRequest(
                "Test Label", "Test Description", true, true, List.of()
        );

        // Verify that the two objects are not equal because passes are different
        assertNotEquals(config1, config3);
    }

    @Test
    void testEqualsWithNull() {
        // Create a MatchingConfigRequest object
        Pass pass1 = new Pass("TestPass", "Description", "0.1", "0.9", List.of(), List.of());
        MatchingConfigRequest config1 = new MatchingConfigRequest(
                "Test Label", "Test Description", true, true, List.of(pass1)
        );

        // Test equality with null
        assertNotEquals(config1, null);

        // Test equality with a different class type
        assertNotEquals(config1, "Some string");
    }

    @Test
    void testToString() {
        // Create a MatchingConfigRequest object
        Pass pass1 = new Pass("TestPass", "Description", "0.1", "0.9", List.of(), List.of());
        MatchingConfigRequest config = new MatchingConfigRequest(
                "Test Label", "Test Description", true, true, List.of(pass1)
        );

        // Expected string representation
        String expectedString = "MatchingConfigRequest {label='Test Label', description='Test Description', isDefault=true, includeMultipleMatches=true, passes=[" +
                "Pass[name=TestPass, description=Description, lowerBound=0.1, upperBound=0.9, blockingCriteria=[], matchingCriteria=[]]]}";

        // Verify the string representation
        assertEquals(expectedString, config.toString());
    }

    @Test
    void testToStringWithEmptyPasses() {
        // Create a MatchingConfigRequest object with an empty passes list
        MatchingConfigRequest config = new MatchingConfigRequest(
                "Test Label", "Test Description", true, true, List.of()
        );

        // Expected string representation with an empty passes list
        String expectedString = "MatchingConfigRequest {label='Test Label', description='Test Description', isDefault=true, includeMultipleMatches=true, passes=[]}";

        // Verify the string representation with empty passes list
        assertEquals(expectedString, config.toString());
    }

    @Test
    void testEqualsWithEmptyPasses() {
        // Create MatchingConfigRequest objects with empty passes lists
        MatchingConfigRequest config1 = new MatchingConfigRequest(
                "Test Label", "Test Description", true, true, List.of()
        );
        MatchingConfigRequest config2 = new MatchingConfigRequest(
                "Test Label", "Test Description", true, true, List.of()
        );

        // Verify that objects with empty passes lists are equal
        assertEquals(config1, config2);

        // Verify hash codes are also equal
        assertEquals(config1.hashCode(), config2.hashCode());
    }

    @Test
    void testNotEqualsWithDifferentPasses() {
        // Create MatchingConfigRequest objects with different passes lists
        Pass pass1 = new Pass("TestPass", "Description", "0.1", "0.9", List.of(), List.of());
        MatchingConfigRequest config1 = new MatchingConfigRequest(
                "Test Label", "Test Description", true, true, List.of(pass1)
        );
        MatchingConfigRequest config2 = new MatchingConfigRequest(
                "Test Label", "Test Description", true, true, List.of()
        );

        // Verify that objects with different passes lists are not equal
        assertNotEquals(config1, config2);
    }
}
