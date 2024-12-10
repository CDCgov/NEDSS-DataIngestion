package gov.cdc.nbs.deduplication.seed.model;

public record DeduplicationEntry(
    Long nbsPersonId,
    Long nbsPersonParentId,
    String mpiPatientId,
    String mpiPersonId) {

}
