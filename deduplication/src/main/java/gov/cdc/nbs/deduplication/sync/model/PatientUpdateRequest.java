package gov.cdc.nbs.deduplication.sync.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.cdc.nbs.deduplication.seed.model.MpiPerson;

public record PatientUpdateRequest(
    String external_person_id,
    @JsonProperty("record") MpiPerson mpiPerson
) {
}
