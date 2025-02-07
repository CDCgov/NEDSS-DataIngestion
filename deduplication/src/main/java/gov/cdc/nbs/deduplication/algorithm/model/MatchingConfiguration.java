package gov.cdc.nbs.deduplication.algorithm.model;

import gov.cdc.nbs.deduplication.algorithm.dto.Pass;

import java.util.List;

public record MatchingConfiguration(
        String label,
        String description,
        boolean isDefault,
        List<Pass> passes,
        BelongingnessRatio belongingnessRatio) {

    public record BelongingnessRatio(Double lower, Double upper) {
    }
}
