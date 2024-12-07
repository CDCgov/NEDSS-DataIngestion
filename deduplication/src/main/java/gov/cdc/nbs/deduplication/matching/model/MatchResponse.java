package gov.cdc.nbs.deduplication.matching.model;

public record MatchResponse(
    Long match,
    MatchType matchType,
    LinkResponse linkResponse) {

  public enum MatchType {
    EXACT,
    POSSIBLE,
    NONE
  }
}
