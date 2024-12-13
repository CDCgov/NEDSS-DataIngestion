package gov.cdc.nbs.deduplication.matching.model;

public record CreatePersonResponse(
    String patient_reference_id,
    String person_reference_id) {

}
