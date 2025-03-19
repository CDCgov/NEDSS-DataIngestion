package gov.cdc.nbs.deduplication.data_elements.dto;

public record DataElement(
        boolean active,
        Double oddsRatio,
        Double logOdds,
        Double threshold
) {}