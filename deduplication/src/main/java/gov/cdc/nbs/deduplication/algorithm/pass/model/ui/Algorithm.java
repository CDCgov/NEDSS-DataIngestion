package gov.cdc.nbs.deduplication.algorithm.pass.model.ui;

import java.util.List;

import gov.cdc.nbs.deduplication.algorithm.pass.model.BlockingAttribute;
import gov.cdc.nbs.deduplication.algorithm.pass.model.MatchingAttribute;

public record Algorithm(List<Pass> passes) {

  public record Pass(
      Long id,
      String name,
      String description,
      boolean active,
      List<BlockingAttribute> blockingCriteria,
      List<MatchingAttributeEntry> matchingCriteria,
      Double lowerBound, // number between 0 and upper bound
      Double upperBound) { // number between lowerbound and total log odds

    public Pass(long id, Pass pass) {
      this(
          id,
          pass.name(),
          pass.description(),
          pass.active(),
          pass.blockingCriteria(),
          pass.matchingCriteria(),
          pass.lowerBound(),
          pass.upperBound());
    }
  }

  public record MatchingAttributeEntry(
      MatchingAttribute attribute,
      MatchingMethod method,
      Double threshold) {
  }

  public enum MatchingMethod {
    EXACT,
    JAROWINKLER
  }
}
