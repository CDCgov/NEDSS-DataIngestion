package gov.cdc.nbs.deduplication.algorithm.dto;

import java.util.List;

public record ExportConfigRecord(
        List<DataElementRecord> dataElements,
        List<MatchingConfigRecord> matchingConfiguration
) {}
