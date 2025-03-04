package gov.cdc.nbs.deduplication.algorithm.mapper;

import gov.cdc.nbs.deduplication.algorithm.dto.*;
import gov.cdc.nbs.deduplication.algorithm.model.AlgorithmUpdateRequest;
import gov.cdc.nbs.deduplication.algorithm.model.MatchingConfiguration;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.Map;

class AlgorithmRequestMapperTest {

    @Test
    void testMapField() {
        // Test known field mappings
        assertEquals("FIRST_NAME", AlgorithmRequestMapper.mapField("First name"));
        assertEquals("LAST_NAME", AlgorithmRequestMapper.mapField("Last name"));

        // Test unknown field (should return original value)
        assertEquals("Unknown field", AlgorithmRequestMapper.mapField("Unknown field"));
    }

    @Test
    void testMapFunc() {
        // Test known function mappings
        assertEquals("func:recordlinker.linking.matchers.compare_fuzzy_match", AlgorithmRequestMapper.mapFunc("jarowinkler"));
        assertEquals("func:recordlinker.linking.matchers.compare_match_any", AlgorithmRequestMapper.mapFunc("compare_match_any"));

        // Test unknown function (should return original value)
        assertEquals("unknown_func", AlgorithmRequestMapper.mapFunc("unknown_func"));
    }

    @Test
    void testMapToAlgorithmRequest() {
        // Mocking blocking criteria as a Map<String, Boolean>
        Map<String, Boolean> blockingCriteria = Map.of(
                "FIRST_NAME", true,
                "LAST_NAME", false
        );
        Kwargs kwargs = new Kwargs("JaroWinkler",Map.of("FIRST_NAME", 0.35), 12.2, Map.of("LAST_NAME", 0.35) );

        // Creating a sample Pass with blocking criteria and matching criteria
        List<Pass> passes = List.of(new Pass(
                "Pass 1",
                "Description",
                "0.1",
                "0.9",
                blockingCriteria, // Use the new Map-based structure
                List.of(new MatchingCriteria(
                        new Field("LAST_NAME", "Last name"),
                        new Method("exact", "matcher")
                )),
                kwargs
        ));

        // Creating a MatchingConfiguration with the new Pass structure
        MatchingConfiguration config = new MatchingConfiguration(
                1L, // id
                "Test Label",
                "Test Description",
                true,
                passes,
                new Double[]{0.1, 0.9}
        );

        // Calling the method under test
        AlgorithmUpdateRequest algorithmUpdateRequest = AlgorithmRequestMapper.mapToAlgorithmRequest(config);

        // Assertions to verify correct mappings
        assertEquals("dibbs-enhanced", algorithmUpdateRequest.label());
        assertEquals("The DIBBs Log-Odds Algorithm. This optional algorithm uses statistical correction to adjust the links between incoming " +
                "records and previously processed patients...", algorithmUpdateRequest.description());
        assertTrue(algorithmUpdateRequest.isDefault());
        assertNotNull(algorithmUpdateRequest.passes());
        assertEquals(1, algorithmUpdateRequest.passes().size()); // Ensure one pass is mapped

        // Verify the belongingnessRatio is set correctly
        assertArrayEquals(new Double[]{0.1, 0.9}, algorithmUpdateRequest.belongingnessRatio());

        // Verify the blocking keys are mapped correctly
        AlgorithmPass mappedPass = algorithmUpdateRequest.passes().get(0);
        assertTrue(mappedPass.blockingKeys().contains("FIRST_NAME")); // Ensure mapped field
        assertFalse(mappedPass.blockingKeys().contains("UNKNOWN_FIELD")); // Ensure unknown fields are not included

        // Verify matching criteria mapping
        assertEquals(1, mappedPass.evaluators().size());
        Evaluator mappedEvaluator = mappedPass.evaluators().get(0);
        assertEquals("LAST_NAME", mappedEvaluator.feature());
        assertEquals("matcher", mappedEvaluator.func()); // Ensure method mapping
    }
}
