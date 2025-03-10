package gov.cdc.nbs.deduplication.algorithm.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;
import java.util.Map;

public record Pass (
        String name,
        String description,
        String lowerBound,
        String upperBound,
        Map<String, Boolean> blockingCriteria, // Map instead of List
        List<MatchingCriteria> matchingCriteria,
        @JsonDeserialize(as = Kwargs.class) Kwargs kwargs
) {}
