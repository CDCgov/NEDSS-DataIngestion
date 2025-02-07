package gov.cdc.nbs.deduplication.algorithm.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import gov.cdc.nbs.deduplication.algorithm.dto.AlgorithmPass;

public record AlgorithmUpdateRequest(
        @JsonProperty("label") String label,
        @JsonProperty("description") String description,
        @JsonProperty("is_default") boolean isDefault,
        @JsonProperty("include_multiple_matches") boolean includeMultipleMatches,
        @JsonProperty("belongingness_ratio") Double[] belongingnessRatio,
        @JsonProperty("passes") List<AlgorithmPass> passes
) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AlgorithmUpdateRequest that = (AlgorithmUpdateRequest) o;
        return isDefault == that.isDefault &&
                includeMultipleMatches == that.includeMultipleMatches &&
                Objects.equals(label, that.label) &&
                Objects.equals(description, that.description) &&
                Objects.equals(passes, that.passes) &&
                Arrays.equals(belongingnessRatio, that.belongingnessRatio);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(label, description, isDefault, includeMultipleMatches, passes);
        result = 31 * result + Arrays.hashCode(belongingnessRatio);
        return result;
    }

    @Override
    public String toString() {
        return "AlgorithmUpdateRequest {"+
                "label = " + label +
                "description = " + description +
                "is_default = " + isDefault +
                "include_multiple_matches = " + includeMultipleMatches +
                "belongingness_ratio = " + Arrays.toString(belongingnessRatio) +
                "passes = " + passes +
                "}";

    }
}
