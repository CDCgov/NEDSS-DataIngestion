package gov.cdc.nbs.deduplication.algorithm.mapper;

import gov.cdc.nbs.deduplication.algorithm.dto.AlgorithmPass;
import gov.cdc.nbs.deduplication.algorithm.model.AlgorithmUpdateRequest;
import gov.cdc.nbs.deduplication.algorithm.model.MatchingConfigRequest;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AlgorithmConfigMapperTest {

    @Test
    void testMapAlgorithmUpdateRequestToMatchingConfigRequest() {
        AlgorithmUpdateRequest request = new AlgorithmUpdateRequest(
                "Test Label",
                "Test Description",
                true,
                true,
                new Double[]{0.1, 0.9},
                List.of(new AlgorithmPass(List.of("FIRST_NAME"), List.of(), "rule", null))
        );

        MatchingConfigRequest result = AlgorithmConfigMapper.mapAlgorithmUpdateRequestToMatchingConfigRequest(request);

        assertNotNull(result);
        assertEquals("Test Label", result.label());
        assertEquals("Test Description", result.description());
        assertTrue(result.isDefault());
        assertTrue(result.includeMultipleMatches());
        assertEquals(1, result.passes().size());
        assertEquals("rule", result.passes().get(0).name());
    }

    @Test
    void testMapAlgorithmUpdateRequestToMatchingConfigRequest_NullRequest() {
        assertNull(AlgorithmConfigMapper.mapAlgorithmUpdateRequestToMatchingConfigRequest(null));
    }

    @Test
    void testMapAlgorithmUpdateRequestToMatchingConfigRequest_EmptyPasses() {
        AlgorithmUpdateRequest request = new AlgorithmUpdateRequest(
                "Test Label",
                "Test Description",
                false,
                false,
                new Double[]{},
                List.of()
        );

        MatchingConfigRequest result = AlgorithmConfigMapper.mapAlgorithmUpdateRequestToMatchingConfigRequest(request);

        assertNotNull(result);
        assertTrue(result.passes().isEmpty());
    }

    @Test
    void testPrivateConstructor() throws Exception {
        Constructor<AlgorithmConfigMapper> constructor = AlgorithmConfigMapper.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        InvocationTargetException thrown = assertThrows(InvocationTargetException.class, constructor::newInstance);
        assertTrue(thrown.getCause() instanceof UnsupportedOperationException);
    }


}
