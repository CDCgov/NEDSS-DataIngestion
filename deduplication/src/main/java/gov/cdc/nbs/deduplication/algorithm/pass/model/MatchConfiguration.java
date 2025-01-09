package gov.cdc.nbs.deduplication.algorithm.pass.model;

import java.util.List;

public record MatchConfiguration(List<Pass> passes) {

  public record Pass(
      String name,
      String description,
      Boolean active,
      List<BlockingCriteria> blockingCriteria,
      List<MatchingCriteria> matchingCriteria,
      Double lowerBound,
      Double upperBound) {
  }

  public record BlockingCriteria(SelectableCriteria field, SelectableCriteria method) {
  }

  public record MatchingCriteria(SelectableCriteria field, SelectableCriteria method) {
  }

  public record SelectableCriteria(String value, String name) {
  }

}
