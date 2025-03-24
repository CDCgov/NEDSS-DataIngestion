package gov.cdc.nbs.deduplication.algorithm.pass.model.dibbs;

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
        @JsonProperty("belongingness_ratio") List<Double> belongingnessRatio,
        List<DibbsPass> passes) {

    public record DibbsPass(
            @JsonProperty("blocking_keys") List<BlockingAttribute> blockingKeys,
            List<Evaluator> evaluators,
            Rule rule,
            Kwargs kwargs) {
    }

    public record Evaluator(MatchingAttribute feature, Func func) {
    }

    public record Kwargs(
            @JsonProperty("similarity_measure") SimilarityMeasure similarityMeasure,
            Map<String, Double> thresholds,
            @JsonProperty("true_match_threshold") Double trueMatchThreshold,
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
        EXACT("func:recordlinker.linking.matchers.compare_probabilistic_exact_match"),
        FUZZY("func:recordlinker.linking.matchers.compare_probabilistic_fuzzy_match");

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
