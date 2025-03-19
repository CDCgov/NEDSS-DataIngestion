package gov.cdc.nbs.deduplication.algorithm.dto;

import java.util.List;

public record MatchingConfigRecord(
        String passName,
        String description,
        List<String> blockingCriteria,
        List<List<String>> matchingCriteria,  // each list contains ["field", "method"]
        String lowerBound,
        String upperBound,

        boolean active
) {}
