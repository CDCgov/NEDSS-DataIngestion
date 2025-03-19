package gov.cdc.nbs.deduplication.algorithm.dto;

public record DataElementRecord(
        String field,
        double oddsRatio,
        double logOdds,
        double threshold
) {}
