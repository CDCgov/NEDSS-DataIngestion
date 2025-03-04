package gov.cdc.nbs.deduplication.algorithm.mapper;

import java.util.*;

import gov.cdc.nbs.deduplication.algorithm.dto.AlgorithmPass;
import gov.cdc.nbs.deduplication.algorithm.dto.Evaluator;
import gov.cdc.nbs.deduplication.algorithm.dto.Pass;
import gov.cdc.nbs.deduplication.algorithm.model.MatchingConfiguration;
import gov.cdc.nbs.deduplication.algorithm.model.AlgorithmUpdateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static gov.cdc.nbs.deduplication.algorithm.mapper.AlgorithmConfigMapper.mapKwargs;

public class AlgorithmRequestMapper {

    private static final Logger log = LoggerFactory.getLogger(AlgorithmRequestMapper.class);

    // Mapping of UI field names to API field names
    private static final Map<String, String> FIELD_NAME_MAP = Map.ofEntries(
            Map.entry("First name", "FIRST_NAME"),
            Map.entry("Last name", "LAST_NAME"),
            Map.entry("Suffix", "SUFFIX"),
            Map.entry("Date of birth", "BIRTHDATE"),
            Map.entry("Current sex", "SEX"),
            Map.entry("Gender", "GENDER"),
            Map.entry("Race", "RACE"),
            Map.entry("Street address", "ADDRESS"),
            Map.entry("City", "CITY"),
            Map.entry("State", "STATE"),
            Map.entry("Zip", "ZIP"),
            Map.entry("County", "COUNTY"),
            Map.entry("Phone number", "TELECOM"),
            Map.entry("MRN", "MRN"),
            Map.entry("SSN", "SSN"),
            Map.entry("Drivers license", "DRIVERS_LICENSE")
    );

    // Mapping of function names to API function values
    private static final Map<String, String> FUNCTION_MAP = Map.of(
            "exact", "func:recordlinker.linking.matchers.compare_match_any",
            "jarowinkler", "func:recordlinker.linking.matchers.compare_fuzzy_match",
            "compare_match_any", "func:recordlinker.linking.matchers.compare_match_any",
            "compare_match_all", "func:recordlinker.linking.matchers.compare_match_all",
            "compare_probabilistic_fuzzy_match", "func:recordlinker.linking.matchers.compare_probabilistic_fuzzy_match"
    );

    private AlgorithmRequestMapper() {
        // Prevent instantiation
    }

    public static String mapField(String fieldName) {
        // Using getOrDefault to handle missing fields gracefully (without throwing exceptions)
        return FIELD_NAME_MAP.getOrDefault(fieldName, fieldName);
    }

    public static String mapFunc(String funcName) {
        // Normalize to lowercase to handle case insensitivity
        String normalizedFuncName = funcName != null ? funcName.toLowerCase() : "";

        // Return mapped function or original if not found
        return FUNCTION_MAP.getOrDefault(normalizedFuncName, funcName);
    }

    public static AlgorithmUpdateRequest mapToAlgorithmRequest(MatchingConfiguration config) {
        return new AlgorithmUpdateRequest(
                "dibbs-enhanced", // Default label
                getDibbsEnhancedDescription(),
                true, // Default is_default flag
                config.isDefault(), // Accessing the 'isDefault' field directly
                getBelongingnessRatio(config), // This is the correct line to pass the belongingness ratio
                mapPasses(config.passes()) // Direct mapping of passes
        );
    }


    private static String getDibbsEnhancedDescription() {
        return "The DIBBs Log-Odds Algorithm. This optional algorithm uses statistical correction to adjust the links between incoming " +
                "records and previously processed patients...";
    }

    private static Double[] getBelongingnessRatio(MatchingConfiguration config) {
        if (config.passes() != null && !config.passes().isEmpty()) {
            Pass firstPass = config.passes().get(0); // Direct access to 'passes' field
            return parseBelongingnessRatio(firstPass.lowerBound(), firstPass.upperBound());
        }
        return new Double[]{0.0, 1.0}; // Default fallback
    }

    private static Double[] parseBelongingnessRatio(String lowerBound, String upperBound) {
        try {
            return new Double[]{
                    Double.parseDouble(lowerBound != null ? lowerBound : "0.0"),
                    Double.parseDouble(upperBound != null ? upperBound : "1.0")
            };
        } catch (NumberFormatException e) {
            log.error("Invalid format for lowerBound or upperBound", e);
            return new Double[]{0.0, 1.0}; // Fallback to default if parsing fails
        }
    }

    private static List<AlgorithmPass> mapPasses(List<Pass> passes) {
        return passes.stream()
                .map(AlgorithmRequestMapper::mapPass)
                .toList();
    }


    private static AlgorithmPass mapPass(Pass pass) {
        // Define all possible data elements (fields)
        Set<String> allFields = Set.of(
                "BIRTHDATE", "FIRST_NAME", "LAST_NAME", "SEX", "ADDRESS", "CITY",
                "STATE", "ZIP", "MRN", "SSN", "PHONE", "EMAIL", "RACE"
        );

        // Initialize blockingKeys map with default 'false' values
        Map<String, Boolean> blockingKeys = new HashMap<>();
        allFields.forEach(field -> blockingKeys.put(field, false));

        // Set existing blocking fields to 'true'
        if (pass.blockingCriteria() != null) {
            blockingKeys.putAll(pass.blockingCriteria());
        }

        List<Evaluator> evaluators = pass.matchingCriteria() != null
                ? pass.matchingCriteria().stream()
                .map(matching -> new Evaluator(
                        mapField(matching.field().name()),  // Map field names
                        mapFunc(matching.method().name())))  // Map function names
                .toList()
                : List.of(); // Empty list fallback

        return new AlgorithmPass(new ArrayList<>(blockingKeys.keySet()),
                evaluators,
                "func:recordlinker.linking.matchers.rule_match",
                mapKwargs(pass.kwargs())
        );
    }
}
