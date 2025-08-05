package gov.cdc.nbs.deduplication.batch.model;

import java.util.ArrayList;
import java.util.List;

public record MatchCandidate(
    String personUid,
    List<String> possibleMatchList) {
  public MatchCandidate(String personUid) {
    this(personUid, new ArrayList<>());
  }
}
