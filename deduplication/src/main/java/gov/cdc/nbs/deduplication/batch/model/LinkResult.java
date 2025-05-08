package gov.cdc.nbs.deduplication.batch.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record LinkResult(
    @JsonProperty("person_reference_id") UUID personReferenceId,
    @JsonProperty("belongingness_ratio") @JsonFormat(shape = JsonFormat.Shape.NUMBER) double belongingnessRatio) {
}
