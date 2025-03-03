package gov.cdc.nbs.deduplication.algorithm.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Kwargs(
        String similarityMeasure,
        Map<String, Double> thresholds,
        Double trueMatchThreshold,
        Map<String, Double> logOdds
) {}
