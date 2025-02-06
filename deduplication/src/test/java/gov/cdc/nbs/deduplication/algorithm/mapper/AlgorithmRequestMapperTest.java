package gov.cdc.nbs.deduplication.algorithm.mapper;

import gov.cdc.nbs.deduplication.algorithm.dto.*;
import gov.cdc.nbs.deduplication.algorithm.model.MatchingConfiguration;
import gov.cdc.nbs.deduplication.algorithm.model.AlgorithmUpdateRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

class AlgorithmRequestMapperTest {

    @Test
    void testMapField() {
        // Test valid field name
        String result = AlgorithmRequestMapper.mapField("First name");
        assertEquals("FIRST_NAME", result);

        // Test invalid field name
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            AlgorithmRequestMapper.mapField("Invalid field");
        });
        assertTrue(exception.getMessage().contains("Invalid field name"));
    }

    @Test
    void testMapFunc() {
        // Test valid function name
        String result = AlgorithmRequestMapper.mapFunc("exact");
        assertEquals("func:recordlinker.linking.matchers.compare_match_any", result);

        // Test invalid function name
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            AlgorithmRequestMapper.mapFunc("invalid_func");
        });
        assertTrue(exception.getMessage().contains("Invalid function"));
    }

    @Test
    void testMapToAlgorithmRequest() {
        // Create mock MatchingConfiguration
        MatchingConfiguration config = mock(MatchingConfiguration.class);

        // Create Pass with lower and upper bounds
        Pass pass = new Pass();
        pass.setLowerBound("0.1");
        pass.setUpperBound("0.9");

        // Create a valid BlockingCriteria and add it to the Pass
        BlockingCriteria blockingCriteria = new BlockingCriteria();
        Field field = new Field();
        field.setName("Date of birth"); // Set a valid field name
        blockingCriteria.setField(field);
        pass.setBlockingCriteria(List.of(blockingCriteria));  // Add blocking criteria

        // Create a valid MatchingCriteria and add it to the Pass
        MatchingCriteria matchingCriteria = new MatchingCriteria();
        Field matchingField = new Field();
        matchingField.setName("First name"); // Set valid field name for MatchingCriteria
        matchingCriteria.setField(matchingField);

        Method matchingMethod = new Method();
        matchingMethod.setValue("jarowinkler"); // Set valid method value
        matchingCriteria.setMethod(matchingMethod);

        pass.setMatchingCriteria(List.of(matchingCriteria)); // Add matching criteria

        // Add mock Pass object to MatchingConfiguration
        when(config.getPasses()).thenReturn(List.of(pass));
        when(config.isIncludeMultipleMatches()).thenReturn(true);

        // Call the mapToAlgorithmRequest method
        AlgorithmUpdateRequest request = AlgorithmRequestMapper.mapToAlgorithmRequest(config);

        // Verify that the mapToAlgorithmRequest method correctly sets the values
        assertNotNull(request);
        assertEquals("dibbs-enhanced", request.getLabel());
        assertEquals("The DIBBs Log-Odds Algorithm. This optional algorithm " +
                "uses statistical correction to adjust the links between incoming " +
                "records and previously processed patients (it does so by taking " +
                "advantage of the fact that some fields are more informative than others—e.g., " +
                "two records matching on MRN is stronger evidence that they should be linked " +
                "than if the records matched on zip code). It can be used if additional " +
                "granularity in matching links is desired. However, while the DIBBs Log-Odds " +
                "Algorithm can create higher-quality links, it is dependent on statistical " +
                "updating and pre-calculated population analysis, which requires some work on " +
                "the part of the user. For those cases where additional precision or stronger matching " +
                "criteria are required, the Log-Odds algorithm is detailed below.", request.getDescription());
        assertTrue(request.isDefault());
        assertTrue(request.isIncludeMultipleMatches());
        assertArrayEquals(new Double[]{0.1, 0.9}, request.getBelongingnessRatio());

        // Verify passes are correctly mapped
        assertNotNull(request.getPasses());
        assertEquals(1, request.getPasses().size());
        AlgorithmPass algorithmPass = request.getPasses().get(0);

        // Verify blocking keys
        assertNotNull(algorithmPass.getBlockingKeys());
        assertEquals(1, algorithmPass.getBlockingKeys().size());  // Only 1 blocking key in this case
        assertEquals("BIRTHDATE", algorithmPass.getBlockingKeys().get(0));  // Verify field mapping

        // Verify evaluator mapping
        assertNotNull(algorithmPass.getEvaluators());
        assertEquals(1, algorithmPass.getEvaluators().size());
        Evaluator evaluator = algorithmPass.getEvaluators().get(0);
        assertEquals("FIRST_NAME", evaluator.getFeature());  // Corrected to getFeature()
        assertEquals("func:recordlinker.linking.matchers.compare_fuzzy_match", evaluator.getFunc());
    }


}

