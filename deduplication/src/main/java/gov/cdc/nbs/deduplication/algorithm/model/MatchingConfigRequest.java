package gov.cdc.nbs.deduplication.algorithm.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import gov.cdc.nbs.deduplication.algorithm.dto.Pass;
import java.util.List;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record MatchingConfigRequest(
        String label,
        String description,
        boolean isDefault,
        boolean includeMultipleMatches,
        List<Pass> passes
) {
    @Override
    public String toString() {
        return "MatchingConfigRequest {" +
                "label='" + label + '\'' +
                ", description='" + description + '\'' +
                ", isDefault=" + isDefault +
                ", includeMultipleMatches=" + includeMultipleMatches +
                ", passes=" + passes +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MatchingConfigRequest that = (MatchingConfigRequest) o;
        return isDefault == that.isDefault &&
                includeMultipleMatches == that.includeMultipleMatches &&
                Objects.equals(label, that.label) &&
                Objects.equals(description, that.description) &&
                Objects.equals(passes, that.passes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(label, description, isDefault, includeMultipleMatches, passes);
    }
}
