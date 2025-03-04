package gov.cdc.nbs.deduplication.sync.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.cdc.nbs.deduplication.seed.model.MpiPerson;

public record PatientCreateRequest(
    String person_reference_id,
    @JsonProperty("record") MpiPerson mpiPerson
) {
}
