package gov.cdc.nbs.deduplication.batch.model;

public record MatchCandidateData(
    String personUid,
    long numOfMatches,
    String dateIdentified) {
}
