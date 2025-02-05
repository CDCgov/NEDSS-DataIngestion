package gov.cdc.nbs.deduplication.algorithm.mapper;

import gov.cdc.nbs.deduplication.algorithm.dto.AlgorithmPass;
import gov.cdc.nbs.deduplication.algorithm.dto.Evaluator;
import gov.cdc.nbs.deduplication.algorithm.dto.Pass;
import gov.cdc.nbs.deduplication.algorithm.model.MatchingConfiguration;
import gov.cdc.nbs.deduplication.algorithm.model.AlgorithmUpdateRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

class AlgorithmRequestMapperTest {

    @Test
    void testMapField() {
        // Test mapping valid field names
        assertEquals("FIRST_NAME", AlgorithmRequestMapper.mapField("First name"));
        assertEquals("BIRTHDATE", AlgorithmRequestMapper.mapField("Date of birth"));

        // Test invalid field name (should throw an exception)
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            AlgorithmRequestMapper.mapField("Invalid field");
        });
        assertEquals("Invalid field name: Invalid field", exception.getMessage());
    }

    @Test
    void testMapFunc() {
        // Test mapping valid function names
        assertEquals("func:recordlinker.linking.matchers.compare_fuzzy_match", AlgorithmRequestMapper.mapFunc("jarowinkler"));
        assertEquals("func:recordlinker.linking.matchers.compare_match_any", AlgorithmRequestMapper.mapFunc("exact"));

        // Test invalid function name (should throw an exception)
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            AlgorithmRequestMapper.mapFunc("invalidFunc");
        });
        assertEquals("Invalid function: invalidFunc", exception.getMessage());
    }

    @Test
    void testMapToAlgorithmRequest() {
        // Create mock MatchingConfiguration
        MatchingConfiguration config = mock(MatchingConfiguration.class);
        Pass pass = new Pass();
        pass.setLowerBound("0.1");
        pass.setUpperBound("0.9");

        // Add mock Pass object to MatchingConfiguration
        when(config.getPasses()).thenReturn(List.of(pass));
        when(config.isIncludeMultipleMatches()).thenReturn(true);

        // Call the mapToAlgorithmRequest method
        AlgorithmUpdateRequest request = AlgorithmRequestMapper.mapToAlgorithmRequest(config);

        // Verify that the mapToAlgorithmRequest method correctly sets the values
        assertNotNull(request);
        assertEquals("dibbs-enhanced", request.getLabel());
        assertEquals("The DIBBs Log-Odds Algorithm. This optional algorithm uses statistical correction ...", request.getDescription());
        assertTrue(request.isDefault());
        assertTrue(request.isIncludeMultipleMatches());
        assertArrayEquals(new Double[]{0.0, 1.0}, request.getBelongingnessRatio());

        // Verify passes are correctly mapped
        assertNotNull(request.getPasses());
        assertEquals(1, request.getPasses().size());
        AlgorithmPass algorithmPass = request.getPasses().get(0);
        assertNotNull(algorithmPass.getBlockingKeys());
        assertEquals(3, algorithmPass.getBlockingKeys().size());
        assertEquals("FIRST_NAME", algorithmPass.getBlockingKeys().get(0));  // Verify field mapping
        assertNotNull(algorithmPass.getEvaluators());
        assertEquals(1, algorithmPass.getEvaluators().size());

        // Verify evaluator mapping - use getFeature instead of getField
        Evaluator evaluator = algorithmPass.getEvaluators().get(0);
        assertEquals("FIRST_NAME", evaluator.getFeature());  // Corrected to getFeature()
        assertEquals("func:recordlinker.linking.matchers.compare_fuzzy_match", evaluator.getFunc());
    }

}

