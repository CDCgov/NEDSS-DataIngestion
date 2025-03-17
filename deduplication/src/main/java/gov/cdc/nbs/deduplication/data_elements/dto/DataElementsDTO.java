package gov.cdc.nbs.deduplication.data_elements.dto;

import java.util.Map;

public record DataElementsDTO(Map<String, DataElementConfig> dataElements) {

    // You can also define the inner record if you prefer
    public record DataElementConfig(
            boolean active,
            Double oddsRatio,
            Double logOdds,
            Double threshold
    ) {}
}