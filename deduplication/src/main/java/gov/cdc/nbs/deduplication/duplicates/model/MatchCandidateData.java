package gov.cdc.nbs.deduplication.duplicates.model;


public record MatchCandidateData(
    String personUid,
    long numOfMatches,
    String dateIdentified
) {
}
