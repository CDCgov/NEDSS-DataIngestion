package gov.cdc.nbs.deduplication.algorithm.dto;

import java.util.List;

public record Pass(
        String name,
        String description,
        List<BlockingCriteria> blockingCriteria,
        List<MatchingCriteria> matchingCriteria) {
}