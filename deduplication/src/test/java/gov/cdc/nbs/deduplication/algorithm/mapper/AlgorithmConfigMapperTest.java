package gov.cdc.nbs.deduplication.algorithm.mapper;

import gov.cdc.nbs.deduplication.algorithm.dto.AlgorithmPass;
import gov.cdc.nbs.deduplication.algorithm.dto.Evaluator;
import gov.cdc.nbs.deduplication.algorithm.dto.Kwargs;
import gov.cdc.nbs.deduplication.algorithm.dto.MatchingCriteria;
import gov.cdc.nbs.deduplication.algorithm.model.AlgorithmUpdateRequest;
import gov.cdc.nbs.deduplication.algorithm.model.MatchingConfigRequest;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import static gov.cdc.nbs.deduplication.algorithm.mapper.AlgorithmConfigMapper.castToMap;
import static gov.cdc.nbs.deduplication.algorithm.mapper.AlgorithmConfigMapper.mapKwargs;
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

    @Test
    void testMapKwargs_NullInput() {
        assertNull(mapKwargs(null), "Should return null when input is null");
    }

    @Test
    void testMapKwargs_AlreadyKwargsInstance() {
        Kwargs originalKwargs = new Kwargs("JaroWinkler", Map.of("LAST_NAME", 0.35), 12.2, Map.of("LAST_NAME", 0.35));
        Kwargs result = mapKwargs(originalKwargs);

        assertSame(originalKwargs, result, "Should return the same instance if input is already Kwargs");
    }

    @Test
    void testMapKwargs_ValidMapInput() {
        Map<String, Object> inputMap = Map.of(
                "similarity_measure", "Levenshtein",
                "thresholds", Map.of("FIRST_NAME", 0.5),
                "true_match_threshold", 0.8,
                "log_odds", Map.of("LAST_NAME", 1.2)
        );

        Kwargs result = mapKwargs(inputMap);

        assertNotNull(result, "Kwargs object should not be null");
        assertEquals("Levenshtein", result.similarityMeasure(), "Incorrect similarity measure");
        assertEquals(0.5, result.thresholds().get("FIRST_NAME"), "Incorrect threshold value");
        assertEquals(0.8, result.trueMatchThreshold(), "Incorrect true match threshold");
        assertEquals(1.2, result.logOdds().get("LAST_NAME"), "Incorrect log odds value");
    }

    @Test
    void testMapKwargs_InvalidTrueMatchThreshold() {
        Map<String, Object> inputMap = Map.of(
                "similarity_measure", "Levenshtein",
                "thresholds", Map.of("FIRST_NAME", 0.5),
                "true_match_threshold", "not-a-number",
                "log_odds", Map.of("LAST_NAME", 1.2)
        );

        Kwargs result = mapKwargs(inputMap);

        assertNotNull(result, "Kwargs object should not be null");
        assertNull(result.trueMatchThreshold(), "True match threshold should be null for invalid input");
    }

    @Test
    void testMapKwargs_EmptyMap() {
        Map<String, Object> inputMap = Map.of();

        Kwargs result = mapKwargs(inputMap);

        assertNotNull(result, "Kwargs object should not be null");
        assertNull(result.similarityMeasure(), "Similarity measure should be null for an empty map");
        assertNull(result.thresholds(), "Thresholds should be null for an empty map");
        assertNull(result.trueMatchThreshold(), "True match threshold should be null for an empty map");
        assertNull(result.logOdds(), "Log odds should be null for an empty map");
    }

    @Test
    void testCastToMap_ValidMap() {
        Map<String, Double> validMap = Map.of("FIRST_NAME", 0.7);
        Map<String, Double> result = castToMap(validMap);

        assertNotNull(result, "Result should not be null for valid input");
        assertEquals(0.7, result.get("FIRST_NAME"), "Incorrect map value");
    }

    @Test
    void testCastToMap_InvalidInput() {
        Object invalidInput = "Not a map";
        Map<String, Double> result = castToMap(invalidInput);

        assertNull(result, "Result should be null for non-map input");
    }

    @Test
    void testCastToMap_NullInput() {
        assertNull(castToMap(null), "Result should be null for null input");
    }
}