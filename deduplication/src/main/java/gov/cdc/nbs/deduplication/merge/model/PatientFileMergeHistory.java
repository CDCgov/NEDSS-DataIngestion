package gov.cdc.nbs.deduplication.merge.model;


public record PatientFileMergeHistory(
    String supersededPersonLocalId,
    String supersededPersonLegalName,
    String mergeTimestamp,
    String mergedByUser
) {
}
