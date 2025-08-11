package gov.cdc.nbs.deduplication.merge.model;

public record PatientMergeQueueStatus(boolean inMergeQueue, Long mergeGroup) {}
