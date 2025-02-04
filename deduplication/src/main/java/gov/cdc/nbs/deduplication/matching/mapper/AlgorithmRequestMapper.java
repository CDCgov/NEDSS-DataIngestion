package gov.cdc.nbs.deduplication.matching.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import gov.cdc.nbs.deduplication.matching.dto.AlgorithmPass;
import gov.cdc.nbs.deduplication.matching.dto.Evaluator;
import gov.cdc.nbs.deduplication.matching.model.MatchingConfiguration;
import gov.cdc.nbs.deduplication.matching.model.AlgorithmUpdateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AlgorithmRequestMapper {

    private static final Logger log = LoggerFactory.getLogger(AlgorithmRequestMapper.class);

    // Map frontend UI field names to valid API field names
    private static final Map<String, String> FIELD_NAME_MAP = Map.ofEntries(
            Map.entry("First name", "FIRST_NAME"),
            Map.entry("Last name", "LAST_NAME"),
            Map.entry("Suffix", "SUFFIX"),
            Map.entry("Date of birth", "BIRTHDATE"),
            Map.entry("Current sex", "SEX"),
            Map.entry("Gender", "GENDER"),
            Map.entry("Race", "RACE"),
            Map.entry("Address", "ADDRESS"),
            Map.entry("City", "CITY"),
            Map.entry("State", "STATE"),
            Map.entry("Zip", "ZIP"),
            Map.entry("County", "COUNTY"),
            Map.entry("Phone number", "TELECOM"),
            Map.entry("MRN", "MRN"),
            Map.entry("SSN", "SSN"),
            Map.entry("Drivers license", "DRIVERS_LICENSE")
    );

    // Map function names to valid API function values
    private static final Map<String, String> FUNCTION_MAP = Map.of(
            "exact", "func:recordlinker.linking.matchers.compare_match_any",
            "jarowinkler", "func:recordlinker.linking.matchers.compare_fuzzy_match",
            "compare_match_any", "func:recordlinker.linking.matchers.compare_match_any",
            "compare_match_all", "func:recordlinker.linking.matchers.compare_match_all",
            "compare_probabilistic_fuzzy_match", "func:recordlinker.linking.matchers.compare_probabilistic_fuzzy_match"
    );

    // Map UI field names to API field names
    public static String mapField(String fieldName) {
        String mappedField = FIELD_NAME_MAP.get(fieldName);
        if (mappedField == null) {
            log.error("Invalid field name: {}", fieldName);
            throw new IllegalArgumentException("Invalid field name: " + fieldName);
        }
        return mappedField;
    }

    // Map function names to valid API function values
    public static String mapFunc(String funcName) {
        String mappedFunc = FUNCTION_MAP.get(funcName);
        if (mappedFunc == null) {
            log.error("Invalid function: {}", funcName);
            throw new IllegalArgumentException("Invalid function: " + funcName);
        }
        return mappedFunc;
    }

    // Map MatchingConfiguration to AlgorithmUpdateRequest for the API
    public static AlgorithmUpdateRequest mapToAlgorithmRequest(MatchingConfiguration config) {
        AlgorithmUpdateRequest request = new AlgorithmUpdateRequest();
        request.setLabel(config.getLabel());
        request.setDescription(config.getDescription());
        request.setIsDefault(config.isDefault());
        request.setIncludeMultipleMatches(config.isIncludeMultipleMatches());
        request.setBelongingnessRatio(new Double[]{0.1, 0.2});  // Example belongingness ratio

        // Map passes with necessary field and function validations
        List<AlgorithmPass> algorithmPasses = config.getPasses().stream()
                .map(pass -> {
                    AlgorithmPass algorithmPass = new AlgorithmPass();

                    // Map blockingKeys with correct field names
                    algorithmPass.setBlockingKeys(pass.getBlockingCriteria().stream()
                            .map(blocking -> mapField(blocking.getField().getName()))  // Convert UI field names to API field names
                            .collect(Collectors.toList()));

                    // Map evaluators with valid function mappings
                    algorithmPass.setEvaluators(pass.getMatchingCriteria().stream()
                            .map(matching -> new Evaluator(
                                    mapField(matching.getField().getName()),   // Extract name from field object
                                    mapFunc(matching.getMethod().getValue()))) // Extract value from method object
                            .collect(Collectors.toList()));

                    algorithmPass.setRule("func:recordlinker.linking.matchers.rule_match");
                    algorithmPass.setKwargs(new HashMap<>());
                    return algorithmPass;
                })
                .collect(Collectors.toList());

        request.setPasses(algorithmPasses);
        return request;
    }
}
