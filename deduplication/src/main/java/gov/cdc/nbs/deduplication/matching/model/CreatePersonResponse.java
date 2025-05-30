package gov.cdc.nbs.deduplication.matching.model;

public record CreatePersonResponse(
    String person_reference_id,
    String external_person_id) {

}
