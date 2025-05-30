package gov.cdc.nbs.deduplication.batch.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.cdc.nbs.deduplication.seed.model.MpiPerson;

public record MatchRequest(
    @JsonProperty("record") MpiPerson mpiPerson,
    @JsonProperty("external_person_id") String externalPersonId,
    @JsonProperty("algorithm") String algorithm) {
}
