package gov.cdc.nbs.deduplication.algorithm.model;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import gov.cdc.nbs.deduplication.algorithm.pass.model.BlockingAttribute;
import gov.cdc.nbs.deduplication.algorithm.pass.model.MatchingAttribute;

public record DibbsAlgorithm(
    String label,
    String description,
    @JsonProperty("is_default") boolean isDefault,
    @JsonProperty("include_multiple_matches") boolean includeMultipleMatches,
    List<DibbsPass> passes,
    @JsonProperty("max_missing_allowed_proportion") Double missingAllowedProportion,
    @JsonProperty("missing_field_points_proportion") Double missingPointsProportion) {

  public record DibbsPass(
      @JsonProperty("blocking_keys") List<BlockingAttribute> blockingKeys,
      List<Evaluator> evaluators,
      Rule rule,
      @JsonProperty("possible_match_window") List<Double> matchWindow,
      Kwargs kwargs) {
  }

  public record Evaluator(MatchingAttribute feature, Func func) {
  }

  public record Kwargs(
      @JsonProperty("similarity_measure") SimilarityMeasure similarityMeasure,
      Map<String, Double> thresholds,
      @JsonProperty("log_odds") Map<String, Double> logOdds) {
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
