package gov.cdc.nbs.deduplication.algorithm.model;

import gov.cdc.nbs.deduplication.algorithm.dto.Pass;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public record MatchingConfiguration(
        Long id,
        String label,
        String description,
        boolean isDefault,
        List<Pass> passes,
        Double[] belongingnessRatio
) {

    // Override equals() to consider array contents
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MatchingConfiguration that = (MatchingConfiguration) o;
        return isDefault == that.isDefault &&
                Objects.equals(id, that.id) &&
                Objects.equals(label, that.label) &&
                Objects.equals(description, that.description) &&
                Objects.equals(passes, that.passes) &&
                Arrays.equals(belongingnessRatio, that.belongingnessRatio);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, label, description, isDefault, passes);
        result = 31 * result + Arrays.hashCode(belongingnessRatio);
        return result;
    }

    @Override
    public String toString() {
        return "MatchingConfiguration{" +
                "id=" + id +
                ", label='" + label + '\'' +
                ", description='" + description + '\'' +
                ", isDefault=" + isDefault +
                ", passes=" + passes +
                ", belongingnessRatio=" + Arrays.toString(belongingnessRatio) +
                '}';
    }
}
