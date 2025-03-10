package gov.cdc.nbs.deduplication.algorithm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

public record Kwargs(
        @JsonProperty("similarity_measure") String similarityMeasure,
        @JsonProperty("thresholds") Map<String, Double> thresholds,
        @JsonProperty("true_match_threshold") Double trueMatchThreshold,
        @JsonProperty("log_odds") Map<String, Double> logOdds
) {}
