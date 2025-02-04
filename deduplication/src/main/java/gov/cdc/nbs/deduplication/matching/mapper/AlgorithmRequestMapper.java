package gov.cdc.nbs.deduplication.matching.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import gov.cdc.nbs.deduplication.matching.dto.AlgorithmPass;
import gov.cdc.nbs.deduplication.matching.dto.Evaluator;
import gov.cdc.nbs.deduplication.matching.dto.Pass;
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

    public static AlgorithmUpdateRequest mapToAlgorithmRequest(MatchingConfiguration config) {
        AlgorithmUpdateRequest request = new AlgorithmUpdateRequest();

        request.setLabel("dibbs-enhanced");

        request.setDescription("description");
        request.setIsDefault(config.isDefault());
        request.setIncludeMultipleMatches(config.isIncludeMultipleMatches());

        // Ensure belongingness_ratio is correctly set (defaults to [0.0, 1.0] if invalid)
        if (config.getPasses() != null && !config.getPasses().isEmpty()) {
            Pass firstPass = config.getPasses().get(0);  // Get the first pass

            String lowerBound = firstPass.getLowerBound();
            String upperBound = firstPass.getUpperBound();

            if (lowerBound != null && upperBound != null) {
                try {
                    double lower = Double.parseDouble(lowerBound);
                    double upper = Double.parseDouble(upperBound);
                    request.setBelongingnessRatio(new Double[]{lower, upper});
                } catch (NumberFormatException e) {
                    log.error("Invalid lowerBound or upperBound format: {} {}", lowerBound, upperBound);
                    request.setBelongingnessRatio(new Double[]{0.0, 1.0});  // Default fallback
                }
            } else {
                log.warn("Lower/Upper bounds missing, using default values.");
                request.setBelongingnessRatio(new Double[]{0.0, 1.0});  // Default fallback
            }
        }

        // Map passes with necessary field and function validations
        List<AlgorithmPass> algorithmPasses = config.getPasses().stream()
                .map(pass -> {
                    AlgorithmPass algorithmPass = new AlgorithmPass();

                    // Ensure blocking_keys is set and valid
                    if (pass.getBlockingCriteria() != null && !pass.getBlockingCriteria().isEmpty()) {
                        // Map the blocking criteria (make sure field names are valid)
                        algorithmPass.setBlockingKeys(pass.getBlockingCriteria().stream()
                                .map(blocking -> mapField(blocking.getField().getName()))  // Ensure valid field names
                                .collect(Collectors.toList()));
                    } else {
                        // Log a warning if blocking keys are missing, and throw an exception to signal the issue
                        log.warn("Blocking keys are missing for pass: {}", pass);
                        throw new IllegalArgumentException("Blocking keys are required for each pass.");
                    }

                    // Map evaluators
                    if (pass.getMatchingCriteria() != null) {
                        algorithmPass.setEvaluators(pass.getMatchingCriteria().stream()
                                .map(matching -> new Evaluator(
                                        mapField(matching.getField().getName()),  // Valid field names
                                        mapFunc(matching.getMethod().getValue())))  // Valid functions
                                .collect(Collectors.toList()));
                    } else {
                        log.warn("Matching criteria is missing for pass: {}", pass);
                    }

                    algorithmPass.setRule("func:recordlinker.linking.matchers.rule_match");
                    algorithmPass.setKwargs(new HashMap<>());
                    return algorithmPass;
                })
                .collect(Collectors.toList());

        request.setPasses(algorithmPasses);
        return request;
    }


    // Method to generate a random string (e.g., UUID)
    private static String generateRandomString() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8);  // Generate a short random string
    }

}
