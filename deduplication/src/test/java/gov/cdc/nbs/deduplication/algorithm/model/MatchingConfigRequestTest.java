package gov.cdc.nbs.deduplication.algorithm.model;

import gov.cdc.nbs.deduplication.algorithm.dto.Pass;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MatchingConfigRequestTest {

    @Test
    void testEqualsAndHashCode() {
        MatchingConfigRequest request1 = new MatchingConfigRequest(
                "Test Label",
                "Test Description",
                true,
                true,
                List.of(new Pass("passName", "description", "0.1", "0.9", List.of(), List.of()))
        );

        // Creating another MatchingConfigRequest with the same values
        MatchingConfigRequest request2 = new MatchingConfigRequest(
                "Test Label",
                "Test Description",
                true,
                true,
                List.of(new Pass("passName", "description", "0.1", "0.9", List.of(), List.of()))
        );

        // Verify that both objects are considered equal
        assertEquals(request1, request2);

        // Verify that the hash codes of both objects are the same
        assertEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    void testEqualsWithDifferentValues() {
        MatchingConfigRequest request1 = new MatchingConfigRequest(
                "Test Label",
                "Test Description",
                true,
                true,
                List.of(new Pass("passName", "description", "0.1", "0.9", List.of(), List.of()))
        );

        // Different label
        MatchingConfigRequest request2 = new MatchingConfigRequest(
                "Different Label",
                "Test Description",
                true,
                true,
                List.of(new Pass("passName", "description", "0.1", "0.9", List.of(), List.of()))
        );
        assertNotEquals(request1, request2);

        // Different isDefault value
        MatchingConfigRequest request3 = new MatchingConfigRequest(
                "Test Label",
                "Test Description",
                false,
                true,
                List.of(new Pass("passName", "description", "0.1", "0.9", List.of(), List.of()))
        );
        assertNotEquals(request1, request3);

        // Different includeMultipleMatches value
        MatchingConfigRequest request4 = new MatchingConfigRequest(
                "Test Label",
                "Test Description",
                true,
                false,
                List.of(new Pass("passName", "description", "0.1", "0.9", List.of(), List.of()))
        );
        assertNotEquals(request1, request4);

        // Different passes list
        MatchingConfigRequest request5 = new MatchingConfigRequest(
                "Test Label",
                "Test Description",
                true,
                true,
                List.of()
        );
        assertNotEquals(request1, request5);
    }

    @Test
    void testEqualsWithDifferentClass() {
        MatchingConfigRequest request = new MatchingConfigRequest(
                "Test Label",
                "Test Description",
                true,
                true,
                List.of(new Pass("passName", "description", "0.1", "0.9", List.of(), List.of()))
        );

        // Compare with an object of a different class
        assertNotEquals(request, new Object());
    }

    @Test
    void testEqualsWithNull() {
        MatchingConfigRequest request = new MatchingConfigRequest(
                "Test Label",
                "Test Description",
                true,
                true,
                List.of(new Pass("passName", "description", "0.1", "0.9", List.of(), List.of()))
        );

        // Compare with null, should return false
        assertNotEquals(null, request);
    }

    @Test
    void testHashCodeWithDifferentValues() {
        MatchingConfigRequest request1 = new MatchingConfigRequest(
                "Test Label",
                "Test Description",
                true,
                true,
                List.of(new Pass("passName", "description", "0.1", "0.9", List.of(), List.of()))
        );

        MatchingConfigRequest request2 = new MatchingConfigRequest(
                "Different Label",
                "Test Description",
                true,
                true,
                List.of(new Pass("passName", "description", "0.1", "0.9", List.of(), List.of()))
        );

        // Verify that the hash codes are different for objects with different values
        assertNotEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    void testToString() {
        // Creating sample MatchingConfigRequest
        MatchingConfigRequest request = new MatchingConfigRequest(
                "Test Label",
                "Test Description",
                true,
                true,
                List.of(new Pass("passName", "description", "0.1", "0.9", List.of(), List.of()))
        );

        // Expected string representation of the object
        String expectedString = "MatchingConfigRequest {label='Test Label', description='Test Description', isDefault=true, includeMultipleMatches=true, passes=[Pass[name=passName, description=description, lowerBound=0.1, upperBound=0.9, blockingCriteria=[], matchingCriteria=[]]]}";

        // Verify the toString method
        assertEquals(expectedString, request.toString());
    }

    @Test
    void testConstructorAndGetters() {
        // Creating sample MatchingConfigRequest
        MatchingConfigRequest request = new MatchingConfigRequest(
                "Test Label",
                "Test Description",
                true,
                false,
                List.of(new Pass("passName", "description", "0.1", "0.9", List.of(), List.of()))
        );

        // Verify that the constructor correctly sets the values
        assertEquals("Test Label", request.label());
        assertEquals("Test Description", request.description());
        assertTrue(request.isDefault());
        assertFalse(request.includeMultipleMatches());
        assertNotNull(request.passes());
        assertEquals(1, request.passes().size());
    }
}
