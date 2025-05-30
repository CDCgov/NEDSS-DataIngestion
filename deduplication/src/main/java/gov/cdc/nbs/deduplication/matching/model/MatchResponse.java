package gov.cdc.nbs.deduplication.matching.model;

public record MatchResponse(
    Long match,
    MatchType matchType) {

  public enum MatchType {
    EXACT,
    POSSIBLE,
    NONE
  }
}
