package gov.cdc.nbs.deduplication.algorithm.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import gov.cdc.nbs.deduplication.algorithm.dto.AlgorithmPass;

public record AlgorithmUpdateRequest(
        @JsonProperty("label") String label,
        @JsonProperty("description") String description,
        @JsonProperty("is_default") boolean isDefault,
        @JsonProperty("include_multiple_matches") boolean includeMultipleMatches,
        @JsonProperty("belongingness_ratio") Double[] belongingnessRatio,
        @JsonProperty("passes") List<AlgorithmPass> passes) {
}
