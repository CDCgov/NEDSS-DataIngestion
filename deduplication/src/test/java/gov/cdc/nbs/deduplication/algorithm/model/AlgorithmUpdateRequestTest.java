package gov.cdc.nbs.deduplication.algorithm.model;

import gov.cdc.nbs.deduplication.algorithm.dto.AlgorithmPass;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AlgorithmUpdateRequestTest {

    @Test
    void testNotEqualsWithDifferentValues() {
        AlgorithmUpdateRequest request1 = new AlgorithmUpdateRequest(
                "Test Label",
                "Test Description",
                true,
                true,
                new Double[] { 0.0, 1.0 },
                List.of(new AlgorithmPass(List.of("FIRST_NAME"), List.of(), "rule", null)));

        // Different label
        AlgorithmUpdateRequest request2 = new AlgorithmUpdateRequest(
                "Different Label",
                "Test Description",
                true,
                true,
                new Double[] { 0.0, 1.0 },
                List.of(new AlgorithmPass(List.of("FIRST_NAME"), List.of(), "rule", null)));

        // Verify that the two objects are not equal
        assertNotEquals(request1, request2);

        // Different belongingnessRatio
        AlgorithmUpdateRequest request3 = new AlgorithmUpdateRequest(
                "Test Label",
                "Test Description",
                true,
                true,
                new Double[] { 0.1, 0.9 },
                List.of(new AlgorithmPass(List.of("FIRST_NAME"), List.of(), "rule", null)));
        assertNotEquals(request1, request3);
    }

    @Test
    void testEqualsWithDifferentClass() {
        AlgorithmUpdateRequest request = new AlgorithmUpdateRequest(
                "Test Label",
                "Test Description",
                true,
                true,
                new Double[] { 0.0, 1.0 },
                List.of(new AlgorithmPass(List.of("FIRST_NAME"), List.of(), "rule", null)));

        // Compare with an object of a different class
        assertNotEquals(request, new Object());
    }

    @Test
    void testEqualsWithNull() {
        AlgorithmUpdateRequest request = new AlgorithmUpdateRequest(
                "Test Label",
                "Test Description",
                true,
                true,
                new Double[] { 0.0, 1.0 },
                List.of(new AlgorithmPass(List.of("FIRST_NAME"), List.of(), "rule", null)));

        // Compare with null, should return false
        assertNotEquals(null, request);
    }

    @Test
    void testHashCodeWithDifferentValues() {
        AlgorithmUpdateRequest request1 = new AlgorithmUpdateRequest(
                "Test Label",
                "Test Description",
                true,
                true,
                new Double[] { 0.0, 1.0 },
                List.of(new AlgorithmPass(List.of("FIRST_NAME"), List.of(), "rule", null)));

        AlgorithmUpdateRequest request2 = new AlgorithmUpdateRequest(
                "Different Label",
                "Test Description",
                true,
                true,
                new Double[] { 0.0, 1.0 },
                List.of(new AlgorithmPass(List.of("FIRST_NAME"), List.of(), "rule", null)));

        // Verify that the hash codes are different for objects with different values
        assertNotEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    void testConstructorAndGetters() {
        // Creating sample AlgorithmUpdateRequest
        AlgorithmUpdateRequest request = new AlgorithmUpdateRequest(
                "Test Label",
                "Test Description",
                true,
                false,
                new Double[] { 0.1, 0.9 },
                List.of(new AlgorithmPass(List.of("FIRST_NAME"), List.of(), "rule", null)));

        // Verify that the constructor correctly sets the values
        assertEquals("Test Label", request.label());
        assertEquals("Test Description", request.description());
        assertTrue(request.isDefault());
        assertFalse(request.includeMultipleMatches());
        assertArrayEquals(new Double[] { 0.1, 0.9 }, request.belongingnessRatio());
        assertNotNull(request.passes());
        assertEquals(1, request.passes().size());
    }

}
