package gov.cdc.nbsDedup.model.dto.matching;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class DeDupResponse {


  boolean matchFound;
  MatchType matchType;

  public DeDupResponse(boolean matchFound, boolean needHumanReview) {
    this.matchFound = matchFound;
    if (!matchFound) {
      matchType = MatchType.NO_MATCH;
    }
    if (matchFound && needHumanReview) {
      matchType = MatchType.MATCH_MANUAL_REVIEW;
    }
    if (matchFound && !needHumanReview) {
      matchType = MatchType.MATCH_AUTO_MERGE;
    }
  }

  public enum MatchType {
    NO_MATCH("No match"),
    MATCH_MANUAL_REVIEW("Match and it needs to go thru Manual review"),
    MATCH_AUTO_MERGE("Match and it can be Auto merged");

    private final String description;

    MatchType(String description) {
      this.description = description;
    }

    @Override
    public String toString() {
      return description;
    }
  }

}
