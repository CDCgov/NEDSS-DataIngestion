package gov.cdc.nbs.deduplication.algorithm.model;

import gov.cdc.nbs.deduplication.algorithm.dto.AlgorithmPass;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AlgorithmUpdateRequestTest {

    @Test
    void testSetAndGetLabel() {
        AlgorithmUpdateRequest request = new AlgorithmUpdateRequest();
        request.setLabel("Test Label");

        assertEquals("Test Label", request.getLabel());
    }

    @Test
    void testSetAndGetDescription() {
        AlgorithmUpdateRequest request = new AlgorithmUpdateRequest();
        request.setDescription("Test Description");

        assertEquals("Test Description", request.getDescription());
    }

    @Test
    void testSetAndGetIsDefault() {
        AlgorithmUpdateRequest request = new AlgorithmUpdateRequest();
        request.setIsDefault(true);

        assertTrue(request.isDefault());
    }

    @Test
    void testSetAndGetIncludeMultipleMatches() {
        AlgorithmUpdateRequest request = new AlgorithmUpdateRequest();
        request.setIncludeMultipleMatches(true);

        assertTrue(request.isIncludeMultipleMatches());
    }

    @Test
    void testSetAndGetBelongingnessRatio() {
        AlgorithmUpdateRequest request = new AlgorithmUpdateRequest();
        Double[] ratio = {0.2, 0.8};
        request.setBelongingnessRatio(ratio);

        assertArrayEquals(ratio, request.getBelongingnessRatio());
    }

    @Test
    void testSetAndGetPasses() {
        AlgorithmUpdateRequest request = new AlgorithmUpdateRequest();
        AlgorithmPass pass = new AlgorithmPass();
        request.setPasses(List.of(pass));

        assertNotNull(request.getPasses());
        assertEquals(1, request.getPasses().size());
        assertEquals(pass, request.getPasses().get(0));
    }

    @Test
    void testFullObject() {
        AlgorithmUpdateRequest request = new AlgorithmUpdateRequest();
        request.setLabel("Test Label");
        request.setDescription("Test Description");
        request.setIsDefault(true);
        request.setIncludeMultipleMatches(true);
        request.setBelongingnessRatio(new Double[]{0.1, 0.9});
        AlgorithmPass pass = new AlgorithmPass();
        request.setPasses(List.of(pass));

        assertEquals("Test Label", request.getLabel());
        assertEquals("Test Description", request.getDescription());
        assertTrue(request.isDefault());
        assertTrue(request.isIncludeMultipleMatches());
        assertArrayEquals(new Double[]{0.1, 0.9}, request.getBelongingnessRatio());
        assertEquals(1, request.getPasses().size());
    }
}
