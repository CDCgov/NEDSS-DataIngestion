package gov.cdc.nbs.deduplication.matching.model;

import com.fasterxml.jackson.annotation.JsonFormat;

public record MatchResponse(
    Long match,
    MatchType matchType,
    LinkResponse linkResponse) {

  @JsonFormat(shape = JsonFormat.Shape.OBJECT)
  public enum MatchType {
    EXACT,
    POSSIBLE,
    NONE
  }
}
