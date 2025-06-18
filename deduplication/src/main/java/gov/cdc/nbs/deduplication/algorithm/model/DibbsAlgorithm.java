package gov.cdc.nbs.deduplication.algorithm.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import gov.cdc.nbs.deduplication.algorithm.pass.model.BlockingAttribute;
import gov.cdc.nbs.deduplication.algorithm.pass.model.MatchingAttribute;

public record DibbsAlgorithm(
    String label,
    String description,
    @JsonProperty("is_default") boolean isDefault,
    @JsonProperty("algorithm_context") AlgorithmContext algorithmContext,
    List<DibbsPass> passes) {

  public record AlgorithmContext(
      @JsonProperty("include_multiple_matches") boolean includeMultipleMatches,
      @JsonProperty("log_odds") List<LogOdd> logOdds,
      Advanced advanced) {
    public record LogOdd(String feature, Double value) {
    }

    public record Advanced(
        @JsonProperty("fuzzy_match_measure") SimilarityMeasure similarityMeasure,
        @JsonProperty("max_missing_allowed_proportion") Double missingAllowedProportion,
        @JsonProperty("missing_field_points_proportion") Double missingPointsProportion) {
    }
  }

  public record DibbsPass(
      String label,
      @JsonProperty("blocking_keys") List<BlockingAttribute> blockingKeys,
      List<Evaluator> evaluators,
      Rule rule,
      @JsonProperty("possible_match_window") List<Double> matchWindow) {
  }

  public record Evaluator(
      MatchingAttribute feature,
      Func func,
      @JsonProperty("fuzzy_match_threshold") Double threshold) {
  }

  public enum SimilarityMeasure {
    JAROWINKLER("JaroWinkler");

    private final String value;

    SimilarityMeasure(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return value;
    }
  }

  public enum Rule {
    PROBABILISTIC("func:recordlinker.linking.matchers.rule_probabilistic_match");

    private final String value;

    Rule(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return value;
    }

  }

  public enum Func {
    EXACT("COMPARE_PROBABILISTIC_EXACT_MATCH"),
    FUZZY("COMPARE_PROBABILISTIC_FUZZY_MATCH");

    private final String value;

    Func(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return value;
    }

  }

}
