package gov.cdc.nbs.deduplication.algorithm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record AlgorithmPass(
        @JsonProperty("blocking_keys") List<String> blockingKeys,
        @JsonProperty("evaluators") List<Evaluator> evaluators,
        @JsonProperty("rule") String rule,
        @JsonProperty("kwargs") Kwargs kwargs
) {}
