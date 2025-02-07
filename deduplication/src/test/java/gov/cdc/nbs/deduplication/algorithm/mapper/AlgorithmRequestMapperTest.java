package gov.cdc.nbs.deduplication.algorithm.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import gov.cdc.nbs.deduplication.algorithm.dto.BlockingCriteria;
import gov.cdc.nbs.deduplication.algorithm.dto.Field;
import gov.cdc.nbs.deduplication.algorithm.dto.MatchingCriteria;
import gov.cdc.nbs.deduplication.algorithm.dto.Method;
import gov.cdc.nbs.deduplication.algorithm.dto.Pass;
import gov.cdc.nbs.deduplication.algorithm.model.AlgorithmUpdateRequest;
import gov.cdc.nbs.deduplication.algorithm.model.MatchingConfiguration;
import gov.cdc.nbs.deduplication.algorithm.model.MatchingConfiguration.BelongingnessRatio;

class AlgorithmRequestMapperTest {

    @Test
    void testMapField() {
        // Test known field mappings
        assertThat(AlgorithmRequestMapper.mapField("First name")).isEqualTo("FIRST_NAME");
        assertThat(AlgorithmRequestMapper.mapField("Last name")).isEqualTo("LAST_NAME");

        // Test unknown field (should return original value)
        assertThat(AlgorithmRequestMapper.mapField("Unknown field")).isEqualTo("Unknown field");
    }

    @Test
    void testMapFunc() {
        // Test known function mappings
        assertThat(AlgorithmRequestMapper.mapFunc("jarowinkler"))
                .isEqualTo("func:recordlinker.linking.matchers.compare_fuzzy_match");
        assertThat(AlgorithmRequestMapper.mapFunc("compare_match_any"))
                .isEqualTo("func:recordlinker.linking.matchers.compare_match_any");

        // Test unknown function (should return original value)
        assertThat(AlgorithmRequestMapper.mapFunc("unknown_func")).isEqualTo("unknown_func");
    }

    @Test
    void testMapToAlgorithmRequest() {
        // Mocking the MatchingConfiguration with relevant values
        MatchingConfiguration config = new MatchingConfiguration(
                "Test Label",
                "Test Description",
                true,
                List.of(new Pass("Pass 1", "Description",
                        List.of(new BlockingCriteria(new Field("FIRST_NAME", "STRING"),
                                new Method("exact", "matcher"))),
                        List.of(new MatchingCriteria(new Field("LAST_NAME", "STRING"),
                                new Method("exact", "matcher"))))),
                new BelongingnessRatio(0.1, 0.9));

        // Calling the mapToAlgorithmRequest method from AlgorithmRequestMapper
        AlgorithmUpdateRequest algorithmUpdateRequest = AlgorithmRequestMapper.mapToAlgorithmRequest(config);

        // Assertions to verify the values have been correctly mapped
        assertThat(algorithmUpdateRequest.label()).isEqualTo("dibbs-enhanced");
        assertThat(algorithmUpdateRequest.description()).isEqualTo(
                "The DIBBs Log-Odds Algorithm. This optional algorithm uses statistical correction to adjust the links between incoming "
                        +
                        "records and previously processed patients...");
        assertThat(algorithmUpdateRequest.isDefault()).isTrue();
        assertThat(algorithmUpdateRequest.passes()).isNotNull();
        assertThat(algorithmUpdateRequest.passes()).hasSize(1); // Verifying that the pass list has one entry
        // Verify the belongingnessRatio is set correctly
        assertThat(algorithmUpdateRequest.belongingnessRatio()).isEqualTo(new Double[] { 0.1, 0.9 });

    }

}
