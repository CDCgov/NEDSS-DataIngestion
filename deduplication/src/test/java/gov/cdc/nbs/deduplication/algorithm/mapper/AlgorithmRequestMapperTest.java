package gov.cdc.nbs.deduplication.algorithm.mapper;

import gov.cdc.nbs.deduplication.algorithm.dto.*;
import gov.cdc.nbs.deduplication.algorithm.model.AlgorithmUpdateRequest;
import gov.cdc.nbs.deduplication.algorithm.model.MatchingConfiguration;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

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
        // Mocking the MatchingConfiguration with relevant values
        MatchingConfiguration config = new MatchingConfiguration(
                1L, // id
                "Test Label",
                "Test Description",
                true,
                List.of(new Pass("Pass 1", "Description", "0.1", "0.9",
                        List.of(new BlockingCriteria(new Field("FIRST_NAME", "STRING"), new Method("exact", "matcher"))),
                        List.of(new MatchingCriteria(new Field("LAST_NAME", "STRING"), new Method("exact", "matcher"))))),
                new Double[]{0.1, 0.9}
        );

        // Calling the mapToAlgorithmRequest method from AlgorithmRequestMapper
        AlgorithmUpdateRequest algorithmUpdateRequest = AlgorithmRequestMapper.mapToAlgorithmRequest(config);

        // Assertions to verify the values have been correctly mapped
        assertEquals("dibbs-enhanced", algorithmUpdateRequest.label());
        assertEquals("The DIBBs Log-Odds Algorithm. This optional algorithm uses statistical correction to adjust the links between incoming " +
                "records and previously processed patients...", algorithmUpdateRequest.description());
        assertTrue(algorithmUpdateRequest.isDefault());
        assertNotNull(algorithmUpdateRequest.passes());
        assertEquals(1, algorithmUpdateRequest.passes().size()); // Verifying that the pass list has one entry

        // Verify the belongingnessRatio is set correctly
        assertArrayEquals(new Double[]{0.1, 0.9}, algorithmUpdateRequest.belongingnessRatio());
    }
}
