package gov.cdc.nbs.deduplication.algorithm.dto;

import java.util.List;
import java.util.Map;

public record Pass (
        String name,
        String description,
        String lowerBound,
        String upperBound,
        Map<String, Boolean> blockingCriteria, // Map instead of List
        List<MatchingCriteria> matchingCriteria,
        Kwargs kwargs
) {}
