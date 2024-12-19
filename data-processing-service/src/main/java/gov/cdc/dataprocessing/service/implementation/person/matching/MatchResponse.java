package gov.cdc.dataprocessing.service.implementation.person.matching;

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
