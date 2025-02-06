package gov.cdc.nbs.deduplication.algorithm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cdc.nbs.deduplication.algorithm.dto.AlgorithmPass;
import gov.cdc.nbs.deduplication.algorithm.dto.Evaluator;
import gov.cdc.nbs.deduplication.algorithm.dto.Pass;
import gov.cdc.nbs.deduplication.algorithm.mapper.AlgorithmRequestMapper;
import gov.cdc.nbs.deduplication.algorithm.model.AlgorithmUpdateRequest;
import gov.cdc.nbs.deduplication.algorithm.model.MatchingConfigRequest;
import gov.cdc.nbs.deduplication.algorithm.model.MatchingConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.List;

@Service
public class AlgorithmService {
    private final RestClient recordLinkageClient;
    private final NamedParameterJdbcTemplate template;
    private static final Logger log = LoggerFactory.getLogger(AlgorithmService.class);

    public AlgorithmService(
            @Qualifier("recordLinkageRestClient") final RestClient recordLinkageClient,
            @Qualifier("deduplicationNamedTemplate") final NamedParameterJdbcTemplate template) {
        this.recordLinkageClient = recordLinkageClient;
        this.template = template;
    }

    public void configureMatching(MatchingConfigRequest request) {
        saveMatchingConfiguration(request);
        updateAlgorithm(request);  // Update algorithm in RecordLinker
    }

    public void saveMatchingConfiguration(MatchingConfigRequest request) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Attempt to serialize the request
            String jsonConfig = objectMapper.writeValueAsString(request);
            String sql = "INSERT INTO match_configuration (configuration) VALUES (:configuration)";
            SqlParameterSource params = new MapSqlParameterSource().addValue("configuration", jsonConfig);

            template.update(sql, params);
        } catch (JsonProcessingException e) {
            log.error("Failed to convert MatchingConfigRequest to JSON for label: {}", request.getLabel(), e);
        } catch (Exception e) {
            log.error("Unexpected error while saving matching configuration", e);
        }
    }

    public MatchingConfigRequest getMatchingConfiguration() {
        String sql = "SELECT TOP 1 configuration FROM match_configuration ORDER BY add_time DESC";
        try {
            String jsonConfig = template.queryForObject(sql, new MapSqlParameterSource(), String.class);
            if (jsonConfig == null || jsonConfig.isEmpty()) {
                log.warn("No matching configuration found in database.");
                return null;
            }
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(jsonConfig, MatchingConfigRequest.class);
        } catch (EmptyResultDataAccessException e) {
            log.warn("No matching configuration found in database.");
            return null;
        } catch (Exception e) {
            log.error("Error retrieving matching configuration", e);
            return null;
        }
    }

    public void updateDibbsConfigurations(MatchingConfigRequest configRequest) {
        // Step 1: Set is_default to false for dibbs-basic
        setDibbsBasicToFalse();

        // Step 2: Proceed with updating dibbs-enhanced
        updateAlgorithm(configRequest);
    }

    // Step 1
    public void setDibbsBasicToFalse() {
        try {
            // Prepare the request body
            AlgorithmUpdateRequest updateRequest = new AlgorithmUpdateRequest();
            updateRequest.setLabel("dibbs-basic");
            updateRequest.setIsDefault(false);
            updateRequest.setIncludeMultipleMatches(true);
            updateRequest.setBelongingnessRatio(new Double[]{0.0, 1.0});
            updateRequest.setDescription("The DIBBs Default Algorithm. " +
                    "Based on field experimentation and statistical analysis, " +
                    "this deterministic two-pass algorithm combines geographical " +
                    "and personal information to maximize linkage quality while " +
                    "minimizing false positives");

            // create a dummy pass for the required fields
            AlgorithmPass dummyPass = new AlgorithmPass();
            dummyPass.setBlockingKeys(List.of("BIRTHDATE", "ADDRESS", "ZIP"));
            dummyPass.setEvaluators(List.of(new Evaluator("FIRST_NAME", "func:recordlinker.linking.matchers.compare_fuzzy_match")));
            dummyPass.setRule("func:recordlinker.linking.matchers.rule_match");
            dummyPass.setKwargs(new HashMap<>());

            // Set the dummy pass in the request
            updateRequest.setPasses(List.of(dummyPass));

            // Convert request to JSON
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonRequest = objectMapper.writeValueAsString(updateRequest);
            log.info("Sending PUT request to /algorithm/dibbs-basic: {}", jsonRequest);

            // Send PUT request to /algorithm/dibbs-basic
            recordLinkageClient.put()
                    .uri("/algorithm/dibbs-basic")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(updateRequest)  // Sending the updated request body
                    .retrieve()
                    .body(Void.class);  // No response body needed

            log.info("Dibbs-basic configuration set to is_default = false.");
        } catch (JsonProcessingException e) {
            log.error("Error converting update request to JSON for dibbs-basic", e);
        } catch (Exception e) {
            log.error("Error while updating dibbs-basic configuration", e);
        }
    }

    // Step 2
    public void updateAlgorithm(MatchingConfigRequest configRequest) {
        MatchingConfiguration config = mapToMatchingConfiguration(configRequest);

        AlgorithmUpdateRequest algorithmRequest = AlgorithmRequestMapper.mapToAlgorithmRequest(config);

        algorithmRequest.setLabel("dibbs-enhanced");

        if (configRequest.getPasses() != null && !configRequest.getPasses().isEmpty()) {
            Pass firstPass = configRequest.getPasses().get(0);

            String lowerBound = firstPass.getLowerBound();
            String upperBound = firstPass.getUpperBound();

            if (lowerBound != null && upperBound != null) {
                try {
                    double lower = Double.parseDouble(lowerBound);
                    double upper = Double.parseDouble(upperBound);
                    algorithmRequest.setBelongingnessRatio(new Double[]{lower, upper});
                } catch (NumberFormatException e) {
                    log.error("Invalid lowerBound or upperBound format: {} {}", lowerBound, upperBound);
                    algorithmRequest.setBelongingnessRatio(new Double[]{0.0, 1.0});  // Default fallback
                }
            } else {
                log.warn("Lower/Upper bounds missing, using default values.");
                algorithmRequest.setBelongingnessRatio(new Double[]{0.0, 1.0});  // Default fallback
            }
        } else {
            log.warn("No passes found, using default belongingnessRatio.");
            algorithmRequest.setBelongingnessRatio(new Double[]{0.0, 1.0});  // Default fallback
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonRequest = objectMapper.writeValueAsString(algorithmRequest);
            log.info("Sending PUT request to /algorithm/dibbs-enhanced: {}", jsonRequest);

            recordLinkageClient.put()
                    .uri("/algorithm/dibbs-enhanced")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(algorithmRequest)
                    .retrieve()
                    .body(Void.class);

            log.info("Algorithm updated successfully.");
        } catch (NumberFormatException e) {
            log.error("Invalid format for lowerBound or upperBound in algorithm configuration", e);
        } catch (Exception e) {
            log.error("Failed to update algorithm", e);
        }
    }

    private MatchingConfiguration mapToMatchingConfiguration(MatchingConfigRequest configRequest) {
        MatchingConfiguration config = new MatchingConfiguration();
        config.setLabel(configRequest.getLabel());
        config.setDescription(configRequest.getDescription());
        config.setDefault(configRequest.isDefault());
        config.setIncludeMultipleMatches(configRequest.isIncludeMultipleMatches());
        config.setPasses(configRequest.getPasses());
        return config;
    }
}
