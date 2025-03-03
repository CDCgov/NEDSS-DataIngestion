package gov.cdc.nbs.deduplication.algorithm.mapper;

import gov.cdc.nbs.deduplication.algorithm.dto.*;
import gov.cdc.nbs.deduplication.algorithm.model.AlgorithmUpdateRequest;
import gov.cdc.nbs.deduplication.algorithm.model.MatchingConfigRequest;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Set;
import java.util.stream.IntStream;

public class AlgorithmConfigMapper {

    private AlgorithmConfigMapper() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }

    private static final Map<String, String> REVERSE_FUNCTION_MAP = Map.of(
            "func:recordlinker.linking.matchers.compare_match_any", "exact",
            "func:recordlinker.linking.matchers.compare_fuzzy_match", "jarowinkler",
            "func:recordlinker.linking.matchers.compare_match_all", "compare_match_all",
            "func:recordlinker.linking.matchers.compare_probabilistic_fuzzy_match", "compare_probabilistic_fuzzy_match"
    );

    private static final Map<String, String> REVERSE_FIELD_NAME_MAP = Map.of(
            "FIRST_NAME", "First name",
            "LAST_NAME", "Last name",
            "BIRTHDATE", "Date of birth",
            "SEX", "Current sex",
            "ADDRESS", "Street address",
            "CITY", "City",
            "STATE", "State",
            "ZIP", "Zip",
            "MRN", "MRN"
    );

    private static final Set<String> ALL_POSSIBLE_BLOCKING_FIELDS = Set.of(
            "First name", "Last name", "Date of birth", "Current sex", "Street address",
            "City", "State", "Zip", "MRN"
    );

    private static List<MatchingCriteria> mapMatchingCriteria(List<Evaluator> evaluators) {
        if (evaluators == null || evaluators.isEmpty()) {
            return List.of();
        }

        return evaluators.stream()
                .map(evaluator -> new MatchingCriteria(
                        new Field(
                                evaluator.feature(),
                                REVERSE_FIELD_NAME_MAP.getOrDefault(evaluator.feature(), evaluator.feature())
                        ),
                        new Method(
                                REVERSE_FUNCTION_MAP.getOrDefault(evaluator.func(), evaluator.func()),
                                REVERSE_FUNCTION_MAP.getOrDefault(evaluator.func(), evaluator.func())
                        )
                ))
                .toList();
    }

    public static MatchingConfigRequest mapAlgorithmUpdateRequestToMatchingConfigRequest(AlgorithmUpdateRequest request) {
        if (request == null) {
            return null;
        }

        return new MatchingConfigRequest(
                request.label(),
                request.description(),
                request.isDefault(),
                request.includeMultipleMatches(),
                mapPasses(request.passes(), request.belongingnessRatio(), request.description())
        );
    }

    private static List<Pass> mapPasses(List<AlgorithmPass> algorithmPasses, Double[] belongingnessRatio, String description) {
        if (algorithmPasses == null || algorithmPasses.isEmpty()) {
            return List.of();
        }

        String lowerBound = belongingnessRatio != null && belongingnessRatio.length > 0 ? belongingnessRatio[0].toString() : "0.0";
        String upperBound = belongingnessRatio != null && belongingnessRatio.length > 1 ? belongingnessRatio[1].toString() : "1.0";

        return IntStream.range(0, algorithmPasses.size())
                .mapToObj(index -> {
                    AlgorithmPass pass = algorithmPasses.get(index);
                    String passName = "DIBBSDefaultPass" + (index + 1);

                    return new Pass(
                            passName,
                            description,
                            lowerBound,
                            upperBound,
                            mapBlockingCriteria(pass.blockingKeys()),
                            mapMatchingCriteria(pass.evaluators()),
                            mapKwargs(pass.kwargs())
                    );
                })
                .toList();
    }

    private static Map<String, Boolean> mapBlockingCriteria(List<String> blockingKeys) {
        Map<String, Boolean> blockingMap = ALL_POSSIBLE_BLOCKING_FIELDS.stream()
                .collect(Collectors.toMap(field -> field, field -> false));

        if (blockingKeys != null) {
            blockingKeys.forEach(key -> blockingMap.put(REVERSE_FIELD_NAME_MAP.getOrDefault(key, key), true));
        }

        return blockingMap;
    }

    public static Kwargs mapKwargs(Object rawKwargs) {
        if (rawKwargs == null) {
            return null;
        }

        if (rawKwargs instanceof Kwargs kwargs) {
            return kwargs;
        }

        if (rawKwargs instanceof Map<?, ?> kwargsMap) {

            return new Kwargs(
                    (String) kwargsMap.get("similarity_measure"),
                    castToMap(kwargsMap.get("thresholds")),
                    kwargsMap.get("true_match_threshold") instanceof Number
                            ? ((Number) kwargsMap.get("true_match_threshold")).doubleValue()
                            : null,
                    castToMap(kwargsMap.get("log_odds"))
            );
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Double> castToMap(Object obj) {
        return obj instanceof Map<?, ?> ? (Map<String, Double>) obj : null;
    }
}