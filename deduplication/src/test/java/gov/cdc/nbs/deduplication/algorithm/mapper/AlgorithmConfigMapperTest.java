package gov.cdc.nbs.deduplication.algorithm.mapper;

import gov.cdc.nbs.deduplication.algorithm.dto.AlgorithmPass;
import gov.cdc.nbs.deduplication.algorithm.dto.Evaluator;
import gov.cdc.nbs.deduplication.algorithm.dto.MatchingCriteria;
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
        assertEquals("DIBBSDefaultPass1", result.passes().get(0).name());
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
    @Test
    void testMapMatchingCriteria() {
        // Case 1: Null evaluators should return an empty list
        List<MatchingCriteria> result1 = invokeMapMatchingCriteria(null);
        assertNotNull(result1);
        assertTrue(result1.isEmpty());

        // Case 2: Empty list of evaluators should return an empty list
        List<MatchingCriteria> result2 = invokeMapMatchingCriteria(List.of());
        assertNotNull(result2);
        assertTrue(result2.isEmpty());

        // Case 3: Valid evaluators should return correctly mapped MatchingCriteria
        Evaluator evaluator1 = new Evaluator("FIRST_NAME", "func:recordlinker.linking.matchers.compare_fuzzy_match");
        Evaluator evaluator2 = new Evaluator("LAST_NAME", "func:recordlinker.linking.matchers.compare_match_any");

        List<MatchingCriteria> result3 = invokeMapMatchingCriteria(List.of(evaluator1, evaluator2));

        assertNotNull(result3);
        assertEquals(2, result3.size());

        assertEquals("First name", result3.get(0).field().name()); // FIXED
        assertEquals("jarowinkler", result3.get(0).method().name());

        assertEquals("Last name", result3.get(1).field().name()); // FIXED
        assertEquals("exact", result3.get(1).method().name());
    }

    // Helper method to invoke private method using reflection
    private List<MatchingCriteria> invokeMapMatchingCriteria(List<Evaluator> evaluators) {
        try {
            var method = AlgorithmConfigMapper.class.getDeclaredMethod("mapMatchingCriteria", List.class);
            method.setAccessible(true);
            return (List<MatchingCriteria>) method.invoke(null, evaluators);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
