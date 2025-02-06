package gov.cdc.nbs.deduplication.algorithm.model;

import gov.cdc.nbs.deduplication.algorithm.dto.AlgorithmPass;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AlgorithmUpdateRequestTest {

    @Test
    void testEqualsAndHashCode() {
        // Creating sample AlgorithmUpdateRequest
        AlgorithmUpdateRequest request1 = new AlgorithmUpdateRequest(
                "Test Label",
                "Test Description",
                true,
                true,
                new Double[]{0.0, 1.0},
                List.of(new AlgorithmPass(List.of("FIRST_NAME"), List.of(), "rule", null))
        );

        // Creating another AlgorithmUpdateRequest with the same values
        AlgorithmUpdateRequest request2 = new AlgorithmUpdateRequest(
                "Test Label",
                "Test Description",
                true,
                true,
                new Double[]{0.0, 1.0},
                List.of(new AlgorithmPass(List.of("FIRST_NAME"), List.of(), "rule", null))
        );

        // Verify that both objects are considered equal
        assertEquals(request1, request2);

        // Verify that the hash codes of both objects are the same
        assertEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    void testNotEquals() {
        // Creating sample AlgorithmUpdateRequest
        AlgorithmUpdateRequest request1 = new AlgorithmUpdateRequest(
                "Test Label",
                "Test Description",
                true,
                true,
                new Double[]{0.0, 1.0},
                List.of(new AlgorithmPass(List.of("FIRST_NAME"), List.of(), "rule", null))
        );

        // Creating another AlgorithmUpdateRequest with different values
        AlgorithmUpdateRequest request2 = new AlgorithmUpdateRequest(
                "Different Label",
                "Test Description",
                true,
                true,
                new Double[]{0.0, 1.0},
                List.of(new AlgorithmPass(List.of("FIRST_NAME"), List.of(), "rule", null))
        );

        // Verify that the two objects are not equal
        assertNotEquals(request1, request2);
    }

    @Test
    void testToString() {
        // Creating sample AlgorithmUpdateRequest
        AlgorithmUpdateRequest request = new AlgorithmUpdateRequest(
                "Test Label",
                "Test Description",
                true,
                true,
                new Double[]{0.0, 1.0},
                List.of(new AlgorithmPass(List.of("FIRST_NAME"), List.of(), "rule", null))
        );

        // Expected string representation of the object
        String expectedString = "AlgorithmUpdateRequest {label = Test Labeldescription = Test Descriptionis_default = trueinclude_multiple_matches = truebelongingness_ratio = [0.0, 1.0]passes = [gov.cdc.nbs.deduplication.algorithm.dto.AlgorithmPass@"+ request.passes().hashCode() +"]}";

        // Verify the toString method
        assertEquals(expectedString, request.toString());
    }

    @Test
    void testConstructorAndGetters() {
        // Creating sample AlgorithmUpdateRequest
        AlgorithmUpdateRequest request = new AlgorithmUpdateRequest(
                "Test Label",
                "Test Description",
                true,
                false,
                new Double[]{0.1, 0.9},
                List.of(new AlgorithmPass(List.of("FIRST_NAME"), List.of(), "rule", null))
        );

        // Verify that the constructor correctly sets the values
        assertEquals("Test Label", request.label());
        assertEquals("Test Description", request.description());
        assertTrue(request.isDefault());
        assertFalse(request.includeMultipleMatches());
        assertArrayEquals(new Double[]{0.1, 0.9}, request.belongingnessRatio());
        assertNotNull(request.passes());
        assertEquals(1, request.passes().size());
    }
}
